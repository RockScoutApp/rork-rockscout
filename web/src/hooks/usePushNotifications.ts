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

  /**
   * The application-server key MUST be the same pair the backend signs pushes
   * with, otherwise every send is rejected with 403 and nothing ever arrives.
   * Ask the backend for its key and only fall back to the build-time env var.
   */
  const resolveServerKey = useCallback(async (): Promise<string> => {
    try {
      const resp = await fetch(`${BACKEND_URL}/push/key`);
      if (resp.ok) {
        const data = (await resp.json()) as { publicKey?: string };
        if (data.publicKey) return data.publicKey;
      }
    } catch {
      // Fall through to the build-time key.
    }
    return VAPID_PUBLIC_KEY;
  }, []);

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
    setState((s) => ({ ...s, loading: true }));
    try {
      const serverKey = await resolveServerKey();
      if (!serverKey) {
        setState((s) => ({ ...s, loading: false }));
        toast.error("Push notifications aren't configured yet.");
        return false;
      }
      // Request permission first.
      const permission = await Notification.requestPermission();
      if (permission !== "granted") {
        setState((s) => ({ ...s, permission: "denied", loading: false }));
        toast.error("Notification permission was denied. Enable it in your browser settings.");
        return false;
      }

      const reg = await navigator.serviceWorker.ready;

      // A subscription created with an older/different key can never receive a
      // push from this backend — drop it and re-subscribe with the live key.
      const existing = await reg.pushManager.getSubscription();
      if (existing) {
        const existingKey = arrayBufferToBase64Url(existing.options.applicationServerKey ?? null);
        if (existingKey !== serverKey) {
          await existing.unsubscribe().catch(() => undefined);
        }
      }

      const sub =
        (await reg.pushManager.getSubscription()) ??
        (await reg.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey: urlBase64ToUint8Array(serverKey).buffer as ArrayBuffer,
        }));

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
  }, [state.supported, selectedCategories, resolveServerKey]);

  /** Sends a real push to this account so delivery can be verified instantly. */
  const sendTest = useCallback(async (): Promise<void> => {
    setState((s) => ({ ...s, loading: true }));
    try {
      const { data } = await supabase.auth.getSession();
      const token = data.session?.access_token;
      if (!token) {
        toast.error("Sign in to send a test notification.");
        return;
      }
      const resp = await fetch(`${BACKEND_URL}/push/test`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-App-Key": APP_KEY,
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({}),
      });
      const result = (await resp.json().catch(() => ({}))) as {
        sent?: number;
        failed?: number;
        error?: string;
      };
      if (!resp.ok) throw new Error(result.error ?? "Test push failed");
      if ((result.sent ?? 0) > 0) {
        toast.success("Test notification sent — check your device.");
      } else {
        toast.error("No device is subscribed yet. Enable push first.");
      }
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Could not send a test push");
    } finally {
      setState((s) => ({ ...s, loading: false }));
    }
  }, []);

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
    sendTest,
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
