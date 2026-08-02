import { Link } from "react-router-dom";
import { BookOpen } from "lucide-react";

/**
 * Secondary navbar button for free-tier sign-in.
 * Tier-aware: only renders when signed out (the AuthPill handles signed-in states).
 * Appears below the premium AuthPill as a smaller, muted outline pill.
 * Free users get a read-only PWA — full database, guides, and map, no camera/ID/social.
 */
export const FreeSignInButton = () => (
  <Link
    to="/install"
    title="Install the free read-only PWA — perfect for learning, no account needed"
    className="hidden items-center gap-1.5 rounded-full border border-border bg-card/40 px-3 py-1.5 text-xs font-medium text-muted-foreground transition-all hover:border-primary/30 hover:text-foreground sm:inline-flex"
  >
    <BookOpen className="h-3 w-3" />
    Free PWA
  </Link>
);
