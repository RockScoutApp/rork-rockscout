import { Activity, ArrowLeftRight, HeartHandshake, Microscope, Check } from "lucide-react";
import type { ReactNode } from "react";

// ── Badge flags ───────────────────────────────────────────────────────────

export interface BadgeFlags {
  topContributor: boolean;
  avidTrader: boolean;
  specimenContributor: boolean;
  expert: boolean;
  /** When true, the Expert badge shows a small green checkmark (AI-auto-verified). */
  expertAutoVerified: boolean;
}

export const EMPTY_BADGE_FLAGS: BadgeFlags = {
  topContributor: false,
  avidTrader: false,
  specimenContributor: false,
  expert: false,
  expertAutoVerified: false,
};

// ── Avatar size tiers ──────────────────────────────────────────────────────

type AvatarSize = "lg" | "md" | "sm" | "xs";

const SIZE_CONFIG: Record<AvatarSize, { diameter: string; badge: string; gap: string; nameClass: string }> = {
  lg: { diameter: "75px", badge: "16px", gap: "8px", nameClass: "text-sm font-semibold" },
  md: { diameter: "48px", badge: "12px", gap: "8px", nameClass: "text-sm font-medium" },
  sm: { diameter: "32px", badge: "9px", gap: "6px", nameClass: "text-xs font-medium" },
  xs: { diameter: "24px", badge: "7px", gap: "4px", nameClass: "text-xs" },
};

// ── Badge colors ────────────────────────────────────────────────────────────

const BADGE_COLORS = {
  amber: "#E8A33D",
  green: "#5CC98C",
  blue: "#6FA8C7",
  purple: "#9B7BD8",
} as const;

const CITRINE_HSL = "36 80% 58%";
const AQUA_HSL = "20 62% 65%";

// ── BadgeOverlay ────────────────────────────────────────────────────────────

interface BadgeSpec {
  icon: typeof Activity;
  color: string;
}

function getBadgeSpecs(flags: BadgeFlags): Array<{ corner: string; spec: BadgeSpec; autoVerified?: boolean }> {
  const specs: Array<{ corner: string; spec: BadgeSpec; autoVerified?: boolean }> = [];
  if (flags.topContributor) {
    specs.push({ corner: "top-0 left-0", spec: { icon: Activity, color: BADGE_COLORS.amber } });
  }
  if (flags.avidTrader) {
    specs.push({ corner: "top-0 right-0", spec: { icon: ArrowLeftRight, color: BADGE_COLORS.green } });
  }
  if (flags.specimenContributor) {
    specs.push({ corner: "bottom-0 left-0", spec: { icon: HeartHandshake, color: BADGE_COLORS.blue } });
  }
  if (flags.expert) {
    specs.push({ corner: "bottom-0 right-0", spec: { icon: Microscope, color: BADGE_COLORS.purple }, autoVerified: flags.expertAutoVerified });
  }
  return specs;
}

function BadgeOverlay({ flags, size }: { flags: BadgeFlags; size: AvatarSize }) {
  const config = SIZE_CONFIG[size];
  const specs = getBadgeSpecs(flags);

  if (specs.length === 0) return null;

  return (
    <>
      {specs.map((entry, i) => {
        const Icon = entry.spec.icon;
        const iconSize = parseInt(config.badge) * 0.65;
        return (
          <div
            key={i}
            className={`absolute ${entry.corner} flex items-center justify-center rounded-full`}
            style={{
              width: config.badge,
              height: config.badge,
              backgroundColor: "rgba(30, 28, 22, 0.85)",
            }}
          >
            {parseInt(config.badge) >= 9 ? (
              <Icon style={{ width: `${iconSize}px`, height: `${iconSize}px`, color: entry.spec.color }} />
            ) : (
              <div
                className="rounded-full"
                style={{ width: config.badge, height: config.badge, backgroundColor: entry.spec.color }}
              />
            )}
            {entry.autoVerified && (
              <div
                className="absolute flex items-center justify-center rounded-full"
                style={{
                  width: `calc(${config.badge} * 0.6)`,
                  height: `calc(${config.badge} * 0.6)`,
                  top: `calc(-${config.badge} * 0.15)`,
                  right: `calc(-${config.badge} * 0.15)`,
                  backgroundColor: "#5CC98C",
                }}
              >
                <Check style={{ width: `${iconSize * 0.7}px`, height: `${iconSize * 0.7}px`, color: "white" }} />
              </div>
            )}
          </div>
        );
      })}
    </>
  );
}

