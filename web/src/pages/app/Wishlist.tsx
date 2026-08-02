import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { Heart, Trash2, Loader2, Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { OptimizedImage } from "@/components/OptimizedImage";

interface WishlistItem {
  id: string;
  specimen_id: string;
  created_at: string;
  specimen: {
    name: string;
    category: string;
    image_url: string;
  }[];
}

export default function Wishlist() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");

  const { data: items, isLoading } = useQuery<WishlistItem[]>({
    queryKey: ["wishlist", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_wishlist")
        .select("id, specimen_id, created_at, specimen:specimen_catalog(name, category, image_url)")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as Array<Omit<WishlistItem, "specimen"> & { specimen: WishlistItem["specimen"] }>;
      return rows.map((r) => ({
        ...r,
        specimen: Array.isArray(r.specimen) ? r.specimen : [r.specimen],
      })) as WishlistItem[];
    },
    enabled: !!user,
  });

  const removeItem = useMutation({
    mutationFn: async (itemId: string) => {
      const { error } = await supabase.from("rockscout_wishlist").delete().eq("id", itemId);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Removed from wishlist");
      queryClient.invalidateQueries({ queryKey: ["wishlist"] });
    },
    onError: () => toast.error("Failed to remove"),
  });

  const filtered = (items ?? []).filter((item) =>
    item.specimen?.[0]?.name?.toLowerCase().includes(search.toLowerCase()),
  );

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Heart className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view your wishlist</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Wishlist
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Specimens you're hunting for next
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search your wishlist..."
          className="pl-10"
        />
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : filtered.length > 0 ? (
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6">
          {filtered.map((item) => (
            <div
              key={item.id}
              className="group relative flex flex-col overflow-hidden rounded-lg border border-border bg-card"
            >
              <button
                onClick={() => navigate(`/app/specimens/${item.specimen_id}`)}
                className="flex flex-1 flex-col text-left"
              >
                <div className="relative aspect-square w-full overflow-hidden bg-muted/30">
                  <OptimizedImage
                    src={item.specimen?.[0]?.image_url}
                    alt={item.specimen?.[0]?.name || ""}
                    loading="lazy"
                    className="h-full w-full object-cover transition-transform group-hover:scale-105"
                  />
                </div>
                <div className="p-2.5">
                  <h3 className="truncate text-sm font-semibold text-foreground">
                    {item.specimen?.[0]?.name}
                  </h3>
                  <p className="mt-0.5 truncate text-xs text-muted-foreground">
                    {item.specimen?.[0]?.category}
                  </p>
                </div>
              </button>
              <button
                onClick={() => removeItem.mutate(item.id)}
                className="absolute right-2 top-2 rounded-full bg-black/60 p-1.5 text-white opacity-0 backdrop-blur transition-opacity group-hover:opacity-100"
                aria-label="Remove from wishlist"
              >
                <Trash2 className="h-3.5 w-3.5" />
              </button>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Heart className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            Your wishlist is empty. Add specimens you're hunting for next.
          </p>
          <Button variant="outline" size="sm" onClick={() => navigate("/app/specimens")}>
            Browse specimens
          </Button>
        </div>
      )}
    </div>
  );
}
