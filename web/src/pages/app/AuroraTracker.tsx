import { useState } from "react";
import { Search, X, Sparkles, ExternalLink } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

interface AuroraInfo {
  kpIndex: number;
  level: string;
  description: string;
  visibility: string;
  color: string;
}

const KP_LEVELS: AuroraInfo[] = [
  { kpIndex: 0, level: "Quiet", description: "No aurora activity. Only visible under very dark skies in far northern latitudes.", visibility: "64°N or higher", color: "#3498DB" },
  { kpIndex: 1, level: "Quiet", description: "Very low activity. Aurora visible in high Arctic regions only.", visibility: "62°N or higher", color: "#3498DB" },
  { kpIndex: 2, level: "Low", description: "Low activity. Visible in northern Canada, Alaska, Scandinavia under dark skies.", visibility: "58°N or higher", color: "#2ECC71" },
  { kpIndex: 3, level: "Low", description: "Low to moderate. Visible in northern US states (Minnesota, North Dakota) under good conditions.", visibility: "56°N or higher", color: "#2ECC71" },
  { kpIndex: 4, level: "Active", description: "Active aurora. Visible across much of the northern US and Europe under dark skies.", visibility: "54°N or higher", color: "#F39C12" },
  { kpIndex: 5, level: "Minor Storm", description: "Minor geomagnetic storm. Aurora visible as far south as Oregon, Idaho, Iowa, New York.", visibility: "50°N or higher", color: "#E67E22" },
  { kpIndex: 6, level: "Moderate Storm", description: "Moderate storm. Aurora visible across most of the northern US. Good photographic conditions.", visibility: "47°N or higher", color: "#E74C3C" },
  { kpIndex: 7, level: "Strong Storm", description: "Strong storm. Aurora visible as far south as northern California, Nevada, Utah, Virginia.", visibility: "44°N or higher", color: "#E74C3C" },
  { kpIndex: 8, level: "Severe Storm", description: "Severe storm. Aurora visible across much of the US. Rare event — visible even near cities.", visibility: "40°N or higher", color: "#9B59B6" },
  { kpIndex: 9, level: "Extreme Storm", description: "Extreme storm. Aurora visible as far south as Texas, Florida, southern Europe. Once-in-a-decade event.", visibility: "35°N or higher", color: "#9B59B6" },
];

const VIEWING_TIPS = [
  { title: "When to look", tips: [
    "Best between 10 PM and 2 AM local time (magnetic midnight)",
    "September–March is peak aurora season in the northern hemisphere",
    "Check the 27-day forecast for planning, 3-hour for go/no-go",
    "Aurora often comes in waves — if you see nothing at 10 PM, check again at midnight",
  ]},
  { title: "Where to go", tips: [
    "Get away from city lights — at least 30 miles from any town",
    "Find a clear northern horizon (unobstructed view to the north)",
    "Higher latitude = better chance, but Kp 5+ brings it south",
    "Use a dark sky map to find Bortle Class 1-3 sites",
  ]},
  { title: "Photographing aurora", tips: [
    "Use a tripod — exposures of 2-15 seconds are needed",
    "Settings: f/2.8 or wider, ISO 1600-6400, 2-10s exposure",
    "Manual focus to infinity (autofocus fails in the dark)",
    "Shoot RAW if possible — you'll need the dynamic range for post-processing",
  ]},
];

export default function AuroraTracker() {
  const [search, setSearch] = useState("");

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Aurora Tracker
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          KP index guide, viewing tips, and forecast resources
        </p>
      </div>

      {/* KP index scale */}
      <div>
        <h2 className="mb-3 font-display text-lg font-bold text-foreground">
          KP Index Scale
        </h2>
        <p className="mb-4 text-sm text-muted-foreground">
          The KP index (0-9) measures geomagnetic activity. Higher KP = aurora
          visible farther south. Find your latitude to know what KP you need.
        </p>
        <div className="space-y-2">
          {KP_LEVELS.filter((kp) =>
            !search.trim() ||
            kp.level.toLowerCase().includes(search.toLowerCase()) ||
            kp.description.toLowerCase().includes(search.toLowerCase()),
          ).map((kp) => (
            <div
              key={kp.kpIndex}
              className="flex items-start gap-3 rounded-lg border border-border bg-card p-3"
            >
              <div
                className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-lg font-bold text-white"
                style={{ backgroundColor: kp.color }}
              >
                {kp.kpIndex}
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    Kp {kp.kpIndex} — {kp.level}
                  </h3>
                  <span className="rounded-full bg-muted px-2 py-0.5 text-[10px] text-muted-foreground">
                    Visible ≥ {kp.visibility}
                  </span>
                </div>
                <p className="mt-1 text-xs text-muted-foreground">
                  {kp.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Viewing tips */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        {VIEWING_TIPS.map((section) => (
          <div
            key={section.title}
            className="space-y-2 rounded-xl border border-border bg-card p-4"
          >
            <h3 className="font-display text-sm font-semibold text-foreground">
              {section.title}
            </h3>
            <ul className="space-y-1">
              {section.tips.map((tip, i) => (
                <li
                  key={i}
                  className="flex items-start gap-1.5 text-xs text-muted-foreground"
                >
                  <span className="mt-0.5 text-primary">•</span>
                  {tip}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>

      {/* Forecast links */}
      <div className="rounded-xl border border-border bg-gradient-to-br from-primary/10 to-card p-5">
        <h2 className="mb-2 font-display text-base font-bold text-foreground">
          Live Forecast Resources
        </h2>
        <p className="mb-4 text-sm text-muted-foreground">
          For real-time aurora forecasts, check these trusted sources:
        </p>
        <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
          {[
            { name: "NOAA Space Weather", url: "https://www.swpc.noaa.gov/products/aurora-30-minute-forecast", desc: "30-minute aurora forecast map (NOAA)" },
            { name: "Aurora Forecast (UAF)", url: "https://www.gi.alaska.edu/monitors/aurora-forecast", desc: "University of Alaska Fairbanks daily forecast" },
            { name: "SpaceWeatherLive", url: "https://www.spaceweatherlive.com/en/aurora-forecast", desc: "3-day KP forecast and hourly predictions" },
            { name: "My Aurora Forecast App", url: "https://www.myauroraforecast.com", desc: "Mobile app with push alerts for your location" },
          ].map((source) => (
            <a
              key={source.name}
              href={source.url}
              target="_blank"
              rel="noopener noreferrer"
              className="group flex items-center gap-2 rounded-lg border border-border bg-card p-3 transition-colors hover:border-primary/40"
            >
              <Sparkles className="h-4 w-4 shrink-0 text-primary" />
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-medium text-foreground">
                  {source.name}
                </p>
                <p className="truncate text-xs text-muted-foreground">
                  {source.desc}
                </p>
              </div>
              <ExternalLink className="h-3.5 w-3.5 shrink-0 text-muted-foreground group-hover:text-primary" />
            </a>
          ))}
        </div>
      </div>
    </div>
  );
}
