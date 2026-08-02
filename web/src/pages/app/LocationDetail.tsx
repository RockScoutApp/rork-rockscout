import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Heart,
  MapPin,
  ExternalLink,
  Phone,
  Clock,
  DollarSign,
  Mountain,
  Navigation,
  Loader2,
  AlertCircle,
} from "lucide-react";
import { toast } from "sonner";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import {
  findLocationByMarkerId,
  getTypeMeta,
  getTypeLabel,
  type DigSite,
  type BlmDigSite,
  type Trailhead,
  type Campground,
  type StatePark,
} from "@/data/locations";
import { CAMPING_GEAR_IDS, HIKING_GEAR_IDS } from "@/data/gear";
import AffiliateGearBox from "@/components/AffiliateGearBox";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

type AnyLocation = DigSite | BlmDigSite | Trailhead | Campground | StatePark;

export default function LocationDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [isFavorited, setIsFavorited] = useState(false);

  const location = id ? findLocationByMarkerId(id) : undefined;

  useQuery({
    queryKey: ["favorite-spot", user?.id, id],
    queryFn: async () => {
      if (!user || !id) return false;
      const { data } = await supabase
        .from("rockscout_favorite_spots")
        .select("id")
        .eq("user_id", user.id)
        .eq("spot_id", id)
        .maybeSingle();
      setIsFavorited(!!data);
      return !!data;
    },
    enabled: !!user && !!id,
  });

  const toggleFavorite = useMutation({
    mutationFn: async () => {
      if (!user || !location) throw new Error("Sign in to save spots");
      if (isFavorited) {
        const { error } = await supabase
          .from("rockscout_favorite_spots")
          .delete()
          .eq("user_id", user.id)
          .eq("spot_id", id!);
        if (error) throw error;
      } else {
        const { error } = await supabase
          .from("rockscout_favorite_spots")
          .insert({
            user_id: user.id,
            spot_id: id!,
            spot_type: "location",
            name: location.name,
            region: location.region,
            latitude: location.latitude,
            longitude: location.longitude,
          });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      setIsFavorited((v) => !v);
      toast.success(isFavorited ? "Removed from favorites" : "Added to favorites");
      queryClient.invalidateQueries({ queryKey: ["favorite-spots"] });
    },
    onError: (err) => {
      toast.error(err instanceof Error ? err.message : "Failed to toggle favorite");
    },
  });

  if (!location) {
    return (
      <ScreenScaffold title="Location Not Found" onBack={() => navigate("/app/map")}>
        <div className="px-4 pb-8">
          <SculptedCard accent="danger" className="flex items-center gap-2 p-4">
            <AlertCircle className="h-4 w-4" style={{ color: "hsl(4 70% 55%)" }} />
            <span className="text-sm" style={{ color: "hsl(4 70% 55%)" }}>Location not found.</span>
          </SculptedCard>
        </div>
      </ScreenScaffold>
    );
  }

  const meta = getTypeMeta((location as { type: string }).type);
  const label = getTypeLabel((location as { type: string }).type);

  const openInMaps = () => {
    const url = `https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}`;
    window.open(url, "_blank", "noopener,noreferrer");
  };

  const digSite = location as DigSite;
  const blmSite = location as BlmDigSite;
  const park = location as StatePark;
  const campground = location as Campground;

  const hasDigSiteFields = "summary" in location;
  const hasBlmFields = "whatToFind" in location;
  const hasParkFields = "description" in location && "hasCamping" in location;

  return (
    <ScreenScaffold title={location.name} onBack={() => navigate("/app/map")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Header card */}
        <SculptedCard accent="aqua" glowing className="space-y-3 p-5">
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-start gap-3">
              <div
                className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl"
                style={{ ["--glow-color" as string]: meta.color }}
              >
                {meta.emoji}
              </div>
              <div>
                <p className="mt-0.5 flex items-center gap-1 text-sm" style={{ color: `hsl(${CITRINE_HEX})` }}>
                  <MapPin className="h-3.5 w-3.5" />
                  {location.region}
                </p>
                <TagChip accent={meta.color}>{label}</TagChip>
              </div>
            </div>
            {user && (
              <SculptedButton
                accent={isFavorited ? "danger" : "aqua"}
                size="sm"
                onClick={() => toggleFavorite.mutate()}
                disabled={toggleFavorite.isPending}
              >
                {toggleFavorite.isPending ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  <Heart className={isFavorited ? "h-4 w-4 fill-current" : "h-4 w-4"} />
                )}
                {isFavorited ? "Saved" : "Save"}
              </SculptedButton>
            )}
          </div>

          <div className="flex flex-wrap gap-2">
            <SculptedButton accent="citrine" size="sm" glowing onClick={openInMaps}>
              <Navigation className="h-4 w-4" />
              Get directions
            </SculptedButton>
            {"website" in location && (location as { website: string | null }).website && (
              <SculptedButton
                accent="aqua"
                size="sm"
                onClick={() => window.open((location as { website: string }).website, "_blank", "noopener,noreferrer")}
              >
                <ExternalLink className="h-4 w-4" />
                Website
              </SculptedButton>
            )}
          </div>
        </SculptedCard>

        {/* GPS coordinates */}
        <SculptedCard accent="cyan" className="p-4">
          <h3 className="mb-2 font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
            GPS Coordinates
          </h3>
          <div className="flex items-center gap-2 font-mono text-sm text-muted-foreground">
            <span>{location.latitude.toFixed(4)},</span>
            <span>{location.longitude.toFixed(4)}</span>
            <button
              onClick={() => {
                navigator.clipboard?.writeText(`${location.latitude}, ${location.longitude}`);
                toast.success("Coordinates copied");
              }}
              className="text-xs hover:underline"
              style={{ color: `hsl(${CITRINE_HEX})` }}
            >
              Copy
            </button>
          </div>
        </SculptedCard>

        {/* Dig site details */}
        {hasDigSiteFields && digSite.summary && (
          <SculptedCard accent="aqua" className="p-4">
            <h3 className="mb-2 font-display text-sm font-bold text-foreground">About this site</h3>
            <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">{digSite.summary}</p>
          </SculptedCard>
        )}

        {/* BLM site details */}
        {hasBlmFields && blmSite.whatToFind && (
          <SculptedCard accent="citrine" className="p-4">
            <h3 className="mb-2 font-display text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>What to find</h3>
            <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">{blmSite.whatToFind}</p>
          </SculptedCard>
        )}

        {/* State park description */}
        {hasParkFields && park.description && (
          <SculptedCard accent="success" className="p-4">
            <h3 className="mb-2 font-display text-sm font-bold" style={{ color: "hsl(147 49% 55%)" }}>About this park</h3>
            <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">{park.description}</p>
          </SculptedCard>
        )}

        {/* Campground description */}
        {campground.description && !hasParkFields && (
          <SculptedCard accent="aqua" className="p-4">
            <h3 className="mb-2 font-display text-sm font-bold text-foreground">About this campground</h3>
            <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">{campground.description}</p>
          </SculptedCard>
        )}

        {/* Info grid */}
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {hasDigSiteFields && digSite.mineralTags && digSite.mineralTags.length > 0 && (
            <SculptedCard accent="citrine" className="p-4">
              <h3 className="mb-2 font-display text-sm font-bold text-foreground">Minerals to find</h3>
              <div className="flex flex-wrap gap-1.5">
                {digSite.mineralTags.map((tag) => (
                  <TagChip key={tag} accent={`hsl(${CITRINE_HEX})`}>{tag}</TagChip>
                ))}
              </div>
            </SculptedCard>
          )}

          {("feeInfo" in location) && (location as { feeInfo: string }).feeInfo && (
            <SculptedCard accent="aqua" className="p-4">
              <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-bold text-foreground">
                <DollarSign className="h-4 w-4" /> Fees
              </h3>
              <p className="text-sm text-[hsl(var(--text-mid))]">{(location as { feeInfo: string }).feeInfo}</p>
            </SculptedCard>
          )}

          {hasDigSiteFields && digSite.hours && (
            <SculptedCard accent="aqua" className="p-4">
              <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-bold text-foreground">
                <Clock className="h-4 w-4" /> Hours
              </h3>
              <p className="text-sm text-[hsl(var(--text-mid))]">{digSite.hours}</p>
            </SculptedCard>
          )}

          {hasDigSiteFields && digSite.difficulty && (
            <SculptedCard accent="aqua" className="p-4">
              <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-bold text-foreground">
                <Mountain className="h-4 w-4" /> Difficulty
              </h3>
              <p className="text-sm text-[hsl(var(--text-mid))]">{digSite.difficulty}</p>
            </SculptedCard>
          )}

          {("phone" in location) && (location as { phone: string | null }).phone && (
            <SculptedCard accent="aqua" className="p-4">
              <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-bold text-foreground">
                <Phone className="h-4 w-4" /> Phone
              </h3>
              <a href={`tel:${(location as { phone: string }).phone}`} className="text-sm hover:underline" style={{ color: `hsl(${CITRINE_HEX})` }}>
                {(location as { phone: string }).phone}
              </a>
            </SculptedCard>
          )}

          {hasBlmFields && blmSite.directions && (
            <SculptedCard accent="aqua" className="p-4 sm:col-span-2">
              <h3 className="mb-2 font-display text-sm font-bold text-foreground">Directions</h3>
              <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">{blmSite.directions}</p>
            </SculptedCard>
          )}

          {hasBlmFields && blmSite.facilities && (
            <SculptedCard accent="aqua" className="p-4">
              <h3 className="mb-1.5 font-display text-sm font-bold text-foreground">Facilities</h3>
              <p className="text-sm text-[hsl(var(--text-mid))]">{blmSite.facilities}</p>
            </SculptedCard>
          )}

          {hasParkFields && park.hasCamping !== undefined && (
            <SculptedCard accent="aqua" className="p-4">
              <h3 className="mb-1.5 font-display text-sm font-bold text-foreground">Camping</h3>
              <p className="text-sm text-[hsl(var(--text-mid))]">{park.hasCamping ? "Camping available" : "No camping"}</p>
            </SculptedCard>
          )}
        </div>

        {/* Tips */}
        {hasDigSiteFields && digSite.tips && (
          <SculptedCard accent="citrine" glowing className="p-4">
            <h3 className="mb-2 font-display text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>Pro tips</h3>
            <p className="text-sm leading-relaxed text-foreground/80">{digSite.tips}</p>
          </SculptedCard>
        )}

        {/* Camping gear for campgrounds */}
        {(location as { type: string }).type === "CAMPGROUND" && (
          <AffiliateGearBox title="Camping Gear" itemIds={CAMPING_GEAR_IDS} accent="#E0A040" />
        )}

        {/* Hiking gear for trailheads */}
        {(location as { type: string }).type === "TRAILHEAD" && (
          <AffiliateGearBox title="Hiking Gear" itemIds={HIKING_GEAR_IDS} accent="#6B9E7E" />
        )}
      </div>
    </ScreenScaffold>
  );
}
