import { useEffect, useState } from "react";

/** Detects if the PWA is running in standalone mode (installed to home screen
 *  or desktop). On iOS Safari this means "Add to Home Screen" was used. */
export function useStandaloneMode(): boolean {
  const [isStandalone, setIsStandalone] = useState(false);

  useEffect(() => {
    const check = () => {
      const standalone =
        window.matchMedia?.("(display-mode: standalone)").matches ||
        (window.navigator as unknown as { standalone?: boolean }).standalone ===
          true;
      setIsStandalone(standalone);
    };
    check();
    window.addEventListener("appinstalled", check);
    return () => window.removeEventListener("appinstalled", check);
  }, []);

  return isStandalone;
}

/** Detects iOS (iPhone/iPad) for the beta disclaimer banner. */
export function isIOS(): boolean {
  if (typeof navigator === "undefined") return false;
  const ua = navigator.userAgent.toLowerCase();
  return (
    /iphone|ipad|ipod/.test(ua) ||
    (ua.includes("mac") && "ontouchend" in document)
  );
}
