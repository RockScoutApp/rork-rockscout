import { useParams, useNavigate } from "react-router-dom";
import {
  Calendar,
  MapPin,
  ExternalLink,
  Phone,
  Mail,
} from "lucide-react";
import { gemShows } from "@/data/locations";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";
import NotFound from "@/pages/NotFound";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

export default function GemShowDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const show = id ? gemShows.find((s) => s.id === id) : undefined;

  if (!show) return <NotFound />;

  return (
    <ScreenScaffold title={show.name} onBack={() => navigate("/app/gem-shows")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Header card */}
        <SculptedCard accent="citrine" glowing className="p-5">
          <div className="flex items-start gap-3">
            <div
              className="icon-badge glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, ["--glow-color" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Calendar className="h-6 w-6" />
            </div>
            <div className="min-w-0 flex-1">
              <p className="mt-1 flex items-center gap-1.5 text-sm" style={{ color: `hsl(${AQUA_HEX})` }}>
                <MapPin className="h-3.5 w-3.5" />
                {show.venue}, {show.city}, {show.state}
              </p>
            </div>
          </div>

          <div className="mt-4 flex flex-wrap gap-2">
            <TagChip accent={`hsl(${CITRINE_HEX})`}>{show.monthLabel}</TagChip>
            <TagChip accent="hsl(36 80% 58%)">{show.dateRange}</TagChip>
            {show.isAnnual && (
              <TagChip accent={`hsl(${AQUA_HEX})`}>Annual Event</TagChip>
            )}
          </div>
        </SculptedCard>

        {/* About */}
        <SculptedCard accent="aqua" className="p-4">
          <h3 className="text-sm font-bold text-foreground">About</h3>
          <p className="mt-2 text-sm leading-relaxed text-[hsl(var(--text-mid))]">
            {show.description}
          </p>
        </SculptedCard>

        {/* Info grid */}
        <div className="grid gap-4 sm:grid-cols-2">
          <SculptedCard accent="citrine" className="p-4">
            <h3 className="text-sm font-bold text-foreground">Entry Fee</h3>
            <p className="mt-1.5 text-sm text-[hsl(var(--text-mid))]">{show.entryFee}</p>
          </SculptedCard>
          <SculptedCard accent="aqua" className="p-4">
            <h3 className="text-sm font-bold text-foreground">Location</h3>
            <p className="mt-1.5 text-sm text-[hsl(var(--text-mid))]">{show.venue}</p>
            <p className="text-sm text-[hsl(var(--text-mid))]">{show.city}, {show.state}</p>
          </SculptedCard>
        </div>

        {/* Action buttons */}
        <div className="flex flex-wrap gap-3">
          <a href={show.website} target="_blank" rel="noopener noreferrer">
            <SculptedButton accent="citrine" glowing>
              <ExternalLink className="h-4 w-4" />
              Visit Website
            </SculptedButton>
          </a>
          {show.phone && (
            <a href={`tel:${show.phone}`}>
              <SculptedButton accent="aqua">
                <Phone className="h-4 w-4" />
                {show.phone}
              </SculptedButton>
            </a>
          )}
          {show.email && (
            <a href={`mailto:${show.email}`}>
              <SculptedButton accent="aqua">
                <Mail className="h-4 w-4" />
                Email
              </SculptedButton>
            </a>
          )}
        </div>
      </div>
    </ScreenScaffold>
  );
}
