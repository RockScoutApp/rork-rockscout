import { useParams, useNavigate } from "react-router-dom";
import { Coins, Calendar, CheckCircle2, Heart } from "lucide-react";
import { SculptedCard, SculptedButton } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

export default function ThankYou() {
  const { tokens, days } = useParams<{ tokens: string; days: string }>();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center space-y-6 px-4 text-center">
      <div
        className="glowing-border flex h-20 w-20 items-center justify-center rounded-full"
        style={{ ["--glow-color" as string]: CITRINE_HEX }}
      >
        <CheckCircle2 className="h-10 w-10" style={{ color: `hsl(${CITRINE_HEX})` }} />
      </div>

      <div>
        <h1 className="font-display text-3xl font-bold text-foreground">
          Thank You!
        </h1>
        <p className="mt-2 max-w-md text-sm text-muted-foreground">
          Your support keeps RockScout running and helps rockhounds everywhere
          discover and learn about the earth's treasures.
        </p>
      </div>

      <div className="flex gap-4">
        {tokens && tokens !== "0" && (
          <SculptedCard accent="citrine" glowing className="px-6 py-4">
            <div className="flex items-center gap-2" style={{ color: `hsl(${CITRINE_HEX})` }}>
              <Coins className="h-5 w-5" />
              <span className="font-display text-2xl font-bold">{tokens}</span>
            </div>
            <p className="mt-1 text-xs text-muted-foreground">ID tokens added</p>
          </SculptedCard>
        )}
        {days && days !== "0" && (
          <SculptedCard accent="aqua" glowing className="px-6 py-4">
            <div className="flex items-center gap-2" style={{ color: `hsl(${AQUA_HEX})` }}>
              <Calendar className="h-5 w-5" />
              <span className="font-display text-2xl font-bold">{days}</span>
            </div>
            <p className="mt-1 text-xs text-muted-foreground">Premium days added</p>
          </SculptedCard>
        )}
      </div>

      <div className="flex gap-3">
        <SculptedButton accent="citrine" glowing onClick={() => navigate("/app")}>
          <Heart className="h-4 w-4" />
          Back to Home
        </SculptedButton>
        <SculptedButton accent="aqua" onClick={() => navigate("/app/identify")}>
          Identify a Rock
        </SculptedButton>
      </div>
    </div>
  );
}
