import { useNavigate } from "react-router-dom";
import { BookOpen, Download } from "lucide-react";
import { usePwaInstall } from "@/hooks/usePwaInstall";
import { useState } from "react";

/**
 * Free PWA install button for the navbar.
 *
 * Taps the browser's native install prompt when available. If the browser has
 * not fired `beforeinstallprompt` (iOS Safari, Firefox, or Chrome before the
 * engagement heuristic), it navigates to the dedicated Free PWA install page
 * so the user gets a clear install button or platform-specific instructions
 * instead of a dead control.
 */
export const FreeSignInButton = () => {
  const navigate = useNavigate();
  const { canInstall, install, installed, hasNativePrompt } = usePwaInstall();
  const [installing, setInstalling] = useState<boolean>(false);

  if (installed) {
    return (
      <span className="inline-flex items-center gap-1.5 rounded-full border border-green-500/40 bg-green-500/10 px-3 py-1.5 text-xs font-medium text-green-600 dark:text-green-400">
        <BookOpen className="h-3 w-3" />
        Free PWA installed
      </span>
    );
  }

  if (!canInstall) {
    return null;
  }

  const handleClick = async () => {
    if (!hasNativePrompt) {
      navigate("/install/free");
      return;
    }
    setInstalling(true);
    try {
      await install();
    } catch {
      navigate("/install/free");
    } finally {
      setInstalling(false);
    }
  };

  return (
    <button
      type="button"
      onClick={handleClick}
      disabled={installing}
      title="Install the free RockScout PWA — no account required"
      className="inline-flex items-center gap-1.5 rounded-full border border-border bg-card/40 px-3 py-1.5 text-xs font-medium text-muted-foreground transition-all hover:border-primary/30 hover:text-foreground sm:inline-flex"
    >
      {installing ? (
        <Download className="h-3 w-3 animate-bounce" />
      ) : (
        <BookOpen className="h-3 w-3" />
      )}
      {installing ? "Installing…" : "Free PWA"}
    </button>
  );
};
