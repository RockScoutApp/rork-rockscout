import { useState, useMemo } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Heart, Gem, Trash2, Loader2, Search, BarChart3 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { OptimizedImage } from "@/components/OptimizedImage";
import { SculptedCard } from "@/components/sculpted";

interface CollectionItem {
  id: string;
  specimen_id: string;
  created_at: string;
  notes: string;
  specimen: {
    name: string;
    category: string;
    rarity: string;
    colors: string;
    image_url: string;
  }[];
}

const ROCK_CLASS_COLORS: Record<string, string> = {
  Igneous: "4 70% 55%",
  Sedimentary: "41 53% 64%",
  Metamorphic: "200 67% 57%",
  Mineral: "36 80% 58%",
  Crystal: "265 47% 67%",
  Fossil: "30 40% 55%",
};

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const AMETHYST_HEX = "265 47% 67%";

export default function Collection() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState<"collection" | "wishlist">("collection");
  const [search, setSearch] = useState("");

  const table = tab === "collection" ? "rockscout_collection" : "rockscout_wishlist";

  const { data: items, isLoading } = useQuery<CollectionItem[]>({
    queryKey: [tab, user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from(table)
        .select("id, specimen_id, created_at, notes, specimen:specimen_catalog(name, category, rarity, colors, image_url)")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      // Supabase joins return the joined row as an array; flatten it.
      const rows = (data ?? []) as Array<Omit<CollectionItem, "specimen"> & { specimen: CollectionItem["specimen"] }>;
      return rows.map((r) => ({
        ...r,
        specimen: Array.isArray(r.specimen) ? r.specimen[0] : r.specimen,
      })) as unknown as CollectionItem[];
    },
    enabled: !!user,
  });

  const removeItem = useMutation({
    mutationFn: async (itemId: string) => {
      const { error } = await supabase.from(table).delete().eq("id", itemId);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Removed");
      queryClient.invalidateQueries({ queryKey: [tab] });
    },
    onError: () => toast.error("Failed to remove"),
  });

  const filtered = (items ?? []).filter((item) =>
    item.specimen?.[0]?.name?.toLowerCase().includes(search.toLowerCase()),
  );

  const [showStats, setShowStats] = useState(false);

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Gem className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view your collection</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          My Collection
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Your saved specimens and wishlist
        </p>
      </div>

      <div className="flex items-center gap-3">
        <Tabs value={tab} onValueChange={(v) => setTab(v as typeof tab)}>
          <TabsList>
            <TabsTrigger value="collection" className="gap-2">
              <Gem className="h-4 w-4" />
              Collection
            </TabsTrigger>
            <TabsTrigger value="wishlist" className="gap-2">
              <Heart className="h-4 w-4" />
              Wishlist
            </TabsTrigger>
          </TabsList>
        </Tabs>
        {tab === "collection" && (items ?? []).length > 0 && (
          <Button
            variant="ghost"
            size="sm"
            className="ml-auto gap-2"
            onClick={() => setShowStats((s) => !s)}
          >
            <BarChart3 className="h-4 w-4" />
            {showStats ? "Hide Stats" : "Statistics"}
          </Button>
        )}
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search your collection..."
          className="pl-10"
        />
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : (
        <>
        {showStats && tab === "collection" && (items ?? []).length > 0 && (
          <CollectionStatisticsDashboard items={items ?? []} />
        )}
        {filtered.length > 0 ? (
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6">
          {filtered.map((item) => (
            <div
              key={item.id}
              className="group relative flex flex-col overflow-hidden dark-card sculpted-raised rounded-lg"
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
                aria-label="Remove"
              >
                <Trash2 className="h-3.5 w-3.5" />
              </button>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          {tab === "collection" ? (
            <Gem className="h-8 w-8 text-muted-foreground" />
          ) : (
            <Heart className="h-8 w-8 text-muted-foreground" />
          )}
          <p className="text-sm text-muted-foreground">
            {tab === "collection"
              ? "Your collection is empty. Identify a rock or browse the database to get started."
              : "Your wishlist is empty. Add specimens you're hunting for next."}
          </p>
          <Button
            variant="outline"
            size="sm"
            onClick={() => navigate("/app/specimens")}
          >
            Browse specimens
          </Button>
        </div>
      )}
        </>
      )}
    </div>
  );
}

/**
 * Collection statistics dashboard — mirrors the Android CollectionStatisticsDashboard.
 * Shows breakdowns by rock class, rarity, color, and date added.
 */
