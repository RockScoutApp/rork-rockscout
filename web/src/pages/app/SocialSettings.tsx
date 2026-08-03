import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Users,
  MapPin,
  UserPlus,
  Navigation,
  Shield,
  Eye,
  EyeOff,
  Loader2,
} from "lucide-react";
import { Switch } from "@/components/ui/switch";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";
const AMETHYST_HEX = "265 47% 67%";

interface ProfileSettings {
  club_enabled: boolean;
  scan_radius_miles: number;
  coarse_lat: number | null;
  coarse_lng: number | null;
  status: string;
}

export default function SocialSettings() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [scanRadius, setScanRadius] = useState(25);

  const { data: settings } = useQuery<ProfileSettings>({
    queryKey: ["social-settings", user?.id],
    queryFn: async () => {
      if (!user) return null;
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("club_enabled, scan_radius_miles, coarse_lat, coarse_lng, status")
        .eq("id", user.id)
        .maybeSingle();
      if (data) setScanRadius((data as ProfileSettings).scan_radius_miles ?? 25);
      return (data as ProfileSettings) ?? null;
    },
    enabled: !!user,
  });

  const updateSettings = useMutation({
    mutationFn: async (updates: Partial<ProfileSettings>) => {
      if (!user) throw new Error("Sign in first");
      const { error } = await supabase
        .from("rockscout_profiles")
        .update(updates)
        .eq("id", user.id);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-settings", user?.id] });
      toast.success("Settings updated");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  const toggleClub = (enabled: boolean) => {
    updateSettings.mutate({ club_enabled: enabled });
  };

  const updateRadius = (radius: number) => {
    setScanRadius(radius);
    updateSettings.mutate({ scan_radius_miles: radius });
  };

  const shareLocation = async () => {
    if (!user) return;
    navigator.geolocation.getCurrentPosition(
      async (pos) => {
        const coarseLat = Math.round(pos.coords.latitude * 10) / 10;
        const coarseLng = Math.round(pos.coords.longitude * 10) / 10;
        await updateSettings.mutateAsync({
          coarse_lat: coarseLat,
          coarse_lng: coarseLng,
        });
        toast.success("Approximate location shared");
      },
      () => toast.error("Could not get your location"),
      { timeout: 10_000 },
    );
  };

  const hideLocation = () => {
    updateSettings.mutate({ coarse_lat: null, coarse_lng: null });
  };

  if (!user) {
    return (
      <ScreenScaffold title="Social Settings" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Users className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to manage social settings</p>
        </div>
      </ScreenScaffold>
    );
  }

  const isSharingLocation = settings?.coarse_lat != null;

  return (
    <ScreenScaffold title="Social Settings" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Manage your connections, visibility, and location sharing
        </p>

        {/* RockScout Friends toggle */}
        <SculptedCard accent="aqua" className="p-5">
          <div className="flex items-start gap-3">
            <div
              className="icon-badge glowing-border flex h-11 w-11 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: AQUA_HEX, ["--glow-color" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
            >
              <Users className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <h3 className="font-display text-sm font-bold text-foreground">
                  RockScout Friends
                </h3>
                <Switch
                  checked={settings?.club_enabled ?? false}
                  onCheckedChange={toggleClub}
                  disabled={updateSettings.isPending}
                />
              </div>
              <p className="mt-1 text-xs text-muted-foreground">
                Enable to connect with other rockhounds, send friend requests, and appear in discovery.
                When disabled, you're invisible to other users.
              </p>
            </div>
          </div>
        </SculptedCard>

        {/* Scan radius */}
        <SculptedCard accent="citrine" className="p-5">
          <div className="flex items-start gap-3">
            <div
              className="icon-badge glowing-border flex h-11 w-11 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, ["--glow-color" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Navigation className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Scan Radius: {scanRadius} miles
              </h3>
              <p className="mt-1 text-xs text-muted-foreground">
                How far from your location to search for nearby hunters.
              </p>
              {/* Slider */}
              <div className="mt-3">
                <input
                  type="range"
                  min={5}
                  max={100}
                  step={5}
                  value={scanRadius}
                  onChange={(e) => setScanRadius(Number(e.target.value))}
                  onMouseUp={() => updateRadius(scanRadius)}
                  onTouchEnd={() => updateRadius(scanRadius)}
                  className="w-full accent-primary"
                />
                <div className="flex justify-between text-[10px] text-muted-foreground">
                  <span>5 mi</span>
                  <span>50 mi</span>
                  <span>100 mi</span>
                </div>
              </div>
            </div>
          </div>
        </SculptedCard>

        {/* Location sharing */}
        <SculptedCard accent="cyan" className="p-5">
          <div className="flex items-start gap-3">
            <div
              className="icon-badge glowing-border flex h-11 w-11 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CYAN_HEX, ["--glow-color" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}
            >
              <MapPin className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Location Sharing
              </h3>
              <p className="mt-1 text-xs text-muted-foreground">
                {isSharingLocation
                  ? "Your approximate location (~7 mile radius) is visible to other hunters."
                  : "Your location is not shared with other users."}
              </p>
              <div className="mt-3 flex gap-2">
                {isSharingLocation ? (
                  <SculptedButton
                    accent="danger"
                    size="sm"
                    onClick={hideLocation}
                    disabled={updateSettings.isPending}
                  >
                    <EyeOff className="h-3.5 w-3.5" />
                    Hide Location
                  </SculptedButton>
                ) : (
                  <SculptedButton
                    accent="cyan"
                    size="sm"
                    glowing
                    onClick={shareLocation}
                    disabled={updateSettings.isPending}
                  >
                    <Eye className="h-3.5 w-3.5" />
                    Share Approximate Location
                  </SculptedButton>
                )}
              </div>
            </div>
          </div>
        </SculptedCard>

        {/* Navigation links */}
        <div className="space-y-3">
          <h2 className="font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
            Quick Actions
          </h2>
          <SculptedCard accent="aqua" interactive className="overflow-hidden"
            onClick={() => window.history.back()}>
            <div className="flex items-center gap-3 p-4">
              <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}>
                <Users className="h-5 w-5" />
              </div>
              <div className="flex-1">
                <h3 className="text-sm font-bold text-foreground">Friends</h3>
                <p className="text-xs text-muted-foreground">View and manage connections</p>
              </div>
            </div>
          </SculptedCard>

          <SculptedCard accent="citrine" interactive className="overflow-hidden"
            onClick={() => window.history.back()}>
            <div className="flex items-center gap-3 p-4">
              <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}>
                <UserPlus className="h-5 w-5" />
              </div>
              <div className="flex-1">
                <h3 className="text-sm font-bold text-foreground">Discover Hunters</h3>
                <p className="text-xs text-muted-foreground">Find rockhounds near you</p>
              </div>
            </div>
          </SculptedCard>

          <SculptedCard accent="cyan" interactive className="overflow-hidden"
            onClick={() => window.history.back()}>
            <div className="flex items-center gap-3 p-4">
              <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}>
                <MapPin className="h-5 w-5" />
              </div>
              <div className="flex-1">
                <h3 className="text-sm font-bold text-foreground">RockScouts Map</h3>
                <p className="text-xs text-muted-foreground">See nearby users on a map</p>
              </div>
            </div>
          </SculptedCard>
        </div>

        {/* Privacy notice */}
        <SculptedCard accent="amethyst" className="p-4">
          <div className="flex items-start gap-3">
            <Shield className="h-5 w-5 shrink-0" style={{ color: `hsl(${AMETHYST_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">Privacy</p>
              <p className="mt-1 text-xs text-muted-foreground">
                Pings are private — only you can see your own ping on the map.
                Share your ping location with someone via Messenger, SMS, or any
                app using the Share button. Scan for nearby hunters only
                populates a card list of connected RockScout Friends — it does
                not show anyone on the map. Pings expire after 4 hours.
              </p>
            </div>
          </div>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
