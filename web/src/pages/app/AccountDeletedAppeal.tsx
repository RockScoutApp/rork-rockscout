import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { AlertTriangle, Send, Loader2, CheckCircle2 } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

/**
 * Account deleted appeal flow — allows users whose accounts were
 * deleted to submit an appeal for reinstatement.
 */
export default function AccountDeletedAppeal() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [reason, setReason] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = async () => {
    if (!email.trim() || !reason.trim()) {
      toast.error("Please fill in all fields");
      return;
    }
    setIsSubmitting(true);
    try {
      const { error } = await supabase.from("rockscout_account_appeals").insert({
        email: email.trim(),
        reason: reason.trim(),
        status: "pending",
      });
      if (error) throw error;
      setSubmitted(true);
      toast.success("Appeal submitted. We'll review it and contact you.");
    } catch {
      toast.error("Failed to submit appeal. Please try again or contact support.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (submitted) {
    return (
      <ScreenScaffold title="Appeal Submitted">
        <div className="flex flex-col items-center justify-center gap-4 px-4 py-16 text-center">
          <div className="flex h-20 w-20 items-center justify-center rounded-full bg-emerald-500/15 ring-2 ring-emerald-500/30">
            <CheckCircle2 className="h-10 w-10 text-emerald-500" />
          </div>
          <h2 className="font-display text-xl font-bold text-foreground">
            Appeal Received
          </h2>
          <p className="max-w-sm text-sm text-[hsl(var(--text-mid))]">
            We've received your appeal and will review it within 48 hours.
            You'll receive an email at <span className="font-bold text-foreground">{email}</span> with our decision.
          </p>
          <SculptedButton accent="aqua" size="sm" onClick={() => navigate("/app")}>
            Back to App
          </SculptedButton>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="Account Appeal">
      <div className="space-y-5 px-4 pb-8">
        {/* Warning banner */}
        <SculptedCard accent="citrine" className="flex items-start gap-3 p-4">
          <AlertTriangle className="h-6 w-6 shrink-0 text-amber-500" />
          <div>
            <h3 className="font-display text-sm font-bold text-foreground">
              Account Deleted
            </h3>
            <p className="mt-1 text-xs text-[hsl(var(--text-mid))]">
              If your account was deleted and you believe this was an error,
              you can submit an appeal below. Our team will review it and
              contact you within 48 hours.
            </p>
          </div>
        </SculptedCard>

        {/* Appeal form */}
        <div className="space-y-4">
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Email address *
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Why should your account be restored? *
            </label>
            <textarea
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Explain what happened and why you believe the deletion was an error..."
              rows={5}
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
          </div>

          <SculptedButton
            accent="citrine"
            size="md"
            className="w-full"
            disabled={isSubmitting || !email.trim() || !reason.trim()}
            onClick={handleSubmit}
          >
            {isSubmitting ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                Submitting…
              </>
            ) : (
              <>
                <Send className="h-4 w-4" />
                Submit Appeal
              </>
            )}
          </SculptedButton>
        </div>

        <p className="text-center text-xs text-muted-foreground">
          Need help? Contact{" "}
          <a href="/support" className="font-semibold underline">
            support
          </a>
        </p>
      </div>
    </ScreenScaffold>
  );
}
