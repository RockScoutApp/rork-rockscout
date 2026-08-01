import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, MapPin, ExternalLink, Tent, DollarSign } from "lucide-react";
import { Button } from "@/components/ui/button";
import { stateParks, findLocationByMarkerId } from "@/data/locations";
import { CAMPING_HIKING_GEAR_IDS } from "@/data/gear";
import AffiliateGearBox from "@/components/AffiliateGearBox";
import NotFound from "@/pages/NotFound";

export default function StateParkDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const park = id ? stateParks.find((s) => s.id === id) : undefined;

  if (!park) return <NotFound />;

  return (
    <div className="space-y-6">
      <Button variant="ghost" size="sm" onClick={() => navigate("/app/state-parks")} className="gap-2">
        <ArrowLeft className="h-4 w-4" />
        Back to State Parks
      </Button>

      <div className="rounded-xl border border-border bg-card p-5">
        <h1 className="font-display text-2xl font-bold text-foreground">{park.name}</h1>
        <p className="mt-1 flex items-center gap-1.5 text-sm text-muted-foreground">
          <MapPin className="h-4 w-4" />
          {park.region}, {park.state}
        </p>
        <p className="mt-3 text-sm leading-relaxed text-muted-foreground">{park.description}</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-lg border border-border bg-card p-4">
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            <DollarSign className="h-4 w-4 text-primary" />
            Entry Fee
          </div>
          <p className="mt-1.5 text-sm text-muted-foreground">{park.feeInfo}</p>
        </div>
        <div className="rounded-lg border border-border bg-card p-4">
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            <Tent className="h-4 w-4 text-primary" />
            Camping
          </div>
          <p className="mt-1.5 text-sm text-muted-foreground">
            {park.hasCamping ? "Camping available" : "No camping"}
          </p>
        </div>
      </div>

      {park.website && (
        <a href={park.website} target="_blank" rel="noopener noreferrer">
          <Button variant="outline" className="gap-2">
            <ExternalLink className="h-4 w-4" />
            Visit Park Website
          </Button>
        </a>
      )}

      <Button variant="outline" onClick={() => navigate("/app/map")} className="gap-2">
        <MapPin className="h-4 w-4" />
        View on Map
      </Button>

      <AffiliateGearBox
        title="Camping & Hiking Gear"
        itemIds={CAMPING_HIKING_GEAR_IDS}
        accent="#6B9E7E"
      />
    </div>
  );
}
