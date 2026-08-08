import { useParams, useNavigate } from "react-router-dom";
import { MapPin, Clock, Hammer, Lightbulb, Users } from "lucide-react";
import { findArtifactOrRelicById } from "@/data/artifacts";
import { OptimizedImage } from "@/components/OptimizedImage";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";
import RelicRegionMap from "@/components/app/RelicRegionMap";
import NotFound from "@/pages/NotFound";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

export default function ArtifactDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const artifact = id ? findArtifactOrRelicById(id) : undefined;

  if (!artifact) return <NotFound />;

  const isWarRelic = artifact.domain === "war_relic";
  const cultureLabel = isWarRelic ? "Origin / Side" : "Culture / Tradition";
  const eraLabel = isWarRelic ? "Origin & Era" : "Time Period";

  return (
    <ScreenScaffold title={artifact.name} onBack={() => navigate("/app/artifacts")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Hero card with image */}
        <SculptedCard accent="aqua" glowing className="overflow-hidden">
          <div className="relative aspect-[4/3] w-full overflow-hidden bg-muted/20">
            <OptimizedImage
              src={artifact.imageUrl}
              alt={artifact.name}
              loading="eager"
              className="h-full w-full object-cover"
            />
          </div>
          <div className="p-5">
            <div className="flex items-start gap-3">
              <span className="text-3xl">{artifact.emoji}</span>
              <div className="min-w-0 flex-1">
                <p className="mt-0.5 text-sm text-muted-foreground">
                  {artifact.family} · {artifact.subFamily}
                </p>
              </div>
            </div>
            <p className="mt-3 text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
              {artifact.tagline}
            </p>
          </div>
        </SculptedCard>

        {/* Time period & culture/origin */}
        <div className="grid gap-4 sm:grid-cols-2">
          <SculptedCard accent="citrine" className="p-4">
            <div className="flex items-center gap-2 text-sm font-bold text-foreground">
              <Clock className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
              {eraLabel}
            </div>
            <p className="mt-1.5 text-sm text-[hsl(var(--text-mid))]">
              {artifact.timePeriod}
            </p>
          </SculptedCard>
          <SculptedCard accent="aqua" className="p-4">
            <div className="flex items-center gap-2 text-sm font-bold text-foreground">
              <Users className="h-4 w-4" style={{ color: `hsl(${AQUA_HEX})` }} />
              {cultureLabel}
            </div>
            <p className="mt-1.5 text-sm text-[hsl(var(--text-mid))]">
              {artifact.tribe}
            </p>
          </SculptedCard>
        </div>

        {/* Overview */}
        <SculptedCard accent="aqua" className="p-4">
          <h3 className="text-sm font-bold text-foreground">Overview</h3>
          <p className="mt-2 text-sm leading-relaxed text-[hsl(var(--text-mid))]">
            {artifact.description}
          </p>
        </SculptedCard>

        {/* How it was made */}
        <SculptedCard accent="citrine" className="p-4">
          <div className="flex items-center gap-2 text-sm font-bold text-foreground">
            <Hammer className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
            How It Was Made
          </div>
          <p className="mt-2 text-sm leading-relaxed text-[hsl(var(--text-mid))]">
            {artifact.howMade}
          </p>
        </SculptedCard>

        {/* Where found */}
        <SculptedCard accent="aqua" className="p-4">
          <div className="flex items-center gap-2 text-sm font-bold text-foreground">
            <MapPin className="h-4 w-4" style={{ color: `hsl(${AQUA_HEX})` }} />
            Where Found
          </div>
          <div className="mt-2 flex flex-wrap gap-2">
            {artifact.whereFound.map((region) => (
              <TagChip key={region} accent={`hsl(${AQUA_HEX})`}>{region}</TagChip>
            ))}
          </div>
        </SculptedCard>

        {/* Visual region map — only for war relics */}
        {isWarRelic && (
          <RelicRegionMap
            whereFound={artifact.whereFound}
            accentHex={AQUA_HEX}
          />
        )}

        {/* Fun facts */}
        <SculptedCard accent="citrine" glowing className="p-4">
          <div className="flex items-center gap-2 text-sm font-bold text-foreground">
            <Lightbulb className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
            {isWarRelic ? "Good to Know" : "Fun Facts"}
          </div>
          <ul className="mt-2 space-y-2">
            {artifact.funFacts.map((fact, i) => (
              <li key={i} className="flex gap-2 text-sm leading-relaxed text-[hsl(var(--text-mid))]">
                <span style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                {fact}
              </li>
            ))}
          </ul>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
