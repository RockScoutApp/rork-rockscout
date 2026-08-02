import { useState, useMemo } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import {
  ChevronLeft,
  ChevronRight,
  Plus,
  Calendar,
  Loader2,
  Compass,
  Trash2,
  Archive,
  Share2,
} from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

interface Trip {
  id: string;
  user_id: string;
  name: string;
  trip_date: string;
  notes: string;
  is_archived: boolean;
}

const DAY_NAMES = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
const MONTH_NAMES = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

function toDateKey(dateStr: string): string {
  return dateStr.split("T")[0];
}

function isSameDay(date1: Date, date2: Date): boolean {
  return (
    date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate()
  );
}

export default function TripCalendar() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [currentMonth, setCurrentMonth] = useState(() => new Date());
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);

  const { data: trips, isLoading } = useQuery<Trip[]>({
    queryKey: ["trips-calendar", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_trips")
        .select("*")
        .eq("user_id", user.id)
        .order("trip_date", { ascending: true });
      if (error) throw error;
      return (data ?? []) as Trip[];
    },
    enabled: !!user,
  });

  const deleteMutation = useMutation({
    mutationFn: async (tripId: string) => {
      await supabase.from("rockscout_trips").delete().eq("id", tripId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["trips-calendar"] });
      queryClient.invalidateQueries({ queryKey: ["trips"] });
      queryClient.invalidateQueries({ queryKey: ["archived-trips"] });
      toast.success("Trip deleted");
    },
    onError: () => toast.error("Failed to delete trip"),
  });

  const archiveMutation = useMutation({
    mutationFn: async (tripId: string) => {
      await supabase
        .from("rockscout_trips")
        .update({ is_archived: true, completed_at: new Date().toISOString() })
        .eq("id", tripId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["trips-calendar"] });
      queryClient.invalidateQueries({ queryKey: ["trips"] });
      queryClient.invalidateQueries({ queryKey: ["archived-trips"] });
      toast.success("Trip archived");
    },
    onError: () => toast.error("Failed to archive trip"),
  });

  const tripsByDate = useMemo(() => {
    const map = new Map<string, Trip[]>();
    if (!trips) return map;
    for (const trip of trips) {
      const key = toDateKey(trip.trip_date);
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(trip);
    }
    return map;
  }, [trips]);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const calendarDays = useMemo(() => {
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const firstDayOfWeek = firstDay.getDay();
    const daysInMonth = lastDay.getDate();

    const prevMonthLastDay = new Date(year, month, 0).getDate();

    const cells: { date: Date; isCurrentMonth: boolean }[] = [];

    for (let i = firstDayOfWeek - 1; i >= 0; i--) {
      cells.push({
        date: new Date(year, month - 1, prevMonthLastDay - i),
        isCurrentMonth: false,
      });
    }

    for (let d = 1; d <= daysInMonth; d++) {
      cells.push({ date: new Date(year, month, d), isCurrentMonth: true });
    }

    const remaining = 42 - cells.length;
    for (let d = 1; d <= remaining; d++) {
      cells.push({ date: new Date(year, month + 1, d), isCurrentMonth: false });
    }

    return cells;
  }, [currentMonth]);

  const displayTrips = useMemo(() => {
    if (!trips) return [];
    if (selectedDate) {
      return trips.filter((t) => {
        const tripDate = new Date(t.trip_date);
        return isSameDay(tripDate, selectedDate);
      });
    }
    const upcoming = trips
      .filter((t) => new Date(t.trip_date) >= today && !t.is_archived)
      .sort((a, b) => new Date(a.trip_date).getTime() - new Date(b.trip_date).getTime());
    return upcoming;
  }, [trips, selectedDate, today]);

  const pastTrips = useMemo(() => {
    if (!trips || selectedDate) return [];
    return trips
      .filter((t) => new Date(t.trip_date) < today)
      .sort((a, b) => new Date(b.trip_date).getTime() - new Date(a.trip_date).getTime())
      .slice(0, 10);
  }, [trips, selectedDate, today]);

  if (!user) {
    return (
      <ScreenScaffold title="Trip Calendar" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Calendar className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view your trip calendar</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="Trip Calendar" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        {/* Header with Plan New Trip button */}
        <div className="flex items-center justify-between">
          <h2 className="font-display text-xl font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
            Trip Calendar
          </h2>
          <SculptedButton accent="citrine" size="sm" onClick={() => navigate("/app/trips")}>
            <Plus className="h-4 w-4" />
            Plan New Trip
          </SculptedButton>
        </div>

        {/* Month navigation */}
        <SculptedCard accent="aqua" className="flex items-center justify-between p-3">
          <button
            onClick={() => setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1))}
            className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/15 transition-colors hover:bg-primary/25"
          >
            <ChevronLeft className="h-5 w-5" style={{ color: `hsl(${AQUA_HEX})` }} />
          </button>
          <span className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
            {MONTH_NAMES[currentMonth.getMonth()]} {currentMonth.getFullYear()}
          </span>
          <button
            onClick={() => setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1))}
            className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/15 transition-colors hover:bg-primary/25"
          >
            <ChevronRight className="h-5 w-5" style={{ color: `hsl(${AQUA_HEX})` }} />
          </button>
        </SculptedCard>

        {/* Day headers */}
        <div className="grid grid-cols-7 gap-1">
          {DAY_NAMES.map((day) => (
            <div key={day} className="text-center text-xs font-bold uppercase text-muted-foreground/60">
              {day}
            </div>
          ))}
        </div>

        {/* Calendar grid */}
        <SculptedCard accent="aqua" className="grid grid-cols-7 gap-1 p-3">
          {calendarDays.map((cell, i) => {
            const dateKey = toDateKey(cell.date.toISOString());
            const dayTrips = tripsByDate.get(dateKey) ?? [];
            const isToday = isSameDay(cell.date, today);
            const isSelected = selectedDate && isSameDay(cell.date, selectedDate);

            return (
              <button
                key={i}
                onClick={() => setSelectedDate(isSelected ? null : cell.date)}
                className={`flex aspect-square flex-col items-start rounded-lg p-1 transition-all ${
                  !cell.isCurrentMonth ? "opacity-30" : ""
                } ${
                  isSelected
                    ? "bg-primary/25 ring-1 ring-primary"
                    : isToday
                    ? "bg-primary/15"
                    : dayTrips.length > 0
                    ? "bg-[hsl(30_10%_12%)]"
                    : "hover:bg-muted/30"
                }`}
              >
                <span
                  className={`text-xs ${isToday ? "font-bold" : ""}`}
                  style={{ color: isToday ? `hsl(${AQUA_HEX})` : undefined }}
                >
                  {cell.date.getDate()}
                </span>
                {dayTrips.slice(0, 2).map((trip) => (
                  <div
                    key={trip.id}
                    className="mt-0.5 flex w-full items-center gap-0.5 overflow-hidden"
                  >
                    <span
                      className="h-1.5 w-1.5 shrink-0 rounded-full"
                      style={{ backgroundColor: trip.is_archived ? "hsl(var(--muted-foreground))" : `hsl(${CITRINE_HEX})` }}
                    />
                    <span className="truncate text-[8px] leading-tight text-foreground/80">
                      {trip.name}
                    </span>
                  </div>
                ))}
                {dayTrips.length > 2 && (
                  <span className="mt-0.5 text-[8px] text-muted-foreground">
                    +{dayTrips.length - 2} more
                  </span>
                )}
              </button>
            );
          })}
        </SculptedCard>

        {/* Selected date or upcoming trips header */}
        <div className="flex items-center justify-between">
          <h3 className="font-display text-base font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
            {selectedDate
              ? `Trips on ${selectedDate.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}`
              : "Upcoming Trips"}
          </h3>
          {selectedDate && (
            <button
              onClick={() => setSelectedDate(null)}
              className="text-xs font-semibold"
              style={{ color: `hsl(${AQUA_HEX})` }}
            >
              Show all
            </button>
          )}
        </div>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : displayTrips.length === 0 ? (
          <SculptedCard accent="citrine" className="flex flex-col items-center justify-center gap-3 py-12 text-center">
            <span className="text-3xl">🕐</span>
            <p className="text-sm text-muted-foreground">
              {selectedDate ? "No trips on this date" : 'No upcoming trips — tap "Plan New Trip" to start'}
            </p>
          </SculptedCard>
        ) : (
          <div className="space-y-3">
            {displayTrips.map((trip) => (
              <CalendarTripCard
                key={trip.id}
                trip={trip}
                onDelete={() => deleteMutation.mutate(trip.id)}
                onArchive={() => archiveMutation.mutate(trip.id)}
              />
            ))}
          </div>
        )}

        {/* Past trips section */}
        {!selectedDate && pastTrips.length > 0 && (
          <>
            <h3 className="font-display text-base font-bold text-muted-foreground">
              Past Trips
            </h3>
            <div className="space-y-3">
              {pastTrips.map((trip) => (
                <CalendarTripCard
                  key={trip.id}
                  trip={trip}
                  isPast
                  onDelete={() => deleteMutation.mutate(trip.id)}
                  onArchive={() => archiveMutation.mutate(trip.id)}
                />
              ))}
            </div>
          </>
        )}

        {/* Trip Planner link */}
        <div className="flex justify-center pt-2">
          <SculptedButton accent="aqua" size="sm" onClick={() => navigate("/app/trips")}>
            <Compass className="h-4 w-4" />
            Open Trip Planner
          </SculptedButton>
        </div>
      </div>
    </ScreenScaffold>
  );
}

