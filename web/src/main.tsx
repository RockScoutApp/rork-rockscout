import { createRoot } from "react-dom/client";

import App from "./App.tsx";
import "./index.css";

createRoot(document.getElementById("root")!).render(<App />);

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
