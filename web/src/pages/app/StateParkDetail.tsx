import { useParams, useNavigate } from "react-router-dom";
import { MapPin, ExternalLink, Tent, DollarSign, Navigation } from "lucide-react";
import { stateParks } from "@/data/locations";
import { CAMPING_HIKING_GEAR_IDS } from "@/data/gear";
import AffiliateGearBox from "@/components/AffiliateGearBox";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";
import NotFound from "@/pages/NotFound";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const SUCCESS_HEX = "147 49% 55%";

export default function StateParkDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const park = id ? stateParks.find((s) => s.id === id) : undefined;

  if (!park) return <NotFound />;

  return (
    <ScreenScaffold title={park.name} onBack={() => navigate("/app/state-parks")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Hero card */}
        <SculptedCard accent="success" glowing className="p-5">
          <div className="flex items-start gap-4">
            <div
              className="glowing-border flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl text-3xl"
              style={{ ["--glow-color" as string]: SUCCESS_HEX }}
            >
              🏞️
            </div>
            <div className="min-w-0 flex-1">
              <h1 className="font-display text-xl font-bold text-foreground">{park.name}</h1>
              <p className="mt-0.5 flex items-center gap-1.5 text-sm" style={{ color: `hsl(${CITRINE_HEX})` }}>
                <MapPin className="h-4 w-4" />
                {park.region}, {park.state}
              </p>
              <p className="mt-2 text-sm leading-relaxed text-[hsl(var(--text-mid))]">{park.description}</p>
            </div>
          </div>
        </SculptedCard>

        {/* Info cards */}
        <div className="grid gap-4 sm:grid-cols-2">
          <SculptedCard accent="citrine" className="p-4">
            <div className="flex items-center gap-3">
              <div
                className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
              >
                <DollarSign className="h-5 w-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-foreground">Entry Fee</h3>
                <p className="text-sm text-[hsl(var(--text-mid))]">{park.feeInfo}</p>
              </div>
            </div>
          </SculptedCard>

          <SculptedCard accent="aqua" className="p-4">
            <div className="flex items-center gap-3">
              <div
                className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
              >
                <Tent className="h-5 w-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-foreground">Camping</h3>
                <p className="text-sm text-[hsl(var(--text-mid))]">
                  {park.hasCamping ? "Camping available" : "No camping"}
                </p>
              </div>
            </div>
          </SculptedCard>
        </div>

        {/* Action buttons */}
        <div className="flex gap-3">
          {park.website && (
            <a href={park.website} target="_blank" rel="noopener noreferrer" className="flex-1">
              <SculptedButton accent="citrine" glowing className="w-full">
                <ExternalLink className="h-4 w-4" />
                Visit Website
              </SculptedButton>
            </a>
          )}
          <SculptedButton accent="aqua" className="flex-1" onClick={() => navigate("/app/map")}>
            <Navigation className="h-4 w-4" />
            View on Map
          </SculptedButton>
        </div>

        {/* Gear recommendations */}
        <AffiliateGearBox
          title="Camping & Hiking Gear"
          itemIds={CAMPING_HIKING_GEAR_IDS}
          accent="#6B9E7E"
        />
      </div>
    </ScreenScaffold>
  );
}
