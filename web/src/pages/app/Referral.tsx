import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Gift,
  Copy,
  Check,
  Users,
  Zap,
  Loader2,
  Share2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

interface Profile {
  id: string;
  display_name: string;
  referral_code: string | null;
  referred_by: string | null;
  xp: number;
  level: number;
}

export default function Referral() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [copied, setCopied] = useState(false);
  const [enteredCode, setEnteredCode] = useState("");

  const { data: profile } = useQuery<Profile>({
    queryKey: ["my-profile-referral", user?.id],
    queryFn: async () => {
      if (!user) return null;
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, referral_code, referred_by, xp, level")
        .eq("id", user.id)
        .maybeSingle();
      if (!data) {
        // Auto-create with a referral code
        const code = generateCode(user.id);
        const { data: created } = await supabase
          .from("rockscout_profiles")
          .insert({
            id: user.id,
            display_name: user.email?.split("@")[0] ?? "Rockhound",
            referral_code: code,
          })
          .select("id, display_name, referral_code, referred_by, xp, level")
          .single();
        return created as Profile;
      }
      return data as Profile;
    },
    enabled: !!user,
  });

  const applyReferral = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to apply a referral code");
      if (!enteredCode.trim()) throw new Error("Enter a referral code");
      const code = enteredCode.trim().toUpperCase();
      if (profile?.referral_code === code) {
        throw new Error("You can't use your own code");
      }
      if (profile?.referred_by) {
        throw new Error("You've already applied a referral code");
      }
      // Find the referrer by their code
      const { data: referrer, error: findErr } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name")
        .eq("referral_code", code)
        .maybeSingle();
      if (findErr || !referrer) {
        throw new Error("That referral code wasn't found");
      }
      // Update this user's referred_by + award XP
      const { error: updateErr } = await supabase
        .from("rockscout_profiles")
        .update({
          referred_by: referrer.id,
          xp: (profile?.xp ?? 0) + 50,
        })
        .eq("id", user.id);
      if (updateErr) throw updateErr;
      // Award XP to the referrer
      const { error: refErr } = await supabase
        .from("rockscout_profiles")
        .update({ xp: (referrer as { xp?: number }).xp ?? 0 + 100 })
        .eq("id", referrer.id);
      if (refErr) throw refErr;
    },
    onSuccess: () => {
      toast.success("Referral applied — you both earned XP!");
      queryClient.invalidateQueries({ queryKey: ["my-profile-referral"] });
      setEnteredCode("");
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to apply code"),
  });

  const referralCode = profile?.referral_code ?? "";
  const referralLink = referralCode
    ? `${window.location.origin}/app?ref=${referralCode}`
    : "";

  const handleCopy = () => {
    navigator.clipboard?.writeText(referralLink);
    setCopied(true);
    toast.success("Referral link copied");
    setTimeout(() => setCopied(false), 2000);
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: "RockScout — Identify. Collect. Trade.",
          text: "Join me on RockScout — the pocket geologist for rockhounds!",
          url: referralLink,
        });
      } catch {
        // User cancelled — no action needed
      }
    } else {
      handleCopy();
    }
  };

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Gift className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to use the referral program</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="text-center">
        <div className="mx-auto mb-3 flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-br from-primary to-amber-500">
          <Gift className="h-8 w-8 text-white" />
        </div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Refer a Friend
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Earn XP for every rockhound you bring to RockScout
        </p>
      </div>

      {/* Your referral code */}
      <div className="rounded-xl border border-border bg-gradient-to-br from-primary/10 to-card p-6">
        <h2 className="mb-1 font-display text-sm font-semibold text-foreground">
          Your referral code
        </h2>
        {referralCode ? (
          <>
            <div className="flex items-center gap-2">
              <code className="rounded-lg bg-muted px-4 py-2 font-mono text-lg font-bold tracking-wider text-foreground">
                {referralCode}
              </code>
              <Button
                size="sm"
                variant="outline"
                onClick={handleCopy}
                className="gap-2"
              >
                {copied ? (
                  <>
                    <Check className="h-4 w-4" />
                    Copied
                  </>
                ) : (
                  <>
                    <Copy className="h-4 w-4" />
                    Copy link
                  </>
                )}
              </Button>
            </div>
            <p className="mt-3 break-all text-xs text-muted-foreground">
              {referralLink}
            </p>
            <Button
              className="mt-4 w-full gap-2"
              variant="outline"
              onClick={handleShare}
            >
              <Share2 className="h-4 w-4" />
              Share with a friend
            </Button>
          </>
        ) : (
          <div className="flex justify-center py-4">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        )}
      </div>

      {/* How it works */}
      <div className="rounded-xl border border-border bg-card p-5">
        <h2 className="mb-3 font-display text-base font-bold text-foreground">
          How it works
        </h2>
        <div className="space-y-3">
          {[
            {
              icon: Share2,
              title: "Share your link",
              desc: "Send your referral link to a fellow rockhound.",
            },
            {
              icon: Users,
              title: "They sign up",
              desc: "Your friend creates a RockScout account using your link.",
            },
            {
              icon: Zap,
              title: "You both earn XP",
              desc: "You get 100 XP, your friend gets 50 XP — and a head start on Level 2.",
            },
          ].map((step, i) => (
            <div key={i} className="flex items-start gap-3">
              <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/15">
                <step.icon className="h-4 w-4 text-primary" />
              </div>
              <div>
                <p className="text-sm font-medium text-foreground">
                  {step.title}
                </p>
                <p className="text-xs text-muted-foreground">{step.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Enter a code */}
      {!profile?.referred_by && (
        <div className="rounded-xl border border-border bg-card p-5">
          <h2 className="mb-1 font-display text-base font-bold text-foreground">
            Have a referral code?
          </h2>
          <p className="mb-3 text-xs text-muted-foreground">
            Enter a friend's code to earn 50 bonus XP.
          </p>
          <div className="flex gap-2">
            <Input
              value={enteredCode}
              onChange={(e) =>
                setEnteredCode(e.target.value.toUpperCase())
              }
              placeholder="e.g. ROCK-AB12"
              className="flex-1 font-mono"
            />
            <Button
              onClick={() => applyReferral.mutate()}
              disabled={
                applyReferral.isPending || !enteredCode.trim()
              }
            >
              {applyReferral.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "Apply"
              )}
            </Button>
          </div>
        </div>
      )}

      {profile?.referred_by && (
        <div className="flex items-center gap-2 rounded-xl border border-primary/30 bg-primary/10 p-4">
          <Check className="h-5 w-5 text-primary" />
          <p className="text-sm font-medium text-primary">
            You've applied a referral code — +50 XP earned!
          </p>
        </div>
      )}

      {/* Your stats */}
      <div className="grid grid-cols-2 gap-3">
        <div className="rounded-xl border border-border bg-card p-4 text-center">
          <Users className="mx-auto mb-1 h-5 w-5 text-primary" />
          <p className="font-display text-xl font-bold text-foreground">
            0
          </p>
          <p className="text-xs text-muted-foreground">Referrals</p>
        </div>
        <div className="rounded-xl border border-border bg-card p-4 text-center">
          <Zap className="mx-auto mb-1 h-5 w-5 text-primary" />
          <p className="font-display text-xl font-bold text-foreground">
            {profile?.xp ?? 0}
          </p>
          <p className="text-xs text-muted-foreground">Total XP</p>
        </div>
      </div>
    </div>
  );
}

/** Generate a short referral code from a user ID. */
function generateCode(userId: string): string {
  const hash = userId.replace(/-/g, "").slice(0, 4).toUpperCase();
  return `ROCK-${hash}`;
}
