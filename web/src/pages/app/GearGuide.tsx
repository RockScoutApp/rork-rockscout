import { useState, useMemo } from "react";
import { ExternalLink, Search, X, Compass, Star } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { gearItems, GEAR_CATEGORIES } from "@/data/gear";
import { recordAffiliateClick } from "@/lib/affiliate-tracker";

export default function GearGuide() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("all");

  const filtered = useMemo(() => {
    let items = gearItems;
    const cat = GEAR_CATEGORIES.find((c) => c.id === category);
    if (cat && cat.id !== "all" && "itemIds" in cat) {
      const idSet = new Set<string>(cat.itemIds);
      items = items.filter((i) => idSet.has(i.id));
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      items = items.filter(
        (i) =>
          i.name.toLowerCase().includes(q) ||
          i.description.toLowerCase().includes(q),
      );
    }
    return items;
  }, [search, category]);

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Gear Guide
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {gearItems.length} recommended tools for rockhounds
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search gear..."
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

      <div className="flex flex-wrap gap-2">
        {GEAR_CATEGORIES.map((cat) => (
          <button
            key={cat.id}
            onClick={() => setCategory(cat.id)}
            className={`rounded-full px-3 py-1.5 text-xs font-medium transition-colors ${
              category === cat.id
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {cat.label}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
        {filtered.map((item) => (
          <a
            key={item.id}
            href={item.url}
            target="_blank"
            rel="noopener noreferrer sponsored"
            onClick={() => recordAffiliateClick(item.id, item.name)}
            className={`group flex flex-col gap-2 rounded-xl border bg-card p-4 transition-all hover:border-primary/40 ${
              item.topPick ? "border-amber-500/40 ring-1 ring-amber-500/20" : "border-border"
            }`}
          >
            <div className="flex items-start gap-3">
              <span className="text-2xl">{item.emoji}</span>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-1.5">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    {item.name}
                  </h3>
                  {item.topPick && (
                    <span className="inline-flex shrink-0 items-center gap-0.5 rounded-full bg-amber-500/15 px-1.5 py-0.5 text-[10px] font-bold uppercase tracking-wide text-amber-500 ring-1 ring-amber-500/40">
                      <Star className="h-2.5 w-2.5 fill-amber-500 text-amber-500" />
                      Top Pick
                    </span>
                  )}
                </div>
                <p className="mt-0.5 text-xs text-primary">{item.priceBand}</p>
              </div>
              <ExternalLink className="h-4 w-4 shrink-0 text-muted-foreground transition-colors group-hover:text-primary" />
            </div>
            <p className="text-xs leading-relaxed text-muted-foreground">
              {item.description}
            </p>
          </a>
        ))}
      </div>

      {filtered.length === 0 && (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Compass className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No gear found. Try a different search or category.
          </p>
        </div>
      )}
    </div>
  );
}
