import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import {
  Calendar,
  Plus,
  Trash2,
  Edit3,
  X,
  Loader2,
  MapPin,
  Archive,
  CheckCircle2,
  Save,
  GripVertical,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { useOfflineSyncContext } from "@/hooks/useOfflineSyncContext";
import { upsertTrip, deleteTrip as offlineDeleteTrip } from "@/lib/offline-mutations";
import { toast } from "sonner";
import {
  allMapMarkers,
  getTypeMeta,
  getTypeLabel,
  type MapMarker,
} from "@/data/locations";

interface TripStop {
  locationId: string;
  locationName: string;
  order: number;
  latitude: number | null;
  longitude: number | null;
  stopType: string;
}

interface Trip {
  id: string;
  user_id: string;
  name: string;
  trip_date: string;
  stops: TripStop[];
  target_specimens: string[];
  gear_checklist: string[];
  notes: string;
  is_archived: boolean;
  completed_at: string | null;
  created_at: string;
  updated_at: string;
}

interface TripForm {
  name: string;
  trip_date: string;
  notes: string;
  stops: TripStop[];
  target_specimens: string[];
  gear_checklist: string[];
}

const EMPTY_FORM: TripForm = {
  name: "",
  trip_date: new Date().toISOString().split("T")[0],
  notes: "",
  stops: [],
  target_specimens: [],
  gear_checklist: ["Rock hammer", "Safety glasses", "Bucket", "Gloves", "Water", "Snacks"],
};

const DEFAULT_GEAR = [
  "Rock hammer",
  "Safety glasses",
  "Bucket",
  "Gloves",
  "Water",
  "Snacks",
  "Sunscreen",
  "First aid kit",
  "GPS / phone charger",
  "Camera",
];

const formatDate = (iso: string): string => {
  const d = new Date(iso + (iso.length === 10 ? "T00:00:00" : ""));
  return d.toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric",
  });
};

