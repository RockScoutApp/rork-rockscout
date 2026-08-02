import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Camera,
  Trash2,
  Loader2,
  MapPin,
  Calendar,
  Edit3,
  X,
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
import { OptimizedImage } from "@/components/OptimizedImage";

interface Capture {
  id: string;
  user_id: string;
  specimen_id: string;
  specimen_emoji: string;
  custom_name: string;
  custom_location: string;
  general_info: string;
  image_urls: string[];
  latitude: number | null;
  longitude: number | null;
  in_collection: boolean;
  in_wishlist: boolean;
  created_at: string;
}

const formatDate = (iso: string): string =>
  new Date(iso).toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric",
  });

export default function FieldCaptures() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [editingCapture, setEditingCapture] = useState<Capture | null>(null);
  const [editName, setEditName] = useState("");
  const [editLocation, setEditLocation] = useState("");
  const [editDescription, setEditDescription] = useState("");

  const { data: captures, isLoading } = useQuery<Capture[]>({
    queryKey: ["field-captures", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_captures")
        .select("*")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as Capture[];
    },
    enabled: !!user,
  });

  const deleteCapture = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_captures")
        .delete()
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Capture deleted");
      queryClient.invalidateQueries({ queryKey: ["field-captures"] });
    },
    onError: () => toast.error("Failed to delete capture"),
  });

  const updateCapture = useMutation({
    mutationFn: async () => {
      if (!editingCapture) return;
      const { error } = await supabase
        .from("rockscout_captures")
        .update({
          custom_name: editName,
          custom_location: editLocation,
          general_info: editDescription,
        })
        .eq("id", editingCapture.id);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Capture updated");
      queryClient.invalidateQueries({ queryKey: ["field-captures"] });
      setEditingCapture(null);
    },
    onError: () => toast.error("Failed to update capture"),
  });

  const handleEdit = (capture: Capture) => {
    setEditingCapture(capture);
    setEditName(capture.custom_name);
    setEditLocation(capture.custom_location);
    setEditDescription(capture.general_info);
  };

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Camera className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">
          Sign in to view your field captures
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Field Captures
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {captures ? `${captures.length} captures` : "Loading..."}
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : captures && captures.length > 0 ? (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {captures.map((capture) => (
            <div
              key={capture.id}
              className="group overflow-hidden dark-card sculpted-raised rounded-xl"
            >
              <div className="relative aspect-square w-full overflow-hidden bg-muted/30">
                <OptimizedImage
                  src={capture.image_urls?.[0]}
                  alt={capture.custom_name || "Field capture"}
                  loading="lazy"
                  className="h-full w-full object-cover transition-transform group-hover:scale-105"
                />
                <div className="absolute right-2 top-2 flex gap-1 opacity-0 transition-opacity group-hover:opacity-100">
                  <button
                    onClick={() => handleEdit(capture)}
                    className="rounded-full bg-black/60 p-1.5 text-white backdrop-blur hover:bg-black/80"
                    aria-label="Edit capture"
                  >
                    <Edit3 className="h-3.5 w-3.5" />
                  </button>
                  <button
                    onClick={() => deleteCapture.mutate(capture.id)}
                    className="rounded-full bg-black/60 p-1.5 text-white backdrop-blur hover:bg-destructive"
                    aria-label="Delete capture"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
              </div>
              <div className="p-3">
                <h3 className="truncate text-sm font-semibold text-foreground">
                  {capture.custom_name || "Untitled capture"}
                </h3>
                {capture.custom_location && (
                  <p className="mt-0.5 flex items-center gap-1 truncate text-xs text-muted-foreground">
                    <MapPin className="h-3 w-3" />
                    {capture.custom_location}
                  </p>
                )}
                <p className="mt-0.5 flex items-center gap-1 text-xs text-muted-foreground">
                  <Calendar className="h-3 w-3" />
                  {formatDate(capture.created_at)}
                </p>
                {capture.general_info && (
                  <p className="mt-1 line-clamp-2 text-xs text-muted-foreground">
                    {capture.general_info}
                  </p>
                )}
                {(capture.in_collection || capture.in_wishlist) && (
                  <div className="mt-2 flex gap-1">
                    {capture.in_collection && (
                      <span className="rounded bg-primary/15 px-1.5 py-0.5 text-[10px] font-medium text-primary">
                        In Collection
                      </span>
                    )}
                    {capture.in_wishlist && (
                      <span className="rounded bg-primary/15 px-1.5 py-0.5 text-[10px] font-medium text-primary">
                        In Wishlist
                      </span>
                    )}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <Camera className="h-8 w-8 text-muted-foreground" />
          <p className="max-w-sm text-sm text-muted-foreground">
            No field captures yet. Use the Field Camera to capture and save
            photos from your rockhounding trips.
          </p>
        </div>
      )}

      {/* Edit dialog */}
      <Dialog
        open={!!editingCapture}
        onOpenChange={(open) => !open && setEditingCapture(null)}
      >
        <DialogContent aria-describedby={undefined} className="max-w-md">
          <DialogHeader>
            <DialogTitle>Edit capture</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="edit-name">Name</Label>
              <Input
                id="edit-name"
                value={editName}
                onChange={(e) => setEditName(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="edit-location">Location</Label>
              <Input
                id="edit-location"
                value={editLocation}
                onChange={(e) => setEditLocation(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="edit-description">Description</Label>
              <Textarea
                id="edit-description"
                value={editDescription}
                onChange={(e) => setEditDescription(e.target.value)}
                rows={3}
              />
            </div>
          </div>
          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={() => setEditingCapture(null)}>
              <X className="mr-1 h-4 w-4" />
              Cancel
            </Button>
            <Button
              onClick={() => updateCapture.mutate()}
              disabled={updateCapture.isPending}
              className="gap-2"
            >
              {updateCapture.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Save className="h-4 w-4" />
              )}
              Save
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
