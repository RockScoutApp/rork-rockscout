import { useState } from "react";
import { Plus, X, Share, MoreVertical, Smartphone } from "lucide-react";
import { usePwaInstall } from "@/hooks/usePwaInstall";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";

/**
 * Always-visible "Add to Home Screen" button for the site footer.
 *
 * The button is shown on every platform unless the site is already running
 * standalone (installed). Tapping it:
 * - On Android Chrome / desktop Chrome / desktop Edge: fires the native
 *   install prompt when available; if the browser hasn't raised
 *   `beforeinstallprompt` yet, we fall back to a short menu-instructions dialog.
 * - On iOS Safari (no programmatic prompt): opens a modal with plain-English
 *   steps — "Tap Share, then Add to Home Screen".
 */
export const InstallAppButton = () => {
  const { canInstall, platform, install, hasNativePrompt } = usePwaInstall();
  const [showIosHelp, setShowIosHelp] = useState(false);
  const [showFallbackHelp, setShowFallbackHelp] = useState(false);

  if (!canInstall) return null;

  const handleClick = async () => {
    if (platform === "ios") {
      setShowIosHelp(true);
      return;
    }
    if (hasNativePrompt) {
      await install();
      return;
    }
    // No native prompt available (e.g. Firefox, or Chrome before
    // beforeinstallprompt fired). Show menu instructions instead.
    setShowFallbackHelp(true);
  };

  return (
    <>
      <button
        type="button"
        onClick={handleClick}
        className="inline-flex items-center gap-2 rounded-full border border-primary/40 bg-primary/10 px-3.5 py-1.5 text-xs font-medium text-primary transition-colors hover:bg-primary/20 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/60"
        aria-label="Add RockScout to your Home Screen"
      >
        <Plus className="h-3.5 w-3.5" aria-hidden="true" />
        {platform === "ios" ? "Add to Home Screen" : platform === "android-chrome" ? "Install app" : "Install to desktop"}
      </button>

      {/* iOS Safari instructions */}
      <Dialog open={showIosHelp} onOpenChange={setShowIosHelp}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Plus className="h-5 w-5 text-primary" aria-hidden="true" />
              Add RockScout to your Home Screen
            </DialogTitle>
            <DialogDescription>
              It takes a few seconds — no app store needed.
            </DialogDescription>
          </DialogHeader>
          <ol className="space-y-3 text-sm text-foreground/90">
            <li className="flex items-start gap-3">
              <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                1
              </span>
              <span className="flex items-center gap-1.5">
                Tap the
                <Share className="inline h-4 w-4 text-primary" aria-hidden="true" />
                <span className="font-medium">Share</span>
                button at the bottom of Safari.
              </span>
            </li>
            <li className="flex items-start gap-3">
              <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                2
              </span>
              <span className="flex items-center gap-1.5">
                Scroll down and tap
                <Plus className="inline h-4 w-4 text-primary" aria-hidden="true" />
                <span className="font-medium">Add to Home Screen</span>.
              </span>
            </li>
            <li className="flex items-start gap-3">
              <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                3
              </span>
              <span>Tap <span className="font-medium">Add</span>. That's it — RockScout will open like an app from your Home Screen.</span>
            </li>
          </ol>
          <div className="mt-2 flex items-start gap-2 rounded-lg bg-muted/40 p-3 text-xs text-muted-foreground">
            <X className="mt-0.5 h-3.5 w-3.5 shrink-0" aria-hidden="true" />
            <span>This just saves the website to your phone. It's the same site — no download, no extra storage used.</span>
          </div>
        </DialogContent>
      </Dialog>

      {/* Fallback instructions for browsers without a native install prompt */}
      <Dialog open={showFallbackHelp} onOpenChange={setShowFallbackHelp}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Smartphone className="h-5 w-5 text-primary" aria-hidden="true" />
              Add RockScout to your Home Screen
            </DialogTitle>
            <DialogDescription>
              No app store needed — it only takes a few seconds.
            </DialogDescription>
          </DialogHeader>
          <ol className="space-y-3 text-sm text-foreground/90">
            <li className="flex items-start gap-3">
              <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                1
              </span>
              <span className="flex items-center gap-1.5">
                Tap the
                <MoreVertical className="inline h-4 w-4 text-primary" aria-hidden="true" />
                <span className="font-medium">browser menu</span>
                (the three dots or bars, usually top or bottom right).
              </span>
            </li>
            <li className="flex items-start gap-3">
              <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                2
              </span>
              <span>Tap <span className="font-medium">Add to Home screen</span> (or <span className="font-medium">Install app</span>).</span>
            </li>
            <li className="flex items-start gap-3">
              <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                3
              </span>
              <span>Confirm, and RockScout will open like an app right from your Home Screen.</span>
            </li>
          </ol>
          <div className="mt-2 flex items-start gap-2 rounded-lg bg-muted/40 p-3 text-xs text-muted-foreground">
            <X className="mt-0.5 h-3.5 w-3.5 shrink-0" aria-hidden="true" />
            <span>This saves the website to your device — same site, no extra storage used.</span>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};
