import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { Bone, Search, X, Shield } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
  prehistoricArtifacts,
  artifactFamilies,
  warRelics,
  warRelicFamilies,
} from "@/data/artifacts";
import { OptimizedImage } from "@/components/OptimizedImage";

export default function Artifacts() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [familyFilter, setFamilyFilter] = useState<string>("ALL");
  const [tab, setTab] = useState<"artifacts" | "relics">("artifacts");

  const isRelicTab = tab === "relics";
  const sourceList = isRelicTab ? warRelics : prehistoricArtifacts;
  const families = isRelicTab ? warRelicFamilies : artifactFamilies;

  const filtered = useMemo(() => {
    let result = sourceList;
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
          a.timePeriod.toLowerCase().includes(q) ||
          a.family.toLowerCase().includes(q),
      );
    }
    return result;
  }, [sourceList, search, familyFilter]);

  const accentColor = isRelicTab
    ? "hsl(204 22% 39%)"
    : "hsl(22 55% 42%)";

  return (
    <div className="space-y-5">
      {/* Pill switcher */}
      <div className="flex items-center justify-center gap-3">
        <button
          onClick={() => {
            setTab("artifacts");
            setFamilyFilter("ALL");
          }}
          className={`inline-flex items-center gap-2 rounded-full px-5 py-2 text-sm font-semibold transition-all ${
            !isRelicTab
              ? "bg-primary/15 text-primary ring-1 ring-primary/30"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          <Bone className="h-4 w-4" />
          Artifacts
        </button>
        <button
          onClick={() => {
            setTab("relics");
            setFamilyFilter("ALL");
          }}
          className={`inline-flex items-center gap-2 rounded-full px-5 py-2 text-sm font-semibold transition-all ${
            isRelicTab
              ? "bg-primary/15 text-primary ring-1 ring-primary/30"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          <Shield className="h-4 w-4" />
          War Relics
        </button>
      </div>

      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          {isRelicTab ? "War Relics" : "Artifacts"}
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {isRelicTab
            ? `${warRelics.length} Civil War & Revolutionary War relics — bullets, buttons, buckles, bayonets & more`
            : `${prehistoricArtifacts.length} prehistoric tools and ornaments — arrowheads, hand axes, beads, and more`}
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder={
            isRelicTab
              ? "Search relics by name, side, or era..."
              : "Search artifacts by name, culture, or period..."
          }
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
        {families.map((f) => (
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
            className="group flex flex-col overflow-hidden dark-card sculpted-raised rounded-lg text-left transition-all hover:border-primary/40"
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
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          {isRelicTab ? (
            <Shield className="h-8 w-8 text-muted-foreground" />
          ) : (
            <Bone className="h-8 w-8 text-muted-foreground" />
          )}
          <p className="text-sm text-muted-foreground">
            No {isRelicTab ? "relics" : "artifacts"} found. Try a different
            search or filter.
          </p>
        </div>
      )}
    </div>
  );
}
