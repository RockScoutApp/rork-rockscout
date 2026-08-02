/**
 * Sculpted stone component library for the RockScout PWA.
 * Replicates the Android app's sculpted() modifier — carved-stone bevels,
 * glowing mineral borders, and press-down sink animations.
 */
import { forwardRef, type ButtonHTMLAttributes, type HTMLAttributes } from "react";
import { cn } from "@/lib/utils";

type RockClass = "igneous" | "sedimentary" | "metamorphic" | "fossil" | "mineral" | "crystal";

const ROCK_CLASS_COLORS: Record<RockClass, string> = {
  igneous: "hsl(var(--rock-igneous))",
  sedimentary: "hsl(var(--rock-sedimentary))",
  metamorphic: "hsl(var(--rock-metamorphic))",
  fossil: "hsl(var(--rock-fossil))",
  mineral: "hsl(var(--rock-mineral))",
  crystal: "hsl(var(--rock-crystal))",
};

export function rockClassColor(cls: RockClass): string {
  return ROCK_CLASS_COLORS[cls];
}

export const rockClassColors = ROCK_CLASS_COLORS;

/* ── SculptedButton ── */

interface SculptedButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  accent?: "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";
  size?: "sm" | "md" | "lg";
  glowing?: boolean;
}

const ACCENT_VARS: Record<NonNullable<SculptedButtonProps["accent"]>, string> = {
  citrine: "36 80% 58%",
  aqua: "20 62% 65%",
  cyan: "174 100% 45%",
  amethyst: "265 47% 67%",
  danger: "4 70% 55%",
  success: "147 49% 55%",
};

const SIZE_CLASSES: Record<NonNullable<SculptedButtonProps["size"]>, string> = {
  sm: "px-3 py-1.5 text-sm rounded-lg",
  md: "px-4 py-2.5 text-sm rounded-xl",
  lg: "px-6 py-3 text-base rounded-xl",
};

export const SculptedButton = forwardRef<HTMLButtonElement, SculptedButtonProps>(
  ({ className, children, accent = "citrine", size = "md", glowing = true, style, ...props }, ref) => {
    const accentVar = ACCENT_VARS[accent];
    return (
      <button
        ref={ref}
        className={cn(
          "sculpted-button sculpted-raised relative inline-flex items-center justify-center gap-2 font-semibold",
          "text-foreground transition-transform",
          glowing && "glowing-border",
          SIZE_CLASSES[size],
          className,
        )}
        style={{
          ["--sculpted-accent" as string]: accentVar,
          ["--glow-color" as string]: accentVar,
          ...style,
        }}
        {...props}
      >
        {children}
      </button>
    );
  },
);
SculptedButton.displayName = "SculptedButton";

/* ── SculptedIconButton ── */

interface SculptedIconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  accent?: "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";
  size?: "sm" | "md" | "lg";
}

const ICON_SIZE_CLASSES: Record<NonNullable<SculptedIconButtonProps["size"]>, string> = {
  sm: "h-9 w-9 rounded-lg",
  md: "h-11 w-11 rounded-xl",
  lg: "h-14 w-14 rounded-2xl",
};

export const SculptedIconButton = forwardRef<HTMLButtonElement, SculptedIconButtonProps>(
  ({ className, children, accent = "aqua", size = "md", style, ...props }, ref) => {
    const accentVar = ACCENT_VARS[accent];
    return (
      <button
        ref={ref}
        className={cn(
          "sculpted-button sculpted-raised inline-flex items-center justify-center",
          ICON_SIZE_CLASSES[size],
          className,
        )}
        style={{
          ["--sculpted-accent" as string]: accentVar,
          ["--glow-color" as string]: accentVar,
          ...style,
        }}
        {...props}
      >
        {children}
      </button>
    );
  },
);
SculptedIconButton.displayName = "SculptedIconButton";

/* ── SculptedCard (DarkCard) ── */

interface SculptedCardProps extends HTMLAttributes<HTMLDivElement> {
  accent?: "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";
  glowing?: boolean;
  interactive?: boolean;
}

