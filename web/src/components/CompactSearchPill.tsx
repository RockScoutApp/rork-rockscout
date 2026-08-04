import { useState, useRef, useEffect } from "react";
import { Search, X } from "lucide-react";

interface CompactSearchPillProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
}

/**
 * A compact single-row search pill that expands on tap/focus.
 * Matches the Android CompactSearchPill composable — saves screen
 * space while keeping search always accessible.
 */
export function CompactSearchPill({
  value,
  onChange,
  placeholder = "Search…",
  className = "",
}: CompactSearchPillProps) {
  const [expanded, setExpanded] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (expanded) {
      inputRef.current?.focus();
    }
  }, [expanded]);

  if (!expanded && !value) {
    return (
      <button
        type="button"
        onClick={() => setExpanded(true)}
        className={`inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-3.5 py-2 text-sm text-muted-foreground transition-all hover:border-primary/40 hover:bg-card ${className}`}
      >
        <Search className="h-4 w-4" />
        <span className="truncate">{placeholder}</span>
      </button>
    );
  }

  return (
    <div
      className={`relative inline-flex items-center rounded-full border border-primary/40 bg-card px-3 py-1.5 transition-all ${className}`}
    >
      <Search className="mr-2 h-4 w-4 shrink-0 text-primary" />
      <input
        ref={inputRef}
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onBlur={() => {
          if (!value) setExpanded(false);
        }}
        placeholder={placeholder}
        className="w-full bg-transparent text-sm text-foreground outline-none placeholder:text-muted-foreground"
        style={{ minWidth: "120px" }}
      />
      {value && (
        <button
          type="button"
          onClick={() => {
            onChange("");
            setExpanded(false);
          }}
          className="ml-2 shrink-0 rounded-full p-0.5 text-muted-foreground hover:text-foreground"
          aria-label="Clear search"
        >
          <X className="h-3.5 w-3.5" />
        </button>
      )}
    </div>
  );
}
