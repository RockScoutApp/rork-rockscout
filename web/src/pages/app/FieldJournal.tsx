import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ClipboardList,
  Plus,
  Trash2,
  Edit3,
  X,
  Loader2,
  Calendar,
  MapPin,
  CloudSun,
  Save,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

interface JournalEntry {
  id: string;
  user_id: string;
  entry_date: string;
  location: string;
  dig_site_id: string | null;
  weather_summary: string;
  notes: string;
  photo_urls: string[];
  created_at: string;
  updated_at: string;
}

interface EntryForm {
  entry_date: string;
  location: string;
  weather_summary: string;
  notes: string;
}

const EMPTY_FORM: EntryForm = {
  entry_date: new Date().toISOString().split("T")[0],
  location: "",
  weather_summary: "",
  notes: "",
};

const formatDate = (iso: string): string => {
  const d = new Date(iso + (iso.length === 10 ? "T00:00:00" : ""));
  return d.toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric",
  });
};

export default function FieldJournal() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [showEditor, setShowEditor] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<EntryForm>(EMPTY_FORM);

  const { data: entries, isLoading } = useQuery<JournalEntry[]>({
    queryKey: ["field-journal", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_field_journal")
        .select("*")
        .eq("user_id", user.id)
        .order("entry_date", { ascending: false });
      if (error) throw error;
      return (data ?? []) as JournalEntry[];
    },
    enabled: !!user,
  });

  const saveEntry = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to save journal entries");
      if (editingId) {
        const { error } = await supabase
          .from("rockscout_field_journal")
          .update({
            entry_date: form.entry_date,
            location: form.location,
            weather_summary: form.weather_summary,
            notes: form.notes,
            updated_at: new Date().toISOString(),
          })
          .eq("id", editingId);
        if (error) throw error;
      } else {
        const { error } = await supabase
          .from("rockscout_field_journal")
          .insert({
            user_id: user.id,
            entry_date: form.entry_date,
            location: form.location,
            weather_summary: form.weather_summary,
            notes: form.notes,
          });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      toast.success(editingId ? "Entry updated" : "Journal entry saved");
      queryClient.invalidateQueries({ queryKey: ["field-journal"] });
      handleCloseEditor();
    },
    onError: (err) => {
      toast.error(err instanceof Error ? err.message : "Failed to save entry");
    },
  });

  const deleteEntry = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_field_journal")
        .delete()
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Entry deleted");
      queryClient.invalidateQueries({ queryKey: ["field-journal"] });
    },
    onError: () => toast.error("Failed to delete entry"),
  });

  const handleOpenEditor = (entry?: JournalEntry) => {
    if (entry) {
      setEditingId(entry.id);
      setForm({
        entry_date: entry.entry_date,
        location: entry.location,
        weather_summary: entry.weather_summary,
        notes: entry.notes,
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
  };

  const handleSave = () => {
    if (!form.location.trim() && !form.notes.trim()) {
      toast.error("Add a location or some notes to save an entry");
      return;
    }
    saveEntry.mutate();
  };

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <ClipboardList className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">
          Sign in to view your field journal
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Field Journal
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            {entries ? `${entries.length} entries` : "Loading..."}
          </p>
        </div>
        <Button
          size="sm"
          onClick={() => handleOpenEditor()}
          className="gap-2"
        >
          <Plus className="h-4 w-4" />
          New entry
        </Button>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : entries && entries.length > 0 ? (
        <div className="space-y-3">
          {entries.map((entry) => (
            <div
              key={entry.id}
              className="group space-y-2 rounded-xl border border-border bg-card p-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-2 text-sm font-medium text-foreground">
                  <Calendar className="h-4 w-4 text-primary" />
                  {formatDate(entry.entry_date)}
                </div>
                <div className="flex gap-1 opacity-0 transition-opacity group-hover:opacity-100">
                  <button
                    onClick={() => handleOpenEditor(entry)}
                    className="rounded-lg p-1.5 text-muted-foreground hover:bg-muted hover:text-foreground"
                    aria-label="Edit entry"
                  >
                    <Edit3 className="h-3.5 w-3.5" />
                  </button>
                  <button
                    onClick={() => deleteEntry.mutate(entry.id)}
                    className="rounded-lg p-1.5 text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
                    aria-label="Delete entry"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
              </div>

              {entry.location && (
                <p className="flex items-center gap-1.5 text-sm text-muted-foreground">
                  <MapPin className="h-3.5 w-3.5" />
                  {entry.location}
                </p>
              )}

              {entry.weather_summary && (
                <p className="flex items-center gap-1.5 text-xs text-muted-foreground">
                  <CloudSun className="h-3.5 w-3.5" />
                  {entry.weather_summary}
                </p>
              )}

              {entry.notes && (
                <p className="whitespace-pre-wrap text-sm leading-relaxed text-foreground/80">
                  {entry.notes}
                </p>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <ClipboardList className="h-8 w-8 text-muted-foreground" />
          <p className="max-w-sm text-sm text-muted-foreground">
            No journal entries yet. Log your field trips and finds — location,
            weather, notes, and the story of the day.
          </p>
          <Button
            variant="outline"
            size="sm"
            onClick={() => handleOpenEditor()}
            className="gap-2"
          >
            <Plus className="h-4 w-4" />
            Write your first entry
          </Button>
        </div>
      )}

      {/* Editor dialog */}
      <Dialog open={showEditor} onOpenChange={setShowEditor}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {editingId ? "Edit entry" : "New journal entry"}
            </DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="entry-date">Date</Label>
              <Input
                id="entry-date"
                type="date"
                value={form.entry_date}
                onChange={(e) =>
                  setForm((f) => ({ ...f, entry_date: e.target.value }))
                }
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="entry-location">Location</Label>
              <Input
                id="entry-location"
                value={form.location}
                onChange={(e) =>
                  setForm((f) => ({ ...f, location: e.target.value }))
                }
                placeholder="e.g. Crater of Diamonds, Murfreesboro, AR"
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="entry-weather">Weather (optional)</Label>
              <Input
                id="entry-weather"
                value={form.weather_summary}
                onChange={(e) =>
                  setForm((f) => ({ ...f, weather_summary: e.target.value }))
                }
                placeholder="e.g. Sunny, 75°F, light breeze"
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="entry-notes">Notes</Label>
              <Textarea
                id="entry-notes"
                value={form.notes}
                onChange={(e) =>
                  setForm((f) => ({ ...f, notes: e.target.value }))
                }
                placeholder="What did you find? How was the trip? Any stories from the day?"
                rows={5}
              />
            </div>
          </div>

          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={handleCloseEditor}>
              <X className="mr-1 h-4 w-4" />
              Cancel
            </Button>
            <Button
              onClick={handleSave}
              disabled={saveEntry.isPending}
              className="gap-2"
            >
              {saveEntry.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Save className="h-4 w-4" />
              )}
              Save entry
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
