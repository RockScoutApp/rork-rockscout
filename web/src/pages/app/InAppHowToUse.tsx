import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, HelpCircle, Search, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  HOW_TO_SECTIONS,
  CATEGORY_LABELS,
  type HowToSection,
} from "@/pages/HowToUse";

export default function InAppHowToUse() {
  const navigate = useNavigate();
  const [selectedIdx, setSelectedIdx] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const selected: HowToSection | null =
    selectedIdx !== null ? HOW_TO_SECTIONS[selectedIdx] ?? null : null;

  const groupedSections = CATEGORY_LABELS.map(({ key, label, accent }) => ({
    key,
    label,
    accent,
    sections: HOW_TO_SECTIONS.filter((s) => s.category === key),
  })).filter((g) => g.sections.length > 0);

  // Filtered sections based on search query
  const filteredSections = searchQuery.trim()
    ? (() => {
        const q = searchQuery.toLowerCase().trim();
        return HOW_TO_SECTIONS.filter((s) =>
          s.title.toLowerCase().includes(q) ||
          s.shortLabel.toLowerCase().includes(q) ||
          (CATEGORY_LABELS.find((c) => c.key === s.category)?.label ?? "").toLowerCase().includes(q) ||
          s.steps.some((step) => step.toLowerCase().includes(q)),
        );
      })()
    : [];
  const isSearching = searchQuery.trim().length > 0;

  return (
    <div className="space-y-6">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Home
      </Button>

      <div>
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
            <HelpCircle className="h-6 w-6 text-primary" />
          </div>
          <div>
            <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
              How to Use RockScout
            </h1>
            <p className="mt-0.5 text-sm text-muted-foreground">
              Everything you need to know to get started
            </p>
          </div>
        </div>
      </div>

      {/* Intro card */}
      <div className="dark-card sculpted-raised rounded-xl p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          Welcome to RockScout! This guide walks you through every feature of
          the app — from AI rock identification to trading, social features,
          trip planning, and more. Tap any section below to read its
          step-by-step instructions.
        </p>
      </div>

      {/* ── Search bar ── */}
      <div className="relative">
        <Search className="absolute left-3.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground/60" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search 72 sections…"
          className="w-full rounded-xl border border-border/60 bg-card/50 py-2.5 pl-10 pr-10 text-sm text-foreground placeholder:text-muted-foreground/60 transition-all focus:border-primary/50 focus:outline-none focus:ring-1 focus:ring-primary/30"
        />
        {isSearching && (
          <button
            type="button"
            onClick={() => setSearchQuery("")}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/60 transition-colors hover:text-foreground"
            aria-label="Clear search"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {/* ── Search results (when searching) ── */}
      {isSearching && (
        <div className="space-y-2">
          {filteredSections.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-sm text-muted-foreground">
                No sections match “{searchQuery}”
              </p>
            </div>
          ) : (
            <>
              <p className="text-xs font-bold text-muted-foreground">
                {filteredSections.length} result{filteredSections.length === 1 ? "" : "s"}
              </p>
              {filteredSections.map((section) => {
                const idx = HOW_TO_SECTIONS.indexOf(section);
                const catLabel = CATEGORY_LABELS.find((c) => c.key === section.category)?.label ?? "";
                return (
                  <button
                    key={`search-${section.title}`}
                    type="button"
                    onClick={() => setSelectedIdx(idx)}
                    className="group flex w-full items-center gap-3 rounded-xl border px-4 py-3 text-left transition-all hover:-translate-y-0.5 hover:shadow-md"
                    style={{
                      borderColor: `color-mix(in srgb, ${section.accent} 30%, transparent)`,
                      backgroundColor: `color-mix(in srgb, ${section.accent} 8%, transparent)`,
                    }}
                  >
                    <span
                      className="grid h-9 w-9 shrink-0 place-items-center rounded-lg ring-1 transition-transform group-hover:scale-110"
                      style={{
                        backgroundColor: `color-mix(in srgb, ${section.accent} 15%, transparent)`,
                        color: section.accent,
                        boxShadow: `inset 0 0 0 1px color-mix(in srgb, ${section.accent} 30%, transparent)`,
                      }}
                    >
                      <section.icon className="h-4 w-4" />
                    </span>
                    <span className="flex min-w-0 flex-col gap-0.5">
                      <span className="text-[10px] font-bold tabular-nums text-muted-foreground/60">
                        {String(idx + 1).padStart(2, "0")} · {catLabel}
                      </span>
                      <span className="text-sm font-semibold text-foreground">
                        {section.title}
                      </span>
                    </span>
                  </button>
                );
              })}
            </>
          )}
        </div>
      )}

      {/* ── Normal content (when not searching) ── */}
      {!isSearching && (
        <>
      {/* ── Icon Index ── */}
      <div className="dark-card sculpted-raised rounded-xl p-4">
        <h2 className="text-sm font-bold text-foreground">Icon Index</h2>
        <p className="mt-0.5 text-xs text-muted-foreground">
          Every feature at a glance — tap any icon to read its full guide.
        </p>
        <div className="mt-4 space-y-5">
          {groupedSections.map(({ key, label, accent, sections }) => (
            <div key={`index-${key}`}>
              <div className="flex items-center gap-2">
                <span
                  className="h-3 w-3 rounded-full"
                  style={{ backgroundColor: accent }}
                />
                <span
                  className="text-sm font-bold"
                  style={{ color: accent }}
                >
                  {label}
                </span>
              </div>
              <div className="mt-2 grid grid-cols-3 gap-2 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-6">
                {sections.map((section) => {
                  const idx = HOW_TO_SECTIONS.indexOf(section);
                  return (
                    <button
                      key={`idx-${section.title}`}
                      type="button"
                      onClick={() => setSelectedIdx(idx)}
                      className="flex flex-col items-center gap-1.5 rounded-xl border px-2 py-2.5 text-center transition-all hover:-translate-y-0.5 hover:shadow-sm"
                      style={{
                        borderColor: `color-mix(in srgb, ${section.accent} 25%, transparent)`,
                        backgroundColor: `color-mix(in srgb, ${section.accent} 5%, transparent)`,
                      }}
                    >
                      <span
                        className="grid h-8 w-8 place-items-center rounded-lg ring-1 transition-transform hover:scale-110"
                        style={{
                          backgroundColor: `color-mix(in srgb, ${section.accent} 15%, transparent)`,
                          color: section.accent,
                          boxShadow: `inset 0 0 0 1px color-mix(in srgb, ${section.accent} 30%, transparent)`,
                        }}
                      >
                        <section.icon className="h-4 w-4" />
                      </span>
                      <span className="text-[10px] font-semibold leading-tight text-foreground">
                        {section.shortLabel}
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* ── Divider ── */}
      <div className="h-px w-full bg-border/40" />

      {/* ── Detailed sections grouped by category ── */}
      {groupedSections.map(({ key, label, accent, sections }) => (
        <div key={`detail-${key}`} className="space-y-3">
          <div
            className="flex items-center gap-2 rounded-xl border px-3 py-2"
            style={{
              borderColor: `color-mix(in srgb, ${accent} 25%, transparent)`,
              backgroundColor: `color-mix(in srgb, ${accent} 6%, transparent)`,
            }}
          >
            <span
              className="h-3 w-3 rounded-full"
              style={{ backgroundColor: accent }}
            />
            <span
              className="text-sm font-bold"
              style={{ color: accent }}
            >
              {label}
            </span>
          </div>

          <div className="grid grid-cols-2 gap-2.5 sm:grid-cols-3 sm:gap-3 md:grid-cols-4 lg:grid-cols-5">
            {sections.map((section) => {
              const idx = HOW_TO_SECTIONS.indexOf(section);
              return (
                <button
                  key={section.title}
                  type="button"
                  onClick={() => setSelectedIdx(idx)}
                  className="group flex items-center gap-2.5 rounded-xl border px-3 py-3 text-left transition-all hover:-translate-y-0.5 hover:shadow-md sm:px-4"
                  style={{
                    borderColor: `color-mix(in srgb, ${section.accent} 30%, transparent)`,
                    backgroundColor: `color-mix(in srgb, ${section.accent} 8%, transparent)`,
                  }}
                >
                  <span
                    className="grid h-8 w-8 shrink-0 place-items-center rounded-lg ring-1 transition-transform group-hover:scale-110 sm:h-9 sm:w-9"
                    style={{
                      backgroundColor: `color-mix(in srgb, ${section.accent} 15%, transparent)`,
                      color: section.accent,
                      boxShadow: `inset 0 0 0 1px color-mix(in srgb, ${section.accent} 30%, transparent)`,
                    }}
                  >
                    <section.icon className="h-4 w-4" />
                  </span>
                  <span className="flex flex-col gap-0.5 min-w-0">
                    <span className="text-[10px] font-bold tabular-nums text-muted-foreground/60">
                      {String(idx + 1).padStart(2, "0")}
                    </span>
                    <span className="text-xs font-semibold text-foreground sm:text-sm">
                      {section.shortLabel}
                    </span>
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      ))}

      {/* Sign-off */}
      <div className="text-center pt-4">
        <p className="font-display text-xl font-bold text-primary">
          Happy Hunting!
        </p>
      </div>
        </>
      )}

      {/* Section popup dialog */}
      <Dialog
        open={selectedIdx !== null}
        onOpenChange={(open) => {
          if (!open) setSelectedIdx(null);
        }}
      >
        <DialogContent
          aria-describedby={undefined}
          className="max-w-2xl max-h-[85vh] overflow-hidden p-0 sm:rounded-2xl"
        >
          {selected && (
            <div className="flex flex-col h-[85vh] sm:h-[85vh]">
              <DialogHeader className="flex-row items-center gap-3 border-b border-border/60 px-5 py-4 sm:px-6">
                <span
                  className="grid h-10 w-10 shrink-0 place-items-center rounded-xl ring-1"
                  style={{
                    backgroundColor: `color-mix(in srgb, ${selected.accent} 15%, transparent)`,
                    color: selected.accent,
                    boxShadow: `inset 0 0 0 1px color-mix(in srgb, ${selected.accent} 30%, transparent)`,
                  }}
                >
                  <selected.icon className="h-5 w-5" />
                </span>
                <DialogTitle className="font-display text-base font-bold text-foreground sm:text-lg">
                  {selected.title}
                </DialogTitle>
              </DialogHeader>

              <div className="flex-1 overflow-y-auto px-5 py-5 sm:px-6">
                <ol className="flex flex-col gap-3">
                  {selected.steps.map((step, i) => (
                    <li
                      key={i}
                      className="flex gap-3 text-sm leading-relaxed text-muted-foreground"
                    >
                      <span
                        className="mt-0.5 shrink-0 font-display text-xs font-bold tabular-nums"
                        style={{ color: selected.accent }}
                      >
                        {i + 1}.
                      </span>
                      <span>{step}</span>
                    </li>
                  ))}
                </ol>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
