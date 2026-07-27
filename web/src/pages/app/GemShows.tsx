import { useState, useMemo } from "react";
import { Calendar, Search, X, ExternalLink, MapPin } from "lucide-react";
import { Input } from "@/components/ui/input";
import { gemShows, type GemShow } from "@/data/locations";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

const monthIndex = (label: string): number => {
  for (let i = 0; i < MONTHS.length; i++) {
    if (label.toLowerCase().includes(MONTHS[i].toLowerCase())) return i + 1;
  }
  return 0;
};

const sortByUpcoming = (shows: GemShow[]): GemShow[] => {
  const now = new Date().getMonth() + 1;
  return [...shows].sort((a, b) => {
    const aM = a.monthIndex || monthIndex(a.monthLabel);
    const bM = b.monthIndex || monthIndex(b.monthLabel);
    const aDiff = aM >= now ? aM - now : 12 - now + aM;
    const bDiff = bM >= now ? bM - now : 12 - now + bM;
    return aDiff - bDiff;
  });
};

export default function GemShows() {
  const [search, setSearch] = useState("");
  const [monthFilter, setMonthFilter] = useState<string>("");

  const filtered = useMemo(() => {
    let shows = sortByUpcoming(gemShows);
    if (monthFilter) {
      shows = shows.filter((s) => s.monthLabel === monthFilter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      shows = shows.filter(
        (s) =>
          s.name.toLowerCase().includes(q) ||
          s.city.toLowerCase().includes(q) ||
          s.state.toLowerCase().includes(q) ||
          s.description.toLowerCase().includes(q),
      );
    }
    return shows;
  }, [search, monthFilter]);

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Gem & Mineral Shows
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {gemShows.length} upcoming shows across the US
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search shows by name, city, or state..."
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
          onClick={() => setMonthFilter("")}
          className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
            !monthFilter
              ? "bg-primary text-primary-foreground"
              : "bg-muted text-muted-foreground hover:bg-muted/70"
          }`}
        >
          All months
        </button>
        {MONTHS.filter((m) =>
          gemShows.some((s) => s.monthLabel === m),
        ).map((m) => (
          <button
            key={m}
            onClick={() => setMonthFilter(m)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              monthFilter === m
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {m.slice(0, 3)}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
        {filtered.map((show) => (
          <div
            key={show.id}
            className="group flex flex-col gap-2 rounded-xl border border-border bg-card p-4 transition-all hover:border-primary/40"
          >
            <div className="flex items-start justify-between gap-2">
              <div className="flex items-center gap-1.5 text-xs font-medium text-primary">
                <Calendar className="h-3.5 w-3.5" />
                {show.monthLabel}
              </div>
              <span className="rounded-full bg-amber-500/15 px-2 py-0.5 text-xs font-medium text-amber-600">
                {show.dateRange}
              </span>
            </div>

            <h3 className="font-display text-sm font-semibold text-foreground">
              {show.name}
            </h3>

            <p className="flex items-center gap-1 text-xs text-muted-foreground">
              <MapPin className="h-3 w-3" />
              {show.city}, {show.state}
            </p>

            <p className="text-xs leading-relaxed text-muted-foreground">
              {show.description}
            </p>

            <div className="mt-1 flex items-center justify-between border-t border-border pt-2">
              <span className="text-xs text-muted-foreground">
                {show.entryFee}
              </span>
              <a
                href={show.website}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center gap-1 text-xs font-medium text-primary hover:underline"
              >
                Website
                <ExternalLink className="h-3 w-3" />
              </a>
            </div>
          </div>
        ))}
      </div>

      {filtered.length === 0 && (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Calendar className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No shows found. Try a different search or month.
          </p>
        </div>
      )}
    </div>
  );
}
