import { useRef, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import L from "leaflet";
import { MapPin, Navigation, Star, ArrowLeft } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";

// Fix Leaflet's default icon path
delete (L.Icon.Default.prototype as unknown as { _getIconUrl?: unknown })._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

export default function SharedSpot() {
  const { lat, lng, name } = useParams<{ lat: string; lng: string; name: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const mapRef = useRef<L.Map | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const latitude = lat ? parseFloat(lat) : null;
  const longitude = lng ? parseFloat(lng) : null;
  const spotName = name ? decodeURIComponent(name) : "Shared Spot";

  useEffect(() => {
    if (!containerRef.current || mapRef.current || latitude === null || longitude === null) return;

    const map = L.map(containerRef.current, {
      center: [latitude, longitude],
      zoom: 13,
      zoomControl: true,
    });

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19,
    }).addTo(map);

    // Custom marker with citrine glow
    const icon = L.divIcon({
      className: "shared-spot-marker",
      html: `<div style="font-size: 28px; filter: drop-shadow(0 0 8px hsl(36 80% 58% / 0.6));">📍</div>`,
      iconSize: [32, 32],
      iconAnchor: [16, 28],
    });

    L.marker([latitude, longitude], { icon })
      .addTo(map)
      .bindPopup(`<strong>${spotName}</strong><br/>${latitude.toFixed(4)}, ${longitude.toFixed(4)}`)
      .openPopup();

    mapRef.current = map;

    return () => {
      map.remove();
      mapRef.current = null;
    };
  }, [latitude, longitude, spotName]);

  const saveToFavorites = async () => {
    if (!user || latitude === null || longitude === null) return;
    const { error } = await supabase
      .from("rockscout_favorite_spots")
      .insert({
        user_id: user.id,
        spot_id: `shared-${latitude}-${longitude}`,
        spot_type: "shared_pin",
        name: spotName,
        latitude,
        longitude,
      });
    if (error) {
      if (error.code === "23505") {
        toast.success("Already in your favorite spots!");
      } else {
        toast.error("Could not save spot");
      }
    } else {
      toast.success("Saved to favorite spots!");
    }
    navigate("/app/favorites");
  };

  if (latitude === null || longitude === null) {
    return (
      <ScreenScaffold title="Shared Spot" onBack={() => navigate("/app")}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <MapPin className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Invalid shared spot coordinates</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title={spotName} onBack={() => navigate("/app")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Map */}
        <SculptedCard accent="citrine" glowing className="overflow-hidden">
          <div ref={containerRef} className="h-[40vh] w-full" />
        </SculptedCard>

        {/* Coordinates card */}
        <SculptedCard accent="aqua" className="p-4">
          <div className="flex items-center gap-3">
            <div
              className="icon-badge glowing-border flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: AQUA_HEX, ["--glow-color" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
            >
              <MapPin className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <p className="text-xs font-semibold text-muted-foreground">Coordinates</p>
              <p className="font-display text-sm font-bold text-foreground">
                {latitude.toFixed(4)}, {longitude.toFixed(4)}
              </p>
            </div>
            <a
              href={`https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}#map=15/${latitude}/${longitude}`}
              target="_blank"
              rel="noopener noreferrer"
              className="sculpted-button sculpted-raised dark-card flex h-9 w-9 items-center justify-center rounded-lg"
              style={{ ["--sculpted-accent" as string]: CYAN_HEX }}
            >
              <Navigation className="h-4 w-4" style={{ color: `hsl(${CYAN_HEX})` }} />
            </a>
          </div>
        </SculptedCard>

        {/* Info card */}
        <SculptedCard accent="cyan" className="p-4">
          <p className="text-sm text-[hsl(var(--text-mid))]">
            A fellow RockScout shared this favorite spot with you. Save it to your
            Favorite Spots to visit later, or view it on the main map.
          </p>
        </SculptedCard>

        {/* Action buttons */}
        <div className="flex gap-3">
          {user ? (
            <SculptedButton accent="citrine" glowing className="flex-1" onClick={saveToFavorites}>
              <Star className="h-4 w-4" />
              Save to Favorites
            </SculptedButton>
          ) : (
            <SculptedButton accent="citrine" className="flex-1" onClick={() => navigate("/app/signin")}>
              Sign in to Save
            </SculptedButton>
          )}
          <SculptedButton accent="aqua" className="flex-1" onClick={() => navigate("/app/map")}>
            <MapPin className="h-4 w-4" />
            View on Map
          </SculptedButton>
        </div>
      </div>
    </ScreenScaffold>
  );
}
