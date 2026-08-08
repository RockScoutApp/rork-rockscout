import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Search, X, Telescope, Star, Globe, ChevronRight } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard } from "@/components/sculpted";

interface Constellation {
  name: string;
  abbr: string;
  hemisphere: string;
  brightestStar: string;
  description: string;
  bestSeason: string;
  mythology: string;
}

const CONSTELLATIONS: Constellation[] = [
  {
    name: "Orion",
    abbr: "Ori",
    hemisphere: "Equatorial",
    brightestStar: "Rigel (Beta Orionis)",
    description: "The Hunter — the most recognizable constellation in the sky. Three belt stars in a straight line, with Betelgeuse (red) above-left and Rigel (blue) below-right. Visible worldwide.",
    bestSeason: "Winter (Nov–Mar)",
    mythology: "Orion was a great hunter in Greek mythology, killed by a scorpion sent by Gaia. Zeus placed him in the sky opposite Scorpius so they never meet.",
  },
  {
    name: "Ursa Major",
    abbr: "UMa",
    hemisphere: "Northern",
    brightestStar: "Alioth (Epsilon UMa)",
    description: "The Great Bear — contains the Big Dipper asterism. The two pointer stars (Merak and Dubhe) point to Polaris. Circumpolar in the northern hemisphere (never sets).",
    bestSeason: "Spring (Mar–May)",
    mythology: "Zeus turned the nymph Callisto into a bear to hide her from Hera. Hera found her and Zeus placed her in the sky as Ursa Major.",
  },
  {
    name: "Cassiopeia",
    abbr: "Cas",
    hemisphere: "Northern",
    brightestStar: "Schedar (Alpha Cas)",
    description: "The Queen — a distinctive W or M shape (depending on orientation). Circumpolar in the northern hemisphere. Easy to find between the Big Dipper and Polaris.",
    bestSeason: "Autumn (Sep–Nov)",
    mythology: "Cassiopeia boasted she was more beautiful than the sea nymphs. Poseidon punished her by placing her in the sky upside-down half the time.",
  },
  {
    name: "Leo",
    abbr: "Leo",
    hemisphere: "Equatorial",
    brightestStar: "Regulus (Alpha Leo)",
    description: "The Lion — a distinctive backward-question-mark (the 'sickle') forms the head and mane, with a triangle of stars forming the hindquarters. Regulus is a bright blue-white star.",
    bestSeason: "Spring (Mar–May)",
    mythology: "The Nemean Lion, slain by Heracles as his first labor. Zeus placed the lion in the sky to honor the feat.",
  },
  {
    name: "Scorpius",
    abbr: "Sco",
    hemisphere: "Southern",
    brightestStar: "Antares (Alpha Sco)",
    description: "The Scorpion — a dramatic J-shaped curve with the red giant Antares at the heart. The stinger (Shaula and Lesath) points upward. Best seen from southern latitudes.",
    bestSeason: "Summer (Jun–Aug)",
    mythology: "The scorpion that killed Orion. Placed in the sky opposite Orion so the two can never appear at the same time.",
  },
  {
    name: "Cygnus",
    abbr: "Cyg",
    hemisphere: "Northern",
    brightestStar: "Deneb (Alpha Cyg)",
    description: "The Swan — also called the Northern Cross. A cross shape with Deneb at the tail. Flies along the Milky Way. Contains the bright binary star Albireo (gold and blue).",
    bestSeason: "Summer (Jun–Sep)",
    mythology: "Zeus disguised himself as a swan to seduce Leda. The swan was placed in the sky flying forever along the Milky Way.",
  },
  {
    name: "Lyra",
    abbr: "Lyr",
    hemisphere: "Northern",
    brightestStar: "Vega (Alpha Lyr)",
    description: "The Lyre — a small parallelogram with brilliant Vega at one corner. Vega is the 5th brightest star in the sky and a cornerstone of the Summer Triangle.",
    bestSeason: "Summer (Jun–Sep)",
    mythology: "The lyre of Orpheus, the greatest musician of Greek myth. After his death, Zeus placed the lyre in the sky.",
  },
  {
    name: "Taurus",
    abbr: "Tau",
    hemisphere: "Equatorial",
    brightestStar: "Aldebaran (Alpha Tau)",
    description: "The Bull — a V-shaped face with red-orange Aldebaran as the eye. Contains two famous star clusters: the Pleiades (Seven Sisters) and the Hyades.",
    bestSeason: "Winter (Nov–Feb)",
    mythology: "Zeus disguised himself as a white bull to abduct Europa. The bull was placed in the sky as Taurus.",
  },
  {
    name: "Gemini",
    abbr: "Gem",
    hemisphere: "Northern",
    brightestStar: "Pollux (Beta Gem)",
    description: "The Twins — two parallel lines of stars representing Castor and Pollux. The heads are marked by Castor (white) and Pollux (orange). Contains the Geminid meteor shower radiant.",
    bestSeason: "Winter (Dec–Mar)",
    mythology: "Castor and Pollux were twin brothers — one mortal, one immortal. When Castor died, Pollux asked Zeus to share his immortality, so they were placed together in the sky.",
  },
  {
    name: "Andromeda",
    abbr: "And",
    hemisphere: "Northern",
    brightestStar: "Alpheratz (Alpha And)",
    description: "The Princess — a chain of stars leading from Pegasus. Contains the Andromeda Galaxy (M31), the most distant object visible to the naked eye at 2.5 million light-years.",
    bestSeason: "Autumn (Sep–Dec)",
    mythology: "Andromeda was chained to a rock as a sacrifice to a sea monster, rescued by Perseus. She was placed in the sky near her rescuer.",
  },
];

