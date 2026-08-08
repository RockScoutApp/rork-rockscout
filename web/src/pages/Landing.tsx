import { Link } from "react-router-dom";
import {
  Camera,
  Map as MapIcon,
  Gem,
  Users,

  ShieldCheck,
  ChevronRight,
  Download,
  Monitor,
  Smartphone,
  Crown,
  Check,
  Mountain,
  Compass,
  Atom,
  MessageCircle,
  Star,
  BookOpen,
  Heart,
  Bell,
  ScrollText,
  Trophy,
  FlaskConical,
  Backpack,
  Send,
  MapPin,
  Mail,
  Image as ImageIcon,
  Zap,
  GraduationCap,
  Boxes,
  Layers,
  ShoppingBag,
  Plus,
  Route,
  BadgeCheck,
  ExternalLink,
  Upload,
  Share2,
  Database,
  HelpCircle,
  Diamond,
  Library,
  Landmark,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { recordAffiliateClick } from "@/lib/affiliate-tracker";
import { getTopPickNames } from "@/lib/top-picks";
import { SITE } from "@/content/legal";
import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { PremiumInstallDialog } from "@/components/PremiumInstallDialog";
import { useState, useMemo } from "react";

const SHOTS = [
  { src: "/images/screenshots/01_home_dashboard.webp", alt: "Home dashboard with AI rock identification, level progress, and Trade Board" },
  { src: "/images/screenshots/02_specimen_detail.webp", alt: "Detailed specimen page for Adamite showing photos, properties, and where it's found" },
  { src: "/images/screenshots/03_specimen_database.webp", alt: "Browseable specimen database with filter chips and search" },
  { src: "/images/screenshots/04_explore_learn.webp", alt: "Explore & Learn section with Rocks Are Amazing and Mohs hardness scale" },
  { src: "/images/screenshots/05_home_features.webp", alt: "Home screen highlighting achievements, Trade Board, Community, Trip Planner, and Field Journal" },
  { src: "/images/screenshots/06_trade_board.webp", alt: "Trade Board for posting specimens to swap, sell, or trade" },
  { src: "/images/screenshots/07_field_kit.webp", alt: "Your Field Kit grid with My Rocks, Wishlist, Field Camera, and Dig Sites" },
  { src: "/images/screenshots/08_specimen_detail_photos.webp", alt: "Specimen detail photo gallery for Adamite with rough, wild, and museum-quality shots" },
  { src: "/images/screenshots/09_profile.webp", alt: "User profile with badges, location, and level progress" },
  { src: "/images/screenshots/10_social_friends.webp", alt: "RockScout Friends screen for scanning nearby collectors and messaging" },
];

const FEATURES = [
  {
    icon: Camera,
    title: "AI rock & mineral ID",
    body: "Snap a photo and get a best-effort ID in seconds. RockScout runs a 5-source pipeline: database visual comparison, three AI models (Claude Haiku, Sonnet, Gemini 2.5 Pro), and a web search cross-check — so you get a confident match.",
    tag: "5-source AI Rock ID",
    image: "/images/rock-id-collection.webp",
    imageAlt: "A collection of colorful rocks and minerals with a magnifying glass",
  },
  {
    icon: MapIcon,
    title: "Field maps & dig sites",
    body: "Discover nearby dig sites, gem shows, and BLM collecting areas. Drop pins, plan trips, cache tiles for offline, and get proximity alerts when you're close to a good spot. Every map can be expanded to fullscreen, the My Location button centers on your GPS, and an accidental pin tap can be undone with the Remove Pin button.",
    tag: "Never miss an outcrop",
    image: "/images/field-maps-dig-sites.webp",
    imageAlt: "A field map spread on a picnic table at a dig site with geologists and mountains in the background",
  },
  {
    icon: Gem,
    title: "Collection & wishlist",
    body: "Catalog every specimen you bring home with photos, location, notes, and rarity. Build a wishlist of what you're hunting next.",
    tag: "Your pocket museum",
    image: "/images/collection-wish-list.webp",
    imageAlt: "A neatly arranged mineral collection with labeled specimens and a wish list notebook",
  },
  {
    icon: Users,
    title: "Community & trade board",
    body: "Connect with other rockhounds, share finds, message, and list specimens to swap — all in a moderated, family-friendly community.",
    tag: "Find your people",
    image: "/images/rock-trading-floor.webp",
    imageAlt: "A bustling trading floor where collectors exchange minerals and gems",
  },
];

const STATS = [
  { value: "900+", label: "Specimens in the database" },
  { value: "50", label: "US states & territories mapped" },
  { value: "4.9★", label: "Early tester rating" },
  { value: "Free & Premium", label: "Free read-only PWA for learning · Premium unlocks IDs, camera & social" },
];

// Specimen marquee — the "wow" strip that names real minerals
const MARQUEE_A = [
  "Quartz", "Amethyst", "Pyrite", "Fluorite", "Calcite", "Galena", "Malachite", "Azurite",
  "Halite", "Mica", "Garnet", "Tourmaline", "Beryl", "Topaz", "Opal", "Agate",
  "Jasper", "Onyx", "Selenite", "Dolomite",
];
const MARQUEE_B = [
  "Adamite", "Barite", "Cinnabar", "Corundum", "Dioptase", "Epidote", "Hematite", "Kyanite",
  "Lepidolite", "Moldavite", "Moonstone", "Obsidian", "Rhodochrosite", "Sodalite", "Sugilite",
  "Tiger's Eye", "Ulexite", "Variscite", "Wulfenite", "Zircon",
];
const MARQUEE_C = [
  "Celestite", "Chalcopyrite", "Citrine", "Crocoite", "Danburite", "Datolite", "Feldspar", "Ferberite",
  "Goshenite", "Herkimer Diamond", "Hiddenite", "Howlite", "Iolite", "Kunzite", "Labradorite",
  "Larimar", "Magnetite", "Olivine", "Peridot", "Spinel",
];

const Sparkle = ({ top, left, delay, duration, color }: { top: string; left: string; delay: string; duration: string; color: string }) => (
  <span
    className="sparkle pointer-events-none absolute h-1.5 w-1.5 rounded-full"
    style={{
      top,
      left,
      backgroundColor: color,
      boxShadow: `0 0 8px ${color}, 0 0 16px ${color}`,
      ["--sparkle-delay" as string]: delay,
      ["--sparkle-duration" as string]: duration,
    }}
  />
);

// Floating specimen chip around the phone mockup
const FloatChip = ({
  className = "",
  rotate = "0deg",
  delay = "0s",
  glyph,
  label,
  sub,
}: {
  className?: string;
  rotate?: string;
  delay?: string;
  glyph: React.ReactNode;
  label: string;
  sub: string;
}) => (
  <div
    className={`specimen-chip tilt-float pointer-events-none absolute hidden rounded-2xl px-3 py-2.5 lg:block ${className}`}
    style={{ ["--tilt" as string]: rotate, animationDelay: delay }}
  >
    <div className="flex items-center gap-2.5">
      <span className="grid h-9 w-9 place-items-center rounded-lg bg-primary/15 text-primary ring-1 ring-primary/25">
        {glyph}
      </span>
      <div className="leading-tight">
        <div className="text-sm font-semibold text-foreground">{label}</div>
        <div className="text-[10px] uppercase tracking-wider text-muted-foreground">{sub}</div>
      </div>
    </div>
  </div>
);

const SpecimenMarquee = ({ reverse = false }: { reverse?: boolean }) => {
  const allNames = [...MARQUEE_A, ...MARQUEE_B, ...MARQUEE_C];
  return (
    <section className="relative border-y border-border/40 bg-card/30 py-5" aria-hidden>
      <div className="marquee-mask overflow-hidden">
        <div className={`flex w-max items-center gap-8 pr-8 ${reverse ? "animate-marquee-rev" : "animate-marquee"}`}>
          {[...allNames, ...allNames].map((name, i) => (
            <span key={`m-${i}`} className="flex items-center gap-3 text-sm font-medium text-muted-foreground">
              <Gem className="h-4 w-4 text-primary/70" />
              <span className="font-display text-base tracking-wide text-foreground/80">{name}</span>
              <span className="text-border">•</span>
            </span>
          ))}
        </div>
      </div>
    </section>
  );
};

// Creator note paragraphs — mirror the Android app's "A Note to My Fellow RockScouts" dialog.
const CREATOR_NOTE_PARAGRAPHS: string[] = [
  "First of all, I'd like to say this is the actual creator typing this. Although I did use AI in the creation of this app, this is not AI writing this note. I assure you, I'm a real, live boy.",
  "I've been a rockhounder for 30+ years, and as every single rockhounder knows, identification can be a bit of a struggle from time to time. If you're picking up your first rock that you're going to keep forever and ever, or you've got 132,649 specimens because you keep every rock you get your hands on (like me), there's always a use for a phenomenal rock ID app. Normally, I wouldn't say phenomenal when describing much of anything (except maybe food). But if you're a rock hound, or even just the outdoorsy type, considering how much hiking and camping info there is in here as well, then that's really the only appropriate word for this. I packed as much accurate ID power and info into this thing as I could. No joke. It uses a 5-source identification pipeline: first the database visual reference comparison (which I put quite a bit of time into), then three AI models tag-teaming at the same time (Claude Haiku, Claude Sonnet, and Gemini 2.5 Pro), and finally a web search cross-check. It even asks you clarifying questions when a rock is tough to get a solid identification on. Basically, it does everything in its power to provide the highest accuracy possible. Plus I included a whole ton of other informational sections, and created a whole social network just for rockhounds. You can build a friends list, chat on an in-app Messenger, post questions and photos to a community-wide Q&A board, show off specimens in a postable Profile feed, haggle on a Trade Board (a marketplace for trading, selling, or buying rocks), snap quick field photos with the field kit camera, and loads more. and yes, there's image moderation, and a fairly forgiving profanity filter. The free version does not have access to any of the social aspects, so it keeps it rated G for the littles, but still gives them a ton of content to explore. And just to be clear — you don't necessarily need a subscription to get you 'money's worth' out of this thing. The free version alone gives you access to the (ad supported) rock identification engine, the entire mineral and specimen database with detailed info pages, the interactive map with dig sites and rock shops, the Field Camera, the glossary, the daily streak challenges, and plenty more to dig in to. (See what I did there?). The free tier is ad supported, but all the ads are the most family-friendly rating I can make them. Free users can watch a couple short videos to receive a free identification token as well. No limit. The free version is still useful, but the premium version completely unlocks the highest levels of ID and everything else in the app. Literally something for all ages and levels of expertise. I do however, have to recommend 18+ due to the social aspect (which users can toggle on or off in their social settings). Gotta keep the kids safe. The adults too, for that matter, so be smart and use your own discretion if you plan on meeting up with other rockhounders. Safety always comes first.",
  "Seeing as how rockhounding is a mostly social hobby, I've added both a \"Submit Specimen\" button and an \"Add Location\" option so users can send in special or unique specimens they've found (that aren't in the current database) and dig sites, rock shops, or shows that aren't on the map yet. After review, both get added to the database and map for every RockScout user to discover. Also, if you find something in RockScout that needs to be added, fixed, made more legible, etc, please take a screenshot and email me with it (through the Contact Us tab) so I can promptly fix the issue. The OCD in me wants this app perfect, so don't hesitate to reach out with issues or suggestions.",
  "So in closing, I'd like to give a huge thank you for helping to support this app. I'll do my best to keep adding things, and tweaking other things, to make this app every rockhounders best friend. Every subscription and donation helps make this one of the most, if not THE most, accurate and thorough rock app you can find. And don't forget, the more people that join, the larger RockScouts social network becomes, so tell all your rockhounding friends to get the app!",
  "Now I'll go ahead and let AI take back over and break down everything this bad boy can do. I know it's a bit of a long read, but bear with it. You'll be happy you did. It's pretty awesome. Happy Hunting!",
];

const Hero = () => {
  const [noteOpen, setNoteOpen] = useState(false);
  return (
  <>
  <section className="relative overflow-hidden grain">
    {/* Sparkle particles echoing the app's specimen-card motif */}
    <div className="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden>
      <Sparkle top="12%" left="18%" delay="0s" duration="3.2s" color="hsl(36 80% 58%)" />
      <Sparkle top="22%" left="72%" delay="0.6s" duration="4s" color="hsl(20 62% 65%)" />
      <Sparkle top="44%" left="8%" delay="1.2s" duration="3.6s" color="hsl(172 30% 28%)" />
      <Sparkle top="64%" left="82%" delay="0.3s" duration="4.4s" color="hsl(36 80% 58%)" />
      <Sparkle top="76%" left="30%" delay="1.8s" duration="3s" color="hsl(20 62% 65%)" />
      <Sparkle top="32%" left="48%" delay="2.1s" duration="5s" color="hsl(172 30% 28%)" />
      <Sparkle top="58%" left="62%" delay="0.9s" duration="3.8s" color="hsl(36 80% 58%)" />
      <Sparkle top="8%" left="88%" delay="2.4s" duration="4.2s" color="hsl(42 80% 50%)" />
    </div>

    <div className="geode-gradient absolute inset-0 -z-10" aria-hidden />
    {/* Slow-rotating compass rose behind the phone */}
    <div className="pointer-events-none absolute right-[8%] top-1/2 hidden -translate-y-1/2 md:block" aria-hidden>
      <div className="animate-spin-slow text-border/30">
        <Compass className="h-[34rem] w-[34rem]" strokeWidth={0.5} />
      </div>
    </div>

    <div className="mx-auto grid max-w-6xl items-center gap-8 px-4 pt-6 pb-12 sm:gap-12 sm:px-6 sm:pt-10 sm:pb-20 md:grid-cols-[1.05fr_1fr] md:py-28">
      <div className="fade-rise">
        <div className="flex flex-wrap items-center gap-2">
          <span className="inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-primary">
            <Star className="h-3.5 w-3.5" /> Field companion for rockhounds
          </span>
          <a
            href="#features"
            className="inline-flex items-center gap-2 rounded-full border border-border bg-card/50 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-primary transition-colors hover:border-primary/40 hover:bg-card"
          >
            <Boxes className="h-3.5 w-3.5" /> Features
          </a>
          <a
            href="#supplies"
            className="inline-flex items-center gap-2 rounded-full border border-border bg-card/50 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-primary transition-colors hover:border-primary/40 hover:bg-card"
          >
            <ShoppingBag className="h-3.5 w-3.5" /> Supplies
          </a>
          <Link
            to="/how-to-use"
            className="inline-flex items-center gap-2 rounded-full border border-border bg-card/50 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-primary transition-colors hover:border-primary/40 hover:bg-card"
          >
            <BookOpen className="h-3.5 w-3.5" /> How to use
          </Link>
          <button
            type="button"
            onClick={() => setNoteOpen(true)}
            className="inline-flex items-center gap-2 rounded-full border border-amber-600/30 bg-amber-100/60 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-amber-800 transition-colors hover:border-amber-600/60 hover:bg-amber-200/70"
          >
            <ScrollText className="h-3.5 w-3.5" /> A note from the creator
          </button>
        </div>
        <img
          src="/images/rockscout-hero-logo.webp"
          alt="RockScout — Identify, Explore, Connect: the most comprehensive rockhounding app ever created"
          className="mt-6 w-full max-w-xl rounded-2xl shadow-[0_20px_60px_-20px_rgba(0,0,0,0.5)] ring-1 ring-primary/20 sm:mt-8"
          loading="eager"
        />
        <h1 className="sr-only">RockScout — Identify, Explore, Connect</h1>
        <p className="mt-4 max-w-md text-balance text-sm leading-relaxed text-muted-foreground sm:mt-6 sm:text-base md:text-lg">
          RockScout puts a pocket geologist in your phone — AI rock &amp; mineral ID, dig-site maps, a
          specimen database, a collection tracker, and a community of hunters and traders — built for the field, ready for any dig.
        </p>

        <div className="mt-6 flex flex-col gap-3">
          <span
            aria-disabled
            className="inline-flex h-14 w-56 cursor-default items-center justify-center gap-2.5 rounded-full bg-primary px-5 font-semibold text-primary-foreground"
          >
            <Download className="h-5 w-5 shrink-0" />
            <span className="text-center leading-tight">
              Coming soon to Google Play
            </span>
          </span>
          <span
            aria-disabled
            className="inline-flex h-14 w-56 cursor-default items-center justify-center gap-2.5 rounded-full border border-primary/40 bg-primary/10 px-5 font-semibold text-primary/80"
          >
            <Smartphone className="h-5 w-5 shrink-0" />
            <span className="text-center leading-tight">
              Coming soon to iOS
            </span>
          </span>
        </div>

        <dl className="mt-8 grid max-w-md grid-cols-2 gap-x-4 gap-y-4 sm:mt-12 sm:gap-x-6 sm:gap-y-5">
          {STATS.map((s) => (
            <div key={s.label} className="border-l-2 border-primary/30 pl-3">
              <dt className="font-display text-2xl font-bold text-foreground sm:text-3xl">{s.value}</dt>
              <dd className="text-xs leading-snug text-muted-foreground">{s.label}</dd>
            </div>
          ))}
        </dl>
      </div>

      {/* Phone mockup with floating specimen chips arranged around it (not on it) */}
      <div className="relative fade-rise flex justify-center px-2 sm:px-4 md:px-10" style={{ animationDelay: "120ms" }}>
        <div className="relative w-full max-w-[380px] py-4 sm:max-w-[480px] sm:py-8 md:py-12">
          {/* Amber glow behind phone */}
          <div className="absolute inset-x-[20%] inset-y-[10%] -z-10 animate-glow-pulse rounded-[3rem] bg-primary/15 blur-3xl" aria-hidden />
          <div className="relative mx-auto max-w-[200px] float-slow rounded-[2.5rem] border border-border/70 bg-card p-2.5 shadow-2xl ring-1 ring-primary/20 sm:max-w-[240px] sm:p-3">
            <div className="overflow-hidden rounded-[2rem] bg-background">
              <img
                src={SHOTS[0].src}
                alt={SHOTS[0].alt}
                className="aspect-[9/19.5] w-full object-cover"
                loading="eager"
              />
            </div>
          </div>

          {/* Floating specimen chips — anchored to the outer edges so they sit beside the phone, never on it */}
          <FloatChip
            className="-left-2 top-6 sm:-left-6"
            rotate="-6deg"
            delay="0s"
            glyph={<Gem className="h-5 w-5" />}
            label="Amethyst"
            sub="Silica · Fe³⁺"
          />
          <FloatChip
            className="-right-2 top-28 sm:-right-6"
            rotate="5deg"
            delay="1.2s"
            glyph={<Star className="h-5 w-5" />}
            label="Pyrite"
            sub="FeS₂ · Mohs 6"
          />
          <FloatChip
            className="-left-1 bottom-28 sm:-left-4"
            rotate="4deg"
            delay="2s"
            glyph={<Mountain className="h-5 w-5" />}
            label="Quartz"
            sub="SiO₂ · 7 Mohs"
          />
          <FloatChip
            className="-right-1 bottom-10 sm:-right-4"
            rotate="-7deg"
            delay="0.6s"
            glyph={<Atom className="h-5 w-5" />}
            label="Fluorite"
            sub="CaF₂ · Halide"
          />
        </div>
      </div>
    </div>
  </section>
  <Dialog open={noteOpen} onOpenChange={setNoteOpen}>
    <DialogContent aria-describedby={undefined} className="max-w-2xl gap-0 overflow-hidden p-0 sm:rounded-2xl">
      <DialogTitle className="sr-only">A Note to My Fellow RockScouts</DialogTitle>
      <div className="scroll-parchment max-h-[80vh] overflow-y-auto px-6 py-6 sm:px-8 sm:py-8">
        <div className="mb-4 flex items-center justify-between gap-3">
          <h2 className="font-display text-xl font-bold text-amber-900 sm:text-2xl">A Note to My Fellow RockScouts</h2>
          <span className="inline-flex items-center gap-1.5 rounded-full border border-amber-700/30 bg-amber-100/60 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-amber-800">
            <ScrollText className="h-3 w-3" /> From the creator
          </span>
        </div>
        <div className="space-y-4">
          {CREATOR_NOTE_PARAGRAPHS.map((p, i) => (
            <p key={i} className="text-[13px] leading-relaxed text-stone-800 sm:text-sm sm:leading-relaxed">{p}</p>
          ))}
        </div>
        <div className="mt-6 flex justify-center">
          <button
            type="button"
            onClick={() => setNoteOpen(false)}
            className="inline-flex items-center justify-center rounded-xl border border-amber-700/40 bg-amber-200/70 px-6 py-2.5 text-sm font-semibold text-amber-900 transition-colors hover:bg-amber-300/70"
          >
            Close
          </button>
        </div>
      </div>
    </DialogContent>
  </Dialog>
  </>
  );
};

const Features = () => (
  <section className="mx-auto max-w-6xl px-4 py-24 sm:px-6">
    <div className="max-w-2xl">
      <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
        <span className="h-px w-8 bg-primary/50" /> What's inside
      </span>
      <h2 className="mt-4 font-display text-4xl font-bold tracking-tight sm:text-5xl">
        Everything a field trip needs
      </h2>
      <p className="mt-4 text-balance text-lg text-muted-foreground">
        A complete digital field kit, all in one app.
      </p>
    </div>
    <div className="mt-14 grid gap-5 sm:grid-cols-2">
      {FEATURES.map((f, i) => (
        <div
          key={f.title}
          className="group relative overflow-hidden rounded-3xl border border-border bg-card/60 transition-all hover:-translate-y-1 hover:border-primary/50 hover:bg-card"
        >
          {f.image ? (
            <div className="relative h-48 w-full overflow-hidden sm:h-56">
              <img
                src={f.image}
                alt={f.imageAlt}
                className="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
                loading="lazy"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-card via-card/80 to-transparent" />
              <span className="pointer-events-none absolute -bottom-7 -right-2 font-display text-[7rem] font-black leading-none text-border/40 transition-colors group-hover:text-primary/10" aria-hidden>
                {String(i + 1).padStart(2, "0")}
              </span>
            </div>
          ) : (
            <div className="absolute -right-16 -top-16 h-40 w-40 rounded-full bg-primary/5 blur-2xl transition-opacity duration-500 group-hover:bg-primary/15" aria-hidden />
          )}
          <div className={`relative p-5 sm:p-7 ${f.image ? "-mt-12 sm:-mt-16" : ""}`}>
            <div className="flex items-center justify-between">
              <div className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-primary/15 text-primary ring-1 ring-primary/30 transition-transform group-hover:scale-110 sm:h-12 sm:w-12">
                <f.icon className="h-5 w-5 sm:h-6 sm:w-6" />
              </div>
              <span className="rounded-full border border-border bg-background/60 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-muted-foreground">
                {f.tag}
              </span>
            </div>
            <h3 className="mt-4 font-display text-xl font-semibold sm:mt-5 sm:text-2xl">{f.title}</h3>
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground sm:mt-3">{f.body}</p>
          </div>
          {!f.image && (
            <span className="pointer-events-none absolute -bottom-6 -right-2 font-display text-[7rem] font-black leading-none text-border/40 transition-colors group-hover:text-primary/10" aria-hidden>
              {String(i + 1).padStart(2, "0")}
            </span>
          )}
        </div>
      ))}
    </div>

  </section>
);

const FEATURES_LIST = [
  // ── Identification & Database ──
  {
    icon: Camera,
    title: "AI rock & mineral ID",
    desc: "Snap a photo and RockScout runs a 5-source identification pipeline: (1) database visual reference comparison, (2) Claude Haiku, (3) Claude Sonnet, (4) Gemini 2.5 Pro, and (5) web search cross-check. Free 7-day trial with 5 tokens; after that, Premium or a donation keeps it going.",
    items: ["Visual reference compare", "Assemblage detection", "Clarifying questions", "Auto web cross-check"],
  },
  {
    icon: FlaskConical,
    title: "Specimen database",
    desc: "900+ entries with photos, properties, and where-to-find locations. Spot a missing one? Submit photos of your find and approved entries go into the shared database for everyone.",
    items: ["900+ profiles", "Community submissions"],
  },
  {
    icon: Upload,
    title: "Submit specimens & add locations",
    desc: "Found a specimen that isn't in the database? Use the Upload Specimen pill on the Specimen Database screen, Field Captures screen, or specimen detail pages. Submit up to 10 photos plus a name, date found, location, and description — images are automatically checked against a 5 MB size limit to prevent upload failures. If the specimen already exists and your location is a common find spot for it, you'll get a small pop-up letting you know it's already included; if the location is unusual for that specimen, it goes to developer review. Approved entries get added to the Specimen Database or Rocks Are Amazing for every RockScout user to discover. Found a great dig site, rock shop, or gem show not on the map? Use the Upload New Location form to add it, reviewed before going live for everyone.",
    items: ["Submit specimens", "5 MB image check", "Submit locations", "Reviewed by RockScout", "Community-built database"],
  },
  {
    icon: Trophy,
    title: "NEW badges & recently added specimens",
    desc: "A NEW badge automatically appears on any specimen or artifact card added to the catalog within the last 7 days. Spot the latest database additions at a glance across the Specimen Database, Artifacts / Relics tile, and any category-filtered view — the badge disappears automatically after 7 days.",
    items: ["7-day NEW badge", "Specimen Database", "Artifacts / Relics tile", "Category-filtered views"],
  },

  // ── Collection & Field Tools ──
  {
    icon: Gem,
    title: "Collection, wishlist & favorite spots",
    desc: "Catalog every specimen you bring home with photos, notes, and rarity. Keep a separate wishlist of dream specimens you're hunting next. Heart any specimen to mark it as a personal favorite — liking is independent from your wishlist. Bookmark state parks, BLM locations, campgrounds, trailheads, and dig sites to your Favorite Spots list — all searchable from global search.",
    items: ["My Rocks", "Wishlist", "Heart favorites", "Specimen map", "Saved images", "Favorite spots (parks, BLM, trailheads, campgrounds, dig sites)", "Aurora saved spots"],
  },
  {
    icon: BookOpen,
    title: "Field journal & camera",
    desc: "Log daily adventures with auto-weather, photos, and notes. The Field Camera snaps a quick capture without running the full ID tool, so you can save a find anywhere. Stays free after the trial — snaps save to your in-app Saved Images folder at no cost.",
    items: ["Auto-weather entries", "Quick field captures", "Free after trial"],
  },
  {
    icon: MapPin,
    title: "Share-a-Spot deep links",
    desc: "Tap a specimen marker on the specimen marker map to open its detail view, then hit Share a Spot to generate a rockscout:// deep link encoding the spot's coordinates and name. Send the link to friends — when they open it, RockScout jumps straight to a Shared Spot screen showing the location on a map with a close button. The easiest way to point fellow rockhounds at an exact find spot.",
    items: ["Tap a specimen marker", "Share a Spot button", "rockscout:// deep link", "Opens on friend's device"],
  },

  // ── Maps & Trip Planning ──
  {
    icon: MapIcon,
    title: "Dig sites, wildlife & offline maps",
    desc: "Free, public, and pay-to-dig sites, plus rock, gem & metaphysical shops and gem shows by month. Every BLM state guide, trailhead, campground, dig site, state park, and beach detail screen includes a Common Wildlife tile showing the animals you might encounter. Tap-to-drop-pin tile downloads on every map, with 3-state satellite zoom (16 → 19) for the detail you need.",
    items: ["Nationwide sites", "Shops & shows", "Common wildlife tiles", "Offline tiles", "Park Here breadcrumb", "3-state zoom"],
  },
  {
    icon: Route,
    title: "Trip Planner & Calendar",
    desc: "Build multi-stop routes with gear checklists and custom map pins, so the whole crew knows where to meet and what to bring. Long-press and drag stops to reorder them — the stop swaps to the position under your finger, with a dashed polyline connecting all stops on the map and estimated travel time between each stop. Move up buttons are also available for quick single-step reordering. Export planned trips to a standalone month-grid Calendar screen — drag and drop trip cards to reschedule, create and edit trips, and archive completed trips.",
    items: ["Multi-stop routes", "Drag-and-drop reorder", "Route polyline", "Est. travel time", "Gear checklists", "Custom pins", "Trip Calendar", "Archived trips"],
  },

  // ── Community & Social ──
  {
    icon: Users,
    title: "RockScout Social",
    desc: "Friends, messages, profiles, posts, and a community board in one place. Discover hunters near you or worldwide, share live map pings of your current spot, and set a hunter status (Off Grid, Hunting, Digging, Trading) with color-coded profile borders.",
    items: ["Friends & messenger", "Discover hunters", "Live map pings", "Hunter status", "Referral links", "Moderated space"],
  },
  {
    icon: Send,
    title: "Profile posts & feed",
    desc: "Share captures and milestones to your profile or straight to social media. Posts, threaded replies, loves, and reposts with image attachments.",
    items: ["Profile feed", "Social sharing", "Threaded replies"],
  },
  {
    icon: MessageCircle,
    title: "Trade board & trading floor",
    desc: "Post specimens to swap or sell, browse HAVE/WANT listings, and chat in-app to close the deal — peer-to-peer, family-friendly, and moderated.",
    items: ["HAVE/WANT listings", "In-app chat", "Pick from wishlist"],
  },
  {
    icon: HelpCircle,
    title: "Community Q&A board",
    desc: "An app-wide feed where rockhounds post questions, photos, and rock stories. Sort by Newest, Most Loved, or Most Commented. Full threaded replies, image attachments, reposts, and a 14-day auto-expire that keeps the feed fresh — expired posts can be browsed and restored from the Archived Posts popup before they're removed permanently.",
    items: ["Sort feeds", "Threaded replies", "Image attachments", "Repost & report", "Archived posts", "14-day auto-expire"],
  },
  {
    icon: Share2,
    title: "Referrals & community rewards",
    desc: "Share your unique referral link from the Profile screen — when friends sign up, you both earn tokens and XP, with celebration pop-ups when they complete sign-up. A built-in profanity filter and screenshot-based reporting keep the community family-friendly, and the Report button on any message or profile flags inappropriate content for review.",
    items: ["Unique referral link", "Both users earn rewards", "Celebration pop-ups", "Profanity filter", "Screenshot reporting"],
  },

  // ── Gamification ──
  {
    icon: Trophy,
    title: "Achievements, XP & badges",
    desc: "101 achievements and 30 badges with confetti level-ups. Earn XP for every action, track your progress on each locked achievement with a visual progress bar, and share brag-worthy level-up cards.",
    items: ["101 achievements", "30 badges", "Progress bars on locked achievements", "XP & leveling"],
  },

  // ── Educational Content & Catalogs ──
  {
    icon: GraduationCap,
    title: "Educational guides",
    desc: "11 built-in guides: BLM lands, the periodic table, paleontology, and more — no internet required. Plus the Aurora Forecaster, Stars & Constellations guide, and a full rock & mineral Glossary for space weather, astronomy, and quick term lookups.",
    items: ["11 guides", "Aurora forecaster", "Night sky guide", "Glossary", "Offline-ready"],
  },
  {
    icon: BookOpen,
    title: "Glossary",
    desc: "A full rock & mineral glossary built right into the field kit. Look up hundreds of geological terms, crystal habits, and mineral properties on the spot — no internet required. Perfect for beginners and experts alike.",
    items: ["Hundreds of terms", "Crystal habits", "Mineral properties", "Offline-ready"],
  },
  {
    icon: Diamond,
    title: "Rocks Are Amazing",
    desc: "A curated gallery of Earth's most stunning formations — enhydros, pseudomorphs, petroleum inclusions, fluorescent minerals, optical phenomena, coprolites, copper-inclusion agates, mineral assemblages, and more. Each wonder opens a full specimen detail page with photos, properties, and where-to-find info. Approved user-submitted specimens can land here alongside the Specimen Database.",
    items: ["Curated wonder gallery", "Swipe-through categories", "User submissions welcome"],
  },
  {
    icon: Landmark,
    title: "Artifacts, relics & stone tools",
    desc: "A growing catalog of 170+ authentic prehistoric artifacts and Civil War / Revolutionary War relics — arrowheads (20+ types), spear points & dart tips, hand axes, flaked stone tools, drill bits, native beads, stone effigies, pipes, ornaments, shell and bone tools, pottery, PLUS 64 war relics: Minie balls, musket balls, artillery shells, uniform buttons (Union eagle & Confederate block), belt buckles (US/CS plates), bayonets, percussion caps, and camp-life items like bone toothbrushes, clay pipes, and hardtack. Each entry has a generated reference image, cultural period or war era, origin/side, and how it was made and used. A NEW badge flags items added in the last 7 days.",
    items: ["170+ artifacts & relics", "Arrowheads", "Civil War bullets", "Uniform buttons", "Belt buckles & plates", "Artillery projectiles", "NEW badge on recent adds"],
  },
  {
    icon: Mountain,
    title: "Natural wonders of the world",
    desc: "72 world-famous geological sites with stunning photos, formation stories, rocks to find, and visitor tips. From the Grand Canyon and Giant's Causeway to Mount Vesuvius, Salar de Uyuni, and the Zhangjiajie Pillars — including the Naica Crystal Caves in Chihuahua, Mexico, where giant selenite crystals grow from floor to ceiling. A perfect bucket-list planner for dig-friendly destinations.",
    items: ["72 geological wonders", "Naica Crystal Caves", "Formation stories", "Rocks to find", "Visitor tips"],
  },
  {
    icon: Library,
    title: "Rock & gem resources",
    desc: "A curated set of trusted external geology, gem, and fossil websites — museums, university mineralogy departments, and reputable reference sites. Links open in your device's browser so you can dig deeper into any topic, making it a perfect research companion alongside the in-app Specimen Database.",
    items: ["Trusted external links", "Museums & universities", "Research companion"],
  },

  // ── Sky & Space ──
  {
    icon: Zap,
    title: "Aurora Forecaster & Space Weather",
    desc: "Real-time space weather with Kp index, Bz, solar wind speed, and visibility status. View 24-hour Kp and 7-day F10.7 trend charts, a 3-day forecast, and active sunspot regions with tappable detail views showing magnetic evolution history. Save custom coordinates to track aurora visibility, set a custom Kp notification threshold for instant push alerts the moment your Kp is reached, and share your Kp status to social media.",
    items: ["Real-time Kp index", "24h Kp trend chart", "7-day F10.7 chart", "3-day forecast", "Sunspot evolution", "Saved spots map", "Custom Kp alerts", "Share to social"],
  },
  {
    icon: Star,
    title: "Stars & Constellations",
    desc: "A complete night sky guide — all 88 IAU constellations with programmatic star charts, 30+ important stars with spectral data, all 8 planets plus dwarf planets, and 37 deep sky objects (galaxies, nebulae, star clusters). Every page features animated twinkling white stars in the background and generated hero images for the most famous objects.",
    items: ["88 constellations", "Star charts", "30+ stars", "10 planets", "37 deep sky objects", "Twinkling stars bg"],
  },

  // ── Search, Alerts & Notifications ──
  {
    icon: Bell,
    title: "Search, alerts & notifications",
    desc: "Global search across specimens, locations, and guides. A dedicated notification center with deep links, a separate mail icon with unread count, instant NWS severe weather alerts the moment they're issued, instant Kp/aurora alerts when your threshold is reached, and proximity pings when you're near a dig site.",
    items: ["Global search", "Notification center", "Message icon", "NWS weather alerts", "Proximity pings"],
  },

  // ── Gear, Storage & Technical ──
  {
    icon: Backpack,
    title: "Gear, tokens & design",
    desc: "Beginner-to-advanced Gear Guide with 77 tools and Amazon links. 7-day free trial, $5.99/mo Premium, or one-time donations for tokens. Immersive 3D design with an optional 2GB offline cache mode plus a one-tap bulk download that caches every specimen photo, guide illustration, and hero image (~3.5 GB) so the whole app works fully offline.",
    items: ["Gear Guide", "Tokens & Premium", "3D design", "2GB cache mode", "Bulk offline download (~3.5 GB)"],
  },
  {
    icon: Database,
    title: "Storage, cache & bulk offline download",
    desc: "Choose Standard (150MB) or Maximum (2GB) cache mode in Storage settings — available to all users, it's your own device storage. The cache covers both specimen photos and map tiles. Want the whole catalog offline? Tap \"Download all images (~3.5 GB)\" to cache every specimen photo plus all educational/guide illustrations and hero art, so every read-only screen (specimen details, geology guides, BLM guide, periodic table) loads instantly with zero signal. A live progress bar with resume and an offline-ready badge keep you informed.",
    items: ["Standard 150MB", "Maximum 2GB", "~3.5 GB bulk download", "Resume on restart", "Offline-ready badge", "Map tile caching"],
  },
  {
    icon: Monitor,
    title: "Desktop web app",
    desc: "The RockScout web app is a full PWA with desktop-optimized layouts — wider multi-column grids, a split map + location list view, and grids that use your screen space efficiently. Install the PWA to your desktop for a standalone app window.",
    items: ["Desktop multi-column grids", "Map + list split view", "Installable PWA"],
  },

  // ── Safety & Account ──
  {
    icon: ShieldCheck,
    title: "App updates, sign-in & safety",
    desc: "When a signing conflict is detected during an update, a friendly dialog explains that the old version must be uninstalled and offers a button to trigger the system uninstall flow. After reinstalling and signing back in, all your settings are restored from the cloud exactly as they were. A confirmation dialog on the logout button prevents accidental sign-outs, and your collections, captures, friends, and achievements are tied to your account — not your device.",
    items: ["Signing conflict dialog", "Settings restored after reinstall", "Logout confirmation", "Account-tied data"],
  },

  // ── Group Chats & Messaging ──
  {
    icon: MessageCircle,
    title: "Group chats",
    desc: "Create group chats with a name, subject, member cap, header image, and profanity filter level (normal or strict). Invite friends — they get a popup to accept or decline. The creator can delete the group at any time. Group chats support all the same features as private chats: image sending, reply threading, user tagging, and scroll speed controls.",
    items: ["Create & name groups", "Subject & header image", "Member caps", "Profanity filter levels", "Invite accept/decline", "Creator delete"],
  },
  {
    icon: Send,
    title: "Reply threading & user tagging",
    desc: "Long-press any message to reply to it — the original comment appears in a preview bar above your input, and your reply shows indented under the original. Type @username to tag someone — their name appears in a bright Citrine pill so they know they got a reply. Tags work in both private and group chats.",
    items: ["Long-press to reply", "Reply preview bar", "Indented reply format", "@username tagging", "Citrine pill highlights"],
  },
  {
    icon: Zap,
    title: "Scroll speed controls",
    desc: "Control how the chat auto-scrolls to new messages: Normal (instant), Half (4-second delay), or Stop (no auto-scroll). When you're scrolled up reading older messages, a Current button jumps you to the latest message instantly.",
    items: ["Normal / Half / Stop", "Current jump button", "Per-user preference"],
  },
  {
    icon: Bell,
    title: "Drafts in notifications",
    desc: "Unfinished email and chat drafts are automatically saved and appear as a notification row. Tap a draft to resume right where you left off — never lose a half-typed message again.",
    items: ["Auto-saved chat drafts", "Email draft persistence", "Resume from notifications"],
  },
  {
    icon: HelpCircle,
    title: "Compact search bars",
    desc: "Search bars across the app collapse into compact single-row pills that expand on tap — saving screen space while keeping search always accessible. Found on Community, Messenger, Specimens, Natural Wonders, Glossary, Dinosaur Dictionary, and more.",
    items: ["Collapsible pills", "Tap to expand", "Saves screen space"],
  },
  {
    icon: ShieldCheck,
    title: "Profanity warning system",
    desc: "A two-tier profanity filter keeps chat family-friendly. Common profanity is silently asterisked. Explicit language is asterisked and triggers a warning popup. Three warnings auto-report the user, five trigger a second report, and six result in a ban. False positives can be reported via support email.",
    items: ["Two-tier filter", "Silent asterisks", "Warning popups", "Auto-report at 3", "Ban at 6", "False-positive support"],
  },
  {
    icon: Landmark,
    title: "Museum add button",
    desc: "Browse a directory of rock, gem, and mineral museums across the country. Found a museum that isn't listed? Use the Add a Museum button on the Museums tab to submit it — after review, it appears in the museum directory for every RockScout user to discover.",
    items: ["Museum directory", "State filter", "Add a Museum button", "User-submitted", "Reviewed before going live"],
  },
  {
    icon: Users,
    title: "Add users to private chats",
    desc: "Add up to 10 users to a private chat thread. The invited user gets an accept/cancel popup — once accepted, they're part of the conversation with full messaging, image sharing, and reply threading support.",
    items: ["10-user max", "Accept/cancel popup", "Full messaging support"],
  },
];

const ScreenshotStrip = () => (
  <section className="py-16 sm:py-24">
    <div className="mx-auto max-w-6xl px-4 sm:px-6">
      <div className="flex items-end justify-between gap-6">
        <div>
          <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
            <span className="h-px w-8 bg-primary/50" /> In the field
          </span>
          <h2 className="mt-4 font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl">See it for real</h2>
          <p className="mt-3 text-sm text-muted-foreground sm:text-base">No mockups here — these are actual screens from the Android app.</p>
        </div>
        <span className="hidden shrink-0 items-center gap-1.5 rounded-full border border-border bg-card/50 px-3 py-1.5 text-xs text-muted-foreground sm:inline-flex">
          <Star className="h-3.5 w-3.5 fill-primary text-primary" /> Swipe to explore
        </span>
      </div>
      <div className="mt-8 flex snap-x gap-4 overflow-x-auto pb-6 [scrollbar-width:thin] sm:mt-10">
        {SHOTS.map((s, i) => (
          <figure
            key={s.src}
            className="group relative w-[200px] flex-none snap-center overflow-hidden rounded-3xl border border-border/70 bg-card p-2.5 shadow-lg transition-all hover:-translate-y-1.5 hover:border-primary/40 hover:shadow-[0_20px_50px_-20px_hsl(var(--primary))] sm:w-[230px]"
          >
            <img
              src={s.src}
              alt={s.alt}
              className="aspect-[9/19.5] w-full rounded-2xl object-cover"
              loading="lazy"
            />
            <span className="absolute left-4 top-5 rounded-full bg-background/80 px-2 py-0.5 font-display text-xs font-bold text-primary backdrop-blur">
              {String(i + 1).padStart(2, "0")}
            </span>
            <figcaption className="sr-only">{s.alt}</figcaption>
          </figure>
        ))}
      </div>
    </div>
  </section>
);

type FieldStory =
  | { type: "story"; src: string; alt: string; caption: string; span: string }
  | { type: "gear"; span: string };

const FIELD_STORIES: FieldStory[] = [
  {
    type: "gear",
    span: "row-span-2 md:col-span-2 md:row-span-2",
  },
  {
    type: "story",
    src: "/images/campfire-night.webp",
    alt: "Rockhounds gathered around a campfire at night with buckets of specimens and tents",
    caption: "Share finds around the campfire. The trade board and messenger keep the crew swapping stories and specimens long after the fire burns out — your collecting crew stays together between trips.",
    span: "md:col-span-2 md:row-span-2",
  },
  {
    type: "story",
    src: "/images/desert-campfire.webp",
    alt: "Diverse group of collectors around a desert campfire under the Milky Way",
    caption: "Build your collecting crew. Discover hunters near you or worldwide, send friend requests, and message inside the app — RockScout's moderated, family-friendly community is built for collectors who want to do this for life.",
    span: "md:col-span-2 md:row-span-1",
  },
  {
    type: "story",
    src: "/images/uv-night-hunt.webp",
    alt: "Collectors searching a rocky beach at dusk with UV flashlights",
    caption: "UV night hunts for fluorescent minerals. A 365nm flashlight turns fluorite, calcite, and willemite into glowing beacons in the dark — log the finds with the Field Camera and let the AI ID them once you're back in signal.",
    span: "md:col-span-2 md:row-span-1",
  },
  {
    type: "story",
    src: "/images/trip-planning.webp",
    alt: "Group of rockhounds planning a trip over maps on a tailgate",
    caption: "Plan group trips to dig sites. The Trip Planner handles multi-stop routes, gear checklists, and custom pins so everyone shows up at the right outcrop with the right tools.",
    span: "md:col-span-2 md:row-span-1",
  },
  {
    type: "story",
    src: "/images/field-journal.webp",
    alt: "Rockhound writing notes in a field journal surrounded by specimens",
    caption: "Log every specimen in your field journal. Auto-weather, photos, and notes are pinned to each entry so the day lives on after you drive home — searchable and shareable from your profile.",
    span: "md:col-span-2 md:row-span-1",
  },
];

type GearItem = { emoji: string; name: string; price: string; url: string; topPick?: boolean };

const GEAR_ITEMS: GearItem[] = [
  { emoji: "🔍", name: "10x Jeweler's Loupe", price: "$8 – $15", url: "https://www.amazon.com/s?k=10x+jewelers+loupe&tag=rockscout-20" },
  { emoji: "🔍", name: "30x Triplet Loupe", price: "$20 – $40", url: "https://www.amazon.com/s?k=30x+triplet+loupe&tag=rockscout-20" },
  { emoji: "🔬", name: "Headband Magnifier", price: "$15 – $50", url: "https://www.amazon.com/s?k=headband+magnifier+hands+free&tag=rockscout-20" },
  { emoji: "🗿", name: "Mohs Hardness Kit", price: "$25 – $60", url: "https://www.amazon.com/s?k=mohs+hardness+kit&tag=rockscout-20" },
  { emoji: "🖨️", name: "Streak Plate", price: "$5 – $10", url: "https://www.amazon.com/s?k=streak+plate+unglazed+porcelain&tag=rockscout-20" },
  { emoji: "🔨", name: "Rock Pick Hammer", price: "$25 – $50", url: "https://www.amazon.com/s?k=rock+pick+hammer+geology&tag=rockscout-20" },
  { emoji: "🔨", name: "Estwing Rock Pick", price: "$45 – $80", url: "https://www.amazon.com/s?k=estwing+rock+pick&tag=rockscout-20" },
  { emoji: "🛠", name: "Rock Chisel Set", price: "$15 – $35", url: "https://www.amazon.com/s?k=rock+chisel+set+geology&tag=rockscout-20" },
  { emoji: "🔨", name: "3 lb Crack Hammer", price: "$25 – $45", url: "https://www.amazon.com/s?k=3lb+crack+hammer&tag=rockscout-20" },
  { emoji: "🪝", name: "Pick and Hook Tool Set", price: "$12 – $25", url: "https://www.amazon.com/s?k=pick+and+hook+tool+set&tag=rockscout-20" },
  { emoji: "✏️", name: "Pneumatic Air Scribe / Prep Pen", price: "$40 – $120", url: "https://www.amazon.com/s?k=pneumatic+air+scribe+prep+pen&tag=rockscout-20" },
  { emoji: "🎒", name: "Field Collection Bag", price: "$20 – $45", url: "https://www.amazon.com/s?k=rockhound+field+bag&tag=rockscout-20" },
  { emoji: "🎒", name: "Waterproof Field Bag", price: "$25 – $60", url: "https://www.amazon.com/s?k=waterproof+dry+bag+field&tag=rockscout-20" },
  { emoji: "🎒", name: "Hiking Daypack", price: "$40 – $130", url: "https://www.amazon.com/s?k=hiking+backpack+daypack&tag=rockscout-20" },
  { emoji: "🪣", name: "Telescoping Rock Scoop", price: "$30 – $90", url: "https://www.amazon.com/s?k=telescoping+rock+scoop&tag=rockscout-20" },
  { emoji: "🪰", name: "Mesh Sifting Sieve", price: "$20 – $60", url: "https://www.amazon.com/s?k=mesh+sifting+sieve+geology&tag=rockscout-20" },
  { emoji: "🥤", name: "Collapsible Strainer", price: "$8 – $20", url: "https://www.amazon.com/s?k=collapsible+colander+strainer&tag=rockscout-20" },
  { emoji: "🧹", name: "Stiff Plastic-Bristle Brush", price: "$5 – $15", url: "https://www.amazon.com/s?k=stiff+bristle+brush+cleaning&tag=rockscout-20" },
  { emoji: "🧲", name: "Fishing Magnets", price: "$15 – $60", url: "https://www.amazon.com/s?k=fishing+magnet+neodymium+rope&tag=rockscout-20" },
  { emoji: "⛏️", name: "Foldable Shovel", price: "$20 – $50", url: "https://www.amazon.com/s?k=foldable+shovel+compact&tag=rockscout-20" },
  { emoji: "🛒", name: "Gorilla Cart", price: "$80 – $150", url: "https://www.amazon.com/s?k=gorilla+cart+garden&tag=rockscout-20" },
  { emoji: "🔦", name: "365nm UV Flashlight", price: "$15 – $40", url: "https://www.amazon.com/s?k=365nm+uv+flashlight&tag=rockscout-20" },
  { emoji: "🟣", name: "Shortwave UV Light", price: "$80 – $300", url: "https://www.amazon.com/s?k=shortwave+uv+light+254nm&tag=rockscout-20" },
  { emoji: "📓", name: "Rite-in-the-Rain Notebook", price: "$10 – $25", url: "https://www.amazon.com/s?k=rite+in+the+rain+notebook&tag=rockscout-20" },
  { emoji: "💧", name: "Small Spray Bottle", price: "$3 – $8", url: "https://www.amazon.com/s?k=small+spray+bottle&tag=rockscout-20" },
  { emoji: "🗺", name: "Handheld GPS Unit", price: "$100 – $300", url: "https://www.amazon.com/s?k=handheld+gps+geocaching&tag=rockscout-20" },
  { emoji: "🌧️", name: "Rain Gear / Shell", price: "$30 – $150", url: "https://www.amazon.com/s?k=rain+gear+waterproof+jacket+pants&tag=rockscout-20" },
  { emoji: "🥽", name: "Chest Waders", price: "$50 – $180", url: "https://www.amazon.com/s?k=fishing+waders+chest&tag=rockscout-20" },
  { emoji: "🥾", name: "Waterproof Hiking Boots", price: "$80 – $200", url: "https://www.amazon.com/s?k=waterproof+hiking+boots+ankle&tag=rockscout-20" },
  { emoji: "👒", name: "Sun Hat / Beach Hat", price: "$15 – $40", url: "https://www.amazon.com/s?k=sun+hat+upf+wide+brim&tag=rockscout-20" },
  { emoji: "🥽", name: "Safety Glasses", price: "$5 – $20", url: "https://www.amazon.com/s?k=safety+glasses+impact&tag=rockscout-20" },
  { emoji: "🧤", name: "Waterproof Gloves", price: "$15 – $40", url: "https://www.amazon.com/s?k=waterproof+work+gloves&tag=rockscout-20" },
  { emoji: "🦵", name: "Kneepads", price: "$15 – $40", url: "https://www.amazon.com/s?k=kneepads+work+padded&tag=rockscout-20" },
  { emoji: "🧘", name: "Kneeling Pad", price: "$10 – $25", url: "https://www.amazon.com/s?k=foam+kneeling+pad&tag=rockscout-20" },
  { emoji: "🦟", name: "Bug Spray", price: "$5 – $15", url: "https://www.amazon.com/s?k=bug+spray+deet+repellent&tag=rockscout-20" },
  { emoji: "🔥", name: "Hand Warmers", price: "$5 – $20", url: "https://www.amazon.com/s?k=hand+warmers+disposable+rechargeable&tag=rockscout-20" },
  { emoji: "🥏", name: "Gold Pan", price: "$10 – $40", url: "https://www.amazon.com/s?k=gold+pan+prospecting&tag=rockscout-20" },
  { emoji: "💰", name: "Portable Gold Sluice", price: "$60 – $200", url: "https://www.amazon.com/s?k=portable+gold+sluice+folding&tag=rockscout-20" },
  { emoji: "🪨", name: "Rock Collection Starter Kit", price: "$20 – $60", url: "https://www.amazon.com/s?k=rock+collection+starter+kit+minerals&tag=rockscout-20" },
  { emoji: "📦", name: "Mineral & Crystal Display Cases", price: "$15 – $80", url: "https://www.amazon.com/s?k=mineral+display+case+riker+mount&tag=rockscout-20" },
  { emoji: "💎", name: "Lapidary Equipment", price: "$100 – $500", url: "https://www.amazon.com/s?k=lapidary+equipment+cabbing+machine&tag=rockscout-20" },
  { emoji: "⛺", name: "Camping Equipment", price: "$50 – $300", url: "https://www.amazon.com/s?k=camping+gear+bundle+essentials&tag=rockscout-20" },
  { emoji: "⛺", name: "Camping Tent", price: "$80 – $250", url: "https://www.amazon.com/s?k=camping+tent+3+person&tag=rockscout-20" },
  { emoji: "🛌", name: "Sleeping Bag", price: "$50 – $180", url: "https://www.amazon.com/s?k=sleeping+bag+3+season+30+degree&tag=rockscout-20" },
  { emoji: "💡", name: "Solar / Battery Lantern", price: "$20 – $60", url: "https://www.amazon.com/s?k=solar+battery+camping+lantern&tag=rockscout-20" },
  { emoji: "📻", name: "Midland NOAA Weather Radio", price: "$30 – $70", url: "https://www.amazon.com/s?k=midland+noaa+weather+radio+hand+crank&tag=rockscout-20" },
  { emoji: "👀", name: "Underwater Viewing Bucket", price: "$25 – $60", url: "https://www.amazon.com/s?k=underwater+viewing+bucket&tag=rockscout-20" },
];

const FieldStories = () => {
  const topPickNames = useMemo(() => getTopPickNames(), []);
  return (
  <section className="relative py-16 scroll-mt-20 sm:py-24" id="supplies">
    <div className="mx-auto max-w-6xl px-4 sm:px-6">
      <div className="mb-10 flex flex-col gap-4 sm:mb-14 md:flex-row md:items-end md:justify-between">
        <div className="max-w-2xl">
          <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
            <span className="h-px w-8 bg-primary/50" /> In the wild
          </span>
          <h2 className="mt-4 font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl">
            Built for real rockhounds
          </h2>
          <p className="mt-4 text-balance text-base text-muted-foreground sm:text-lg">
            Campfire IDs, midnight UV hunts, group trips, and field journals — RockScout is made for the moments that happen off-trail.
          </p>
        </div>
        <span className="hidden shrink-0 items-center gap-1.5 rounded-full border border-border bg-card/50 px-3 py-1.5 text-xs text-muted-foreground sm:inline-flex">
          <Star className="h-3.5 w-3.5 fill-primary text-primary" /> Founder & community shots
        </span>
      </div>

      <div className="grid grid-cols-1 gap-3 auto-rows-[150px] sm:auto-rows-[260px] sm:grid-cols-2 sm:gap-4 md:grid-cols-4">
        {FIELD_STORIES.map((story) => (
          <figure
            key={story.type === "gear" ? "gear" : story.src}
            className={`group relative overflow-hidden rounded-3xl border border-border/70 bg-card shadow-md transition-all hover:-translate-y-1 hover:border-primary/40 hover:shadow-xl ${story.span}`}
          >
            {story.type === "gear" ? (
              <>
                <div className="relative flex h-full flex-col p-4 sm:p-5">
                  <div className="flex items-center justify-between gap-3">
                    <p className="font-display text-base font-semibold text-foreground sm:text-lg">Gear up for the field</p>
                    <span className="hidden rounded-full border border-primary/30 bg-primary/10 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider text-primary sm:inline-block">
                      Amazon
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-muted-foreground">The same gear guide linked inside the app — curated for every kind of rockhound.</p>
                  <ul className="mt-2 flex-1 space-y-1 overflow-y-auto pr-1 [scrollbar-width:thin] sm:mt-3">
                    {GEAR_ITEMS.map((item) => (
                      <li key={item.name}>
                        <a
                          href={item.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={() => recordAffiliateClick(item.name.replace(/\s+/g, '_').toLowerCase(), item.name)}
                          className={`flex items-center gap-2 rounded-xl border px-2.5 py-1.5 text-xs text-card-foreground transition-colors hover:border-primary/40 hover:bg-primary/5 ${
                            topPickNames.has(item.name) ? "border-amber-500/50 bg-amber-500/5" : "border-border/70 bg-card"
                          }`}
                        >
                          <span className="text-sm" aria-hidden>{item.emoji}</span>
                          <span className="flex-1 truncate font-medium">{item.name}</span>
                          {topPickNames.has(item.name) && (
                            <span className="inline-flex shrink-0 items-center gap-0.5 rounded-full bg-amber-500/15 px-1 py-0.5 text-[9px] font-bold uppercase tracking-wide text-amber-500 ring-1 ring-amber-500/40">
                              <Star className="h-2 w-2 fill-amber-500 text-amber-500" />
                              Top
                            </span>
                          )}
                          <span className="shrink-0 text-muted-foreground">{item.price}</span>
                          <ExternalLink className="h-3 w-3 shrink-0 opacity-60" />
                        </a>
                      </li>
                    ))}
                  </ul>
                  <p className="mt-2 text-[10px] leading-tight text-muted-foreground">
                    As an Amazon Associate, RockScout earns from qualifying purchases.
                  </p>
                </div>
              </>
            ) : (
              <>
                <div className="absolute inset-0">
                  <img
                    src={story.src}
                    alt={story.alt}
                    className="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/85 via-black/40 to-black/10" />
                </div>
                <figcaption className="absolute bottom-0 left-0 w-full p-3 sm:p-4">
                  <p className="font-display text-sm font-semibold leading-snug text-white drop-shadow-[0_2px_4px_rgba(0,0,0,0.9)]">{story.caption}</p>
                </figcaption>
              </>
            )}
          </figure>
        ))}
      </div>
    </div>
  </section>
  );
};

const CTA = () => {
  const [premiumDialogOpen, setPremiumDialogOpen] = useState(false);
  return (
  <section className="mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-24">
    <div className="relative overflow-hidden rounded-[1.5rem] border border-primary/30 bg-gradient-to-br from-card via-card to-primary/10 p-6 text-center sm:rounded-[2rem] sm:p-10 md:p-20">
      <div className="pointer-events-none absolute inset-0 geode-gradient opacity-70" aria-hidden />
      {/* Floating sparkles in CTA */}
      <div className="pointer-events-none absolute inset-0" aria-hidden>
        <Sparkle top="18%" left="12%" delay="0s" duration="3s" color="hsl(36 80% 58%)" />
        <Sparkle top="70%" left="84%" delay="1s" duration="4s" color="hsl(20 62% 65%)" />
        <Sparkle top="30%" left="78%" delay="2s" duration="3.5s" color="hsl(172 30% 28%)" />
      </div>
      <div className="relative">
        <h2 className="text-balance font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl">
          Ready to find your next specimen?
        </h2>
        <p className="mx-auto mt-4 max-w-md text-balance text-base text-muted-foreground sm:mt-5 sm:text-lg">
          RockScout is coming soon to Google Play and the App Store. Be the first to know when it launches.
        </p>
        <div className="mt-7 flex flex-col items-center justify-center gap-3">
          <span
            aria-disabled
            className="inline-flex h-14 w-56 cursor-default items-center justify-center gap-2.5 rounded-full bg-primary px-5 font-semibold text-primary-foreground"
          >
            <Download className="h-5 w-5 shrink-0" />
            <span className="text-center leading-tight">
              Coming soon to Google Play
            </span>
          </span>
          <Link
            to="/install/free"
            className="inline-flex h-14 w-56 items-center justify-center gap-2.5 rounded-full border border-primary/40 bg-primary/10 px-5 font-semibold text-primary/80 transition-transform hover:scale-[1.02] active:scale-[0.98]"
          >
            <Monitor className="h-5 w-5 shrink-0" />
            <span className="text-center leading-tight">
              Install Free PWA
            </span>
          </Link>
          <button
            type="button"
            onClick={() => setPremiumDialogOpen(true)}
            className="inline-flex h-14 w-56 items-center justify-center gap-2.5 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 px-5 font-semibold text-amber-600 transition-transform hover:scale-[1.02] active:scale-[0.98] dark:text-amber-400"
          >
            <Crown className="h-5 w-5 shrink-0" />
            <span className="text-center leading-tight">
              Install Premium PWA
            </span>
          </button>
          <span
            aria-disabled
            className="inline-flex h-14 w-56 cursor-default items-center justify-center gap-2.5 rounded-full border border-border bg-card/40 px-5 font-semibold text-muted-foreground/60"
          >
            <Smartphone className="h-5 w-5 shrink-0" />
            <span className="text-center leading-tight">
              Coming soon to iOS
            </span>
          </span>
          <Link
            to="/support"
            className="inline-flex items-center justify-center gap-1.5 rounded-2xl border border-border bg-card/60 px-6 py-4 text-sm font-medium text-foreground transition-colors hover:bg-card"
          >
            Questions? Visit Support <ChevronRight className="h-4 w-4" />
          </Link>
        </div>
        <ul className="mx-auto mt-9 flex max-w-md flex-wrap items-center justify-center gap-x-5 gap-y-2 text-xs text-muted-foreground">
          <li className="inline-flex items-center gap-1.5"><Check className="h-3.5 w-3.5 text-primary" /> Free tier recommended for everyone</li>
          <li className="inline-flex items-center gap-1.5"><BadgeCheck className="h-3.5 w-3.5 text-primary" /> Premium (18+): unlimited 5-source IDs + social</li>
          <li className="inline-flex items-center gap-1.5"><ShieldCheck className="h-3.5 w-3.5 text-primary" /> Safety is the 1st, 2nd & 3rd rule</li>
          <li className="inline-flex items-center gap-1.5"><Check className="h-3.5 w-3.5 text-primary" /> Moderated, family-friendly community</li>
        </ul>
      </div>
    </div>
    <PremiumInstallDialog open={premiumDialogOpen} onOpenChange={setPremiumDialogOpen} />
  </section>
  );
};

const Landing = () => {
  // JSON-LD structured data for the landing page
  const jsonLd = {
    "@context": "https://schema.org",
    "@type": "SoftwareApplication",
    name: SITE.name,
    applicationCategory: "LifestyleApplication",
    operatingSystem: "Android",
    url: SITE.url,
    description: SITE.description,
    offers: { "@type": "Offer", price: "0", priceCurrency: "USD" },
    screenshot: SHOTS.map((s) => `${SITE.url}${s.src}`),
  };
  if (typeof document !== "undefined") {
    let script = document.getElementById("ld-jsonld") as HTMLScriptElement | null;
    if (!script) {
      script = document.createElement("script");
      script.id = "ld-jsonld";
      script.type = "application/ld+json";
      document.head.appendChild(script);
    }
    script.textContent = JSON.stringify(jsonLd);
  }
  return (
    <Layout title={`${SITE.name} — ${SITE.tagline}`} description={SITE.description} ogImage="/images/og-share-card.webp">
      <Hero />
      <SpecimenMarquee />
      <Features />
      <ScreenshotStrip />
      <SpecimenMarquee reverse />
      <FieldStories />
      <SpecimenMarquee />
      <section id="features" className="relative scroll-mt-24 py-16 sm:py-24">
        <div className="mx-auto max-w-6xl px-4 sm:px-6">
          <div className="max-w-2xl">
            <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
              <span className="h-px w-8 bg-primary/50" /> Full feature list
            </span>
            <h2 className="mt-4 font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl">
              Everything packed into one app
            </h2>
            <p className="mt-4 text-balance text-base text-muted-foreground sm:text-lg">
              Related tools grouped together — so you can see the full picture without scrolling through a wall of boxes.
            </p>
          </div>
          <ul className="mt-10 grid gap-4 sm:mt-14 sm:gap-5 md:grid-cols-2 lg:grid-cols-3">
            {FEATURES_LIST.map((f) => (
              <li
                key={f.title}
                className="group flex flex-col gap-3 rounded-2xl border border-border/60 bg-background/40 p-4 transition-all hover:-translate-y-0.5 hover:border-primary/40 hover:bg-card/60 sm:p-5"
              >
                <div className="flex items-center gap-3">
                  <span className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25 transition-transform group-hover:scale-110 sm:h-10 sm:w-10">
                    <f.icon className="h-4 w-4 sm:h-5 sm:w-5" />
                  </span>
                  <p className="font-display font-semibold text-foreground">{f.title}</p>
                </div>
                <p className="text-sm leading-relaxed text-muted-foreground">{f.desc}</p>
                {f.items && (
                  <ul className="mt-auto flex flex-wrap gap-1.5 pt-1">
                    {f.items.map((it) => (
                      <li key={it} className="rounded-full border border-border/70 bg-background/60 px-2.5 py-0.5 text-[11px] font-medium text-muted-foreground">{it}</li>
                    ))}
                  </ul>
                )}
              </li>
            ))}
          </ul>
        </div>
      </section>
      <SpecimenMarquee reverse />
      <CTA />
    </Layout>
  );
};

export default Landing;
