import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Zap,
  Crown,
  Gift,
  Check,
  Loader2,
  Sparkles,
  Heart,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

const BACKEND_URL = import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL as string;
const APP_KEY = import.meta.env.EXPO_PUBLIC_RORK_APP_KEY as string;

interface Profile {
  id: string;
  is_pro: boolean;
  level: number;
  xp: number;
}

const PREMIUM_FEATURES = [
  "Unlimited AI rock identifications",
  "All 3 AI models (Haiku + Sonnet + Gemini Pro)",
  "Ad-free experience",
  "250-mile scan radius",
  "24-hour pings on the map",
  "Premium gem badge on your profile",
  "Early access to new features",
];

const DONATION_TIERS = [
  {
    amount: 2,
    tokens: 5,
    unlockDays: 2,
    label: "Supporter",
    description: "5 ID tokens + 2 days of full access (Haiku + Sonnet models)",
  },
  {
    amount: 4,
    tokens: 10,
    unlockDays: 5,
    label: "Field Patron",
    description: "10 ID tokens + 5 days of full access (Haiku + Sonnet models)",
  },
];

const TOKEN_PACKS = [
  { tokens: 1, price: 0.99, label: "Single ID" },
  { tokens: 4, price: 2.99, label: "Quick Pack" },
  { tokens: 10, price: 5.99, label: "Field Pack" },
];

