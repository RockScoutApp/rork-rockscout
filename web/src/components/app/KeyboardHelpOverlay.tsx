import { useEffect } from "react";
import { X } from "lucide-react";
import { SHORTCUTS } from "@/hooks/useKeyboardShortcuts";
import { cn } from "@/lib/utils";

interface KeyboardHelpOverlayProps {
  open: boolean;
  onClose: () => void;
}

/** Renders a kbd-style key cap. */
const KeyCap = ({ children }: { children: React.ReactNode }) => (
  <kbd
    className={cn(
      "inline-flex min-w-[1.75rem] items-center justify-center rounded-md border border-border bg-muted px-2 py-1",
      "font-sans text-xs font-semibold text-foreground shadow-[0_1px_0_hsl(var(--border))]",
    )}
  >
    {children}
  </kbd>
);

const GROUP_ORDER = ["Navigate", "Search", "Help"] as const;

/** Modal overlay listing all keyboard shortcuts. Closes on Esc, backdrop click,
 *  or the X button. Keyboard focus is trapped loosely via the close button. */
export default function KeyboardHelpOverlay({
  open,
  onClose,
}: KeyboardHelpOverlayProps) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        e.preventDefault();
        onClose();
      }
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  if (!open) return null;

  const grouped = GROUP_ORDER.map((group) => ({
    group,
    items: SHORTCUTS.filter((s) => s.group === group),
  }));

  return (
    <div
      className="fixed inset-0 z-[100] flex items-center justify-center p-4"
      role="dialog"
      aria-modal="true"
      aria-label="Keyboard shortcuts"
    >
      <button
        type="button"
        aria-label="Close shortcuts help"
        onClick={onClose}
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
      />
      <div className="relative w-full max-w-lg overflow-hidden rounded-2xl border border-border bg-card shadow-2xl">
        <div className="flex items-center justify-between border-b border-border px-5 py-4">
          <div>
            <h2 className="font-display text-lg font-bold text-foreground">
              Keyboard shortcuts
            </h2>
            <p className="mt-0.5 text-xs text-muted-foreground">
              Press <KeyCap>g</KeyCap> then a letter to jump anywhere.
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close"
            className="rounded-lg p-2 text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="max-h-[70vh] overflow-y-auto px-5 py-4">
          <div className="space-y-6">
            {grouped.map(({ group, items }) => (
              <div key={group}>
                <h3 className="mb-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  {group}
                </h3>
                <ul className="space-y-1.5">
                  {items.map((s) => {
                    const keys = s.keys.split(" ");
                    return (
                      <li
                        key={s.keys}
                        className="flex items-center justify-between gap-3 rounded-lg px-2 py-1.5 transition-colors hover:bg-muted/40"
                      >
                        <span className="text-sm text-foreground">
                          {s.label}
                        </span>
                        <span className="flex shrink-0 items-center gap-1">
                          {keys.map((k, i) => (
                            <span key={i} className="flex items-center gap-1">
                              {i > 0 && (
                                <span className="text-xs text-muted-foreground">
                                  then
                                </span>
                              )}
                              <KeyCap>{k}</KeyCap>
                            </span>
                          ))}
                        </span>
                      </li>
                    );
                  })}
                </ul>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
