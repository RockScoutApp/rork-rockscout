import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { Archive, Loader2, Calendar, MapPin } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";

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
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Archive className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view archived trips</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">Archived Trips</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">Your completed and archived trip plans</p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : trips && trips.length > 0 ? (
        <div className="space-y-3">
          {trips.map((trip) => (
            <div key={trip.id} className="rounded-xl border border-border bg-card p-4">
              <div className="flex items-start justify-between gap-2">
                <h3 className="font-display text-sm font-semibold text-foreground">{trip.name}</h3>
                <span className="shrink-0 rounded bg-muted px-2 py-0.5 text-[10px] font-medium text-muted-foreground">Archived</span>
              </div>
              <p className="mt-1 flex items-center gap-1.5 text-xs text-muted-foreground">
                <Calendar className="h-3 w-3" />{trip.trip_date}
              </p>
              {trip.notes && <p className="mt-1.5 text-sm text-muted-foreground">{trip.notes}</p>}
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Archive className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No archived trips yet.</p>
          <button onClick={() => navigate("/app/trips")} className="text-sm font-medium text-primary hover:underline">
            Plan a trip
          </button>
        </div>
      )}
    </div>
  );
}
