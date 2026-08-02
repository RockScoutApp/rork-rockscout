import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import {
  Heart,
  Trash2,
  Loader2,
  Search,
  Navigation,
  X,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { getTypeMeta, type MapMarker } from "@/data/locations";

interface FavoriteSpot {
  id: string;
  spot_id: string;
  spot_type: string;
  name: string;
  region: string;
  latitude: number;
  longitude: number;
  created_at: string;
}

export default function FavoriteSpots() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");

  const { data: spots, isLoading } = useQuery<FavoriteSpot[]>({
    queryKey: ["favorite-spots", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_favorite_spots")
        .select("*")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as FavoriteSpot[];
    },
    enabled: !!user,
  });

  const removeSpot = useMutation({
    mutationFn: async (spotId: string) => {
      const { error } = await supabase
        .from("rockscout_favorite_spots")
        .delete()
        .eq("id", spotId);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Removed from favorites");
      queryClient.invalidateQueries({ queryKey: ["favorite-spots"] });
    },
    onError: () => toast.error("Failed to remove"),
  });

  const filtered = (spots ?? []).filter(
    (s) =>
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.region.toLowerCase().includes(search.toLowerCase()),
  );

  const openInMaps = (spot: FavoriteSpot) => {
    const url = `https://www.google.com/maps/search/?api=1&query=${spot.latitude},${spot.longitude}`;
    window.open(url, "_blank", "noopener,noreferrer");
  };

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Heart className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view your favorite spots</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Favorite Spots
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {spots ? `${spots.length} saved locations` : "Loading..."}
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search your favorite spots..."
          className="pl-10"
        />
        {search && (
          <button
            onClick={() => setSearch("")}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : filtered.length > 0 ? (
        <div className="space-y-2">
          {filtered.map((spot) => {
            const meta = getTypeMeta(spot.spot_type === "location" ? "PUBLIC_DIG" : spot.spot_type);
            return (
              <div
                key={spot.id}
                className="group flex items-center gap-3 dark-card sculpted-raised rounded-lg p-3 transition-colors hover:border-primary/40"
              >
                <button
                  onClick={() => navigate(`/app/locations/${spot.spot_id}`)}
                  className="flex min-w-0 flex-1 items-center gap-3 text-left"
                >
                  <div
                    className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-lg"
                    style={{ backgroundColor: `${meta.color}20` }}
                  >
                    {meta.emoji}
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-semibold text-foreground">
                      {spot.name}
                    </p>
                    <p className="truncate text-xs text-muted-foreground">
                      {spot.region}
                    </p>
                  </div>
                </button>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => openInMaps(spot)}
                  className="shrink-0 px-2"
                  aria-label="Get directions"
                >
                  <Navigation className="h-4 w-4" />
                </Button>
                <button
                  onClick={() => removeSpot.mutate(spot.id)}
                  className="shrink-0 rounded-lg p-2 text-muted-foreground transition-colors hover:bg-destructive/10 hover:text-destructive"
                  aria-label="Remove from favorites"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <Heart className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            {search
              ? "No matching spots found."
              : "No favorite spots yet. Bookmark dig sites, state parks, and campgrounds from the map to keep them one tap away."}
          </p>
          <Button
            variant="outline"
            size="sm"
            onClick={() => navigate("/app/map")}
          >
            Explore the map
          </Button>
        </div>
      )}
    </div>
  );
}
