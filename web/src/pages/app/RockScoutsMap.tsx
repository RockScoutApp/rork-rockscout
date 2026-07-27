import { MapPin } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";

export default function RockScoutsMap() {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <MapPin className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view the RockScouts map</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">RockScouts Map</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">See nearby RockScout users (approximate locations)</p>
      </div>

      <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
        <MapPin className="h-8 w-8 text-muted-foreground" />
        <p className="max-w-sm text-sm text-muted-foreground">
          The RockScouts Map shows approximate locations of other RockScout users who
          have enabled location sharing. Your exact position is never shared — only an
          approximate area is visible to others.
        </p>
      </div>
    </div>
  );
}
