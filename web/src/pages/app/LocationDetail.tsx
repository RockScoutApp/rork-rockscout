import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ArrowLeft,
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
import { Button } from "@/components/ui/button";
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

type AnyLocation = DigSite | BlmDigSite | Trailhead | Campground | StatePark;

export default function LocationDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [isFavorited, setIsFavorited] = useState(false);

  const location = id ? findLocationByMarkerId(id) : undefined;

  // Check if this spot is favorited
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
      <div className="space-y-4">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate("/app/map")}
          className="gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to map
        </Button>
        <div className="flex items-center gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
          <AlertCircle className="h-4 w-4" />
          Location not found.
        </div>
      </div>
    );
  }

  const meta = getTypeMeta((location as { type: string }).type);
  const label = getTypeLabel((location as { type: string }).type);

  const openInMaps = () => {
    const url = `https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}`;
    window.open(url, "_blank", "noopener,noreferrer");
  };

  // Type-specific fields
  const digSite = location as DigSite;
  const blmSite = location as BlmDigSite;
  const park = location as StatePark;
  const campground = location as Campground;

  const hasDigSiteFields = "summary" in location;
  const hasBlmFields = "whatToFind" in location;
  const hasParkFields = "description" in location && "hasCamping" in location;

  return (
    <div className="space-y-5">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app/map")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to map
      </Button>

      {/* Header card */}
      <div className="space-y-3 rounded-xl border border-border bg-card p-5">
        <div className="flex items-start justify-between gap-3">
          <div className="flex items-start gap-3">
            <div
              className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl"
              style={{ backgroundColor: `${meta.color}20` }}
            >
              {meta.emoji}
            </div>
            <div>
              <h1 className="font-display text-xl font-bold text-foreground md:text-2xl">
                {location.name}
              </h1>
              <p className="mt-0.5 flex items-center gap-1 text-sm text-muted-foreground">
                <MapPin className="h-3.5 w-3.5" />
                {location.region}
              </p>
              <span
                className="mt-1 inline-block rounded-full px-2 py-0.5 text-xs font-medium"
                style={{ backgroundColor: `${meta.color}20`, color: meta.color }}
              >
                {label}
              </span>
            </div>
          </div>
          {user && (
            <Button
              variant={isFavorited ? "default" : "outline"}
              size="sm"
              onClick={() => toggleFavorite.mutate()}
              disabled={toggleFavorite.isPending}
              className="gap-2"
            >
              {toggleFavorite.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Heart
                  className={isFavorited ? "h-4 w-4 fill-current" : "h-4 w-4"}
                />
              )}
              {isFavorited ? "Saved" : "Save"}
            </Button>
          )}
        </div>

        <div className="flex flex-wrap gap-2">
          <Button size="sm" onClick={openInMaps} className="gap-2">
            <Navigation className="h-4 w-4" />
            Get directions
          </Button>
          {"website" in location && (location as { website: string | null }).website && (
            <Button
              size="sm"
              variant="outline"
              onClick={() =>
                window.open(
                  (location as { website: string }).website,
                  "_blank",
                  "noopener,noreferrer",
                )
              }
              className="gap-2"
            >
              <ExternalLink className="h-4 w-4" />
              Website
            </Button>
          )}
        </div>
      </div>

      {/* GPS coordinates */}
      <div className="rounded-xl border border-border bg-card p-4">
        <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
          GPS Coordinates
        </h3>
        <div className="flex items-center gap-2 font-mono text-sm text-muted-foreground">
          <span>{location.latitude.toFixed(4)},</span>
          <span>{location.longitude.toFixed(4)}</span>
          <button
            onClick={() => {
              navigator.clipboard?.writeText(
                `${location.latitude}, ${location.longitude}`,
              );
              toast.success("Coordinates copied");
            }}
            className="text-xs text-primary hover:underline"
          >
            Copy
          </button>
        </div>
      </div>

      {/* Dig site details */}
      {hasDigSiteFields && digSite.summary && (
        <div className="rounded-xl border border-border bg-card p-4">
          <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
            About this site
          </h3>
          <p className="text-sm leading-relaxed text-muted-foreground">
            {digSite.summary}
          </p>
        </div>
      )}

      {/* BLM site details */}
      {hasBlmFields && blmSite.whatToFind && (
        <div className="rounded-xl border border-border bg-card p-4">
          <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
            What to find
          </h3>
          <p className="text-sm leading-relaxed text-muted-foreground">
            {blmSite.whatToFind}
          </p>
        </div>
      )}

      {/* State park description */}
      {hasParkFields && park.description && (
        <div className="rounded-xl border border-border bg-card p-4">
          <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
            About this park
          </h3>
          <p className="text-sm leading-relaxed text-muted-foreground">
            {park.description}
          </p>
        </div>
      )}

      {/* Campground description */}
      {campground.description && !hasParkFields && (
        <div className="rounded-xl border border-border bg-card p-4">
          <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
            About this campground
          </h3>
          <p className="text-sm leading-relaxed text-muted-foreground">
            {campground.description}
          </p>
        </div>
      )}

      {/* Info grid */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {hasDigSiteFields && digSite.mineralTags && digSite.mineralTags.length > 0 && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
              Minerals to find
            </h3>
            <div className="flex flex-wrap gap-1.5">
              {digSite.mineralTags.map((tag) => (
                <span
                  key={tag}
                  className="rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-medium text-primary"
                >
                  {tag}
                </span>
              ))}
            </div>
          </div>
        )}

        {("feeInfo" in location) && (location as { feeInfo: string }).feeInfo && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-semibold text-foreground">
              <DollarSign className="h-4 w-4 text-muted-foreground" />
              Fees
            </h3>
            <p className="text-sm text-muted-foreground">
              {(location as { feeInfo: string }).feeInfo}
            </p>
          </div>
        )}

        {hasDigSiteFields && digSite.hours && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-semibold text-foreground">
              <Clock className="h-4 w-4 text-muted-foreground" />
              Hours
            </h3>
            <p className="text-sm text-muted-foreground">{digSite.hours}</p>
          </div>
        )}

        {hasDigSiteFields && digSite.difficulty && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-semibold text-foreground">
              <Mountain className="h-4 w-4 text-muted-foreground" />
              Difficulty
            </h3>
            <p className="text-sm text-muted-foreground">{digSite.difficulty}</p>
          </div>
        )}

        {("phone" in location) && (location as { phone: string | null }).phone && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-1.5 flex items-center gap-1.5 font-display text-sm font-semibold text-foreground">
              <Phone className="h-4 w-4 text-muted-foreground" />
              Phone
            </h3>
            <a
              href={`tel:${(location as { phone: string }).phone}`}
              className="text-sm text-primary hover:underline"
            >
              {(location as { phone: string }).phone}
            </a>
          </div>
        )}

        {hasBlmFields && blmSite.directions && (
          <div className="rounded-xl border border-border bg-card p-4 sm:col-span-2">
            <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
              Directions
            </h3>
            <p className="text-sm leading-relaxed text-muted-foreground">
              {blmSite.directions}
            </p>
          </div>
        )}

        {hasBlmFields && blmSite.facilities && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-1.5 font-display text-sm font-semibold text-foreground">
              Facilities
            </h3>
            <p className="text-sm text-muted-foreground">{blmSite.facilities}</p>
          </div>
        )}

        {hasParkFields && park.hasCamping !== undefined && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-1.5 font-display text-sm font-semibold text-foreground">
              Camping
            </h3>
            <p className="text-sm text-muted-foreground">
              {park.hasCamping ? "Camping available" : "No camping"}
            </p>
          </div>
        )}
      </div>

      {/* Tips */}
      {hasDigSiteFields && digSite.tips && (
        <div className="rounded-xl border border-primary/20 bg-primary/5 p-4">
          <h3 className="mb-2 font-display text-sm font-semibold text-primary">
            Pro tips
          </h3>
          <p className="text-sm leading-relaxed text-foreground/80">
            {digSite.tips}
          </p>
        </div>
      )}
    </div>
  );
}
