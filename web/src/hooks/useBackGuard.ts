import { useCallback, useEffect, useRef } from "react";
import { useLocation } from "react-router-dom";

/**
 * Prevents the browser back button from leaving the PWA app shell.
 *
 * Two behaviours:
 * 1. **Root guard** — at `/app` (the app home), an extra history entry is
 *    pushed so that pressing back stays inside the app instead of navigating
 *    away to the marketing site or closing the tab.
 * 2. **Dialog guard** — when a Radix Dialog / Sheet / Drawer / Popover is
 *    open, the back button closes the topmost overlay instead of navigating
 *    to the previous route.
 *
 * The hook monitors the DOM for open Radix overlays
 * (`[data-state="open"]` on `[role="dialog"]` or `[data-vaul-drawer]`).
 */
export function useBackGuard() {
  const location = useLocation();
  const isAppRoute = location.pathname.startsWith("/app");
  const isAppRoot = location.pathname === "/app";
  const armedRef = useRef(false);

  /**
   * Check whether any Radix overlay (Dialog, Sheet, Drawer, Popover,
   * AlertDialog) is currently open in the DOM.
   */
  const hasOpenOverlay = useCallback((): boolean => {
    const openElements = document.querySelectorAll(
      '[data-state="open"][role="dialog"], [data-state="open"][role="alertdialog"], [data-vaul-drawer][data-state="open"]',
    );
    return openElements.length > 0;
  }, []);

  /**
   * Close the topmost Radix overlay by dispatching an Escape key event.
   * Radix listens for `Escape` on the overlay and dismisses it, which
   * triggers the app's `onOpenChange(false)` callback.
   */
  const closeTopmostOverlay = useCallback((): boolean => {
    if (!hasOpenOverlay()) return false;
    document.dispatchEvent(
      new KeyboardEvent("keydown", {
        key: "Escape",
        keyCode: 27,
        code: "Escape",
        bubbles: true,
        cancelable: true,
      }),
    );
    return true;
  }, [hasOpenOverlay]);

  useEffect(() => {
    if (!isAppRoute) return;

    const handlePopState = (_event: PopStateEvent) => {
      // If a dialog/sheet/drawer is open, close it and re-arm the guard
      // so the user stays on the same route.
      if (closeTopmostOverlay()) {
        window.history.pushState({ appGuard: true }, "");
        return;
      }

      // At root `/app`, never let the user go back beyond the app shell.
      // Re-push the guard entry to absorb the back press.
      if (window.location.pathname === "/app") {
        window.history.pushState({ appGuard: true }, "");
        return;
      }

      // For non-root app routes, allow normal back navigation (React Router
      // handles it). But if the user is about to leave `/app` entirely,
      // redirect them back to the app root instead.
      if (!window.location.pathname.startsWith("/app")) {
        window.history.replaceState({}, "", "/app");
        // Force React Router to pick up the change
        window.dispatchEvent(new PopStateEvent("popstate"));
      }
    };

    // Arm the guard: push a dummy state at root so there's always one
    // extra entry to absorb the back press.
    if (isAppRoot && !armedRef.current) {
      armedRef.current = true;
      window.history.pushState({ appGuard: true }, "");
    }

    // Reset armed flag when leaving root
    if (!isAppRoot) {
      armedRef.current = false;
    }

    window.addEventListener("popstate", handlePopState);
    return () => {
      window.removeEventListener("popstate", handlePopState);
    };
  }, [isAppRoute, isAppRoot, closeTopmostOverlay]);

  // Re-arm the guard whenever we navigate back to root
  useEffect(() => {
    if (isAppRoute && isAppRoot && !armedRef.current) {
      armedRef.current = true;
      window.history.pushState({ appGuard: true }, "");
    }
  }, [isAppRoute, isAppRoot, location.pathname]);
}
