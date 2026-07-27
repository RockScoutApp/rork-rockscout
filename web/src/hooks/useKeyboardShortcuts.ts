import { useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";

/**
 * Global keyboard shortcuts for the PWA desktop experience.
 *
 * Navigation (g then key — vim-style "go" prefix):
 *   g h → Home          g i → Identify       g s → Specimens
 *   g c → Collection    g m → Map            g f → Favorite Spots
 *   g j → Field Journal g t → Trip Planner   g r → Reference Library
 *   g a → Achievements  g e → Gear Guide      g o → Trade Board
 *   g n → Community     g p → Profile        g l → Settings
 *
 * Single-key:
 *   /  → focus the first search input on the page
 *   ?  → toggle the shortcuts help overlay
 *   Esc→ blur active element / close help
 *
 * Shortcuts are ignored while typing in inputs/textareas/contenteditable
 * (except Esc, which always works).
 */
export interface ShortcutAction {
  keys: string;
  label: string;
  group: "Navigate" | "Search" | "Help";
}

export const SHORTCUTS: readonly ShortcutAction[] = [
  { keys: "g h", label: "Home", group: "Navigate" },
  { keys: "g i", label: "Identify a rock", group: "Navigate" },
  { keys: "g s", label: "Specimen database", group: "Navigate" },
  { keys: "g c", label: "My collection", group: "Navigate" },
  { keys: "g m", label: "Maps & dig sites", group: "Navigate" },
  { keys: "g f", label: "Favorite spots", group: "Navigate" },
  { keys: "g j", label: "Field journal", group: "Navigate" },
  { keys: "g t", label: "Trip planner", group: "Navigate" },
  { keys: "g r", label: "Reference library", group: "Navigate" },
  { keys: "g a", label: "Achievements", group: "Navigate" },
  { keys: "g e", label: "Gear guide", group: "Navigate" },
  { keys: "g o", label: "Trade board", group: "Navigate" },
  { keys: "g n", label: "Community", group: "Navigate" },
  { keys: "g p", label: "Profile", group: "Navigate" },
  { keys: "g l", label: "Settings", group: "Navigate" },
  { keys: "/", label: "Focus search", group: "Search" },
  { keys: "?", label: "Show this help", group: "Help" },
  { keys: "Esc", label: "Close help / blur field", group: "Help" },
] as const;

const NAV_MAP: Record<string, string> = {
  h: "/app",
  i: "/app/identify",
  s: "/app/specimens",
  c: "/app/collection",
  m: "/app/map",
  f: "/app/favorites",
  j: "/app/journal",
  t: "/app/trips",
  r: "/app/reference",
  a: "/app/achievements",
  e: "/app/gear",
  o: "/app/trade",
  n: "/app/community",
  p: "/app/profile",
  l: "/app/settings",
};

const isTypingTarget = (el: EventTarget | null): boolean => {
  if (!(el instanceof HTMLElement)) return false;
  const tag = el.tagName.toLowerCase();
  if (tag === "input" || tag === "textarea" || tag === "select") return true;
  return el.isContentEditable;
};

interface UseKeyboardShortcutsOptions {
  onToggleHelp?: () => void;
}

export function useKeyboardShortcuts(
  options: UseKeyboardShortcutsOptions = {},
): void {
  const navigate = useNavigate();
  const { onToggleHelp } = options;

  const handleKey = useCallback(
    (e: KeyboardEvent) => {
      const typing = isTypingTarget(document.activeElement);

      // Esc always works — close help or blur the active field.
      if (e.key === "Escape") {
        if (onToggleHelp) onToggleHelp();
        if (document.activeElement instanceof HTMLElement) {
          document.activeElement.blur();
        }
        return;
      }

      // Ignore shortcuts while typing, except Esc (handled above).
      if (typing) return;
      // Ignore when a modifier is held (let browser shortcuts work).
      if (e.metaKey || e.ctrlKey || e.altKey) return;

      // "?" opens help.
      if (e.key === "?") {
        e.preventDefault();
        onToggleHelp?.();
        return;
      }

      // "/" focuses the first visible search input on the page.
      if (e.key === "/") {
        e.preventDefault();
        const inputs = Array.from(
          document.querySelectorAll<HTMLInputElement>(
            'input[type="text"], input[type="search"], input:not([type])',
          ),
        );
        const visible = inputs.find((el) => {
          const rect = el.getBoundingClientRect();
          return rect.width > 0 && rect.height > 0;
        });
        visible?.focus();
        return;
      }

      // "g" prefix navigation handled via a data attribute on the window.
      if (e.key === "g") {
        (window as unknown as { __rsPendingGo?: boolean }).__rsPendingGo = true;
        // Clear the pending prefix if the next key isn't pressed quickly.
        window.setTimeout(() => {
          (window as unknown as { __rsPendingGo?: boolean }).__rsPendingGo =
            false;
        }, 700);
        return;
      }

      const pendingGo = (window as unknown as { __rsPendingGo?: boolean })
        .__rsPendingGo;
      if (pendingGo) {
        (window as unknown as { __rsPendingGo?: boolean }).__rsPendingGo =
          false;
        const target = NAV_MAP[e.key.toLowerCase()];
        if (target) {
          e.preventDefault();
          navigate(target);
        }
      }
    },
    [navigate, onToggleHelp],
  );

  useEffect(() => {
    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
  }, [handleKey]);
}