export default function TripPlanner() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { drainNow } = useOfflineSyncContext();
  const [tab, setTab] = useState<"active" | "archived">("active");
  const [showEditor, setShowEditor] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<TripForm>(EMPTY_FORM);
  const [stopSearch, setStopSearch] = useState("");
  const [newTarget, setNewTarget] = useState("");
  const [showGearPicker, setShowGearPicker] = useState(false);

  const { data: trips, isLoading } = useQuery<Trip[]>({
    queryKey: ["trips", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_trips")
        .select("*")
        .eq("user_id", user.id)
        .order("trip_date", { ascending: false });
      if (error) throw error;
      return (data ?? []) as Trip[];
    },
    enabled: !!user,
  });

  const saveTrip = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to save trips");
      if (!form.name.trim()) throw new Error("Give your trip a name");
      const id = editingId || crypto.randomUUID();
      await upsertTrip(
        {
          id,
          user_id: user.id,
          name: form.name,
          trip_date: form.trip_date,
          notes: form.notes,
          stops: form.stops,
          target_specimens: form.target_specimens,
          gear_checklist: form.gear_checklist,
          is_archived: false,
          completed_at: null,
          created_at: new Date().toISOString(),
          updated_at: new Date().toISOString(),
        },
        user.id,
      );
    },
    onSuccess: () => {
      toast.success(editingId ? "Trip updated" : "Trip created");
      queryClient.invalidateQueries({ queryKey: ["trips"] });
      drainNow();
      handleCloseEditor();
    },
    onError: (err) => {
      toast.error(err instanceof Error ? err.message : "Failed to save trip");
    },
  });

  const archiveTrip = useMutation({
    mutationFn: async (trip: Trip) => {
      if (!user) return;
      await upsertTrip(
        {
          ...trip,
          is_archived: true,
          completed_at: new Date().toISOString(),
          updated_at: new Date().toISOString(),
        },
        user.id,
      );
    },
    onSuccess: () => {
      toast.success("Trip archived");
      queryClient.invalidateQueries({ queryKey: ["trips"] });
      drainNow();
    },
    onError: () => toast.error("Failed to archive trip"),
  });

  const deleteTrip = useMutation({
    mutationFn: async (id: string) => {
      await offlineDeleteTrip(id);
    },
    onSuccess: () => {
      toast.success("Trip deleted");
      queryClient.invalidateQueries({ queryKey: ["trips"] });
      drainNow();
    },
    onError: () => toast.error("Failed to delete trip"),
  });

  const handleOpenEditor = (trip?: Trip) => {
    if (trip) {
      setEditingId(trip.id);
      setForm({
        name: trip.name,
        trip_date: trip.trip_date,
        notes: trip.notes,
        stops: trip.stops ?? [],
        target_specimens: trip.target_specimens ?? [],
        gear_checklist: trip.gear_checklist ?? [],
      });
    } else {
      setEditingId(null);
      setForm(EMPTY_FORM);
    }
    setShowEditor(true);
  };

  const handleCloseEditor = () => {
    setShowEditor(false);
    setEditingId(null);
    setForm(EMPTY_FORM);
    setStopSearch("");
    setNewTarget("");
  };

  const addStop = (marker: MapMarker) => {
    setForm((f) => ({
      ...f,
      stops: [
        ...f.stops,
        {
          locationId: marker.id,
          locationName: marker.name,
          order: f.stops.length,
          latitude: marker.latitude,
          longitude: marker.longitude,
          stopType: marker.type,
        },
      ],
    }));
    setStopSearch("");
  };

  const removeStop = (index: number) => {
    setForm((f) => ({
      ...f,
      stops: f.stops
        .filter((_, i) => i !== index)
        .map((s, i) => ({ ...s, order: i })),
    }));
  };

  const moveStop = (index: number, dir: -1 | 1) => {
    setForm((f) => {
      const stops = [...f.stops];
      const newIndex = index + dir;
      if (newIndex < 0 || newIndex >= stops.length) return f;
      [stops[index], stops[newIndex]] = [stops[newIndex], stops[index]];
      return {
        ...f,
        stops: stops.map((s, i) => ({ ...s, order: i })),
      };
    });
  };

  const addTarget = () => {
    if (!newTarget.trim()) return;
    setForm((f) => ({
      ...f,
      target_specimens: [...f.target_specimens, newTarget.trim()],
    }));
    setNewTarget("");
  };

  const removeTarget = (index: number) => {
    setForm((f) => ({
      ...f,
      target_specimens: f.target_specimens.filter((_, i) => i !== index),
    }));
  };

  const toggleGear = (item: string) => {
    setForm((f) => ({
      ...f,
      gear_checklist: f.gear_checklist.includes(item)
        ? f.gear_checklist.filter((g) => g !== item)
        : [...f.gear_checklist, item],
    }));
  };

  const filteredStops = stopSearch.trim()
    ? allMapMarkers
        .filter((m) =>
          m.name.toLowerCase().includes(stopSearch.toLowerCase()) ||
          m.region.toLowerCase().includes(stopSearch.toLowerCase()),
        )
        .slice(0, 15)
    : [];

  const visibleTrips = (trips ?? []).filter((t) =>
    tab === "active" ? !t.is_archived : t.is_archived,
  );

  const openStopInMaps = (stop: TripStop) => {
    if (stop.latitude && stop.longitude) {
      const url = `https://www.google.com/maps/search/?api=1&query=${stop.latitude},${stop.longitude}`;
      window.open(url, "_blank", "noopener,noreferrer");
    }
  };

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Calendar className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to plan trips</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Trip Planner
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            Plan multi-stop rockhounding adventures
          </p>
        </div>
        <Button size="sm" onClick={() => handleOpenEditor()} className="gap-2">
          <Plus className="h-4 w-4" />
          New trip
        </Button>
      </div>

      <Tabs value={tab} onValueChange={(v) => setTab(v as typeof tab)}>
        <TabsList>
          <TabsTrigger value="active" className="gap-2">
            <Calendar className="h-4 w-4" />
            Active
          </TabsTrigger>
          <TabsTrigger value="archived" className="gap-2">
            <Archive className="h-4 w-4" />
            Archived
          </TabsTrigger>
        </TabsList>
      </Tabs>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : visibleTrips.length > 0 ? (
        <div className="space-y-3">
          {visibleTrips.map((trip) => (
            <div
              key={trip.id}
              className="group space-y-3 dark-card sculpted-raised rounded-xl p-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h3 className="font-display text-lg font-semibold text-foreground">
                    {trip.name}
                  </h3>
                  <p className="mt-0.5 flex items-center gap-1.5 text-sm text-muted-foreground">
                    <Calendar className="h-3.5 w-3.5" />
                    {formatDate(trip.trip_date)}
                  </p>
                </div>
                <div className="flex gap-1 opacity-0 transition-opacity group-hover:opacity-100">
                  {!trip.is_archived && (
                    <>
                      <button
                        onClick={() => handleOpenEditor(trip)}
                        className="rounded-lg p-1.5 text-muted-foreground hover:bg-muted hover:text-foreground"
                        aria-label="Edit trip"
                      >
                        <Edit3 className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => archiveTrip.mutate(trip)}
                        className="rounded-lg p-1.5 text-muted-foreground hover:bg-primary/10 hover:text-primary"
                        aria-label="Archive trip"
                      >
                        <CheckCircle2 className="h-4 w-4" />
                      </button>
                    </>
                  )}
                  <button
                    onClick={() => deleteTrip.mutate(trip.id)}
                    className="rounded-lg p-1.5 text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
                    aria-label="Delete trip"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              </div>

              {trip.stops && trip.stops.length > 0 && (
                <div className="space-y-1.5">
                  {trip.stops
                    .slice()
                    .sort((a, b) => a.order - b.order)
                    .map((stop, i) => {
                      const meta = getTypeMeta(stop.stopType);
                      return (
                        <div
                          key={i}
                          className="flex items-center gap-2 rounded-lg bg-muted/30 px-3 py-2"
                        >
                          <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-bold text-primary">
                            {i + 1}
                          </span>
                          <span className="text-base">{meta.emoji}</span>
                          <button
                            onClick={() =>
                              navigate(`/app/locations/${stop.locationId}`)
                            }
                            className="min-w-0 flex-1 truncate text-left text-sm font-medium text-foreground hover:text-primary"
                          >
                            {stop.locationName}
                          </button>
                          {stop.latitude && stop.longitude && (
                            <button
                              onClick={() => openStopInMaps(stop)}
                              className="shrink-0 text-xs text-primary hover:underline"
                            >
                              Directions
                            </button>
                          )}
                        </div>
                      );
                    })}
                </div>
              )}

              {trip.target_specimens && trip.target_specimens.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  <span className="text-xs text-muted-foreground">Hunting for:</span>
                  {trip.target_specimens.map((specimen, i) => (
                    <span
                      key={i}
                      className="rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-medium text-primary"
                    >
                      {specimen}
                    </span>
                  ))}
                </div>
              )}

              {trip.notes && (
                <p className="text-sm leading-relaxed text-muted-foreground">
                  {trip.notes}
                </p>
              )}

              {trip.is_archived && trip.completed_at && (
                <p className="flex items-center gap-1 text-xs text-muted-foreground">
                  <CheckCircle2 className="h-3 w-3" />
                  Completed {formatDate(trip.completed_at)}
                </p>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <Calendar className="h-8 w-8 text-muted-foreground" />
          <p className="max-w-sm text-sm text-muted-foreground">
            {tab === "active"
              ? "No active trips. Plan your next rockhounding adventure with multi-stop routing, target specimens, and a gear checklist."
              : "No archived trips. Completed trips will show here."}
          </p>
          {tab === "active" && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => handleOpenEditor()}
              className="gap-2"
            >
              <Plus className="h-4 w-4" />
              Plan a trip
            </Button>
          )}
        </div>
      )}

      {/* Trip editor dialog */}
      <Dialog open={showEditor} onOpenChange={setShowEditor}>
        <DialogContent aria-describedby={undefined} className="max-h-[90vh] max-w-lg overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {editingId ? "Edit trip" : "New trip"}
            </DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="trip-name">Trip name</Label>
              <Input
                id="trip-name"
                value={form.name}
                onChange={(e) =>
                  setForm((f) => ({ ...f, name: e.target.value }))
                }
                placeholder="e.g. Arkansas crystal weekend"
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="trip-date">Date</Label>
              <Input
                id="trip-date"
                type="date"
                value={form.trip_date}
                onChange={(e) =>
                  setForm((f) => ({ ...f, trip_date: e.target.value }))
                }
              />
            </div>

            {/* Stops */}
            <div className="space-y-2">
              <Label>Stops</Label>
              {form.stops.length > 0 && (
                <div className="space-y-1.5">
                  {form.stops.map((stop, i) => {
                    const meta = getTypeMeta(stop.stopType);
                    return (
                      <div
                        key={i}
                        className="flex items-center gap-2 rounded-lg border border-border bg-muted/30 px-3 py-2"
                      >
                        <GripVertical className="h-4 w-4 shrink-0 text-muted-foreground" />
                        <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-bold text-primary">
                          {i + 1}
                        </span>
                        <span className="text-base">{meta.emoji}</span>
                        <span className="min-w-0 flex-1 truncate text-sm font-medium text-foreground">
                          {stop.locationName}
                        </span>
                        <div className="flex shrink-0 flex-col">
                          <button
                            onClick={() => moveStop(i, -1)}
                            disabled={i === 0}
                            className="text-xs text-muted-foreground disabled:opacity-30"
                          >
                            ▲
                          </button>
                          <button
                            onClick={() => moveStop(i, 1)}
                            disabled={i === form.stops.length - 1}
                            className="text-xs text-muted-foreground disabled:opacity-30"
                          >
                            ▼
                          </button>
                        </div>
                        <button
                          onClick={() => removeStop(i)}
                          className="shrink-0 text-muted-foreground hover:text-destructive"
                        >
                          <X className="h-4 w-4" />
                        </button>
                      </div>
                    );
                  })}
                </div>
              )}

              <div className="relative">
                <Input
                  value={stopSearch}
                  onChange={(e) => setStopSearch(e.target.value)}
                  placeholder="Search dig sites, parks, shops to add..."
                />
                {filteredStops.length > 0 && (
                  <div className="absolute z-10 mt-1 max-h-60 w-full overflow-y-auto dark-card sculpted-raised rounded-lg shadow-lg">
                    {filteredStops.map((marker) => {
                      const meta = getTypeMeta(marker.type);
                      return (
                        <button
                          key={marker.id}
                          onClick={() => addStop(marker)}
                          className="flex w-full items-center gap-2 px-3 py-2 text-left transition-colors hover:bg-muted/50"
                        >
                          <span className="text-base">{meta.emoji}</span>
                          <div className="min-w-0 flex-1">
                            <p className="truncate text-sm font-medium text-foreground">
                              {marker.name}
                            </p>
                            <p className="truncate text-xs text-muted-foreground">
                              {marker.region}
                            </p>
                          </div>
                          <Plus className="h-4 w-4 shrink-0 text-primary" />
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>

            {/* Target specimens */}
            <div className="space-y-2">
              <Label>Target specimens (what you're hunting for)</Label>
              {form.target_specimens.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {form.target_specimens.map((specimen, i) => (
                    <span
                      key={i}
                      className="flex items-center gap-1 rounded-full bg-primary/10 px-2.5 py-1 text-xs font-medium text-primary"
                    >
                      {specimen}
                      <button
                        onClick={() => removeTarget(i)}
                        className="hover:text-primary/70"
                      >
                        <X className="h-3 w-3" />
                      </button>
                    </span>
                  ))}
                </div>
              )}
              <div className="flex gap-2">
                <Input
                  value={newTarget}
                  onChange={(e) => setNewTarget(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      e.preventDefault();
                      addTarget();
                    }
                  }}
                  placeholder="e.g. Quartz, Amethyst, Herkimer Diamond"
                />
                <Button
                  size="sm"
                  variant="outline"
                  onClick={addTarget}
                  disabled={!newTarget.trim()}
                >
                  Add
                </Button>
              </div>
            </div>

            {/* Gear checklist */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label>Gear checklist ({form.gear_checklist.length} items)</Label>
                <button
                  onClick={() => setShowGearPicker((v) => !v)}
                  className="text-xs text-primary hover:underline"
                >
                  {showGearPicker ? "Done" : "Add from list"}
                </button>
              </div>
              {form.gear_checklist.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {form.gear_checklist.map((item, i) => (
                    <span
                      key={i}
                      className="flex items-center gap-1 rounded-full bg-muted px-2.5 py-1 text-xs font-medium text-foreground"
                    >
                      {item}
                      <button
                        onClick={() => toggleGear(item)}
                        className="hover:text-destructive"
                      >
                        <X className="h-3 w-3" />
                      </button>
                    </span>
                  ))}
                </div>
              )}
              {showGearPicker && (
                <div className="flex flex-wrap gap-1.5 rounded-lg border border-border bg-muted/20 p-3">
                  {DEFAULT_GEAR.filter(
                    (g) => !form.gear_checklist.includes(g),
                  ).map((item) => (
                    <button
                      key={item}
                      onClick={() => toggleGear(item)}
                      className="rounded-full border border-border px-2.5 py-1 text-xs text-muted-foreground transition-colors hover:border-primary/40 hover:text-foreground"
                    >
                      + {item}
                    </button>
                  ))}
                </div>
              )}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="trip-notes">Notes</Label>
              <Textarea
                id="trip-notes"
                value={form.notes}
                onChange={(e) =>
                  setForm((f) => ({ ...f, notes: e.target.value }))
                }
                placeholder="Trip plans, meeting times, backup sites, weather notes..."
                rows={3}
              />
            </div>
          </div>

          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={handleCloseEditor}>
              <X className="mr-1 h-4 w-4" />
              Cancel
            </Button>
            <Button
              onClick={() => saveTrip.mutate()}
              disabled={saveTrip.isPending}
              className="gap-2"
            >
              {saveTrip.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Save className="h-4 w-4" />
              )}
              Save trip
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
