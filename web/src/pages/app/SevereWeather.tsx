import { useState } from "react";
import { Search, X, CloudRain, Wind, AlertTriangle } from "lucide-react";
import { Input } from "@/components/ui/input";

interface WeatherHazard {
  id: string;
  name: string;
  category: "Thunderstorm" | "Flooding" | "Heat" | "Cold" | "Wind" | "Visibility";
  emoji: string;
  severity: "Watch" | "Warning" | "Advisory";
  description: string;
  rockhoundRisk: string;
  safetyTips: string[];
}

const HAZARDS: WeatherHazard[] = [
  {
    id: "lightning",
    name: "Lightning",
    category: "Thunderstorm",
    emoji: "⚡",
    severity: "Warning",
    description: "Lightning strikes are the #1 weather danger for rockhounds in the field. Strikes can occur 10+ miles from the storm — 'bolt from the blue' is real.",
    rockhoundRisk: "Exposed ridge tops, metal rock hammers, and being the tallest object on a flat surface all increase strike risk. Quarries and open-pit mines are especially dangerous.",
    safetyTips: [
      "If you hear thunder, go inside — lightning can strike 10+ miles from the storm",
      "Drop metal tools (rock hammer, chisels) and move 20+ feet away from them",
      "Avoid ridge tops, solitary trees, and open water — seek a low area",
      "If caught in the open: crouch low, feet together, minimize contact with ground",
      "A hard-top vehicle is a safe shelter — the metal frame acts as a Faraday cage",
      "Wait 30 minutes after the last thunder before resuming field work",
    ],
  },
  {
    id: "flash-flood",
    name: "Flash Flooding",
    category: "Flooding",
    emoji: "🌊",
    severity: "Warning",
    description: "Flash floods can occur with no rain at your location — storms miles upstream can send a wall of water down a dry wash in minutes. Desert canyons are especially dangerous.",
    rockhoundRisk: "Creek beds, dry washes, and canyon floors are prime collecting spots — and the most dangerous places during flash floods. Water can rise feet in seconds.",
    safetyTips: [
      "Check the forecast for the entire watershed, not just your location",
      "If water starts rising or turning muddy, get to high ground immediately",
      "Never camp in a dry wash or canyon floor",
      "A 6-inch wall of water can knock you off your feet; 2 feet can float a car",
      "Be especially cautious in slot canyons — there may be no escape route",
      "Turn around, don't drown — never drive through flooded roads",
    ],
  },
  {
    id: "heat",
    name: "Extreme Heat",
    category: "Heat",
    emoji: "🥵",
    severity: "Warning",
    description: "Heat exhaustion and heat stroke are serious risks for desert rockhounds. Body temperature can exceed 104°F, leading to organ damage and death.",
    rockhoundRisk: "Desert collecting (sunstone, agate, geode beds) often involves hiking in remote areas with no shade. Carrying heavy rocks and gear increases exertion.",
    safetyTips: [
      "Carry at least 1 gallon of water per person per day (minimum)",
      "Collect early morning or late afternoon — avoid 10 AM to 4 PM in summer",
      "Wear a wide-brimmed hat and light-colored, loose clothing",
      "Take breaks in shade — even the shadow of your vehicle helps",
      "Watch for heat exhaustion: nausea, dizziness, headache, heavy sweating",
      "Heat stroke (no sweating, confusion, hot dry skin) = call 911 immediately",
    ],
  },
  {
    id: "hypothermia",
    name: "Hypothermia",
    category: "Cold",
    emoji: "🥶",
    severity: "Warning",
    description: "Hypothermia can occur at temperatures as warm as 50°F if you're wet and windy. Mountain collecting (Antero, Himalaya) is especially risky.",
    rockhoundRisk: "High-elevation collecting sites (Mount Antero 14,000ft, Himalaya mine) can have sudden temperature drops. Getting wet in a creek compounds the risk.",
    safetyTips: [
      "Dress in layers — wool or synthetic, never cotton (cotton kills)",
      "Carry a waterproof shell — rain gear doubles as wind protection",
      "If someone is shivering uncontrollably, get them warm and dry immediately",
      "Confusion and cessation of shivering are late-stage signs — seek help",
      "Carry emergency Mylar space blankets — they reflect 90% of body heat",
      "Check mountain weather forecasts — conditions change fast at altitude",
    ],
  },
  {
    id: "wind",
    name: "High Winds",
    category: "Wind",
    emoji: "💨",
    severity: "Advisory",
    description: "Sustained winds over 30 mph or gusts over 45 mph can make field work dangerous — flying debris, blowing dust, and difficulty standing.",
    rockhoundRisk: "Quarry walls and cliff edges become dangerous in high winds. Blowing dust damages eyes and equipment. Tents and shade structures become projectiles.",
    safetyTips: [
      "Secure all loose gear — tarps and tents can become airborne",
      "Wear safety glasses to protect eyes from blowing dust and sand",
      "Avoid cliff edges and quarry walls — wind can push you off balance",
      "If dust reduces visibility, pull over and wait it out",
      "Check wind forecasts before driving on dirt roads — visibility can drop to zero",
    ],
  },
  {
    id: "fog",
    name: "Dense Fog",
    category: "Visibility",
    emoji: "🌫️",
    severity: "Advisory",
    description: "Fog reduces visibility to less than 1/4 mile, making driving and navigation dangerous. Coastal and valley collecting sites are most affected.",
    rockhoundRisk: "Driving to remote sites in fog is hazardous. GPS may be unreliable in canyons. Getting lost in fog in remote terrain is a serious risk.",
    safetyTips: [
      "Slow down and use low-beam headlights (not high beams — they reflect back)",
      "If visibility drops below 100 feet, pull off the road and wait",
      "Carry a GPS and mark your vehicle location before hiking",
      "Stay on established trails — fog makes navigation extremely difficult",
      "Carry a whistle or personal locator beacon for emergencies",
    ],
  },
];

