import { useState, useCallback, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Download,
  CheckCircle2,
  Share,
  MoreVertical,
  MonitorDown,
  Loader2,
} from "lucide-react";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

const PWA_START_URL = "/app";

interface FreeInstallDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const INSTALL_GUIDES: Record<Exclude<Platform, "unsupported">, { heading: string; steps: string[] }> = {
  ios: {
    heading: "Add RockScout to your Home Screen",
    steps: [
      "Tap the Share button in Safari's toolbar",
      "Scroll down and tap \"Add to Home Screen\"",
      "Tap \"Add\" — RockScout installs like a native app",
    ],
  },
  "android-chrome": {
    heading: "Install RockScout from Chrome",
    steps: [
      "Tap the ⋮ menu, top right in Chrome",
      "Tap \"Install app\" or \"Add to Home screen\"",
      "Confirm — RockScout opens full screen from now on",
    ],
  },
  "desktop-chrome": {
    heading: "Install RockScout from Chrome",
    steps: [
      "Click the install icon at the right of the address bar",
      "Or open ⋮ → Cast, save and share → Install page as app",
      "Click \"Install\"",
    ],
  },
  "desktop-edge": {
    heading: "Install RockScout from Edge",
    steps: [
      "Click the ⋯ menu, top right in Edge",
      "Choose Apps → Install this site as an app",
      "Click \"Install\"",
    ],
  },
};

function GuideIcon({ platform }: { platform: Exclude<Platform, "unsupported"> }) {
  if (platform === "ios") return <Share className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
  if (platform === "android-chrome")
    return <MoreVertical className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
  return <MonitorDown className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
}

/**
 * Shared free PWA install dialog.
 *
 * Opens when the user clicks the Free PWA button. It shows one clear primary
 * action: Install Free PWA. If the browser has fired `beforeinstallprompt`,
 * the button triggers the native install/cancel popup. If not (iOS Safari,
 * Firefox, etc.), the same dialog immediately shows platform-specific manual
 * instructions.
 */
export function FreeInstallDialog({ open, onOpenChange }: FreeInstallDialogProps) {
  const { install, installed, hasNativePrompt, platform } = usePwaInstall();
  const navigate = useNavigate();
  const [installing, setInstalling] = useState(false);
  const [showGuide, setShowGuide] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Reset internal state when the dialog closes.
  useEffect(() => {
    if (!open) {
      setInstalling(false);
      setShowGuide(false);
      setError(null);
      return;
    }
    // If there is no programmatic prompt, show manual instructions right away.
    if (!hasNativePrompt && !installed) {
      setShowGuide(true);
    }
  }, [open, hasNativePrompt, installed]);

  const handleInstall = useCallback(async () => {
    setError(null);
    if (!hasNativePrompt) {
      setShowGuide(true);
      return;
    }
    setInstalling(true);
    try {
      const accepted = await install();
      if (accepted) {
        navigate(PWA_START_URL);
      } else {
        // User dismissed the native prompt. Surface instructions so they can
        // still install manually if they change their mind.
        setShowGuide(true);
      }
    } catch (err) {
      console.error("Free PWA install failed:", err);
      setError("Install couldn't start. Try using your browser's menu to add this site to your home screen.");
      setShowGuide(true);
    } finally {
      setInstalling(false);
    }
  }, [hasNativePrompt, install, navigate]);

  const dialogTitle = installed ? "Free PWA installed!" : "Install Free PWA";
  const dialogDescription = installed
    ? "RockScout is installed on this device. Open it to start exploring."
    : "Install the free RockScout PWA for read-only exploration — no account needed.";

  const guide = platform !== "unsupported" ? INSTALL_GUIDES[platform] : null;

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        onOpenChange(next);
        if (!next) {
          setInstalling(false);
          setShowGuide(false);
          setError(null);
        }
      }}
    >
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 font-display text-xl">
            <Download className="h-5 w-5 text-primary" />
            {dialogTitle}
          </DialogTitle>
          <DialogDescription>{dialogDescription}</DialogDescription>
        </DialogHeader>

        {installed ? (
          <div className="space-y-3">
            <div className="flex items-center justify-center gap-2 rounded-lg border border-emerald-500/40 bg-emerald-500/10 p-4 text-sm font-medium text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 className="h-5 w-5" />
              Free PWA installed!
            </div>
            <Button
              type="button"
              onClick={() => {
                onOpenChange(false);
                navigate(PWA_START_URL);
              }}
              size="lg"
              className="w-full inline-flex items-center gap-2 rounded-full bg-primary text-primary-foreground hover:bg-primary/90"
            >
              Open RockScout
            </Button>
          </div>
        ) : (
          <div className="space-y-3">
            <Button
              type="button"
              onClick={handleInstall}
              disabled={installing}
              size="lg"
              className={cn(
                "w-full inline-flex items-center gap-2 rounded-full",
                "border border-primary/50 bg-primary/10 font-semibold text-primary",
                "transition-all hover:border-primary hover:bg-primary/15 hover:shadow-sm disabled:opacity-50",
              )}
            >
              {installing ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Download className="h-4 w-4" />
              )}
              {installing ? "Installing…" : hasNativePrompt ? "Install Free PWA" : "How to install"}
            </Button>

            {error && (
              <div className="rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-xs text-destructive">
                {error}
              </div>
            )}

            {showGuide && guide && (
              <div className="rounded-xl border border-border bg-card/90 p-3 text-left shadow-sm">
                <p className="mb-2 flex items-center gap-1.5 text-xs font-semibold text-foreground">
                  <GuideIcon platform={platform as Exclude<Platform, "unsupported">} />
                  {guide.heading}
                </p>
                <ol className="space-y-1.5">
                  {guide.steps.map((step, i) => (
                    <li
                      key={step}
                      className="flex gap-2 text-xs leading-relaxed text-muted-foreground"
                    >
                      <span className="grid h-4 w-4 shrink-0 place-items-center rounded-full bg-primary/15 text-[10px] font-bold text-primary">
                        {i + 1}
                      </span>
                      {step}
                    </li>
                  ))}
                </ol>
              </div>
            )}

            {showGuide && platform === "unsupported" && (
              <p className="text-center text-xs text-muted-foreground">
                Use your browser's menu to install this site as an app.
              </p>
            )}
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
