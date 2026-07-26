import { X, Info } from "lucide-react";
import { useState, useEffect } from "react";
import { isIOS, useStandaloneMode } from "@/hooks/usePlatform";

const DISMISS_KEY = "rockscout:ios_beta_dismissed";

export default function IosBetaBanner() {
  const isStandalone = useStandaloneMode();
  const [dismissed, setDismissed] = useState(false);

  useEffect(() => {
    if (typeof localStorage === "undefined") return;
    setDismissed(localStorage.getItem(DISMISS_KEY) === "true");
  }, []);

  if (!isIOS() || !isStandalone || dismissed) return null;

  const dismiss = () => {
    localStorage.setItem(DISMISS_KEY, "true");
    setDismissed(true);
  };

  return (
    <div className="relative z-40 border-b border-primary/20 bg-primary/10 px-4 py-2.5">
      <div className="mx-auto flex max-w-4xl items-center gap-2.5">
        <Info className="h-4 w-4 shrink-0 text-primary" />
        <p className="flex-1 text-xs leading-relaxed text-primary/90">
          <span className="font-semibold">iOS beta</span> — this mostly works,
          but some things are clunkier than they&apos;ll be in the native iOS
          app. Coming soon.
        </p>
        <button
          onClick={dismiss}
          className="shrink-0 rounded-md p-1 text-primary/60 transition-colors hover:bg-primary/15 hover:text-primary"
          aria-label="Dismiss beta notice"
        >
          <X className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
}
