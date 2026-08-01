import { createRoot } from "react-dom/client";

import App from "./App.tsx";
import { ErrorBoundary } from "./components/ErrorBoundary";
import { registerServiceWorker } from "./lib/swUpdate";
import "./index.css";

const root = createRoot(document.getElementById("root")!);
root.render(
  <ErrorBoundary>
    <App />
  </ErrorBoundary>,
);

// Clear the boot placeholder + self-healing timer once React has mounted.
// requestIdleCallback (fallback to setTimeout) ensures the first paint has
// committed before we remove the placeholder, avoiding a flash of empty body.
const clearBoot = () => {
  const done = (window as unknown as { __rockscoutBootDone?: () => void }).__rockscoutBootDone;
  if (typeof done === "function") done();
};
if (typeof (window as unknown as { requestIdleCallback?: (cb: () => void) => void }).requestIdleCallback === "function") {
  (window as unknown as { requestIdleCallback: (cb: () => void) => void }).requestIdleCallback(clearBoot);
} else {
  setTimeout(clearBoot, 0);
}

// Register the service worker for offline support and keep it up to date.
// This only enables the PWA shell cache — it does NOT trigger an install
// prompt. The install prompt is suppressed in usePwaInstall and only shown
// when the user taps "Install app".
//
// registerServiceWorker also polls for new builds and raises the
// "rockscout:update-ready" event that <UpdateBanner /> listens for, so a
// deployed update reaches installed PWAs instead of being pinned to whatever
// build the user first loaded.
window.addEventListener("load", registerServiceWorker);
