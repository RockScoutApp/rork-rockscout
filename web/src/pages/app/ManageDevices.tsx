import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Trash2, Smartphone, Monitor, Tablet } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { getDeviceFingerprint } from "@/lib/deviceFingerprint";

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

  // Register or update this device's last_seen_at
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
      <div className="flex min-h-[60vh] flex-col items-center justify-center text-center">
        <p className="text-muted-foreground">
          Device management is a Premium feature.{" "}
          <a href="/app/paywall" className="text-primary hover:underline">
            Go Premium
          </a>
        </p>
      </div>
    );
  }

  const currentFingerprint = getDeviceFingerprint();
  const remaining = Math.max(0, MAX_DEVICES - devices.length);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-bold">Manage Devices</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Your Premium PWA can be installed on up to {MAX_DEVICES} additional
          devices. You've used {devices.length} of {MAX_DEVICES}.
          {remaining > 0 && ` ${remaining} slot${remaining === 1 ? "" : "s"} remaining.`}
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
        </div>
      ) : devices.length === 0 ? (
        <div className="rounded-xl border border-border bg-card/50 p-8 text-center">
          <p className="text-muted-foreground">
            No devices registered yet. Install the PWA on this device to register it.
          </p>
          <button
            onClick={() => registerMutation.mutate()}
            disabled={registerMutation.isPending}
            className="mt-4 inline-flex items-center gap-2 rounded-full bg-primary px-5 py-2.5 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:opacity-50"
          >
            {registerMutation.isPending ? "Registering..." : "Register this device"}
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {devices.map((device) => {
            const isCurrent = device.device_fingerprint === currentFingerprint;
            const icon = getDeviceIcon(device.device_label);
            return (
              <div
                key={device.id}
                className="flex items-center gap-4 rounded-xl border border-border bg-card/50 p-4"
              >
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                  {icon}
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <span className="font-medium text-foreground">
                      {device.device_label}
                    </span>
                    {isCurrent && (
                      <span className="rounded-full bg-primary/15 px-2 py-0.5 text-[10px] font-semibold text-primary">
                        This device
                      </span>
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
            );
          })}
          {remaining > 0 && (
            <button
              onClick={() => registerMutation.mutate()}
              disabled={registerMutation.isPending}
              className="inline-flex items-center gap-2 rounded-full border border-border px-5 py-2.5 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted/50 hover:text-foreground disabled:opacity-50"
            >
              {registerMutation.isPending ? "Registering..." : "Register this device"}
            </button>
          )}
        </div>
      )}

      {removeDevice.isError && (
        <p className="text-sm text-destructive">
          Failed to remove device. Please try again.
        </p>
      )}
    </div>
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
    return <Smartphone className="h-5 w-5 text-primary" />;
  if (/Tablet/i.test(label))
    return <Tablet className="h-5 w-5 text-primary" />;
  return <Monitor className="h-5 w-5 text-primary" />;
}
