// RockScout service worker — offline shell caching.
// Caches the app shell so the site still loads with no connection.
// Network-first for navigation requests (fresh content when online),
// cache-first for static assets.
const CACHE_NAME = "rockscout-v11";
const TILE_CACHE = "rockscout-tiles-v1";
const IMAGE_CACHE = "rockscout-images-v1";
// Caches that must survive activation. TILE_CACHE and IMAGE_CACHE are
// deliberately unversioned: downloaded images and map tiles are expensive
// to refetch, so a new shell version must not wipe them.
const KEEP_CACHES = [CACHE_NAME, TILE_CACHE, IMAGE_CACHE];
const SHELL_ASSETS = [
  "/",
  "/app",
  "/index.html",
  "/favicon.png",
  "/favicon-32.png",
  "/apple-touch-icon.png",
  "/pwa-192.png",
  "/pwa-512.png",
];
const TILE_HOSTS = [
  "tile.openstreetmap.org",
  "a.tile.openstreetmap.org",
  "b.tile.openstreetmap.org",
  "c.tile.openstreetmap.org",
];

// Background Sync — when the browser fires the "rockscout-offline-sync" sync
// event (connectivity restored after being offline), notify all open clients
// to drain their IndexedDB sync queue. The actual sync logic lives in the app
// (useOfflineSync hook) because it needs the Supabase auth session.
self.addEventListener("sync", (event) => {
  if (event.tag === "rockscout-offline-sync") {
    event.waitUntil(
      self.clients.matchAll({ includeUncontrolled: true, type: "window" }).then(
        (clients) => {
          clients.forEach((client) =>
            client.postMessage({ type: "OFFLINE_SYNC_TRIGGER" })
          );
        }
      )
    );
  }
});

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches
      .open(CACHE_NAME)
      .then((cache) =>
        // Add assets one at a time. cache.addAll() is atomic — a single 404 or
        // flaky response rejects the whole batch, the install fails, and the
        // site is left with no service worker at all.
        Promise.all(
          SHELL_ASSETS.map((url) =>
            cache.add(new Request(url, { cache: "reload" })).catch(() => undefined)
          )
        )
      )
  );
  // NOTE: deliberately no skipWaiting() here. The new worker parks in the
  // "waiting" state so the page can tell the user an update is ready and then
  // activate it on demand (see the SKIP_WAITING message below). Activating
  // silently mid-session would swap the asset cache underneath running code.
});

// The page asks us to activate immediately once the user accepts the update.
self.addEventListener("message", (event) => {
  if (event.data === "SKIP_WAITING" || (event.data && event.data.type === "SKIP_WAITING")) {
    self.skipWaiting();
  }
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys.filter((k) => KEEP_CACHES.indexOf(k) === -1).map((k) => caches.delete(k))
      )
    ).then(() => self.clients.claim())
  );
});

self.addEventListener("push", (event) => {
  var payload = { title: "RockScout", body: "", url: "" };
  try {
    if (event.data) {
      var parsed = event.data.json();
      if (parsed && typeof parsed === "object") {
        if (parsed.title) payload.title = parsed.title;
        if (parsed.body) payload.body = parsed.body;
        if (parsed.url) payload.url = parsed.url;
      }
    }
  } catch (e) {
    try {
      if (event.data) payload.body = event.data.text();
    } catch (e2) {
      /* ignore */
    }
  }
  var title = payload.title || "RockScout";
  var body = payload.body || "";
  var url = payload.url || "/app/notifications";
  event.waitUntil(
    self.registration.showNotification(title, {
      body: body,
      icon: "/pwa-192.png",
      badge: "/pwa-192.png",
      data: { url: url },
      tag: "rockscout-notification",
      renotify: true,
    }),
  );
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  var targetUrl = (event.notification.data && event.notification.data.url) || "/app/notifications";
  event.waitUntil(
    self.clients.matchAll({ type: "window", includeUncontrolled: true }).then((clientList) => {
      // Focus an existing open tab if one matches the origin.
      for (var i = 0; i < clientList.length; i++) {
        var client = clientList[i];
        if ("focus" in client) {
          client.focus();
          if (client.navigate) client.navigate(targetUrl);
          return;
        }
      }
      // Otherwise open a new tab.
      if (self.clients.openWindow) return self.clients.openWindow(targetUrl);
      return Promise.resolve();
    }),
  );
});

self.addEventListener("fetch", (event) => {
  var request = event.request;
  if (request.method !== "GET") return;

  // Range requests (audio/video seeking) must go straight to the network.
  // Answering a Range request with a cached 200 breaks media playback.
  if (request.headers.has("range")) return;

  var url = new URL(request.url);

  // Map tiles: cache-first into a separate cache, with stale-while-revalidate.
  if (TILE_HOSTS.includes(url.hostname)) {
    event.respondWith(
      caches.open(TILE_CACHE).then((cache) =>
        cache.match(request).then((cached) => {
          const fetchPromise = fetch(request)
            .then((response) => {
              if (response.ok) cache.put(request, response.clone());
              return response;
            })
            .catch(() => cached || Response.error());
          return cached || fetchPromise;
        }),
      ),
    );
    return;
  }

  // r2-pub.rork.com images and our image proxy: cache-first with
  // stale-while-revalidate. Images are immutable (content-addressed by UUID),
  // so a cached copy is always valid. The proxy adds cache-control headers
  // for edge caching; the SW provides instant repeat loads from disk.
  if (url.hostname === "r2-pub.rork.com" ||
      (url.hostname === "rockscout-finder-backend.rork.app" && url.pathname === "/img")) {
    event.respondWith(
      caches.open(IMAGE_CACHE).then((cache) =>
        cache.match(request).then((cached) => {
          const fetchPromise = fetch(request)
            .then((response) => {
              if (response.ok) cache.put(request, response.clone());
              return response;
            })
            .catch(() => cached || Response.error());
          return cached || fetchPromise;
        }),
      ),
    );
    return;
  }

  // Never touch cross-origin or API requests.
  if (url.origin !== self.location.origin) return;

  // Never cache the service worker itself, so a broken SW can always be replaced.
  if (url.pathname === "/sw.js") return;

  // APK downloads are large and immutable per release — never cache them in the
  // PWA shell, otherwise the service worker install/activate will OOM or pin
  // an outdated APK forever.
  if (url.pathname.startsWith("/download/")) return;

  // Navigation requests: network-first, fall back to cached shell when offline.
  if (request.mode === "navigate") {
    event.respondWith(
      fetch(request)
        .then((response) => {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
          return response;
        })
        .catch(() => caches.match(request).then((r) => r || caches.match("/index.html")))
    );
    return;
  }

  // Build output under /assets/ is content-hashed — the filename changes every
  // deploy, so cache-first is both safe and optimal.
  if (url.pathname.startsWith("/assets/")) {
    event.respondWith(
      caches.match(request).then(
        (cached) =>
          cached ||
          fetch(request).then((response) => {
            const copy = response.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
            return response;
          })
      )
    );
    return;
  }

  // Everything else same-origin (icons, images, audio, manifest, …) keeps a
  // stable filename across deploys. Serve the cached copy instantly but always
  // refresh it in the background, so a new build's assets land on the very next
  // visit instead of being pinned forever.
  event.respondWith(
    caches.open(CACHE_NAME).then((cache) =>
      cache.match(request).then((cached) => {
        const network = fetch(request)
          .then((response) => {
            if (response && response.ok) cache.put(request, response.clone());
            return response;
          })
          .catch(() => cached);
        return cached || network;
      })
    )
  );
});
