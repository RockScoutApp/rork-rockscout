import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { Bone, Search, X } from "lucide-react";
import { Input } from "@/components/ui/input";
import { artifacts, artifactFamilies } from "@/data/artifacts";
import { OptimizedImage } from "@/components/OptimizedImage";

export default function Artifacts() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [familyFilter, setFamilyFilter] = useState<string>("ALL");

  const filtered = useMemo(() => {
    let result = artifacts;
    if (familyFilter !== "ALL") {
      result = result.filter((a) => a.family === familyFilter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(
        (a) =>
          a.name.toLowerCase().includes(q) ||
          a.tagline.toLowerCase().includes(q) ||
          a.tribe.toLowerCase().includes(q) ||
          a.timePeriod.toLowerCase().includes(q),
      );
    }
    return result;
  }, [search, familyFilter]);

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Artifacts
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {artifacts.length} prehistoric tools and ornaments — arrowheads, hand
          axes, beads, and more
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search artifacts by name, culture, or period..."
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
        <button
          onClick={() => setFamilyFilter("ALL")}
          className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
            familyFilter === "ALL"
              ? "bg-primary text-primary-foreground"
              : "bg-muted text-muted-foreground hover:bg-muted/70"
          }`}
        >
          All
        </button>
        {artifactFamilies.map((f) => (
          <button
            key={f}
            onClick={() => setFamilyFilter(f)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              familyFilter === f
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {f}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6">
        {filtered.map((artifact) => (
          <button
            key={artifact.id}
            onClick={() => navigate(`/app/artifacts/${artifact.id}`)}
            className="group flex flex-col overflow-hidden rounded-lg border border-border bg-card text-left transition-all hover:border-primary/40"
          >
            <div className="relative aspect-square w-full overflow-hidden bg-muted/30">
              <OptimizedImage
                src={artifact.imageUrl}
                alt={artifact.name}
                loading="lazy"
                className="h-full w-full object-cover transition-transform group-hover:scale-105"
              />
            </div>
            <div className="p-2.5">
              <h3 className="truncate text-sm font-semibold text-foreground">
                {artifact.name}
              </h3>
              <p className="mt-0.5 truncate text-xs text-muted-foreground">
                {artifact.family}
              </p>
            </div>
          </button>
        ))}
      </div>

      {filtered.length === 0 && (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Bone className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No artifacts found. Try a different search or filter.
          </p>
        </div>
      )}
    </div>
  );
}