export const SculptedCard = forwardRef<HTMLDivElement, SculptedCardProps>(
  ({ className, children, accent = "citrine", glowing = false, interactive = false, style, ...props }, ref) => {
    const accentVar = ACCENT_VARS[accent];
    return (
      <div
        ref={ref}
        className={cn(
          "dark-card relative overflow-hidden",
          "sculpted-raised",
          interactive && "sculpted-button cursor-pointer",
          glowing && "glowing-border",
          className,
        )}
        style={{
          ["--sculpted-accent" as string]: accentVar,
          ["--glow-color" as string]: accentVar,
          ...style,
        }}
        {...props}
      >
        {children}
      </div>
    );
  },
);
SculptedCard.displayName = "SculptedCard";

/* ── TagChip — category-colored pill ── */

interface TagChipProps extends HTMLAttributes<HTMLSpanElement> {
  rockClass?: RockClass;
  accent?: string;
}

export const TagChip = forwardRef<HTMLSpanElement, TagChipProps>(
  ({ className, children, rockClass, accent, style, ...props }, ref) => {
    const colorVar = rockClass
      ? ACCENT_VARS[rockClass === "mineral" ? "citrine" : rockClass === "crystal" ? "amethyst" : "aqua"]
      : accent ?? ACCENT_VARS.citrine;
    const rockColor = rockClass ? ROCK_CLASS_COLORS[rockClass] : undefined;
    return (
      <span
        ref={ref}
        className={cn(
          "inline-flex items-center gap-1.5 rounded-full px-2.5 py-1",
          "text-xs font-semibold border",
          className,
        )}
        style={{
          backgroundColor: `hsl(${rockColor ? `var(--rock-${rockClass})` : colorVar} / 0.15)`,
          borderColor: `hsl(${rockColor ? `var(--rock-${rockClass})` : colorVar} / 0.35)`,
          color: rockColor ?? `hsl(${colorVar})`,
          ...style,
        }}
        {...props}
      >
        {children}
      </span>
    );
  },
);
TagChip.displayName = "TagChip";

/* ── DashboardTile — photo header, icon badge, breathing glow ── */

interface DashboardTileProps {
  label: string;
  subtitle?: string;
  icon: React.ReactNode;
  accent: "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";
  imageUrl?: string;
  count?: number;
  onClick?: () => void;
  className?: string;
}

export const DashboardTile = forwardRef<HTMLButtonElement, DashboardTileProps>(
  ({ label, subtitle, icon, accent, imageUrl, count, onClick, className }, ref) => {
    const accentVar = ACCENT_VARS[accent];
    return (
      <button
        ref={ref}
        onClick={onClick}
        className={cn(
          "sculpted-button sculpted-raised dark-card group relative flex h-44 flex-col overflow-hidden",
          "text-left",
          className,
        )}
        style={{
          ["--sculpted-accent" as string]: accentVar,
          ["--glow-color" as string]: accentVar,
          ["--tile-accent" as string]: accentVar,
          ["--breathe-delay" as string]: `${label.split("").reduce((a, c) => a + c.charCodeAt(0), 0) % 800}ms`,
        }}
      >
        {/* Photo header with color wash */}
        <div className="absolute inset-x-0 top-0 h-24 overflow-hidden">
          {imageUrl ? (
            <img
              src={imageUrl}
              alt=""
              className="h-full w-full object-cover"
            />
          ) : (
            <div
              className="tile-header-wash breathing-glow h-full w-full"
              style={{ ["--breathe-duration" as string]: "3s" }}
            />
          )}
          <div className="absolute inset-0 bg-gradient-to-b from-transparent to-[hsl(30_10%_7%)]" />
        </div>

        {/* Bottom content area */}
        <div className="relative mt-auto p-3.5">
          <div
            className="icon-badge glowing-border mb-2 flex h-10 w-10 items-center justify-center rounded-xl"
            style={{ ["--badge-accent" as string]: accentVar, ["--glow-color" as string]: accentVar }}
          >
            {icon}
          </div>
          <p
            className="text-sm font-bold leading-tight"
            style={{ color: `hsl(${accentVar})` }}
          >
            {label}
          </p>
          {subtitle && (
            <p className="mt-0.5 text-xs font-medium text-[hsl(var(--text-mid))] line-clamp-1">
              {subtitle}
            </p>
          )}
          {count !== undefined && count > 0 && (
            <span
              className="absolute right-3 top-3 rounded-full px-2 py-0.5 text-xs font-bold"
              style={{
                backgroundColor: `hsl(${accentVar} / 0.2)`,
                color: `hsl(${accentVar})`,
              }}
            >
              {count}
            </span>
          )}
        </div>
      </button>
    );
  },
);
DashboardTile.displayName = "DashboardTile";

