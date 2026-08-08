import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Crown, Check, Smartphone, Gift } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { syncEntitlement } from "@/lib/entitlement";
import { useEffect } from "react";

interface Profile {
  id: string;
  is_pro: boolean;
  level: number;
  xp: number;
}

const PREMIUM_FEATURES = [
  "Unlimited AI rock identifications",
  "5-source AI system (database + Haiku + Sonnet + Gemini + web cross-check)",
  "Ad-free experience",
  "250-mile scan radius",
  "24-hour pings on the map",
  "Premium gem badge on your profile",
  "Early access to new features",
];

export default function Paywall() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

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

  const isPro = profile?.is_pro ?? false;

  // Sync RevenueCat entitlement → Supabase when the Paywall opens, so a
  // user who just bought Premium on Android sees it reflected here.
  useEffect(() => {
    if (user?.id) {
      void syncEntitlement(user.id).then(() => {
        // Invalidate the profile query so the updated is_pro is fetched.
        void queryClient.invalidateQueries({ queryKey: ["my-profile-paywall", user.id] });
      });
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]);

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
              <Crown className="h-5 w-5 text-primary" />
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

        {isPro ? (
          <div className="mt-5 flex items-center justify-center gap-2 rounded-lg bg-primary/10 py-3">
            <Check className="h-5 w-5 text-primary" />
            <span className="text-sm font-medium text-primary">Active</span>
          </div>
        ) : (
          <div className="mt-5 dark-card sculpted-raised rounded-lg p-4 text-center">
            <div className="mb-2 flex items-center justify-center gap-2">
              <Smartphone className="h-5 w-5 text-primary" />
              <span className="text-sm font-semibold text-foreground">
                Subscribe in the mobile app
              </span>
            </div>
            <p className="text-xs text-muted-foreground">
              Premium is available as an in-app purchase in the RockScout
              Android and iOS apps. Download the app, sign in with this same
              email, and your Premium subscription unlocks here automatically.
            </p>
          </div>
        )}
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
