import { useMemo } from "react";
import { ExternalLink, Star } from "lucide-react";
import { type GearItem, getGearByIds } from "@/data/gear";
import { recordAffiliateClick } from "@/lib/affiliate-tracker";
import { getTopPickIds } from "@/lib/top-picks";

interface AffiliateGearBoxProps {
  /** Section title displayed above the scrolling box. */
  title: string;
  /** Gear item IDs to display. */
  itemIds: string[];
  /** Accent color for the section (hex string). */
  accent?: string;
}

/**
 * A compact, scrollable affiliate gear box — the web equivalent of the
 * Android ScrollingGearBox composable. Shows a fixed-height scrolling list
 * of gear items with affiliate links. Matches the home page gear tile pattern.
 */
export default function AffiliateGearBox({
  title,
  itemIds,
  accent = "#5BC0BE",
}: AffiliateGearBoxProps) {
  const items = useMemo(() => getGearByIds(itemIds), [itemIds]);
  const topPickIds = useMemo(() => getTopPickIds(), []);

  if (items.length === 0) return null;

  return (
    <div className="space-y-2">
      <h3
        className="text-xs font-bold uppercase tracking-wide"
        style={{ color: accent }}
      >
        {title}
      </h3>
      <div
        className="max-h-[200px] overflow-y-auto rounded-xl border p-2"
        style={{
          borderColor: `${accent}40`,
          background:
            "linear-gradient(to bottom, #2A2820, #1E1C16, #16140F)",
        }}
      >
        <div className="space-y-1.5">
          {items.map((item) => {
            const isTopPick = topPickIds.has(item.id);
            return (
              <a
                key={item.id}
                href={item.url}
                target="_blank"
                rel="noopener noreferrer sponsored"
                onClick={() => recordAffiliateClick(item.id, item.name)}
                className="flex items-center gap-2.5 rounded-lg border p-2 transition-colors hover:bg-white/5"
                style={{
                  borderColor: isTopPick
                    ? `${accent}60`
                    : `${accent}30`,
                  backgroundColor: isTopPick
                    ? "rgba(61,56,38,0.9)"
                    : "rgba(58,56,48,0.9)",
                }}
              >
                <div
                  className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-sm"
                  style={{
                    backgroundColor: `${accent}22`,
                    border: `1px solid ${accent}35`,
                  }}
                >
                  {item.emoji}
                </div>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-1.5">
                    <span className="truncate text-sm font-semibold text-white">
                      {item.name}
                    </span>
                    {isTopPick && (
                      <span
                        className="flex shrink-0 items-center gap-0.5 rounded px-1 py-0.5 text-[10px] font-bold"
                        style={{
                          backgroundColor: "rgba(224,176,75,0.25)",
                          border: "1px solid rgba(224,176,75,0.5)",
                          color: "#E0B04B",
                        }}
                      >
                        <Star className="h-2 w-2 fill-current" />
                        Top Pick
                      </span>
                    )}
                  </div>
                  <span
                    className="text-xs font-semibold"
                    style={{ color: accent }}
                  >
                    {item.priceBand}
                  </span>
                </div>
                <ExternalLink
                  className="h-4 w-4 shrink-0"
                  style={{ color: accent }}
                />
              </a>
            );
          })}
        </div>
        <p
          className="mt-2 text-center text-[10px]"
          style={{ color: accent }}
        >
          Links will open separately in your browser
        </p>
      </div>
    </div>
  );
}