function CollectionStatisticsDashboard({ items }: { items: CollectionItem[] }) {
  const stats = useMemo(() => {
    const specimens = items.map((i) => i.specimen?.[0]).filter(Boolean) as {
      name: string; category: string; rarity: string; colors: string; image_url: string;
    }[];

    const byClass = new Map<string, number>();
    for (const s of specimens) {
      const cat = s.category || "Unknown";
      byClass.set(cat, (byClass.get(cat) ?? 0) + 1);
    }

    const byRarity = new Map<string, number>();
    for (const s of specimens) {
      const r = s.rarity || "Unknown";
      byRarity.set(r, (byRarity.get(r) ?? 0) + 1);
    }

    const byColor = new Map<string, number>();
    for (const s of specimens) {
      const colors = (s.colors || "").split(/[,/\s]+/).filter(Boolean);
      for (const c of colors) {
        const lc = c.toLowerCase();
        byColor.set(lc, (byColor.get(lc) ?? 0) + 1);
      }
    }

    const byMonth = new Map<string, number>();
    for (const item of items) {
      const d = new Date(item.created_at);
      const key = d.toLocaleDateString("en-US", { month: "short", year: "numeric" });
      byMonth.set(key, (byMonth.get(key) ?? 0) + 1);
    }

    return {
      total: items.length,
      byClass: [...byClass.entries()].sort((a, b) => b[1] - a[1]),
      byRarity: [...byRarity.entries()].sort((a, b) => b[1] - a[1]),
      byColor: [...byColor.entries()].sort((a, b) => b[1] - a[1]).slice(0, 8),
      byMonth: [...byMonth.entries()].sort((a, b) => a[0].localeCompare(b[0])).slice(-6),
    };
  }, [items]);

  if (stats.total === 0) return null;

  return (
    <div className="space-y-4">
      <SculptedCard accent="citrine" className="flex items-center gap-4 p-5" glowing>
        <div
          className="glowing-border flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl font-display text-2xl font-bold"
          style={{ ["--glow-color" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})`, backgroundColor: `hsl(${CITRINE_HEX} / 0.15)` }}
        >
          {stats.total}
        </div>
        <div>
          <h3 className="font-display text-base font-bold text-foreground">Total specimens</h3>
          <p className="text-sm text-[hsl(var(--text-mid))]">in your collection cabinet</p>
        </div>
      </SculptedCard>

      {stats.byClass.length > 0 && (
        <StatSection title="BY TYPE" items={stats.byClass} accentHsl={AQUA_HEX} colorMap={ROCK_CLASS_COLORS} />
      )}
      {stats.byRarity.length > 0 && (
        <StatSection title="BY RARITY" items={stats.byRarity} accentHsl={AMETHYST_HEX} />
      )}
      {stats.byColor.length > 0 && (
        <StatSection title="BY COLOR" items={stats.byColor} accentHsl={CITRINE_HEX} capitalize />
      )}
      {stats.byMonth.length > 0 && (
        <StatSection title="ADDED OVER TIME" items={stats.byMonth} accentHsl={CITRINE_HEX} />
      )}
    </div>
  );
}

function StatSection({
  title,
  items,
  accentHsl,
  colorMap,
  capitalize,
}: {
  title: string;
  items: [string, number][];
  accentHsl: string;
  colorMap?: Record<string, string>;
  capitalize?: boolean;
}) {
  const maxCount = Math.max(...items.map(([, c]) => c), 1);
  return (
    <div>
      <h4 className="mb-2 text-xs font-extrabold tracking-wider" style={{ color: `hsl(${accentHsl})` }}>
        {title}
      </h4>
      <SculptedCard accent="aqua" className="space-y-2 p-4">
        {items.map(([label, count]) => {
          const pct = count / maxCount;
          const colorHsl = colorMap?.[label] ?? accentHsl;
          const displayLabel = capitalize ? label.charAt(0).toUpperCase() + label.slice(1) : label;
          return (
            <div key={label} className="flex items-center gap-3">
              <span className="w-28 shrink-0 truncate text-sm font-medium text-foreground">
                {displayLabel}
              </span>
              <div
                className="h-5 flex-1 overflow-hidden rounded-full"
                style={{ backgroundColor: `hsl(${colorHsl} / 0.12)` }}
              >
                <div
                  className="h-full rounded-full transition-all"
                  style={{ width: `${pct * 100}%`, backgroundColor: `hsl(${colorHsl} / 0.5)` }}
                />
              </div>
              <span className="w-8 shrink-0 text-right font-display text-sm font-bold" style={{ color: `hsl(${colorHsl})` }}>
                {count}
              </span>
            </div>
          );
        })}
      </SculptedCard>
    </div>
  );
}