// ── UserAvatar ──────────────────────────────────────────────────────────────

interface UserAvatarProps {
  imagePath: string | null | undefined;
  displayName: string;
  size?: AvatarSize;
  badgeFlags?: BadgeFlags | null;
  onClick?: () => void;
  showName?: boolean;
  nameClassName?: string;
  borderColor?: string;
  className?: string;
}

export function UserAvatar({
  imagePath,
  displayName,
  size = "md",
  badgeFlags = null,
  onClick,
  showName = true,
  nameClassName,
  borderColor,
  className = "",
}: UserAvatarProps) {
  const config = SIZE_CONFIG[size];
  const glowColor = borderColor ?? `hsl(${CITRINE_HSL} / 0.5)`;
  const hasPhoto = imagePath && imagePath.trim() !== "";

  return (
    <div className={`flex items-center ${className}`} style={{ gap: config.gap }}>
      <div
        className="relative shrink-0"
        style={{ width: config.diameter, height: config.diameter }}
      >
        <div
          className={`flex h-full w-full items-center justify-center overflow-hidden rounded-full ${onClick ? "cursor-pointer" : ""}`}
          onClick={onClick}
          style={{
            background: hasPhoto
              ? "transparent"
              : `linear-gradient(135deg, hsl(${CITRINE_HSL} / 0.15), hsl(${AQUA_HSL} / 0.08))`,
            boxShadow: `0 0 8px ${glowColor}`,
            border: `2px solid ${glowColor}`,
          }}
        >
          {hasPhoto ? (
            <img
              src={imagePath!}
              alt={displayName}
              className="h-full w-full object-cover"
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = "none";
              }}
            />
          ) : (
            <span
              style={{
                fontSize: `calc(${config.diameter} * 0.6)`,
                fontWeight: "bold",
                color: `hsl(${CITRINE_HSL} / 0.7)`,
                lineHeight: 1,
              }}
            >
              ?
            </span>
          )}
        </div>
        {badgeFlags && <BadgeOverlay flags={badgeFlags} size={size} />}
      </div>
      {showName && (
        <span className={nameClassName ?? config.nameClass} style={{ color: "inherit" }}>
          {displayName}
        </span>
      )}
    </div>
  );
}

// ── BadgeRow ──────────────────────────────────────────────────────────────

const BADGE_NAMES: Record<string, string> = {
  topContributor: "Top Contributor",
  avidTrader: "Avid Trader",
  specimenContributor: "Specimen Contributor",
  expert: "Expert",
};

const BADGE_ICONS: Record<string, typeof Activity> = {
  topContributor: Activity,
  avidTrader: ArrowLeftRight,
  specimenContributor: HeartHandshake,
  expert: Microscope,
};

const BADGE_COLOR_MAP: Record<string, string> = {
  topContributor: BADGE_COLORS.amber,
  avidTrader: BADGE_COLORS.green,
  specimenContributor: BADGE_COLORS.blue,
  expert: BADGE_COLORS.purple,
};

export function BadgeRow({ flags, className = "" }: { flags: BadgeFlags; className?: string }) {
  const earned = Object.keys(BADGE_NAMES).filter((key) => flags[key as keyof BadgeFlags]);
  if (earned.length === 0) return null;

  return (
    <div className={`flex flex-wrap gap-2 ${className}`}>
      {earned.map((key) => {
        const Icon = BADGE_ICONS[key];
        const color = BADGE_COLOR_MAP[key];
        return (
          <div
            key={key}
            className="flex items-center gap-1 rounded-full px-2.5 py-1"
            style={{ backgroundColor: "rgba(30, 28, 22, 0.6)" }}
          >
            <Icon className="h-4 w-4" style={{ color }} />
            <span className="text-xs font-medium" style={{ color }}>
              {BADGE_NAMES[key]}
            </span>
          </div>
        );
      })}
    </div>
  );
}
