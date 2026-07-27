import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, MapPin } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";

export default function SharedSpot() {
  const { lat, lng, name } = useParams<{ lat: string; lng: string; name: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const latitude = lat ? parseFloat(lat) : null;
  const longitude = lng ? parseFloat(lng) : null;

  return (
    <div className="space-y-6">
      <Button variant="ghost" size="sm" onClick={() => navigate("/app")} className="gap-2">
        <ArrowLeft className="h-4 w-4" />
        Back to Home
      </Button>

      <div className="rounded-xl border border-border bg-card p-5">
        <div className="flex items-start gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
            <MapPin className="h-6 w-6 text-primary" />
          </div>
          <div className="min-w-0 flex-1">
            <h1 className="font-display text-2xl font-bold text-foreground">
              {name ? decodeURIComponent(name) : "Shared Spot"}
            </h1>
            {latitude !== null && longitude !== null && (
              <p className="mt-1 text-sm text-muted-foreground">
                {latitude.toFixed(4)}, {longitude.toFixed(4)}
              </p>
            )}
          </div>
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          A fellow RockScout shared this favorite spot with you. Visit the Maps page to
          see it in context, or add it to your Favorite Spots to visit later.
        </p>
      </div>

      {user && latitude !== null && longitude !== null && (
        <Button
          onClick={async () => {
            const { error } = await (await import("@/lib/supabase")).supabase
              .from("rockscout_favorite_spots")
              .insert({
                user_id: user.id,
                spot_id: `shared-${latitude}-${longitude}`,
                spot_type: "shared_pin",
                name: name ? decodeURIComponent(name) : "Shared Spot",
                latitude,
                longitude,
              });
            if (error) {
              // Duplicate — already saved
            }
            navigate("/app/favorites");
          }}
          className="gap-2"
        >
          <MapPin className="h-4 w-4" />
          Save to Favorite Spots
        </Button>
      )}

      <Button variant="outline" onClick={() => navigate("/app/map")} className="gap-2">
        <MapPin className="h-4 w-4" />
        View on Map
      </Button>
    </div>
  );
}
