// RockScout service worker — offline shell caching.
// Caches the app shell so the site still loads with no connection.
// Network-first for navigation requests (fresh content when online),
// cache-first for static assets.
const CACHE_NAME = "rockscout-v6";
const TILE_CACHE = "rockscout-tiles-v1";
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

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(SHELL_ASSETS)).then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k)))
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

  // Never touch cross-origin or API requests.
  if (url.origin !== self.location.origin) return;

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

  // Static assets: cache-first.
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
});