const NOAA_LINKS = [
  { name: "NOAA Weather Radio", url: "https://www.weather.gov/nwr", desc: "Official weather alerts via radio (best for remote areas)" },
  { name: "NWS Alerts Map", url: "https://www.weather.gov/alerts", desc: "Live map of all active weather warnings" },
  { name: "WPC Fronts", url: "https://www.wpc.ncep.noaa.gov/national_forecast/natfcst.php", desc: "Weather Prediction Center national forecast" },
  { name: "SPC Convective", url: "https://www.spc.noaa.gov/", desc: "Storm Prediction Center — severe thunderstorm & tornado outlook" },
];

export default function SevereWeather() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("ALL");

  const categories = ["ALL", "Thunderstorm", "Flooding", "Heat", "Cold", "Wind", "Visibility"];

  const filtered = HAZARDS.filter((h) => {
    if (category !== "ALL" && h.category !== category) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        h.name.toLowerCase().includes(q) ||
        h.description.toLowerCase().includes(q) ||
        h.rockhoundRisk.toLowerCase().includes(q)
      );
    }
    return true;
  });

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Severe Weather Guide
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {HAZARDS.length} weather hazards every rockhound should know
        </p>
      </div>

      <div className="rounded-xl border border-amber-500/30 bg-amber-500/5 p-4">
        <h3 className="mb-1 flex items-center gap-2 font-display text-sm font-semibold text-amber-600">
          <AlertTriangle className="h-4 w-4" />
          Field safety first
        </h3>
        <p className="text-sm text-foreground/80">
          Weather is the #1 danger for rockhounds in the field. Always check the
          forecast for your collecting area <strong>and the surrounding watershed</strong>{" "}
          before you go. Carry a NOAA weather radio for remote areas.
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search weather hazards..."
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
            {cat === "ALL" ? "All Hazards" : cat}
          </button>
        ))}
      </div>

      <div className="space-y-3">
        {filtered.map((hazard) => (
          <div
            key={hazard.id}
            className="space-y-3 rounded-xl border border-border bg-card p-4"
          >
            <div className="flex items-start gap-3">
              <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-amber-500/15 text-2xl">
                {hazard.emoji}
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <h3 className="font-display text-base font-semibold text-foreground">
                    {hazard.name}
                  </h3>
                  <span className="rounded-full bg-amber-500/15 px-2 py-0.5 text-[10px] font-medium text-amber-600">
                    {hazard.severity}
                  </span>
                </div>
                <span className="text-xs text-muted-foreground">{hazard.category}</span>
              </div>
            </div>

            <p className="text-sm leading-relaxed text-muted-foreground">
              {hazard.description}
            </p>

            <div className="rounded-lg border border-amber-500/20 bg-amber-500/5 p-2.5">
              <p className="text-xs text-foreground/80">
                <span className="font-semibold text-amber-600">Rockhound risk:</span>{" "}
                {hazard.rockhoundRisk}
              </p>
            </div>

            <div>
              <p className="mb-1.5 text-xs font-medium text-foreground">Safety tips</p>
              <ul className="space-y-1">
                {hazard.safetyTips.map((tip, i) => (
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
          </div>
        ))}
      </div>

      {/* NOAA links */}
      <div className="rounded-xl border border-border bg-card p-4">
        <h2 className="mb-2 flex items-center gap-2 font-display text-sm font-semibold text-foreground">
          <CloudRain className="h-4 w-4 text-primary" />
          Official Weather Resources
        </h2>
        <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
          {NOAA_LINKS.map((link) => (
            <a
              key={link.name}
              href={link.url}
              target="_blank"
              rel="noopener noreferrer"
              className="rounded-lg border border-border p-2.5 transition-colors hover:border-primary/40"
            >
              <p className="text-sm font-medium text-foreground">{link.name}</p>
              <p className="text-xs text-muted-foreground">{link.desc}</p>
            </a>
          ))}
        </div>
      </div>
    </div>
  );
}