export default function Paywall() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [processing, setProcessing] = useState<string | null>(null);

  const { data: profile } = useQuery<Profile>({
    queryKey: ["my-profile-paywall", user?.id],
    queryFn: async () => {
      if (!user) return { id: "", is_pro: false, level: 1, xp: 0 };
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, is_pro, level, xp")
        .eq("id", user.id)
        .maybeSingle();
      return (data as Profile) ?? { id: user.id, is_pro: false, level: 1, xp: 0 };
    },
    enabled: !!user,
  });

  const startCheckout = useMutation({
    mutationFn: async ({
      type,
      priceId,
    }: {
      type: "subscription" | "donation" | "tokens";
      priceId: string;
    }) => {
      if (!user) throw new Error("Sign in to purchase.");
      const { data } = await supabase.auth.getSession();
      const token = data.session?.access_token;
      const response = await fetch(`${BACKEND_URL}/stripe/checkout`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-App-Key": APP_KEY,
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({
          type,
          priceId,
          userId: user.id,
          email: user.email,
        }),
      });
      if (!response.ok) {
        const body = (await response.json().catch(() => ({}))) as {
          error?: string;
        };
        throw new Error(
          body.error || `Checkout could not start (${response.status})`,
        );
      }
      const checkout = (await response.json()) as { url: string };
      // Redirect to Stripe-hosted Checkout.
      window.location.href = checkout.url;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["my-profile-paywall"] });
    },
    onError: (err) => {
      toast.error(err instanceof Error ? err.message : "Checkout failed");
    },
  });

  const handlePurchase = async (type: "subscription" | "donation" | "tokens", priceId: string) => {
    setProcessing(priceId);
    try {
      await startCheckout.mutateAsync({ type, priceId });
    } finally {
      setProcessing(null);
    }
  };

  const isPro = profile?.is_pro ?? false;

  return (
    <div className="space-y-6">
      <div className="text-center">
        <div className="mx-auto mb-3 flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-br from-primary to-amber-500">
          <Crown className="h-8 w-8 text-white" />
        </div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          RockScout Premium
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Unlimited identifications, ad-free, and pro features
        </p>
      </div>

      {isPro && (
        <div className="flex items-center gap-2 rounded-xl border border-primary/30 bg-primary/10 p-4">
          <Crown className="h-5 w-5 text-primary" />
          <p className="text-sm font-medium text-primary">
            You're a Premium member — thank you for supporting RockScout!
          </p>
        </div>
      )}

      {/* Premium subscription */}
      <div className="rounded-xl border border-border bg-gradient-to-br from-primary/10 to-card p-6">
        <div className="flex items-start justify-between gap-4">
          <div>
            <div className="flex items-center gap-2">
              <Sparkles className="h-5 w-5 text-primary" />
              <h2 className="font-display text-xl font-bold text-foreground">
                Monthly Premium
              </h2>
            </div>
            <p className="mt-1 text-sm text-muted-foreground">
              Everything unlocked, every month
            </p>
          </div>
          <div className="text-right">
            <p className="font-display text-3xl font-bold text-foreground">
              $5.99
            </p>
            <p className="text-xs text-muted-foreground">per month</p>
          </div>
        </div>

        <ul className="mt-4 space-y-2">
          {PREMIUM_FEATURES.map((feature) => (
            <li key={feature} className="flex items-center gap-2 text-sm">
              <Check className="h-4 w-4 shrink-0 text-primary" />
              <span className="text-foreground/80">{feature}</span>
            </li>
          ))}
        </ul>

        <Button
          className="mt-5 w-full gap-2"
          size="lg"
          disabled={isPro || processing === "premium"}
          onClick={() => handlePurchase("subscription", "premium-monthly")}
        >
          {processing === "premium" ? (
            <Loader2 className="h-5 w-5 animate-spin" />
          ) : isPro ? (
            <>
              <Check className="h-5 w-5" />
              Active
            </>
          ) : (
            <>
              <Crown className="h-5 w-5" />
              Go Premium
            </>
          )}
        </Button>
      </div>

      {/* Donations */}
      <div>
        <h2 className="mb-3 font-display text-lg font-bold text-foreground">
          One-Time Donations
        </h2>
        <p className="mb-4 text-sm text-muted-foreground">
          Support RockScout and get tokens + temporary full access. No recurring
          charge.
        </p>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {DONATION_TIERS.map((tier) => (
            <div
              key={tier.amount}
              className="rounded-xl border border-border bg-card p-4"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Heart className="h-5 w-5 text-primary" />
                  <span className="font-display text-sm font-semibold text-foreground">
                    {tier.label}
                  </span>
                </div>
                <span className="font-display text-xl font-bold text-foreground">
                  ${tier.amount}
                </span>
              </div>
              <p className="mt-2 text-xs text-muted-foreground">
                {tier.description}
              </p>
              <Button
                className="mt-3 w-full"
                variant="outline"
                disabled={processing === `donation-${tier.amount}`}
                onClick={() =>
                  handlePurchase("donation", `donation-${tier.amount}`)
                }
              >
                {processing === `donation-${tier.amount}` ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  `Donate $${tier.amount}`
                )}
              </Button>
            </div>
          ))}
        </div>
      </div>

      {/* Token packs */}
      <div>
        <h2 className="mb-3 font-display text-lg font-bold text-foreground">
          Token Packs
        </h2>
        <p className="mb-4 text-sm text-muted-foreground">
          Need a few more identifications? Buy tokens individually — no
          subscription needed.
        </p>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
          {TOKEN_PACKS.map((pack) => (
            <div
              key={pack.tokens}
              className="rounded-xl border border-border bg-card p-4 text-center"
            >
              <div className="mx-auto mb-2 flex h-10 w-10 items-center justify-center rounded-full bg-primary/15">
                <Zap className="h-5 w-5 text-primary" />
              </div>
              <p className="font-display text-2xl font-bold text-foreground">
                {pack.tokens}
              </p>
              <p className="text-xs text-muted-foreground">
                {pack.tokens === 1 ? "token" : "tokens"}
              </p>
              <p className="mt-1 text-sm font-medium text-foreground">
                ${pack.price}
              </p>
              <Button
                className="mt-3 w-full"
                variant="outline"
                size="sm"
                disabled={processing === `tokens-${pack.tokens}`}
                onClick={() =>
                  handlePurchase("tokens", `tokens-${pack.tokens}`)
                }
              >
                {processing === `tokens-${pack.tokens}` ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  `Buy ${pack.tokens}`
                )}
              </Button>
            </div>
          ))}
        </div>
      </div>

      {/* Free tier reminder */}
      <div className="rounded-xl border border-dashed border-border bg-card/30 p-4 text-center">
        <Gift className="mx-auto mb-2 h-6 w-6 text-muted-foreground" />
        <p className="text-sm text-muted-foreground">
          The free tier includes ad-supported identification, the full specimen
          database, dig site maps, and all educational guides — always.
        </p>
      </div>
    </div>
  );
}
