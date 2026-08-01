/**
 * Service-worker update pipeline for the PWA.
 *
 * A PWA that never checks for a new service worker will happily serve the build
 * a user installed months ago. This module makes updates deterministic:
 *
 * 1. Register `/sw.js` and keep a handle on the registration.
 * 2. Poll for a new worker on an interval, whenever the tab becomes visible,
 *    and whenever the browser comes back online.
 * 3. When a new worker finishes installing while an old one is still in control,
 *    emit `rockscout:update-ready` so the UI can offer a one-tap refresh.
 * 4. `applyUpdate()` tells the waiting worker to take over, then reloads once —
 *    and only once — as soon as it does.
 */

const UPDATE_READY_EVENT = "rockscout:update-ready";
/** How often to ask the browser to re-fetch sw.js while the tab is open. */
const POLL_INTERVAL_MS = 30 * 60 * 1000;

let registration: ServiceWorkerRegistration | null = null;
let reloading = false;

/** True once a new build is installed and only waiting for a refresh. */
export function isUpdateReady(): boolean {
  return registration?.waiting != null;
}

function announceUpdateReady(): void {
  window.dispatchEvent(new CustomEvent(UPDATE_READY_EVENT));
}

/** Subscribe to "a new version is ready" — returns an unsubscribe function. */
export function onUpdateReady(listener: () => void): () => void {
  window.addEventListener(UPDATE_READY_EVENT, listener);
  return () => window.removeEventListener(UPDATE_READY_EVENT, listener);
}

/**
 * Activates the waiting worker and reloads into the new build.
 * Falls back to a plain reload if there's no waiting worker (e.g. the update
 * already activated in another tab).
 */
export function applyUpdate(): void {
  const waiting = registration?.waiting;
  if (!waiting) {
    window.location.reload();
    return;
  }
  waiting.postMessage("SKIP_WAITING");
  // `controllerchange` fires once the new worker takes over. Guard against the
  // reload loop browsers are famous for by only ever reloading once.
  window.setTimeout(() => {
    if (!reloading) {
      reloading = true;
      window.location.reload();
    }
  }, 3000);
}

function watchInstalling(reg: ServiceWorkerRegistration): void {
  const installing = reg.installing;
  if (!installing) return;
  installing.addEventListener("statechange", () => {
    // An installed worker while another one controls the page == an update.
    // With no controller it's the very first install, which needs no prompt.
    if (installing.state === "installed" && navigator.serviceWorker.controller) {
      announceUpdateReady();
    }
  });
}

/** Registers the service worker and wires up update detection. */
export function registerServiceWorker(): void {
  if (!("serviceWorker" in navigator)) return;

  navigator.serviceWorker.addEventListener("controllerchange", () => {
    if (reloading) return;
    reloading = true;
    window.location.reload();
  });

  navigator.serviceWorker
    .register("/sw.js")
    .then((reg) => {
      registration = reg;

      // A worker may already be waiting from a previous visit.
      if (reg.waiting && navigator.serviceWorker.controller) {
        announceUpdateReady();
      }

      watchInstalling(reg);
      reg.addEventListener("updatefound", () => watchInstalling(reg));

      const checkForUpdate = (): void => {
        reg.update().catch(() => undefined);
      };

      window.setInterval(checkForUpdate, POLL_INTERVAL_MS);
      document.addEventListener("visibilitychange", () => {
        if (document.visibilityState === "visible") checkForUpdate();
      });
      window.addEventListener("online", checkForUpdate);
    })
    .catch((err: unknown) => {
      // The site works fine without offline support — never block boot on this.
      console.warn("SW registration failed:", err);
    });
}
