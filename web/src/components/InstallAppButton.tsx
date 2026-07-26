import { Clock } from "lucide-react";

/**
 * Footer install-status badge. While RockScout is not yet available on any
 * platform, this renders a non-interactive "Coming soon" pill instead of an
 * install action. Swap back to the PWA install flow once downloads open up.
 */
export const InstallAppButton = () => (
  <span
    aria-disabled
    className="inline-flex cursor-default items-center gap-2 rounded-full border border-primary/40 bg-primary/10 px-3.5 py-1.5 text-xs font-medium text-primary/80"
  >
    <Clock className="h-3.5 w-3.5" aria-hidden="true" />
    Coming soon to Android, iOS &amp; PC
  </span>
);
