import { useCallback } from "react";
import { useNavigate, useLocation } from "react-router-dom";

/**
 * Safe back navigation for the PWA.
 *
 * `window.history.back()` can close a standalone PWA or navigate to the
 * marketing site when there's no history entry to go back to. This hook
 * falls back to `/app` (Home) when there's no previous entry, preventing
 * the white-screen crash users see when the PWA exits unexpectedly.
 */
export function useSafeBack() {
  const navigate = useNavigate();
  const location = useLocation();

  return useCallback(() => {
    // If we can go back in history, do so — but only within the app.
    // React Router's location history is the safest check.
    if (window.history.length > 1) {
      // Use React Router navigation so the back guard hook can intercept
      // and keep us inside the app shell.
      navigate(-1);
    } else {
      // No history — go to Home instead of letting the PWA close.
      navigate("/app");
    }
  }, [navigate, location.pathname]);
}
