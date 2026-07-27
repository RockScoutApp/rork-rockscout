import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Shield, Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

const DISCLAIMER_TEXT = `RockScout is a field guide and identification tool — not a professional appraisal service. Rock and mineral identifications are AI-generated and may not be 100% accurate.

When collecting on public land, always check local regulations and obtain permits where required. Do not collect in national parks, protected areas, or on private land without permission.

Rockhounding involves natural hazards: unstable terrain, steep cliffs, falling rocks, and dangerous wildlife. Always wear appropriate safety gear, carry water and a first aid kit, and never go alone to remote locations.

Trade listings are user-to-user transactions. RockScout is not responsible for the accuracy of listings or the outcome of trades. Always inspect specimens in person before completing a trade.

By using RockScout, you agree to follow all local, state, and federal laws regarding rock and mineral collecting.`;

const CURRENT_VERSION = "2026.1";

export default function Disclaimer() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [accepted, setAccepted] = useState(false);
  const isGate = new URLSearchParams(window.location.search).get("isGate") === "true";

  const handleAccept = () => {
    setAccepted(true);
    toast.success("Disclaimer accepted");
    if (isGate) {
      navigate("/app");
    }
  };

  return (
    <div className="space-y-5">
      <div className="flex items-center gap-3">
        <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
          <Shield className="h-6 w-6 text-primary" />
        </div>
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Disclaimer
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            Please read and accept before continuing
          </p>
        </div>
      </div>

      <div className="rounded-xl border border-border bg-card p-5">
        <p className="whitespace-pre-wrap text-sm leading-relaxed text-muted-foreground">
          {DISCLAIMER_TEXT}
        </p>
      </div>

      {accepted ? (
        <div className="flex items-center gap-2 rounded-lg border border-primary/30 bg-primary/5 p-4 text-sm font-medium text-primary">
          <Check className="h-4 w-4" />
          Thank you! You can continue using RockScout.
        </div>
      ) : (
        <div className="flex gap-3">
          <Button onClick={handleAccept} className="gap-2">
            <Check className="h-4 w-4" />
            I Accept
          </Button>
          {!isGate && (
            <Button variant="outline" onClick={() => navigate("/app")}>
              Back
            </Button>
          )}
        </div>
      )}
    </div>
  );
}
