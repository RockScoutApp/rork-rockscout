import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { Archive, Loader2, Calendar, MapPin, Compass } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const AMETHYST_HEX = "265 47% 67%";

interface Trip {
  id: string;
  name: string;
  trip_date: string;
  notes: string;
  is_archived: boolean;
}

export default function ArchivedTrips() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const { data: trips, isLoading } = useQuery<Trip[]>({
    queryKey: ["archived-trips", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_trips")
        .select("*")
        .eq("user_id", user.id)
        .eq("is_archived", true)
        .order("trip_date", { ascending: false });
      if (error) throw error;
      return (data ?? []) as Trip[];
    },
    enabled: !!user,
  });

  if (!user) {
    return (
      <ScreenScaffold title="Archived Trips" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Archive className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view archived trips</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="Archived Trips" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Your completed and archived trip plans
        </p>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : trips && trips.length > 0 ? (
          <div className="space-y-3">
            {trips.map((trip) => (
              <SculptedCard key={trip.id} accent="amethyst" className="p-4">
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-start gap-3">
                    <div
                      className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                      style={{ ["--badge-accent" as string]: AMETHYST_HEX, color: `hsl(${AMETHYST_HEX})` }}
                    >
                      <Archive className="h-5 w-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <h3 className="font-display text-sm font-bold text-foreground">{trip.name}</h3>
                      <p className="mt-0.5 flex items-center gap-1.5 text-xs text-muted-foreground">
                        <Calendar className="h-3 w-3" />
                        {trip.trip_date}
                      </p>
                    </div>
                  </div>
                  <TagChip accent={`hsl(${AMETHYST_HEX})`}>Archived</TagChip>
                </div>
                {trip.notes && (
                  <p className="mt-2 border-t border-border pt-2 text-sm text-[hsl(var(--text-mid))]">
                    {trip.notes}
                  </p>
                )}
              </SculptedCard>
            ))}
          </div>
        ) : (
          <SculptedCard accent="amethyst" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <Archive className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">No archived trips yet.</p>
            <SculptedButton accent="citrine" size="sm" onClick={() => navigate("/app/trips")}>
              <Compass className="h-4 w-4" />
              Plan a Trip
            </SculptedButton>
          </SculptedCard>
        )}
      </div>
    </ScreenScaffold>
  );
}
