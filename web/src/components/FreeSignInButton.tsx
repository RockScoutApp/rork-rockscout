import { BookOpen, Download } from "lucide-react";
import { FreeInstallDialog } from "@/components/FreeInstallDialog";
import { usePwaInstall } from "@/hooks/usePwaInstall";
import { useState } from "react";

/**
 * Free PWA install button for the navbar.
 *
 * Opens the shared FreeInstallDialog so the user always sees a clear popup.
 * When the browser has fired `beforeinstallprompt`, the dialog immediately
 * surfaces the native install/cancel prompt. Otherwise it shows platform-specific
 * "Add to Home Screen" instructions inside the same dialog.
 */
export const FreeSignInButton = () => {
  const { installed, canInstall } = usePwaInstall();
  const [installing, setInstalling] = useState<boolean>(false);
  const [dialogOpen, setDialogOpen] = useState(false);

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

  const handleClick = () => {
    setDialogOpen(true);
  };

  return (
    <>
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
      <FreeInstallDialog open={dialogOpen} onOpenChange={setDialogOpen} />
    </>
  );
};
