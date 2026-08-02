import { useParams, useNavigate } from "react-router-dom";
import { MapPin, ExternalLink, Navigation, Mountain } from "lucide-react";
import { blmDigSites, trailheads, campgrounds, type BlmDigSite, type Trailhead, type Campground } from "@/data/locations";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

/**
 * Unified BLM detail screen — handles BLM dig sites, trailheads, and campgrounds
 * based on the type prefix in the ID.
 */
export default function BlmDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  let site: BlmDigSite | Trailhead | Campground | null = null;
  let kind: "site" | "trailhead" | "campground" = "site";

  if (id?.startsWith("blm-")) {
    const realId = id.slice(4);
    site = blmDigSites.find((s) => s.id === realId) ?? null;
    kind = "site";
  } else if (id?.startsWith("th-")) {
    const realId = id.slice(3);
    site = trailheads.find((s) => s.id === realId) ?? null;
    kind = "trailhead";
  } else if (id?.startsWith("cg-")) {
    const realId = id.slice(3);
    site = campgrounds.find((s) => s.id === realId) ?? null;
    kind = "campground";
  }

  if (!site) {
    return (
      <ScreenScaffold title="Not Found" onBack={() => navigate("/app/blm-guide")}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Mountain className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">This BLM location doesn't exist.</p>
          <button
            onClick={() => navigate("/app/blm-guide")}
            className="text-sm font-semibold text-primary"
          >
            Back to BLM Guide
          </button>
        </div>
      </ScreenScaffold>
    );
  }

  const title =
    kind === "site" ? (site as BlmDigSite).name :
    kind === "trailhead" ? (site as Trailhead).name :
    (site as Campground).name;

  const region = site.region;
  const lat = site.latitude;
  const lng = site.longitude;

  const accentColor =
    kind === "site" ? "#C97B4A" :
    kind === "trailhead" ? "#6B9E7E" :
    "#E0A040";

  const kindLabel =
    kind === "site" ? "BLM Collecting Site" :
    kind === "trailhead" ? "Trailhead" :
    "Campground";

  const blmSite = kind === "site" ? (site as BlmDigSite) : null;
  const trailhead = kind === "trailhead" ? (site as Trailhead) : null;
  const campground = kind === "campground" ? (site as Campground) : null;

  const description =
    blmSite?.whatToFind ?? trailhead?.description ?? campground?.description ?? "";

  return (
    <ScreenScaffold title={title} onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        {/* Header card */}
        <SculptedCard accent="aqua" className="p-5">
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0 flex-1">
              <h1 className="font-display text-xl font-bold text-foreground">{title}</h1>
              <p className="mt-1 flex items-center gap-1.5 text-sm text-muted-foreground">
                <MapPin className="h-3.5 w-3.5" />
                {region}
              </p>
            </div>
            <span
              className="shrink-0 rounded-full px-3 py-1 text-xs font-bold"
              style={{ backgroundColor: `${accentColor}25`, color: accentColor }}
            >
              {kindLabel}
            </span>
          </div>
          {description && (
            <p className="mt-3 text-sm leading-relaxed text-[hsl(var(--text-mid))]">
              {description}
            </p>
          )}
        </SculptedCard>

        {/* Details */}
        {blmSite && (
          <>
            {blmSite.directions && (
              <SculptedCard accent="aqua" className="space-y-1 p-4">
                <h3 className="text-xs font-bold uppercase text-muted-foreground">Directions</h3>
                <p className="text-sm text-foreground">{blmSite.directions}</p>
              </SculptedCard>
            )}
            {blmSite.facilities && (
              <SculptedCard accent="aqua" className="space-y-1 p-4">
                <h3 className="text-xs font-bold uppercase text-muted-foreground">Facilities</h3>
                <p className="text-sm text-foreground">{blmSite.facilities}</p>
              </SculptedCard>
            )}
            {(blmSite.feeInfo || blmSite.difficulty) && (
              <div className="flex flex-wrap gap-2">
                {blmSite.feeInfo && <TagChip accent={`hsl(${CITRINE_HEX})`}>{blmSite.feeInfo}</TagChip>}
                {blmSite.difficulty && <TagChip accent={`hsl(${AQUA_HEX})`}>{blmSite.difficulty}</TagChip>}
              </div>
            )}
          </>
        )}

        {campground?.feeInfo && (
          <TagChip accent={`hsl(${CITRINE_HEX})`}>{campground.feeInfo}</TagChip>
        )}

        {/* Actions */}
        <div className="flex flex-wrap gap-3">
          <SculptedButton
            accent="aqua"
            size="sm"
            onClick={() => window.open(
              `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`,
              "_blank",
              "noopener,noreferrer",
            )}
          >
            <Navigation className="h-4 w-4" />
            Open in Maps
          </SculptedButton>
          {blmSite?.website && (
            <SculptedButton
              accent="citrine"
              size="sm"
              onClick={() => window.open(blmSite.website!, "_blank", "noopener,noreferrer")}
            >
              <ExternalLink className="h-4 w-4" />
              Official Site
            </SculptedButton>
          )}
        </div>
      </div>
    </ScreenScaffold>
  );
}
