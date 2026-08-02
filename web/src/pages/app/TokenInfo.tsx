import { useQuery } from "@tanstack/react-query";
import { Coins, Info, Zap, ArrowLeft } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";

export default function TokenInfo() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const { data: profile } = useQuery<{ tokens: number; premium_days: number }>({
    queryKey: ["token-info", user?.id],
    queryFn: async () => {
      if (!user) return { tokens: 0, premium_days: 0 };
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("tokens, premium_days")
        .eq("id", user.id)
        .maybeSingle();
      return (data as { tokens: number; premium_days: number }) ?? { tokens: 0, premium_days: 0 };
    },
    enabled: !!user,
  });

  return (
    <div className="space-y-5">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Home
      </Button>

      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Token Info
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Your ID token balance and how to earn more
        </p>
      </div>

      <div className="rounded-xl border border-border bg-gradient-to-br from-primary/10 to-card p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-14 w-14 items-center justify-center rounded-full bg-primary/20 ring-2 ring-primary/30">
            <Coins className="h-7 w-7 text-primary" />
          </div>
          <div>
            <p className="font-display text-3xl font-bold text-foreground">
              {profile?.tokens ?? 0}
            </p>
            <p className="text-sm text-muted-foreground">ID tokens available</p>
          </div>
        </div>
      </div>

      <div className="dark-card sculpted-raised rounded-lg p-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
          <Info className="h-4 w-4 text-primary" />
          What are ID tokens?
        </div>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          ID tokens are used each time you identify a rock with the AI tool.
          Free users get a limited number of tokens to try the feature. When
          you run out, you can earn more or upgrade to Premium for unlimited
          identifications.
        </p>
      </div>

      <div className="dark-card sculpted-raised rounded-lg p-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
          <Zap className="h-4 w-4 text-primary" />
          How to earn more tokens
        </div>
        <ul className="mt-2 space-y-2 text-sm text-muted-foreground">
          <li className="flex gap-2">
            <span className="text-primary">•</span>
            Refer a friend — earn tokens for each signup
          </li>
          <li className="flex gap-2">
            <span className="text-primary">•</span>
            Make a donation to support RockScout — tokens included as a thank you
          </li>
          <li className="flex gap-2">
            <span className="text-primary">•</span>
            Upgrade to Premium for unlimited identifications
          </li>
        </ul>
      </div>

      <div className="flex gap-3">
        <Button onClick={() => navigate("/app/referral")} variant="outline" className="gap-2">
          <Coins className="h-4 w-4" />
          Refer a Friend
        </Button>
        <Button onClick={() => navigate("/app/paywall")} className="gap-2">
          <Zap className="h-4 w-4" />
          Go Premium
        </Button>
      </div>
    </div>
  );
}
