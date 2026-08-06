import { useRef, useEffect, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import L from "leaflet";
import {
  MapPin,
  Loader2,
  Navigation,
  Crosshair,
  Share2,
  Trash2,
  ShieldCheck,
} from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { SculptedCard, ScreenScaffold, SculptedButton } from "@/components/sculpted";
import { isValidCoordinate, runMapSafe, safeMarker, safeRemoveMap, safeSetView } from "@/lib/mapSafe";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const WARNING_HEX = "45 90% 55%";

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

interface SharedPing extends HunterPing {
  profile?: { display_name: string; avatar_emoji: string; level: number };
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
  const queryClient = useQueryClient();
  const mapRef = useRef<L.Map | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const markersRef = useRef<L.Marker[]>([]);
  const [sharingLocation, setSharingLocation] = useState(false);
  const [removingPing, setRemovingPing] = useState(false);

  // ── Load ONLY my active pings (private) ──
  const { data: myPings, isLoading } = useQuery<SharedPing[]>({
    queryKey: ["my-pings"],
    queryFn: async () => {
      if (!user) return [];
      const now = new Date().toISOString();
      const { data, error } = await supabase
        .from("rockscout_pings")
        .select("*")
        .eq("user_id", user.id)
        .gt("expires_at", now)
        .order("created_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as HunterPing[];
      if (rows.length === 0) return [];

      // Fetch my own profile for the popup
      const { data: profile } = await supabase
        .from("rockscout_profiles")
        .select("display_name, avatar_emoji, level")
        .eq("id", user.id)
        .maybeSingle();

      return rows.map((r) => ({
        ...r,
        profile: profile ?? undefined,
      }));
    },
    enabled: !!user,
    refetchInterval: 30_000,
  });

  // ── Initialize map ──
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const map = L.map(containerRef.current, {
      center: [39.5, -98.35],
      zoom: 4,
      zoomControl: true,
      attributionControl: true,
    });

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(map);

    mapRef.current = map;

    // Resize observer keeps the map correctly sized after navigation or tab changes.
    const resizeObserver = new ResizeObserver(() => {
      runMapSafe("resize", () => map.invalidateSize());
    });
    resizeObserver.observe(containerRef.current);

    return () => {
      resizeObserver.disconnect();
      safeRemoveMap(map);
      mapRef.current = null;
    };
  }, []);

  // ── Update markers when my pings change ──
  useEffect(() => {
    if (!mapRef.current) return;

    markersRef.current.forEach((m) => runMapSafe("remove marker", () => m.remove()));
    markersRef.current = [];

    (myPings ?? []).forEach((ping) => {
      if (!isValidCoordinate(ping.lat, ping.lng)) return;
      const emoji = ping.profile?.avatar_emoji ?? "⛏️";
      const marker = safeMarker(mapRef.current, ping.lat, ping.lng, { icon: createEmojiIcon(emoji) });
      if (!marker) return;
      marker.bindPopup(
        `<div style="font-family: sans-serif; min-width: 120px;">
          <div style="font-size: 20px; margin-bottom: 4px;">${emoji}</div>
          <strong>Your ping</strong><br/>
          ${ping.label ? `<span style="font-size: 12px;">${ping.label}</span><br/>` : ""}
          <span style="color: #aaa; font-size: 10px;">Expires ${new Date(ping.expires_at).toLocaleString()}</span>
        </div>`,
      );
      runMapSafe("add marker", () => marker.addTo(mapRef.current!));
      markersRef.current.push(marker);
    });
  }, [myPings]);

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

      // Remove any existing ping first (only one active at a time)
      await supabase.from("rockscout_pings").delete().eq("user_id", user.id);

      const { error } = await supabase.from("rockscout_pings").insert({
        user_id: user.id,
        lat: pos.coords.latitude,
        lng: pos.coords.longitude,
        label: "I'm here!",
        expires_at: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(),
      });

      if (error) throw error;

      if (mapRef.current) {
        safeSetView(mapRef.current, pos.coords.latitude, pos.coords.longitude, 10);
      }

      queryClient.invalidateQueries({ queryKey: ["my-pings"] });
      toast_success("Ping dropped! It's private — share it with someone via the Share button.");
    } catch {
      toast_error("Could not get your location. Check browser permissions.");
    } finally {
      setSharingLocation(false);
    }
  };

  // ── Remove my ping ──
  const removePing = async () => {
    if (!user) return;
    setRemovingPing(true);
    try {
      await supabase.from("rockscout_pings").delete().eq("user_id", user.id);
      queryClient.invalidateQueries({ queryKey: ["my-pings"] });
      toast_success("Ping removed.");
    } catch {
      toast_error("Could not remove ping. Try again.");
    } finally {
      setRemovingPing(false);
    }
  };

  // ── Share ping location via Web Share API ──
  const sharePing = async () => {
    const ping = (myPings ?? [])[0];
    if (!ping) return;
    const mapsUrl = `https://www.google.com/maps?q=${ping.lat},${ping.lng}`;
    const shareText = `My RockScout ping: ${ping.label}\nMaps: ${mapsUrl}`;
    if (navigator.share) {
      try {
        await navigator.share({
          title: "RockScout ping location",
          text: shareText,
          url: mapsUrl,
        });
      } catch {
        // User cancelled — no action needed
      }
    } else {
      // Fallback: copy to clipboard
      try {
        await navigator.clipboard.writeText(shareText);
        toast_success("Ping location copied to clipboard — paste it into Messenger or any app.");
      } catch {
        toast_error("Could not share. Copy this link: " + mapsUrl);
      }
    }
  };

  // ── Center map on user's current position ──
  const centerOnMe = async () => {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        safeSetView(mapRef.current, pos.coords.latitude, pos.coords.longitude, 11);
      },
      () => toast_error("Could not get your location"),
      { timeout: 10_000 },
    );
  };

  const hasActivePing = (myPings ?? []).length > 0;

  if (!user) {
    return (
      <ScreenScaffold title="RockScouts Map">
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <MapPin className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to use the RockScouts map</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="RockScouts Map">
      <div className="space-y-4 px-4 pb-8">
        {/* Private badge */}
        <div
          className="flex items-center gap-2 rounded-lg border px-3 py-2 text-xs font-semibold"
          style={{
            borderColor: `hsl(${WARNING_HEX} / 0.4)`,
            backgroundColor: `hsl(${WARNING_HEX} / 0.1)`,
            color: `hsl(${WARNING_HEX})`,
          }}
        >
          <ShieldCheck className="h-4 w-4 shrink-0" />
          Your ping is private — only you can see it. Use Share to send it to someone via Messenger.
        </div>

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
            {sharingLocation ? "Dropping Ping…" : hasActivePing ? "Update Ping" : "Drop a Ping"}
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

        {/* Share + Remove (only when ping is active) */}
        {hasActivePing && (
          <div className="flex gap-3">
            <SculptedButton
              accent="citrine"
              className="flex-1"
              onClick={sharePing}
            >
              <Share2 className="h-4 w-4" />
              Share Ping Location
            </SculptedButton>
            <SculptedButton
              accent="aqua"
              className="flex-1"
              disabled={removingPing}
              onClick={removePing}
            >
              <Trash2 className="h-4 w-4" />
              {removingPing ? "Removing…" : "Remove Ping"}
            </SculptedButton>
          </div>
        )}

        {/* Info cards */}
        <SculptedCard accent="cyan" className="p-4">
          <div className="flex items-start gap-3">
            <MapPin className="h-5 w-5 shrink-0" style={{ color: `hsl(${AQUA_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">
                Your Ping: {hasActivePing ? "Live" : "None active"}
              </p>
              <p className="mt-1 text-xs text-muted-foreground">
                Your ping is private — nobody else can see it on their map.
                Use the Share button to send your location to someone via
                Messenger, SMS, or any app. Pings expire after 4 hours.
              </p>
            </div>
          </div>
        </SculptedCard>

        <SculptedCard accent="aqua" className="p-4">
          <div className="flex items-start gap-3">
            <ShieldCheck className="h-5 w-5 shrink-0" style={{ color: `hsl(${AQUA_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">Privacy</p>
              <p className="mt-1 text-xs text-muted-foreground">
                Pings are private by design. Scan for nearby hunters only
                populates a card list of connected RockScout Friends — it does
                not show anyone on this map. The only way someone sees your
                ping is if you share it with them.
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
