import { createRoot } from "react-dom/client";

import App from "./App.tsx";
import "./index.css";

const root = createRoot(document.getElementById("root")!);
root.render(<App />);

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

// Register the service worker for offline support. This only enables the PWA
// shell cache — it does NOT trigger an install prompt. The install prompt is
// suppressed in usePwaInstall and only shown when the user taps "Install app".
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker.register("/sw.js").catch((err) => {
      // Swallow errors — the site works fine without offline support.
      console.warn("SW registration failed:", err);
    });
  });
}
