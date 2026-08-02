import { Navigate, useLocation } from "react-router-dom";
import { Lock, Crown, Camera, Sparkles } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { getRouteTier } from "@/lib/routeAccess";
import { Link } from "react-router-dom";
import type { ReactNode } from "react";

/**
 * Wraps a route element and gates access based on the user's tier and
 * authentication state.
 *
 * - "open" routes: always rendered (read-only content).
 * - "bookmarks" routes: require an account (collection, wishlist, profile,
 *   settings). Anonymous users are redirected to sign-in.
 * - "premium" routes: free users see a lock screen with a "Go Premium" CTA.
 *   Anonymous users also see the lock screen (which leads to the paywall).
 * - "social" routes: invisible to free and anonymous users — redirect to Home.
 */
export function PremiumGate({
  routePath,
  children,
}: {
  routePath: string;
  children: ReactNode;
}) {
  const { user, isLoading: authLoading } = useAuth();
  const { tier, isLoading: tierLoading } = useTier();
  const location = useLocation();
  const accessTier = getRouteTier(routePath);

  const isLoading = authLoading || tierLoading;

  if (isLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  // Anonymous users can only access open routes. Everything else needs an
  // account (bookmarks) or shows the premium paywall (premium/social).
  if (!user) {
    if (accessTier === "open") return <>{children}</>;
    if (accessTier === "bookmarks") {
      return <Navigate to="/app/signin" state={{ from: location.pathname }} replace />;
    }
    // premium and social fall through to the same handling as free users below.
  }

  // Premium users get full access to everything
  if (tier === "premium") return <>{children}</>;

  // Free user (or anonymous on premium/social routes)
  if (accessTier === "social") {
    // Social routes are invisible to free users — redirect to Home
    return <Navigate to="/app" replace />;
  }

  if (accessTier === "premium") {
    return <LockedScreen routePath={routePath} />;
  }

  // "open" and "bookmarks" routes are accessible to authenticated users
  return <>{children}</>;
}

/**
 * The lock screen shown to free users who try to access a premium route.
 */
function LockedScreen({ routePath }: { routePath: string }) {
  const featureInfo = getFeatureInfo(routePath);

  return (
    <div className="flex min-h-[70vh] flex-col items-center justify-center px-6 text-center">
      <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-primary/10 ring-1 ring-primary/20">
        <Lock className="h-10 w-10 text-primary" />
      </div>
      <h2 className="mb-2 font-display text-2xl font-bold text-foreground">
        This is a Premium feature
      </h2>
      <p className="mb-1 max-w-md text-muted-foreground">
        {featureInfo.description}
      </p>
      <p className="mb-6 max-w-md text-sm text-muted-foreground/80">
        Upgrade to Premium to unlock {featureInfo.shortLabel} and everything
        else in RockScout.
      </p>
      <div className="flex flex-col items-center gap-3 sm:flex-row">
        <Link
          to="/app/paywall"
          className="inline-flex items-center gap-2 rounded-full bg-primary px-6 py-3 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90"
        >
          <Crown className="h-4 w-4" />
          Go Premium
        </Link>
        <Link
          to="/app"
          className="inline-flex items-center gap-2 rounded-full border border-border px-6 py-3 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted/50 hover:text-foreground"
        >
          Back to Home
        </Link>
      </div>
      <div className="mt-8 flex flex-wrap justify-center gap-4 text-xs text-muted-foreground/70">
        <span className="inline-flex items-center gap-1.5">
          <Sparkles className="h-3.5 w-3.5" />
          Unlimited AI IDs
        </span>
        <span className="inline-flex items-center gap-1.5">
          <Camera className="h-3.5 w-3.5" />
          Field Camera
        </span>
        <span className="inline-flex items-center gap-1.5">
          <Crown className="h-3.5 w-3.5" />
          All features unlocked
        </span>
      </div>
    </div>
  );
}

interface FeatureInfo {
  shortLabel: string;
  description: string;
}

function getFeatureInfo(routePath: string): FeatureInfo {
  const map: Record<string, FeatureInfo> = {
    identify: {
      shortLabel: "AI rock identification",
      description:
        "Snap a photo and RockScout's 3-model AI engine identifies your specimen with confidence scores, database cross-checks, and web search verification.",
    },
    scan: {
      shortLabel: "the barcode scanner",
      description: "Scan specimen barcodes to quickly look up catalog entries.",
    },
    journal: {
      shortLabel: "the field journal",
      description:
        "Log your field trips and finds with location, weather, notes, and the story of the day — synced across all your devices.",
    },
    trips: {
      shortLabel: "the trip planner",
      description:
        "Build multi-stop rockhounding routes with drag-and-drop reordering, gear checklists, and a calendar view.",
    },
    "archived-trips": {
      shortLabel: "archived trips",
      description: "Browse and restore your completed rockhounding trips.",
    },
  };
  return (
    map[routePath] ?? {
      shortLabel: "this feature",
      description: "This feature is part of RockScout Premium.",
    }
  );
}
