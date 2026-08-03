import { useCallback, useEffect, useState } from "react";

type BeforeInstallPromptEvent = Event & {
  prompt: () => Promise<void>;
  userChoice: Promise<{ outcome: "accepted" | "dismissed" }>;
};

export type Platform =
  | "ios"
  | "android-chrome"
  | "desktop-chrome"
  | "desktop-edge"
  | "unsupported";

/**
 * Detects whether the user is on a platform that can install the site as a PWA.
 * iOS Safari does not fire `beforeinstallprompt` — users must use Share → Add to Home Screen,
 * so we detect iOS separately and surface plain-English instructions there.
 */
function detectPlatform(): Platform {
  if (typeof navigator === "undefined" || typeof window === "undefined") return "unsupported";
  const ua = navigator.userAgent.toLowerCase();
  const isIOS = /iphone|ipad|ipod/.test(ua) || (ua.includes("mac") && "ontouchend" in document);
  if (isIOS) return "ios";
  const isAndroid = /android/.test(ua);
  if (isAndroid) return "android-chrome";
  const isEdge = /edg/.test(ua);
  if (isEdge) return "desktop-edge";
  const isChrome = /chrome/.test(ua) && !/edg|opr|firefox/.test(ua);
  if (isChrome) return "desktop-chrome";
  return "unsupported";
}

function isStandalone(): boolean {
  if (typeof window === "undefined") return false;
  return (
    window.matchMedia?.("(display-mode: standalone)").matches ||
    // iOS Safari standalone indicator
    (window.navigator as unknown as { standalone?: boolean }).standalone === true
  );
}

/**
 * PWA install hook. Captures the browser's `beforeinstallprompt` event so we can
 * trigger the install prompt on demand (no automatic popup), and detects iOS users
 * who need manual "Add to Home Screen" instructions.
 *
 * Returns:
 * - `canInstall`: true when an install button should be shown
 * - `platform`: the detected platform for tailoring UI/instructions
 * - `install`: triggers the native install prompt (Android Chrome, desktop Chrome/Edge).
 *   Returns `true` if the user accepted the install, `false` otherwise.
 * - `installed`: true once the app has been installed / is running standalone
 */
export function usePwaInstall() {
  const [deferredPrompt, setDeferredPrompt] = useState<BeforeInstallPromptEvent | null>(null);
  const [platform] = useState<Platform>(() => detectPlatform());
  const [installed, setInstalled] = useState<boolean>(() => isStandalone());
  // Whether the browser has actually raised beforeinstallprompt (i.e. the
  // native one-tap install prompt is available). On iOS and on browsers that
  // never fire the event, this stays false and the UI falls back to
  // menu-driven instructions instead of being hidden entirely.
  const [hasNativePrompt, setHasNativePrompt] = useState<boolean>(false);

  useEffect(() => {
    const handler = (e: Event) => {
      // Suppress the automatic browser install prompt; we'll trigger it ourselves.
      e.preventDefault();
      setDeferredPrompt(e as BeforeInstallPromptEvent);
      setHasNativePrompt(true);
    };
    const installedHandler = () => {
      setInstalled(true);
      setDeferredPrompt(null);
    };
    window.addEventListener("beforeinstallprompt", handler);
    window.addEventListener("appinstalled", installedHandler);
    return () => {
      window.removeEventListener("beforeinstallprompt", handler);
      window.removeEventListener("appinstalled", installedHandler);
    };
  }, []);

  const install = useCallback(async (): Promise<boolean> => {
    if (!deferredPrompt) return false;
    await deferredPrompt.prompt();
    const choice = await deferredPrompt.userChoice;
    const accepted = choice.outcome === "accepted";
    if (accepted) {
      setInstalled(true);
    }
    setDeferredPrompt(null);
    setHasNativePrompt(false);
    return accepted;
  }, [deferredPrompt]);

  // Always show the button when not already installed — every major browser
  // has an "Add to Home Screen" / "Install app" menu entry, so we surface
  // instructions even when the programmatic prompt is unavailable.
  const canInstall = !installed && platform !== "unsupported";

  return { canInstall, platform, install, installed, hasNativePrompt };
}
