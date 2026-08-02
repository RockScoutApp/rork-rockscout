import { useState, useMemo } from "react";
import { Search, X, BookOpen } from "lucide-react";
import { Input } from "@/components/ui/input";
import glossaryDataRaw from "@/data/glossary.json";

interface GlossaryEntry {
  term: string;
  category: string;
  definition: string;
}

const CATEGORY_COLORS: Record<string, string> = {
  MINERALOGY: "#5CC9E6",
  AURORA_SPACE: "#5CC98C",
  WEATHER: "#FF8A3D",
  PALEONTOLOGY: "#E8C44A",
  LAPIDARY: "#E8A33D",
  GEOLOGY: "#6FA8C7",
};

const CATEGORY_LABELS: Record<string, string> = {
  MINERALOGY: "Mineralogy",
  AURORA_SPACE: "Aurora & Space",
  WEATHER: "Weather",
  PALEONTOLOGY: "Paleontology",
  LAPIDARY: "Lapidary",
  GEOLOGY: "Geology",
};

const entries = (glossaryDataRaw as { glossary: GlossaryEntry[] }).glossary;

export default function Glossary() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("ALL");

  const categories = useMemo(
    () => [...new Set(entries.map((e) => e.category))].sort(),
    [],
  );

  const filtered = useMemo(() => {
    let result = entries;
    if (category !== "ALL") {
      result = result.filter((e) => e.category === category);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(
        (e) =>
          e.term.toLowerCase().includes(q) ||
          e.definition.toLowerCase().includes(q),
      );
    }
    return result;
  }, [search, category]);

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Glossary
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {entries.length} terms — mineralogy, geology, paleontology & more
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search terms..."
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
          onClick={() => setCategory("ALL")}
          className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
            category === "ALL"
              ? "bg-primary text-primary-foreground"
              : "bg-muted text-muted-foreground hover:bg-muted/70"
          }`}
        >
          All
        </button>
        {categories.map((cat) => (
          <button
            key={cat}
            onClick={() => setCategory(cat)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              category === cat
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {CATEGORY_LABELS[cat] ?? cat}
          </button>
        ))}
      </div>

      {filtered.length > 0 ? (
        <div className="space-y-2">
          {filtered.map((entry) => {
            const color = CATEGORY_COLORS[entry.category] ?? "#666";
            return (
              <div
                key={entry.term}
                className="dark-card sculpted-raised rounded-lg p-3"
              >
                <div className="flex items-center gap-2">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    {entry.term}
                  </h3>
                  <span
                    className="rounded-full px-2 py-0.5 text-[10px] font-medium"
                    style={{
                      backgroundColor: `${color}20`,
                      color,
                    }}
                  >
                    {CATEGORY_LABELS[entry.category] ?? entry.category}
                  </span>
                </div>
                <p className="mt-1 text-sm leading-relaxed text-muted-foreground">
                  {entry.definition}
                </p>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <BookOpen className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No terms found. Try a different search.
          </p>
        </div>
      )}
    </div>
  );
}
