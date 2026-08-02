import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { Search as SearchIcon, X, Bone, MapPin, BookOpen } from "lucide-react";
import { Input } from "@/components/ui/input";
import { artifacts } from "@/data/artifacts";
import { digSites, blmDigSites, stateParks, getTypeLabel, getTypeMeta } from "@/data/locations";
import { OptimizedImage } from "@/components/OptimizedImage";

interface SearchResult {
  id: string;
  name: string;
  type: "specimen" | "artifact" | "location";
  subtitle: string;
  imageUrl?: string;
  emoji?: string;
  route: string;
}

export default function Search() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");

  const results = useMemo<SearchResult[]>(() => {
    if (!query.trim()) return [];
    const q = query.toLowerCase();

    const artifactResults: SearchResult[] = artifacts
      .filter(
        (a) =>
          a.name.toLowerCase().includes(q) ||
          a.tagline.toLowerCase().includes(q) ||
          a.family.toLowerCase().includes(q) ||
          a.tribe.toLowerCase().includes(q),
      )
      .slice(0, 20)
      .map((a) => ({
        id: a.id,
        name: a.name,
        type: "artifact",
        subtitle: `${a.family} · ${a.timePeriod}`,
        imageUrl: a.imageUrl,
        route: `/app/artifacts/${a.id}`,
      }));

    const locationResults: SearchResult[] = [
      ...digSites,
      ...blmDigSites.map((s) => ({ ...s, type: "BLM_DIG_SITE" as const })),
      ...stateParks.map((s) => ({ ...s, type: "STATE_PARK" as const })),
    ]
      .filter(
        (l) =>
          l.name.toLowerCase().includes(q) ||
          l.region.toLowerCase().includes(q),
      )
      .slice(0, 20)
      .map((l) => {
        const meta = getTypeMeta((l as { type: string }).type);
        return {
          id: l.id,
          name: l.name,
          type: "location" as const,
          subtitle: `${getTypeLabel((l as { type: string }).type)} · ${l.region}`,
          emoji: meta.emoji,
          route: `/app/locations/${l.id}`,
        };
      });

    return [...artifactResults, ...locationResults];
  }, [query]);

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Search
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Search artifacts and locations
        </p>
      </div>

      <div className="relative">
        <SearchIcon className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search artifacts, locations..."
          className="pl-10"
          autoFocus
        />
        {query && (
          <button
            onClick={() => setQuery("")}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {query.trim() && (
        <p className="text-sm text-muted-foreground">
          {results.length} result{results.length !== 1 ? "s" : ""}
        </p>
      )}

      {results.length > 0 ? (
        <div className="space-y-2">
          {results.map((result) => (
            <button
              key={`${result.type}-${result.id}`}
              onClick={() => navigate(result.route)}
              className="flex w-full items-center gap-3 rounded-lg border border-border bg-card p-3 text-left transition-colors hover:border-primary/40"
            >
              {result.imageUrl ? (
                <div className="relative h-12 w-12 shrink-0 overflow-hidden rounded-lg bg-muted/30">
                  <OptimizedImage
                    src={result.imageUrl}
                    alt={result.name}
                    loading="lazy"
                    className="h-full w-full object-cover"
                  />
                </div>
              ) : (
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-muted/30 text-2xl">
                  {result.emoji ?? "🔍"}
                </div>
              )}
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-semibold text-foreground">
                  {result.name}
                </p>
                <p className="truncate text-xs text-muted-foreground">
                  {result.subtitle}
                </p>
              </div>
              <span className="shrink-0 rounded-full bg-muted px-2 py-0.5 text-[10px] font-medium text-muted-foreground">
                {result.type}
              </span>
            </button>
          ))}
        </div>
      ) : (
        query.trim() && (
          <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
            <SearchIcon className="h-8 w-8 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No results for "{query}". Try a different search term.
            </p>
          </div>
        )
      )}

      {!query.trim() && (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <SearchIcon className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            Start typing to search across artifacts and locations.
          </p>
        </div>
      )}
    </div>
  );
}
