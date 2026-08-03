import { useState, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { Loader2, Gem, Search, X } from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface CollectionItem {
  id: string;
  specimen_id: string;
  created_at: string;
}

interface UserProfile {
  id: string;
  display_name: string;
}

interface Specimen {
  id: string;
  name: string;
  category: string;
  image_url: string | null;
}

const CATEGORIES = ["All", "Igneous", "Sedimentary", "Metamorphic", "Mineral", "Fossil", "Crystal"];

export default function UserCollection() {
  const { id: userId, mode } = useParams<{ id: string; mode: string }>();
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("All");

  const isWishlist = mode === "wishlist";

  const { data: profile } = useQuery<UserProfile>({
    queryKey: ["user-profile", userId],
    queryFn: async () => {
      if (!userId) throw new Error("No user ID");
      const { data, error } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name")
        .eq("id", userId)
        .maybeSingle();
      if (error) throw error;
      return data as UserProfile;
    },
    enabled: !!userId,
  });

  const { data: items, isLoading } = useQuery<CollectionItem[]>({
    queryKey: ["user-collection", userId, isWishlist],
    queryFn: async () => {
      if (!userId) return [];
      const table = isWishlist ? "rockscout_wishlist" : "rockscout_collection";
      const { data, error } = await supabase
        .from(table)
        .select("*")
        .eq("user_id", userId)
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as CollectionItem[];
    },
    enabled: !!userId,
  });

  const specimenIds = useMemo(() => items?.map((i) => i.specimen_id) ?? [], [items]);

  const { data: specimens } = useQuery<Specimen[]>({
    queryKey: ["specimens-by-ids", specimenIds],
    queryFn: async () => {
      if (specimenIds.length === 0) return [];
      const { data, error } = await supabase
        .from("specimen_catalog")
        .select("id, name, category, image_url")
        .in("id", specimenIds);
      if (error) throw error;
      return (data ?? []) as Specimen[];
    },
    enabled: specimenIds.length > 0,
  });

  const filteredSpecimens = useMemo(() => {
    if (!specimens) return [];
    let result = specimens;
    if (filter !== "All") {
      result = result.filter((s) => s.category === filter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter((s) => s.name.toLowerCase().includes(q));
    }
    return result;
  }, [specimens, filter, search]);

  const title = `${profile?.display_name ?? "RockScout"}'s ${isWishlist ? "Wishlist" : "Rocks"}`;

  return (
    <ScreenScaffold title={title}>
      <div className="space-y-4 px-4 pb-8">
        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : !items || items.length === 0 ? (
          <div className="flex flex-col items-center justify-center gap-4 px-4 py-16 text-center">
            <div className="flex h-24 w-24 items-center justify-center rounded-full bg-primary/10">
              <span className="text-4xl">{isWishlist ? "🔖" : "🪨"}</span>
            </div>
            <h3 className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
              {isWishlist ? "No wishlist items" : "No rocks yet"}
            </h3>
            <p className="max-w-xs text-sm text-[hsl(var(--text-mid))]">
              {profile?.display_name ?? "This hunter"} hasn't added any specimens to their
              {" "}{isWishlist ? "wishlist" : "collection"} yet.
            </p>
          </div>
        ) : (
          <>
            {/* Count + search */}
            <p className="text-xs font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
              {filteredSpecimens.length} specimen{filteredSpecimens.length !== 1 ? "s" : ""}
            </p>

            <div className="relative">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search specimens..."
                className="pl-9"
              />
              {search && (
                <button
                  onClick={() => setSearch("")}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground"
                >
                  <X className="h-4 w-4" />
                </button>
              )}
            </div>

            {/* Category filter */}
            <div className="flex flex-wrap gap-2">
              {CATEGORIES.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setFilter(cat)}
                  className={`rounded-full px-3 py-1 text-xs font-bold transition-all ${
                    filter === cat
                      ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                      : "border border-border text-muted-foreground hover:text-foreground"
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>

            {/* Specimen list */}
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              {filteredSpecimens.map((spec) => (
                <SculptedCard
                  key={spec.id}
                  accent="aqua"
                  interactive
                  className="overflow-hidden p-0"
                  onClick={() => navigate(`/app/specimens/${spec.id}`)}
                >
                  <div className="flex items-center gap-3 p-3">
                    {spec.image_url ? (
                      <img
                        src={spec.image_url}
                        alt={spec.name}
                        className="h-16 w-16 shrink-0 rounded-lg object-cover"
                        loading="lazy"
                      />
                    ) : (
                      <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-lg bg-muted/30">
                        <Gem className="h-6 w-6 text-muted-foreground" />
                      </div>
                    )}
                    <div className="min-w-0 flex-1">
                      <h4 className="truncate font-display text-sm font-bold text-foreground">
                        {spec.name}
                      </h4>
                      {spec.category && (
                        <TagChip accent={`hsl(${AQUA_HEX})`} className="mt-1">
                          {spec.category}
                        </TagChip>
                      )}
                    </div>
                  </div>
                </SculptedCard>
              ))}
            </div>

            {filteredSpecimens.length === 0 && (
              <p className="py-8 text-center text-sm text-muted-foreground">
                No specimens match your search
              </p>
            )}
          </>
        )}
      </div>
    </ScreenScaffold>
  );
}
