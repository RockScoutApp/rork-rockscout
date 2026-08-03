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
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold, StatTile } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

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
      <ScreenScaffold title="Refer a Friend">
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Gift className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to use the referral program</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="Refer a Friend">
     <div className="space-y-5 px-4 pb-8">
      <div className="text-center">
        <div
          className="glowing-border mx-auto mb-3 flex h-16 w-16 items-center justify-center rounded-full"
          style={{ ["--glow-color" as string]: CITRINE_HEX, background: `radial-gradient(circle, hsl(${CITRINE_HEX} / 0.3), hsl(${CITRINE_HEX} / 0.05))` }}
        >
          <Gift className="h-8 w-8" style={{ color: `hsl(${CITRINE_HEX})` }} />
        </div>
        <p className="text-sm text-muted-foreground">
          Earn XP for every rockhound you bring to RockScout
        </p>
      </div>

      {/* Your referral code */}
      <SculptedCard accent="citrine" glowing className="p-6">
        <h2 className="mb-1 font-display text-sm font-bold text-foreground">Your referral code</h2>
        {referralCode ? (
          <>
            <div className="flex items-center gap-2">
              <code className="rounded-lg bg-muted px-4 py-2 font-mono text-lg font-bold tracking-wider text-foreground">{referralCode}</code>
              <SculptedButton accent="aqua" size="sm" onClick={handleCopy}>
                {copied ? <><Check className="h-4 w-4" /> Copied</> : <><Copy className="h-4 w-4" /> Copy link</>}
              </SculptedButton>
            </div>
            <p className="mt-3 break-all text-xs text-muted-foreground">{referralLink}</p>
            <SculptedButton accent="citrine" glowing className="mt-4 w-full" onClick={handleShare}>
              <Share2 className="h-4 w-4" />
              Share with a friend
            </SculptedButton>
          </>
        ) : (
          <div className="flex justify-center py-4">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        )}
      </SculptedCard>

      {/* How it works */}
      <SculptedCard accent="aqua" className="p-5">
        <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>How it works</h2>
        <div className="space-y-3">
          {[
            { icon: Share2, title: "Share your link", desc: "Send your referral link to a fellow rockhound." },
            { icon: Users, title: "They sign up", desc: "Your friend creates a RockScout account using your link." },
            { icon: Zap, title: "You both earn XP", desc: "You get 100 XP, your friend gets 50 XP — and a head start on Level 2." },
          ].map((step, i) => (
            <div key={i} className="flex items-start gap-3">
              <div
                className="icon-badge flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
                style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
              >
                <step.icon className="h-4 w-4" />
              </div>
              <div>
                <p className="text-sm font-bold text-foreground">{step.title}</p>
                <p className="text-xs text-[hsl(var(--text-mid))]">{step.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </SculptedCard>

      {/* Enter a code */}
      {!profile?.referred_by && (
        <SculptedCard accent="citrine" className="p-5">
          <h2 className="mb-1 font-display text-base font-bold text-foreground">Have a referral code?</h2>
          <p className="mb-3 text-xs text-muted-foreground">Enter a friend's code to earn 50 bonus XP.</p>
          <div className="flex gap-2">
            <Input
              value={enteredCode}
              onChange={(e) => setEnteredCode(e.target.value.toUpperCase())}
              placeholder="e.g. ROCK-AB12"
              className="flex-1 font-mono"
            />
            <SculptedButton
              accent="citrine"
              glowing
              onClick={() => applyReferral.mutate()}
              disabled={applyReferral.isPending || !enteredCode.trim()}
            >
              {applyReferral.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : "Apply"}
            </SculptedButton>
          </div>
        </SculptedCard>
      )}

      {profile?.referred_by && (
        <SculptedCard accent="success" glowing className="flex items-center gap-2 p-4">
          <Check className="h-5 w-5" style={{ color: "hsl(147 49% 55%)" }} />
          <p className="text-sm font-bold" style={{ color: "hsl(147 49% 55%)" }}>
            You've applied a referral code — +50 XP earned!
          </p>
        </SculptedCard>
      )}

      {/* Your stats */}
      <div className="grid grid-cols-2 gap-3">
        <StatTile label="Referrals" value={0} accent="aqua" icon={<Users className="h-4 w-4" />} />
        <StatTile label="Total XP" value={profile?.xp ?? 0} accent="citrine" icon={<Zap className="h-4 w-4" />} />
      </div>
     </div>
    </ScreenScaffold>
  );
}

/** Generate a short referral code from a user ID. */
function generateCode(userId: string): string {
  const hash = userId.replace(/-/g, "").slice(0, 4).toUpperCase();
  return `ROCK-${hash}`;
}
