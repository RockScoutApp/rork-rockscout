import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Trash2, Smartphone, Monitor, Tablet, Plus } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { getDeviceFingerprint } from "@/lib/deviceFingerprint";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const AMETHYST_HEX = "265 47% 67%";

interface DeviceRow {
  id: string;
  device_label: string;
  device_fingerprint: string;
  user_agent: string | null;
  installed_at: string;
  last_seen_at: string;
}

const MAX_DEVICES = 2;

export default function ManageDevices() {
  const { user } = useAuth();
  const { isPremium } = useTier();
  const queryClient = useQueryClient();
  const [removingId, setRemovingId] = useState<string | null>(null);

  const { data: devices = [], isLoading } = useQuery<DeviceRow[]>({
    queryKey: ["installed-devices", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_installed_devices")
        .select("*")
        .eq("user_id", user.id)
        .order("installed_at", { ascending: false });
      if (error) throw error;
      return (data as DeviceRow[]) ?? [];
    },
    enabled: !!user && isPremium,
  });

  const removeDevice = useMutation({
    mutationFn: async (deviceId: string) => {
      const { error } = await supabase
        .from("rockscout_installed_devices")
        .delete()
        .eq("id", deviceId);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["installed-devices", user?.id] });
      setRemovingId(null);
    },
  });

  const registerMutation = useMutation({
    mutationFn: async () => {
      if (!user) return;
      const fp = getDeviceFingerprint();
      const label = detectLabel();
      const { error } = await supabase
        .from("rockscout_installed_devices")
        .upsert(
          {
            user_id: user.id,
            device_fingerprint: fp,
            device_label: label,
            user_agent: navigator.userAgent,
            last_seen_at: new Date().toISOString(),
          },
          { onConflict: "user_id,device_fingerprint" },
        );
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["installed-devices", user?.id] });
    },
  });

  if (!isPremium) {
    return (
      <ScreenScaffold title="Manage Devices">
        <div className="flex min-h-[60vh] flex-col items-center justify-center px-4 text-center">
          <p className="text-sm text-muted-foreground">
            Device management is a Premium feature.
          </p>
          <SculptedButton accent="citrine" className="mt-4" onClick={() => window.location.assign("/app/paywall")}>
            Go Premium
          </SculptedButton>
        </div>
      </ScreenScaffold>
    );
  }

  const currentFingerprint = getDeviceFingerprint();
  const remaining = Math.max(0, MAX_DEVICES - devices.length);

  return (
    <ScreenScaffold title="Manage Devices">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Your Premium PWA can be installed on up to {MAX_DEVICES} additional
          devices. You've used {devices.length} of {MAX_DEVICES}.
          {remaining > 0 && ` ${remaining} slot${remaining === 1 ? "" : "s"} remaining.`}
        </p>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
          </div>
        ) : devices.length === 0 ? (
          <SculptedCard accent="aqua" className="flex flex-col items-center gap-4 p-8 text-center">
            <Monitor className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No devices registered yet. Install the PWA on this device to register it.
            </p>
            <SculptedButton accent="citrine" glowing onClick={() => registerMutation.mutate()}
              disabled={registerMutation.isPending}>
              <Plus className="h-4 w-4" />
              {registerMutation.isPending ? "Registering..." : "Register this device"}
            </SculptedButton>
          </SculptedCard>
        ) : (
          <div className="space-y-3">
            {devices.map((device) => {
              const isCurrent = device.device_fingerprint === currentFingerprint;
              const icon = getDeviceIcon(device.device_label);
              return (
                <SculptedCard key={device.id} accent="amethyst" className="p-4">
                  <div className="flex items-center gap-4">
                    <div
                      className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                      style={{ ["--badge-accent" as string]: AMETHYST_HEX, color: `hsl(${AMETHYST_HEX})` }}
                    >
                      {icon}
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium text-foreground">
                          {device.device_label}
                        </span>
                        {isCurrent && (
                          <TagChip accent={`hsl(${CITRINE_HEX})`}>This device</TagChip>
                        )}
                      </div>
                      <p className="text-xs text-muted-foreground">
                        Installed {new Date(device.installed_at).toLocaleDateString()}
                        {" · "}
                        Last seen {new Date(device.last_seen_at).toLocaleDateString()}
                      </p>
                    </div>
                    <button
                      onClick={() => {
                        setRemovingId(device.id);
                        removeDevice.mutate(device.id);
                      }}
                      disabled={removingId === device.id || removeDevice.isPending}
                      className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:bg-destructive/10 hover:text-destructive disabled:opacity-50"
                      title="Remove device"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </SculptedCard>
              );
            })}
            {remaining > 0 && (
              <SculptedButton
                accent="citrine"
                className="w-full"
                onClick={() => registerMutation.mutate()}
                disabled={registerMutation.isPending}
              >
                <Plus className="h-4 w-4" />
                {registerMutation.isPending ? "Registering..." : "Register this device"}
              </SculptedButton>
            )}
          </div>
        )}

        {removeDevice.isError && (
          <p className="text-sm text-destructive">
            Failed to remove device. Please try again.
          </p>
        )}
      </div>
    </ScreenScaffold>
  );
}

function detectLabel(): string {
  const ua = navigator.userAgent.toLowerCase();
  const width = window.innerWidth;
  const isMobile = /android|iphone|ipod|windows phone/.test(ua);
  const isTablet =
    /ipad|tablet|kindle|silk/.test(ua) ||
    (/android/.test(ua) && !/mobile/.test(ua));
  const browser = /edg/i.test(navigator.userAgent)
    ? "Edge"
    : /chrome/i.test(navigator.userAgent)
      ? "Chrome"
      : /firefox/i.test(navigator.userAgent)
        ? "Firefox"
        : /safari/i.test(navigator.userAgent)
          ? "Safari"
          : "Browser";

  let type: string;
  if (isTablet || (!isMobile && width >= 768 && width < 1024)) type = "Tablet";
  else if (isMobile || width < 768) type = "Phone";
  else type = "PC";

  return `${type} — ${browser}`;
}

function getDeviceIcon(label: string) {
  if (/Phone/i.test(label))
    return <Smartphone className="h-5 w-5" />;
  if (/Tablet/i.test(label))
    return <Tablet className="h-5 w-5" />;
  return <Monitor className="h-5 w-5" />;
}
