import { useState, useMemo, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { Search, Filter, Loader2, X } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { cn } from "@/lib/utils";

interface Specimen {
  id: string;
  name: string;
  category: string;
  tagline: string;
  colors: string;
  hardness: string;
  luster: string;
  rarity: string;
  image_url: string;
}

const PAGE_SIZE = 24;

export default function Specimens() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("");
  const [rarity, setRarity] = useState<string>("");
  const [page, setPage] = useState(0);
  const [showFilters, setShowFilters] = useState(false);

  const { data, isLoading } = useQuery<{ items: Specimen[]; total: number }>({
    queryKey: ["specimens", search, category, rarity, page],
    queryFn: async () => {
      let query = supabase
        .from("specimen_catalog")
        .select("id, name, category, tagline, colors, hardness, luster, rarity, image_url", { count: "exact" });

      if (search.trim()) {
        query = query.or(`name.ilike.%${search}%,category.ilike.%${search}%,tagline.ilike.%${search}%`);
      }
      if (category) query = query.ilike("category", `%${category}%`);
      if (rarity) query = query.eq("rarity", rarity);

      query = query.range(page * PAGE_SIZE, (page + 1) * PAGE_SIZE - 1).order("name");

      const { data: items, error, count } = await query;
      if (error) throw error;
      return { items: (items ?? []) as Specimen[], total: count ?? 0 };
    },
  });

  const categories = useMemo(
    () => ["Mineral", "Silicate", "Oxide", "Sulfide", "Carbonate", "Igneous", "Sedimentary", "Metamorphic", "Fossil"],
    [],
  );
  const rarities = useMemo(() => ["Common", "Uncommon", "Rare"], []);

  const clearFilters = useCallback(() => {
    setCategory("");
    setRarity("");
    setSearch("");
    setPage(0);
  }, []);

  const hasFilters = Boolean(search || category || rarity);

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Specimen Database
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            {data ? `${data.total} specimens` : "Loading..."}
          </p>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={() => setShowFilters((v) => !v)}
          className="gap-2"
        >
          <Filter className="h-4 w-4" />
          Filters
        </Button>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
          placeholder="Search rocks, minerals, fossils..."
          className="pl-10"
        />
      </div>

      {showFilters && (
        <div className="space-y-3 rounded-xl border border-border bg-card p-4">
          <div className="flex flex-wrap gap-2">
            {categories.map((cat) => (
              <button
                key={cat}
                onClick={() => {
                  setCategory(category === cat ? "" : cat);
                  setPage(0);
                }}
                className={cn(
                  "rounded-full px-3 py-1 text-xs font-medium transition-colors",
                  category === cat
                    ? "bg-primary text-primary-foreground"
                    : "bg-muted text-muted-foreground hover:bg-muted/70",
                )}
              >
                {cat}
              </button>
            ))}
          </div>
          <div className="flex flex-wrap gap-2">
            {rarities.map((r) => (
              <button
                key={r}
                onClick={() => {
                  setRarity(rarity === r ? "" : r);
                  setPage(0);
                }}
                className={cn(
                  "rounded-full px-3 py-1 text-xs font-medium transition-colors",
                  rarity === r
                    ? "bg-primary text-primary-foreground"
                    : "bg-muted text-muted-foreground hover:bg-muted/70",
                )}
              >
                {r}
              </button>
            ))}
          </div>
          {hasFilters && (
            <button
              onClick={clearFilters}
              className="flex items-center gap-1 text-xs text-primary hover:underline"
            >
              <X className="h-3 w-3" />
              Clear all filters
            </button>
          )}
        </div>
      )}

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : data && data.items.length > 0 ? (
        <>
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4">
            {data.items.map((specimen) => (
              <button
                key={specimen.id}
                onClick={() => navigate(`/app/specimens/${specimen.id}`)}
                className="group flex flex-col overflow-hidden rounded-lg border border-border bg-card text-left transition-all hover:border-primary/40"
              >
                <div className="aspect-square w-full overflow-hidden bg-muted/30">
                  {specimen.image_url && (
                    <img
                      src={specimen.image_url}
                      alt={specimen.name}
                      loading="lazy"
                      className="h-full w-full object-cover transition-transform group-hover:scale-105"
                    />
                  )}
                </div>
                <div className="p-2.5">
                  <h3 className="truncate text-sm font-semibold text-foreground">
                    {specimen.name}
                  </h3>
                  <p className="mt-0.5 truncate text-xs text-muted-foreground">
                    {specimen.category}
                  </p>
                </div>
              </button>
            ))}
          </div>

          {data.total > PAGE_SIZE && (
            <div className="flex items-center justify-center gap-3 pt-4">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                Previous
              </Button>
              <span className="text-sm text-muted-foreground">
                Page {page + 1} of {Math.ceil(data.total / PAGE_SIZE)}
              </span>
              <Button
                variant="outline"
                size="sm"
                disabled={(page + 1) * PAGE_SIZE >= data.total}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="rounded-lg border border-border bg-card p-8 text-center text-muted-foreground">
          No specimens found. Try adjusting your search or filters.
        </div>
      )}
    </div>
  );
}