function CalendarTripCard({
  trip,
  isPast = false,
  onDelete,
  onArchive,
}: {
  trip: Trip;
  isPast?: boolean;
  onDelete: () => void;
  onArchive: () => void;
}) {
  const dateText = new Date(trip.trip_date).toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "numeric",
  });

  return (
    <SculptedCard accent="citrine" className="p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <p
            className="text-xs font-semibold"
            style={{ color: isPast ? "hsl(var(--muted-foreground))" : `hsl(${CITRINE_HEX})` }}
          >
            {dateText}
          </p>
          <h4 className="mt-0.5 font-display text-sm font-bold text-foreground">
            {trip.name}
          </h4>
          {trip.is_archived && (
            <TagChip accent="hsl(265 47% 67%)" className="mt-1">
              Archived
            </TagChip>
          )}
        </div>
        <div className="flex items-center gap-1.5">
          {!isPast && (
            <button
              onClick={onArchive}
              className="flex h-8 w-8 items-center justify-center rounded-lg bg-muted/30 text-muted-foreground transition-colors hover:bg-muted/50"
              title="Archive trip"
            >
              <Archive className="h-4 w-4" />
            </button>
          )}
          <button
            onClick={onDelete}
            className="flex h-8 w-8 items-center justify-center rounded-lg bg-muted/30 text-muted-foreground transition-colors hover:bg-muted/50"
            title="Delete trip"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>
      {trip.notes && (
        <p className="mt-2 border-t border-border pt-2 text-xs text-[hsl(var(--text-mid))]">
          {trip.notes}
        </p>
      )}
    </SculptedCard>
  );
}
