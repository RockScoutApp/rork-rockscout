import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, HelpCircle, X } from "lucide-react";
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
  const selected: HowToSection | null =
    selectedIdx !== null ? HOW_TO_SECTIONS[selectedIdx] ?? null : null;

  const groupedSections = CATEGORY_LABELS.map(({ key, label, accent }) => ({
    key,
    label,
    accent,
    sections: HOW_TO_SECTIONS.filter((s) => s.category === key),
  })).filter((g) => g.sections.length > 0);

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
