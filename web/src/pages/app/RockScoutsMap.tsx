import { useRef, useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import L from "leaflet";
import {
  MapPin,
  Loader2,
  Users,
  Navigation,
  Crosshair,
} from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { SculptedCard, ScreenScaffold, SculptedButton } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const CYAN_HEX = "174 100% 45%";

// Fix Leaflet's default icon path issue with bundlers
delete (L.Icon.Default.prototype as unknown as { _getIconUrl?: unknown })._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

interface HunterPing {
  id: string;
  user_id: string;
  lat: number;
  lng: number;
  label: string;
  expires_at: string;
}

interface HunterProfile {
  id: string;
  display_name: string;
  avatar_emoji: string;
  level: number;
  coarse_lat?: number | null;
  coarse_lng?: number | null;
}

interface SharedPing extends HunterPing {
  profile?: HunterProfile;
}

function createEmojiIcon(emoji: string): L.DivIcon {
  return L.divIcon({
    className: "rockscout-hunter-marker",
    html: `<div style="font-size: 24px; line-height: 1; filter: drop-shadow(0 2px 4px rgba(0,0,0,0.5));">${emoji}</div>`,
    iconSize: [32, 32],
    iconAnchor: [16, 16],
  });
}

export default function RockScoutsMap() {
  const { user } = useAuth();
  const mapRef = useRef<L.Map | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const markersRef = useRef<L.Marker[]>([]);
  const [sharingLocation, setSharingLocation] = useState(false);

  // ── Load active pings ──
  const { data: pings, isLoading } = useQuery<SharedPing[]>({
    queryKey: ["hunter-pings"],
    queryFn: async () => {
      const now = new Date().toISOString();
      const { data, error } = await supabase
        .from("rockscout_pings")
        .select("*")
        .gt("expires_at", now)
        .order("created_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as HunterPing[];
      if (rows.length === 0) return [];

      const userIds = [...new Set(rows.map((r) => r.user_id))];
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level")
        .in("id", userIds);

      const profileMap = new Map((profiles ?? []).map((p) => [p.id, p]));
      return rows.map((r) => ({
        ...r,
        profile: profileMap.get(r.user_id),
      }));
    },
    refetchInterval: 30_000,
  });

  // ── Load nearby hunters with shared location ──
  const { data: nearbyHunters = [] } = useQuery<HunterProfile[]>({
    queryKey: ["nearby-hunters"],
    queryFn: async () => {
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level, coarse_lat, coarse_lng")
        .not("coarse_lat", "is", null)
        .neq("id", user?.id ?? "")
        .limit(50);
      return (data ?? []) as HunterProfile[];
    },
  });

  // ── Initialize map ──
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const map = L.map(containerRef.current, {
      center: [39.5, -98.35], // US center
      zoom: 4,
      zoomControl: true,
      attributionControl: true,
    });

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(map);

    mapRef.current = map;

    return () => {
      map.remove();
      mapRef.current = null;
    };
  }, []);

  // ── Update markers when data changes ──
  useEffect(() => {
    if (!mapRef.current) return;

    // Clear existing markers
    markersRef.current.forEach((m) => m.remove());
    markersRef.current = [];

    // Add ping markers
    (pings ?? []).forEach((ping) => {
      const emoji = ping.profile?.avatar_emoji ?? "🧗";
      const marker = L.marker([ping.lat, ping.lng], { icon: createEmojiIcon(emoji) })
        .bindPopup(
          `<div style="font-family: sans-serif; min-width: 120px;">
            <div style="font-size: 20px; margin-bottom: 4px;">${emoji}</div>
            <strong>${ping.profile?.display_name ?? "Unknown hunter"}</strong><br/>
            <span style="color: #888; font-size: 12px;">Level ${ping.profile?.level ?? 1}</span><br/>
            ${ping.label ? `<span style="font-size: 12px;">${ping.label}</span><br/>` : ""}
            <span style="color: #aaa; font-size: 10px;">Expires ${new Date(ping.expires_at).toLocaleString()}</span>
          </div>`,
        )
        .addTo(mapRef.current);
      markersRef.current.push(marker);
    });

    // Add nearby hunter markers
    nearbyHunters.forEach((hunter) => {
      if (hunter.coarse_lat == null || hunter.coarse_lng == null) return;
      const emoji = hunter.avatar_emoji ?? "⛏️";
      const marker = L.marker([hunter.coarse_lat, hunter.coarse_lng], {
        icon: createEmojiIcon(emoji),
        opacity: 0.7,
      })
        .bindPopup(
          `<div style="font-family: sans-serif; min-width: 120px;">
            <div style="font-size: 20px; margin-bottom: 4px;">${emoji}</div>
            <strong>${hunter.display_name ?? "Unknown hunter"}</strong><br/>
            <span style="color: #888; font-size: 12px;">Level ${hunter.level ?? 1}</span><br/>
            <span style="color: #aaa; font-size: 10px;">Approximate location</span>
          </div>`,
        )
        .addTo(mapRef.current);
      markersRef.current.push(marker);
    });
  }, [pings, nearbyHunters]);

  // ── Drop a ping at user's location ──
  const dropPing = async () => {
    if (!user) return;
    setSharingLocation(true);
    try {
      const pos = await new Promise<GeolocationPosition>((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject, {
          timeout: 10_000,
          enableHighAccuracy: false,
        });
      });

      const { error } = await supabase.from("rockscout_pings").insert({
        user_id: user.id,
        lat: pos.coords.latitude,
        lng: pos.coords.longitude,
        label: "Out hunting!",
        expires_at: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(), // 4 hours
      });

      if (error) throw error;

      // Also update coarse location on profile
      await supabase
        .from("rockscout_profiles")
        .update({
          coarse_lat: Math.round(pos.coords.latitude * 10) / 10,
          coarse_lng: Math.round(pos.coords.longitude * 10) / 10,
          coarse_updated_at: new Date().toISOString(),
        })
        .eq("id", user.id);

      // Center map on user
      if (mapRef.current) {
        mapRef.current.setView([pos.coords.latitude, pos.coords.longitude], 10);
      }

      // Add user marker
      const { data: profile } = await supabase
        .from("rockscout_profiles")
        .select("avatar_emoji, display_name, level")
        .eq("id", user.id)
        .maybeSingle();

      if (mapRef.current) {
        const marker = L.marker([pos.coords.latitude, pos.coords.longitude], {
          icon: createEmojiIcon(profile?.avatar_emoji ?? "⛏️"),
        })
          .bindPopup(
            `<div style="font-family: sans-serif;">
              <div style="font-size: 20px;">${profile?.avatar_emoji ?? "⛏️"}</div>
              <strong>You</strong><br/>
              <span style="color: #888; font-size: 12px;">Out hunting!</span>
            </div>`,
          )
          .addTo(mapRef.current);
        markersRef.current.push(marker);
      }

      // Refresh pings
      setTimeout(() => {
        // Will refetch via React Query interval
      }, 1000);

      toast_success("Ping dropped! Other hunters can see you for 4 hours.");
    } catch {
      toast_error("Could not get your location. Check browser permissions.");
    } finally {
      setSharingLocation(false);
    }
  };

  // ── Center map on user's current position ──
  const centerOnMe = async () => {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        if (mapRef.current) {
          mapRef.current.setView([pos.coords.latitude, pos.coords.longitude], 11);
        }
      },
      () => toast_error("Could not get your location"),
      { timeout: 10_000 },
    );
  };

  if (!user) {
    return (
      <ScreenScaffold title="RockScouts Map" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <MapPin className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view the RockScouts map</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="RockScouts Map" onBack={() => window.history.back()}>
      <div className="space-y-4 px-4 pb-8">
        {/* Map container */}
        <div className="relative overflow-hidden rounded-xl border border-border">
          <div ref={containerRef} className="h-[50vh] w-full md:h-[60vh]" />
          {isLoading && (
            <div className="absolute inset-0 flex items-center justify-center bg-background/60 backdrop-blur-sm">
              <Loader2 className="h-6 w-6 animate-spin text-primary" />
            </div>
          )}
        </div>

        {/* Action buttons */}
        <div className="flex gap-3">
          <SculptedButton
            accent="citrine"
            glowing
            className="flex-1"
            disabled={sharingLocation}
            onClick={dropPing}
          >
            <Navigation className="h-4 w-4" />
            {sharingLocation ? "Dropping Ping…" : "Drop a Ping"}
          </SculptedButton>
          <SculptedButton
            accent="aqua"
            className="flex-1"
            onClick={centerOnMe}
          >
            <Crosshair className="h-4 w-4" />
            Center on Me
          </SculptedButton>
        </div>

        {/* Info cards */}
        <SculptedCard accent="cyan" className="p-4">
          <div className="flex items-start gap-3">
            <Users className="h-5 w-5 shrink-0" style={{ color: `hsl(${CYAN_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">
                Active Pings: {(pings ?? []).length}
              </p>
              <p className="mt-1 text-xs text-muted-foreground">
                Pings show exact locations for 4 hours, then auto-expire.
                Your approximate location (rounded to 0.1°) is visible to other
                hunters when you drop a ping.
              </p>
            </div>
          </div>
        </SculptedCard>

        <SculptedCard accent="aqua" className="p-4">
          <div className="flex items-start gap-3">
            <MapPin className="h-5 w-5 shrink-0" style={{ color: `hsl(${AQUA_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">Privacy</p>
              <p className="mt-1 text-xs text-muted-foreground">
                Your exact position is never stored — only an approximate area
                (rounded to ~7 miles). Pings expire after 4 hours automatically.
                Disable location sharing anytime in Social Settings.
              </p>
            </div>
          </div>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}

// Inline toast helpers (to avoid importing sonner at the bottom)
function toast_success(msg: string) {
  import("sonner").then(({ toast }) => toast.success(msg));
}
function toast_error(msg: string) {
  import("sonner").then(({ toast }) => toast.error(msg));
}
