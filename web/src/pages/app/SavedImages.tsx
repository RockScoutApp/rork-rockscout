import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Image as ImageIcon, Trash2, Loader2 } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { OptimizedImage } from "@/components/OptimizedImage";

interface SavedImage {
  id: string;
  user_id: string;
  image_url: string;
  thumbnail_url: string;
  source: string;
  created_at: string;
}

export default function SavedImages() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const { data: images, isLoading } = useQuery<SavedImage[]>({
    queryKey: ["saved-images", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_saved_images")
        .select("*")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as SavedImage[];
    },
    enabled: !!user,
  });

  const deleteImage = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_saved_images")
        .delete()
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Image removed");
      queryClient.invalidateQueries({ queryKey: ["saved-images"] });
    },
    onError: () => toast.error("Failed to remove image"),
  });

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <ImageIcon className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view your saved images</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          My Saved Images
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {images ? `${images.length} images` : "Loading..."}
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : images && images.length > 0 ? (
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6">
          {images.map((img) => (
            <div
              key={img.id}
              className="group relative overflow-hidden dark-card sculpted-raised rounded-lg"
            >
              <div className="relative aspect-square w-full overflow-hidden bg-muted/30">
                <OptimizedImage
                  src={img.image_url}
                  alt="Saved image"
                  loading="lazy"
                  className="h-full w-full object-cover transition-transform group-hover:scale-105"
                />
              </div>
              <button
                onClick={() => deleteImage.mutate(img.id)}
                className="absolute right-2 top-2 rounded-full bg-black/60 p-1.5 text-white opacity-0 backdrop-blur transition-opacity group-hover:opacity-100"
                aria-label="Remove image"
              >
                <Trash2 className="h-3.5 w-3.5" />
              </button>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <ImageIcon className="h-8 w-8 text-muted-foreground" />
          <p className="max-w-sm text-sm text-muted-foreground">
            No saved images yet. Use the Field Camera and choose "Save to My
            Saved Images" to build your gallery.
          </p>
        </div>
      )}
    </div>
  );
}