/* ── ScreenScaffold — standard screen wrapper with back button ── */

interface ScreenScaffoldProps {
  title: string;
  onBack: () => void;
  children: React.ReactNode;
  actions?: React.ReactNode;
  className?: string;
}

export function ScreenScaffold({
  title,
  onBack,
  children,
  actions,
  className,
}: ScreenScaffoldProps) {
  return (
    <div className={cn("flex min-h-full flex-col", className)}>
      <div className="flex items-center gap-2 px-2 pt-8 pb-2 md:pt-4">
        <SculptedIconButton
          accent="aqua"
          size="sm"
          onClick={onBack}
          aria-label="Back"
        >
          <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
        </SculptedIconButton>
        <h1
          className="flex-1 truncate font-display text-xl font-bold md:text-2xl"
          style={{ color: "hsl(var(--aqua))" }}
        >
          {title}
        </h1>
        {actions}
      </div>
      <div className="flex-1">{children}</div>
    </div>
  );
}

/* ── StatTile — colored stat display ── */

interface StatTileProps {
  label: string;
  value: string | number;
  accent: "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";
  icon?: React.ReactNode;
}

export function StatTile({ label, value, accent, icon }: StatTileProps) {
  const accentVar = ACCENT_VARS[accent];
  return (
    <div
      className="sculpted-raised dark-card flex flex-col items-center gap-1 p-3 text-center"
      style={{ ["--sculpted-accent" as string]: accentVar }}
    >
      {icon && (
        <div
          className="icon-badge flex h-8 w-8 items-center justify-center rounded-lg"
          style={{ ["--badge-accent" as string]: accentVar, color: `hsl(${accentVar})` }}
        >
          {icon}
        </div>
      )}
      <span className="text-2xl font-bold" style={{ color: `hsl(${accentVar})` }}>
        {value}
      </span>
      <span className="text-xs font-medium text-[hsl(var(--text-mid))]">{label}</span>
    </div>
  );
}

/* ── ProfileStatBar — XP/level progress bar ── */

interface ProfileStatBarProps {
  current: number;
  max: number;
  accent?: "citrine" | "aqua" | "cyan" | "amethyst";
  label?: string;
}

export function ProfileStatBar({
  current,
  max,
  accent = "citrine",
  label,
}: ProfileStatBarProps) {
  const accentVar = ACCENT_VARS[accent];
  const pct = Math.min(100, (current / max) * 100);
  return (
    <div className="w-full">
      {label && (
        <div className="mb-1 flex justify-between text-xs font-medium text-[hsl(var(--text-mid))]">
          <span>{label}</span>
          <span>{current} / {max}</span>
        </div>
      )}
      <div className="h-2.5 w-full overflow-hidden rounded-full bg-[hsl(var(--muted))]">
        <div
          className="h-full rounded-full transition-all duration-500"
          style={{
            width: `${pct}%`,
            background: `linear-gradient(90deg, hsl(${accentVar} / 0.6), hsl(${accentVar}))`,
            boxShadow: `0 0 8px hsl(${accentVar} / 0.4)`,
          }}
        />
      </div>
    </div>
  );
}
