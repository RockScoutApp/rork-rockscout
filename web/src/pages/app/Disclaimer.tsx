import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Shield, Check, AlertTriangle } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const DISCLAIMER_KEY = "rockscout-disclaimer-accepted";
const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const DANGER_HEX = "4 70% 55%";

const DISCLAIMER_SECTIONS = [
  {
    icon: AlertTriangle,
    title: "AI Identification",
    text: "RockScout is a field guide and identification tool — not a professional appraisal service. Rock and mineral identifications are AI-generated and may not be 100% accurate.",
  },
  {
    icon: Shield,
    title: "Collecting Regulations",
    text: "When collecting on public land, always check local regulations and obtain permits where required. Do not collect in national parks, protected areas, or on private land without permission.",
  },
  {
    icon: AlertTriangle,
    title: "Natural Hazards",
    text: "Rockhounding involves natural hazards: unstable terrain, steep cliffs, falling rocks, and dangerous wildlife. Always wear appropriate safety gear, carry water and a first aid kit, and never go alone to remote locations.",
  },
  {
    icon: Shield,
    title: "Trade Disclaimer",
    text: "Trade listings are user-to-user transactions. RockScout is not responsible for the accuracy of listings or the outcome of trades. Always inspect specimens in person before completing a trade.",
  },
  {
    icon: Check,
    title: "User Responsibility",
    text: "By using RockScout, you agree to follow all local, state, and federal laws regarding rock and mineral collecting.",
  },
];

const CURRENT_VERSION = "2026.1";

export default function Disclaimer() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [accepted, setAccepted] = useState(false);
  const isGate = new URLSearchParams(window.location.search).get("isGate") === "true";

  const handleAccept = () => {
    setAccepted(true);
    try {
      localStorage.setItem(DISCLAIMER_KEY, "true");
    } catch {
      // localStorage may be blocked in private browsing — continue without persistence
    }
    toast.success("Disclaimer accepted");
    navigate("/app");
  };

  return (
    <ScreenScaffold title="Disclaimer" onBack={() => !isGate && navigate("/app")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Header */}
        <SculptedCard accent="citrine" glowing className="p-5">
          <div className="flex items-start gap-3">
            <div
              className="icon-badge glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, ["--glow-color" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Shield className="h-6 w-6" />
            </div>
            <div>
              <h2 className="font-display text-lg font-bold text-foreground">
                Please read and accept
              </h2>
              <p className="mt-0.5 text-sm text-muted-foreground">
                Version {CURRENT_VERSION} · Updated August 2026
              </p>
            </div>
          </div>
        </SculptedCard>

        {/* Disclaimer sections */}
        <div className="space-y-3">
          {DISCLAIMER_SECTIONS.map((section, i) => (
            <SculptedCard
              key={i}
              accent={section.title.includes("Hazard") ? "danger" : "aqua"}
              className="p-4"
            >
              <div className="flex items-start gap-3">
                <div
                  className="icon-badge flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
                  style={{
                    ["--badge-accent" as string]: section.title.includes("Hazard") ? DANGER_HEX : AQUA_HEX,
                    color: `hsl(${section.title.includes("Hazard") ? DANGER_HEX : AQUA_HEX})`,
                  }}
                >
                  <section.icon className="h-4 w-4" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-foreground">{section.title}</h3>
                  <p className="mt-1 text-xs leading-relaxed text-[hsl(var(--text-mid))]">
                    {section.text}
                  </p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>

        {/* Accept button */}
        {accepted ? (
          <SculptedCard accent="success" glowing className="flex items-center gap-3 p-4">
            <Check className="h-5 w-5" style={{ color: "hsl(147 49% 55%)" }} />
            <p className="text-sm font-medium text-foreground">
              Thank you! You can continue using RockScout.
            </p>
          </SculptedCard>
        ) : (
          <div className="flex gap-3">
            <SculptedButton accent="citrine" glowing size="lg" className="flex-1" onClick={handleAccept}>
              <Check className="h-4 w-4" />
              I Accept
            </SculptedButton>
            {!isGate && (
              <SculptedButton accent="aqua" size="lg" onClick={() => navigate("/app")}>
                Back
              </SculptedButton>
            )}
          </div>
        )}
      </div>
    </ScreenScaffold>
  );
}