const IMPORTANT_STARS = [
  { name: "Sirius", constellation: "Canis Major", magnitude: -1.46, type: "A1V (blue-white)", distance: "8.6 ly", note: "The brightest star in the night sky. A binary star system." },
  { name: "Canopus", constellation: "Carina", magnitude: -0.74, type: "A9II (yellow-white giant)", distance: "310 ly", note: "Second brightest star. Used for spacecraft navigation." },
  { name: "Arcturus", constellation: "Boötes", magnitude: -0.05, type: "K1.5III (orange giant)", distance: "36.7 ly", note: "The brightest star in the northern celestial hemisphere." },
  { name: "Vega", constellation: "Lyra", magnitude: 0.03, type: "A0V (blue-white)", distance: "25 ly", note: "The standard for zero magnitude. Part of the Summer Triangle." },
  { name: "Capella", constellation: "Auriga", magnitude: 0.08, type: "G8III + G0III (yellow giants)", distance: "42.9 ly", note: "A quadruple star system appearing as one yellow point." },
  { name: "Rigel", constellation: "Orion", magnitude: 0.13, type: "B8Ia (blue supergiant)", distance: "860 ly", note: "The brightest star in Orion despite being 'Beta' Orionis." },
  { name: "Betelgeuse", constellation: "Orion", magnitude: 0.42, type: "M1-2Ia (red supergiant)", distance: "548 ly", note: "A variable star that may go supernova within 100,000 years." },
  { name: "Antares", constellation: "Scorpius", magnitude: 1.06, type: "M1.5Iab (red supergiant)", distance: "550 ly", note: "The 'rival of Mars' due to its red color. 700x the Sun's diameter." },
  { name: "Polaris", constellation: "Ursa Minor", magnitude: 1.98, type: "F7Ib (yellow supergiant)", distance: "433 ly", note: "The North Star — within 1° of the celestial north pole." },
  { name: "Aldebaran", constellation: "Taurus", magnitude: 0.86, type: "K5III (orange giant)", distance: "65 ly", note: "The 'eye of the bull.' Pioneer 10 will pass near it in ~2 million years." },
];

