import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Coins, Calendar, CheckCircle2, Heart } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function ThankYou() {
  const { tokens, days } = useParams<{ tokens: string; days: string }>();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center space-y-6 text-center">
      <div className="flex h-20 w-20 items-center justify-center rounded-full bg-primary/20 ring-4 ring-primary/30">
        <CheckCircle2 className="h-10 w-10 text-primary" />
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
          <div className="rounded-xl border border-border bg-card px-6 py-4">
            <div className="flex items-center gap-2 text-primary">
              <Coins className="h-5 w-5" />
              <span className="font-display text-2xl font-bold">{tokens}</span>
            </div>
            <p className="mt-1 text-xs text-muted-foreground">ID tokens added</p>
          </div>
        )}
        {days && days !== "0" && (
          <div className="rounded-xl border border-border bg-card px-6 py-4">
            <div className="flex items-center gap-2 text-primary">
              <Calendar className="h-5 w-5" />
              <span className="font-display text-2xl font-bold">{days}</span>
            </div>
            <p className="mt-1 text-xs text-muted-foreground">Premium days added</p>
          </div>
        )}
      </div>

      <div className="flex gap-3">
        <Button onClick={() => navigate("/app")} className="gap-2">
          <Heart className="h-4 w-4" />
          Back to Home
        </Button>
        <Button
          onClick={() => navigate("/app/identify")}
          variant="outline"
          className="gap-2"
        >
          Identify a Rock
        </Button>
      </div>
    </div>
  );
}
