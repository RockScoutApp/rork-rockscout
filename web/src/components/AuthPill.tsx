import { useState, useCallback } from "react";
import { useNavigate, Link } from "react-router-dom";
import {
  Crown,
  LogOut,
  Loader2,
  LayoutGrid,
  ChevronDown,
  Sparkles,
  Smartphone,
  Download,
  CheckCircle2,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall } from "@/hooks/usePwaInstall";
import { PremiumInstallDialog } from "@/components/PremiumInstallDialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { cn } from "@/lib/utils";

/**
 * Navbar sign-in pill. Lets website visitors sign in to verify their Premium
 * status — which is required to install the RockScout Premium PWA and keep
 * data synced.
 *
 * States:
 *  - Signed out: "Premium PWA" pill → opens the shared PremiumInstallDialog.
 *  - Signed in + Premium: amber "Premium" pill with a crown + dropdown that
 *    includes an "Install Premium PWA" action (also opens the shared dialog).
 *  - Signed in + Free: outline pill with email + dropdown (upgrade prompt).
 */
export function AuthPill() {
  const { user, isLoading, signOut } = useAuth();
  const { isPremium: isPro, isLoading: tierLoading } = useTier();
  const { installed, hasNativePrompt } = usePwaInstall();
  const navigate = useNavigate();
  const [dialogOpen, setDialogOpen] = useState(false);

  const openDialog = useCallback(() => setDialogOpen(true), []);
  const closeDialog = useCallback(() => setDialogOpen(false), []);

  const handleSignOut = useCallback(async () => {
    await signOut();
  }, [signOut]);

  // Loading state while the session and tier are being restored.
  if (isLoading || tierLoading) {
    return (
      <span
        aria-disabled
        className="inline-flex items-center gap-1.5 rounded-full border border-border bg-card/60 px-3.5 py-1.5 text-xs font-medium text-muted-foreground"
      >
        <Loader2 className="h-3.5 w-3.5 animate-spin" />
        <span className="hidden sm:inline">Checking account…</span>
      </span>
    );
  }

  // Signed out — show the Premium PWA pill that opens the shared dialog.
  if (!user) {
    return (
      <>
        <button
          type="button"
          onClick={openDialog}
          title="Sign in to install the Premium PWA"
          className="group inline-flex items-center gap-1.5 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/10 to-primary/10 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm sm:px-4 sm:py-2 sm:text-sm dark:text-amber-400"
        >
          <Crown className="h-3.5 w-3.5 transition-transform group-hover:scale-110" />
          <span>Premium PWA</span>
        </button>

        <PremiumInstallDialog open={dialogOpen} onOpenChange={setDialogOpen} />
      </>
    );
  }

  // Signed in — show premium-status pill with a dropdown.
  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <button
            type="button"
            title={
              isPro
                ? "Premium verified — install the PWA on this device"
                : "Signed in — upgrade to Premium to install the PWA"
            }
            className={cn(
              "inline-flex items-center gap-1.5 rounded-full border px-3 py-1.5 text-xs font-semibold transition-all hover:shadow-sm sm:px-3.5 sm:py-2 sm:text-sm",
              isPro
                ? "border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 text-amber-600 dark:text-amber-400"
                : "border-border bg-card/60 text-foreground",
            )}
          >
            {isPro ? (
              <Crown className="h-3.5 w-3.5" />
            ) : (
              <Sparkles className="h-3.5 w-3.5 text-primary" />
            )}
            <span className="max-w-[8rem] truncate sm:max-w-none">
              {isPro ? "Premium" : user.email?.split("@")[0] ?? "Account"}
            </span>
            <ChevronDown className="h-3 w-3 opacity-60" />
          </button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-60">
          <DropdownMenuLabel className="font-normal">
            <p className="text-xs text-muted-foreground">Signed in as</p>
            <p className="truncate text-sm font-medium text-foreground">
              {user.email}
            </p>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          {isPro ? (
            <>
              <DropdownMenuItem
                onClick={() => {
                  if (installed) {
                    navigate("/app");
                  } else {
                    openDialog();
                  }
                }}
                className="gap-2"
              >
                <Download className="h-4 w-4" />
                {installed ? "Premium PWA installed" : "Install Premium PWA"}
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => navigate("/app/manage-devices")}
                className="gap-2"
              >
                <Smartphone className="h-4 w-4" />
                Manage devices
              </DropdownMenuItem>
            </>
          ) : (
            <DropdownMenuItem
              onClick={openDialog}
              className="gap-2"
            >
              <Crown className="h-4 w-4 text-amber-500" />
              Upgrade to Premium
            </DropdownMenuItem>
          )}
          <DropdownMenuItem
            onClick={() => navigate("/app")}
            className="gap-2"
          >
            <LayoutGrid className="h-4 w-4" />
            Open the app
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            onClick={handleSignOut}
            className="gap-2 text-destructive focus:text-destructive"
          >
            <LogOut className="h-4 w-4" />
            Sign out
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      {/* Shared premium dialog — opened from the navbar pill (signed out) or
          the dropdown (signed in, premium or free). */}
      <PremiumInstallDialog open={dialogOpen} onOpenChange={setDialogOpen} />
    </>
  );
}