export default function StarsConstellations() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [tab, setTab] = useState<"constellations" | "stars">("constellations");

  const filteredConstellations = CONSTELLATIONS.filter((c) =>
    !search.trim() ||
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.description.toLowerCase().includes(search.toLowerCase()),
  );

  const filteredStars = IMPORTANT_STARS.filter((s) =>
    !search.trim() ||
    s.name.toLowerCase().includes(search.toLowerCase()) ||
    s.constellation.toLowerCase().includes(search.toLowerCase()),
  );

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Stars & Constellations
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Night sky guide for field trips — {CONSTELLATIONS.length} constellations & {IMPORTANT_STARS.length} bright stars
        </p>
      </div>

      {/* Sub-screen navigation */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        <SculptedCard accent="cyan" interactive className="overflow-hidden" onClick={() => navigate("/app/planets")}>
          <div className="flex items-center gap-3 p-4">
            <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl" style={{ ["--badge-accent" as string]: "174 100% 45%", color: "hsl(174 100% 45%)" }}>
              <Globe className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="text-sm font-bold text-foreground">Planets</h3>
              <p className="text-xs text-muted-foreground">10 solar system bodies</p>
            </div>
            <ChevronRight className="h-4 w-4 text-muted-foreground" />
          </div>
        </SculptedCard>
        <SculptedCard accent="amethyst" interactive className="overflow-hidden" onClick={() => navigate("/app/deep-sky")}>
          <div className="flex items-center gap-3 p-4">
            <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl" style={{ ["--badge-accent" as string]: "265 47% 67%", color: "hsl(265 47% 67%)" }}>
              <Star className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="text-sm font-bold text-foreground">Deep Sky Objects</h3>
              <p className="text-xs text-muted-foreground">37 galaxies & nebulae</p>
            </div>
            <ChevronRight className="h-4 w-4 text-muted-foreground" />
          </div>
        </SculptedCard>
        <SculptedCard accent="citrine" interactive className="overflow-hidden" onClick={() => navigate("/app/important-stars")}>
          <div className="flex items-center gap-3 p-4">
            <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl" style={{ ["--badge-accent" as string]: "36 80% 58%", color: "hsl(36 80% 58%)" }}>
              <Star className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="text-sm font-bold text-foreground">Important Stars</h3>
              <p className="text-xs text-muted-foreground">30 brightest stars</p>
            </div>
            <ChevronRight className="h-4 w-4 text-muted-foreground" />
          </div>
        </SculptedCard>
      </div>

      <div className="flex gap-2">
        <button
          onClick={() => setTab("constellations")}
          className={`rounded-full px-4 py-1.5 text-sm font-medium transition-colors ${
            tab === "constellations"
              ? "bg-primary text-primary-foreground"
              : "bg-muted text-muted-foreground hover:bg-muted/70"
          }`}
        >
          Constellations
        </button>
        <button
          onClick={() => setTab("stars")}
          className={`rounded-full px-4 py-1.5 text-sm font-medium transition-colors ${
            tab === "stars"
              ? "bg-primary text-primary-foreground"
              : "bg-muted text-muted-foreground hover:bg-muted/70"
          }`}
        >
          Bright Stars
        </button>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder={tab === "constellations" ? "Search constellations..." : "Search stars..."}
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

      {tab === "constellations" ? (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {filteredConstellations.map((c) => (
            <div
              key={c.name}
              className="space-y-2 dark-card sculpted-raised rounded-xl p-4"
            >
              <div className="flex items-start justify-between gap-2">
                <div>
                  <h3 className="font-display text-base font-semibold text-foreground">
                    {c.name}
                  </h3>
                  <p className="text-xs text-muted-foreground">
                    {c.abbr} · {c.hemisphere}
                  </p>
                </div>
                <span className="shrink-0 rounded-full bg-primary/10 px-2 py-0.5 text-[10px] font-medium text-primary">
                  {c.bestSeason}
                </span>
              </div>
              <p className="text-sm leading-relaxed text-muted-foreground">
                {c.description}
              </p>
              <p className="text-xs text-muted-foreground">
                <span className="font-medium text-foreground">Brightest star:</span>{" "}
                {c.brightestStar}
              </p>
              <p className="rounded-lg bg-muted/30 p-2 text-xs italic text-muted-foreground">
                {c.mythology}
              </p>
            </div>
          ))}
        </div>
      ) : (
        <div className="space-y-2">
          {filteredStars.map((star) => (
            <div
              key={star.name}
              className="flex items-start gap-3 dark-card sculpted-raised rounded-lg p-3"
            >
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary/15">
                <Star className="h-5 w-5 text-primary" />
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    {star.name}
                  </h3>
                  <span className="text-xs text-muted-foreground">
                    {star.constellation}
                  </span>
                  <span className="rounded-full bg-primary/10 px-2 py-0.5 text-[10px] font-mono text-primary">
                    mag {star.magnitude}
                  </span>
                </div>
                <p className="mt-1 text-xs text-muted-foreground">
                  <span className="font-medium text-foreground">Type:</span> {star.type} ·{" "}
                  <span className="font-medium text-foreground">Distance:</span> {star.distance}
                </p>
                <p className="mt-0.5 text-xs text-muted-foreground">{star.note}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {filteredConstellations.length === 0 && filteredStars.length === 0 && (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <Telescope className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No results found. Try a different search.
          </p>
        </div>
      )}
    </div>
  );
}
