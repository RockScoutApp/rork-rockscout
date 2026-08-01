import { RefreshCw, X } from "lucide-react";
import { useCallback, useEffect, useState } from "react";

import { applyUpdate, isUpdateReady, onUpdateReady } from "@/lib/swUpdate";

/**
 * Floating "new version is ready" prompt for the installed PWA / web app.
 *
 * A service-worker-backed app keeps serving the build the user first loaded
 * until the worker is swapped out. This banner makes that swap explicit and
 * one-tap, so a shipped update actually reaches people instead of waiting for
 * them to happen to close every tab.
 */
export function UpdateBanner() {
  const [ready, setReady] = useState<boolean>(false);
  const [dismissed, setDismissed] = useState<boolean>(false);
  const [applying, setApplying] = useState<boolean>(false);

  useEffect(() => {
    if (isUpdateReady()) setReady(true);
    return onUpdateReady(() => {
      setReady(true);
      setDismissed(false);
    });
  }, []);

  const handleUpdate = useCallback(() => {
    setApplying(true);
    applyUpdate();
  }, []);

  const handleDismiss = useCallback(() => {
    setDismissed(true);
  }, []);

  if (!ready || dismissed) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      className="fixed inset-x-3 bottom-[calc(env(safe-area-inset-bottom)+1rem)] z-[70] mx-auto flex max-w-md items-center gap-3 rounded-2xl border border-amber-400/40 bg-stone-900/95 px-4 py-3 shadow-2xl shadow-black/50 backdrop-blur-md sm:inset-x-auto sm:right-4"
    >
      <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-amber-400/15 text-amber-300">
        <RefreshCw className={applying ? "h-4 w-4 animate-spin" : "h-4 w-4"} />
      </span>
      <div className="min-w-0 flex-1">
        <p className="text-sm font-semibold text-stone-50">New version available</p>
        <p className="truncate text-xs text-stone-400">
          Refresh to get the latest RockScout.
        </p>
      </div>
      <button
        type="button"
        onClick={handleUpdate}
        disabled={applying}
        className="shrink-0 rounded-full bg-amber-400 px-4 py-2 text-xs font-bold text-stone-950 transition hover:bg-amber-300 active:scale-95 disabled:opacity-60"
      >
        {applying ? "Updating…" : "Update"}
      </button>
      <button
        type="button"
        onClick={handleDismiss}
        aria-label="Dismiss update notice"
        className="shrink-0 rounded-full p-1 text-stone-500 transition hover:text-stone-300"
      >
        <X className="h-4 w-4" />
      </button>
    </div>
  );
}

export default UpdateBanner;
