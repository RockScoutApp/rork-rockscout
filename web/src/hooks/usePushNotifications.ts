import { useState, useEffect, useCallback } from "react";
import { supabase } from "@/lib/supabase";
import { toast } from "sonner";

const BACKEND_URL = import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL as string;
const APP_KEY = import.meta.env.EXPO_PUBLIC_RORK_APP_KEY as string;
const VAPID_PUBLIC_KEY = import.meta.env.EXPO_PUBLIC_VAPID_PUBLIC_KEY as string;

const PUSH_CATEGORIES = [
  { id: "social", label: "Social — friends, messages, posts" },
  { id: "trade", label: "Trade board — interest on your listings" },
  { id: "weather", label: "Severe weather — NWS alerts for your area" },
  { id: "aurora", label: "Aurora — KP index spikes" },
  { id: "engagement", label: "Engagement — likes and comments" },
] as const;

type PushCategory = (typeof PUSH_CATEGORIES)[number]["id"];

interface PushSubscriptionState {
  supported: boolean;
  permission: NotificationPermission | "unsupported";
  subscribed: boolean;
  loading: boolean;
}

/**
 * Hook for managing Web Push subscriptions via the /push/* Worker endpoints.
 * Handles permission request, subscription, and per-category storage.
 */
export function usePushNotifications() {
  const [state, setState] = useState<PushSubscriptionState>({
    supported: false,
    permission: "default",
    subscribed: false,
    loading: false,
  });
  const [selectedCategories, setSelectedCategories] = useState<Set<PushCategory>>(
    new Set(["social", "trade", "weather"]),
  );

  useEffect(() => {
    if (!("Notification" in window) || !("serviceWorker" in navigator) || !("PushManager" in window)) {
      setState((s) => ({ ...s, supported: false, permission: "unsupported" }));
      return;
    }
    setState((s) => ({
      ...s,
      supported: true,
      permission: Notification.permission as NotificationPermission,
    }));

    // Check if already subscribed.
    navigator.serviceWorker.ready
      .then((reg) => reg.pushManager.getSubscription())
      .then((sub) => {
        if (sub) setState((s) => ({ ...s, subscribed: true }));
      })
      .catch(() => {
        // ignore — non-fatal
      });
  }, []);

  const subscribe = useCallback(async (): Promise<boolean> => {
    if (!state.supported) {
      toast.error("Push notifications aren't supported on this browser.");
      return false;
    }
    if (!VAPID_PUBLIC_KEY) {
      toast.error("Push notifications aren't configured yet.");
      return false;
    }

    setState((s) => ({ ...s, loading: true }));
    try {
      // Request permission first.
      const permission = await Notification.requestPermission();
      if (permission !== "granted") {
        setState((s) => ({ ...s, permission: "denied", loading: false }));
        toast.error("Notification permission was denied. Enable it in your browser settings.");
        return false;
      }

      const reg = await navigator.serviceWorker.ready;
      const sub = await reg.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array(VAPID_PUBLIC_KEY).buffer as ArrayBuffer,
      });

      // Get the Supabase session token.
      const { data } = await supabase.auth.getSession();
      const token = data.session?.access_token;

      const resp = await fetch(`${BACKEND_URL}/push/subscribe`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-App-Key": APP_KEY,
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({
          endpoint: sub.endpoint,
          keys: {
            p256dh: arrayBufferToBase64Url(sub.getKey("p256dh")),
            auth: arrayBufferToBase64Url(sub.getKey("auth")),
          },
          categories: Array.from(selectedCategories),
          platform: detectPlatform(),
        }),
      });

      if (!resp.ok) {
        const body = (await resp.json().catch(() => ({}))) as { error?: string };
        throw new Error(body.error || "Subscription failed");
      }

      setState((s) => ({
        ...s,
        subscribed: true,
        permission: "granted",
        loading: false,
      }));
      toast.success("Push notifications enabled!");
      return true;
    } catch (err) {
      setState((s) => ({ ...s, loading: false }));
      toast.error(err instanceof Error ? err.message : "Could not enable push");
      return false;
    }
  }, [state.supported, selectedCategories]);

  const unsubscribe = useCallback(async (): Promise<void> => {
    setState((s) => ({ ...s, loading: true }));
    try {
      const reg = await navigator.serviceWorker.ready;
      const sub = await reg.pushManager.getSubscription();
      if (sub) {
        const { data } = await supabase.auth.getSession();
        const token = data.session?.access_token;
        await fetch(`${BACKEND_URL}/push/unsubscribe`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "X-App-Key": APP_KEY,
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
          },
          body: JSON.stringify({ endpoint: sub.endpoint }),
        });
        await sub.unsubscribe();
      }
      setState((s) => ({ ...s, subscribed: false, loading: false }));
      toast.success("Push notifications disabled");
    } catch {
      setState((s) => ({ ...s, loading: false }));
      toast.error("Could not disable push");
    }
  }, []);

  const toggleCategory = useCallback((id: PushCategory) => {
    setSelectedCategories((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }, []);

  return {
    ...state,
    categories: PUSH_CATEGORIES,
    selectedCategories,
    subscribe,
    unsubscribe,
    toggleCategory,
  };
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

function urlBase64ToUint8Array(base64String: string): Uint8Array {
  const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, "+").replace(/_/g, "/");
  const raw = atob(base64);
  const output = new Uint8Array(raw.length);
  for (let i = 0; i < raw.length; i++) {
    output[i] = raw.charCodeAt(i);
  }
  return output;
}

function arrayBufferToBase64Url(buf: ArrayBuffer | null): string {
  if (!buf) return "";
  const bytes = new Uint8Array(buf);
  let binary = "";
  for (const b of bytes) binary += String.fromCharCode(b);
  return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

function detectPlatform(): string {
  const ua = navigator.userAgent;
  if (/iPhone|iPad|iPod/.test(ua)) return "ios-safari";
  if (/Android/.test(ua)) return "android-chrome";
  if (/Mac/.test(ua)) return "macos";
  if (/Windows/.test(ua)) return "windows";
  if (/Linux/.test(ua)) return "linux";
  return "unknown";
}
