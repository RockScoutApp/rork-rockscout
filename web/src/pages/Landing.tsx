import { Link } from "react-router-dom";
import {
  Camera,
  Map as MapIcon,
  Gem,
  Users,
  Sparkles,
  ShieldCheck,
  ChevronRight,
  Download,
  Apple,
  Check,
  Mountain,
  Compass,
  Atom,
  MessageCircle,
  Star,
  BookOpen,
  Heart,
  Bell,
  Trophy,
  FlaskConical,
  Search,
  Backpack,
  Send,
  MapPin,
  Mail,
  Image as ImageIcon,
  GraduationCap,
  Boxes,
  Layers,
  Sun,
  Cloud,
  ShoppingBag,
  Eye,
  Camera as Camera2,
  Plus,
  Route,
  NotebookPen,
  Globe2,
  BadgeCheck,
  Gift,
  Bookmark,
  ExternalLink,
  MessageSquare,
  Upload,
  Share2,
  Database,
  HelpCircle,
  Diamond,
  School,
  Library,
  Hammer,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { SITE } from "@/content/legal";

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
    body: "Snap a photo and get a best-effort ID in seconds. RockScout cross-checks five sources — visual AI, 918+ specimen profiles, locality data, physical properties, and community consensus — so you get a confident match.",
    tag: "5 ways to identify",
    image: "/images/rock-id-collection.webp",
    imageAlt: "A collection of colorful rocks and minerals with a magnifying glass",
  },
  {
    icon: MapIcon,
    title: "Field maps & dig sites",
    body: "Discover nearby dig sites, gem shows, and BLM collecting areas. Drop pins, plan trips, cache tiles for offline, and get proximity alerts when you're close to a good spot.",
    tag: "Never miss a outcrop",
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

const STEPS = [
  {
    n: "01",
    icon: Camera,
    title: "Snap a specimen",
    body: "Open the field camera and point it at any rock, crystal, or mineral. No prep, no perfect lighting required.",
  },
  {
    n: "02",
    icon: Atom,
    title: "Get a 5-source ID",
    body: "Visual AI, 918+ specimen profiles, locality data, physical properties, and community consensus all weigh in to give you a best-effort match in seconds."
  },
  {
    n: "03",
    icon: Gem,
    title: "Save to your collection",
    body: "Tag it with location, notes, and rarity. Build a wishlist of what you're hunting next. Level up as your catalog grows.",
  },
  {
    n: "04",
    icon: MessageCircle,
    title: "Trade with the community",
    body: "List duplicates on the trade board, message other collectors, and swap specimens — all in a moderated, family-friendly space.",
  },
];

const STATS = [
  { value: "918+", label: "Specimens in the database" },
  { value: "50", label: "US states & territories mapped" },
  { value: "4.9★", label: "Early tester rating" },
  { value: "Free", label: "To browse + field camera · Pro unlocks IDs & social" },
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

const Hero = () => (
  <section className="relative overflow-hidden grain">
    {/* Sparkle particles echoing the app's specimen-card motif */}
    <div className="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden>
      <Sparkle top="12%" left="18%" delay="0s" duration="3.2s" color="hsl(36 80% 58%)" />
      <Sparkle top="22%" left="72%" delay="0.6s" duration="4s" color="hsl(20 62% 65%)" />
      <Sparkle top="44%" left="8%" delay="1.2s" duration="3.6s" color="hsl(172 30% 55%)" />
      <Sparkle top="64%" left="82%" delay="0.3s" duration="4.4s" color="hsl(36 80% 58%)" />
      <Sparkle top="76%" left="30%" delay="1.8s" duration="3s" color="hsl(20 62% 65%)" />
      <Sparkle top="32%" left="48%" delay="2.1s" duration="5s" color="hsl(172 30% 55%)" />
      <Sparkle top="58%" left="62%" delay="0.9s" duration="3.8s" color="hsl(36 80% 58%)" />
      <Sparkle top="8%" left="88%" delay="2.4s" duration="4.2s" color="hsl(48 86% 70%)" />
    </div>

    <div className="geode-gradient absolute inset-0 -z-10" aria-hidden />
    {/* Slow-rotating compass rose behind the phone */}
    <div className="pointer-events-none absolute right-[8%] top-1/2 hidden -translate-y-1/2 md:block" aria-hidden>
      <div className="animate-spin-slow text-border/30">
        <Compass className="h-[34rem] w-[34rem]" strokeWidth={0.5} />
      </div>
    </div>

    <div className="mx-auto grid max-w-6xl items-center gap-12 px-4 pt-10 pb-20 sm:px-6 md:grid-cols-[1.05fr_1fr] md:py-28">
      <div className="fade-rise">
        <div className="flex flex-wrap items-center gap-2">
          <span className="inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-3.5 py-1.5 text-xs font-semibold uppercase tracking-wider text-primary">
            <Sparkles className="h-3.5 w-3.5" /> Field companion for rockhounds
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
        </div>
        <img
          src="/images/rockscout-hero-logo.webp"
          alt="RockScout — Identify, Explore, Connect: the most comprehensive rockhounding app ever created"
          className="mt-8 w-full max-w-xl rounded-2xl shadow-[0_20px_60px_-20px_rgba(0,0,0,0.5)] ring-1 ring-primary/20"
          loading="eager"
        />
        <h1 className="sr-only">RockScout — Identify, Explore, Connect</h1>
        <p className="mt-6 max-w-md text-balance text-base leading-relaxed text-muted-foreground sm:text-lg">
          RockScout puts a pocket geologist in your phone — AI rock &amp; mineral ID, dig-site maps, a
          specimen database, a collection tracker, and a community of hunters and traders — built for the field, ready for any dig.
        </p>

        <div className="mt-9 flex flex-col gap-3 sm:flex-row sm:items-center">
          <span
            aria-disabled="true"
            className="inline-flex items-center justify-center gap-2.5 rounded-2xl border border-primary/40 bg-primary/10 px-6 py-3.5 font-semibold text-primary/80"
          >
            <Download className="h-5 w-5" />
            Coming soon to Google Play
          </span>
          <span className="inline-flex items-center justify-center gap-2 rounded-2xl border border-border bg-card/50 px-5 py-3.5 text-sm font-medium text-muted-foreground">
            <Apple className="h-4 w-4" /> iOS coming soon
          </span>
        </div>

        <dl className="mt-12 grid max-w-md grid-cols-2 gap-x-6 gap-y-5">
          {STATS.map((s) => (
            <div key={s.label} className="border-l-2 border-primary/30 pl-3">
              <dt className="font-display text-3xl font-bold text-foreground">{s.value}</dt>
              <dd className="text-xs leading-snug text-muted-foreground">{s.label}</dd>
            </div>
          ))}
        </dl>
      </div>

      {/* Phone mockup with floating specimen chips arranged around it (not on it) */}
      <div className="relative fade-rise flex justify-center px-4 sm:px-10" style={{ animationDelay: "120ms" }}>
        <div className="relative w-full max-w-[480px] py-8 sm:py-12">
          {/* Amber glow behind phone */}
          <div className="absolute inset-x-[20%] inset-y-[10%] -z-10 animate-glow-pulse rounded-[3rem] bg-primary/15 blur-3xl" aria-hidden />
          <div className="relative mx-auto max-w-[240px] float-slow rounded-[2.5rem] border border-border/70 bg-card p-3 shadow-2xl ring-1 ring-primary/20">
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
            glyph={<Sparkles className="h-5 w-5" />}
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
);

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
          <div className={`relative p-7 ${f.image ? "-mt-16" : ""}`}>
            <div className="flex items-center justify-between">
              <div className="inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/15 text-primary ring-1 ring-primary/30 transition-transform group-hover:scale-110">
                <f.icon className="h-6 w-6" />
              </div>
              <span className="rounded-full border border-border bg-background/60 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-muted-foreground">
                {f.tag}
              </span>
            </div>
            <h3 className="mt-5 font-display text-2xl font-semibold">{f.title}</h3>
            <p className="mt-3 text-sm leading-relaxed text-muted-foreground">{f.body}</p>
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
  {
    icon: Camera,
    title: "AI rock & mineral ID",
    desc: "Snap a photo and three models (Claude Haiku, Sonnet, Gemini Pro) cross-check five sources — visual AI, 918+ specimen profiles, locality data, physical properties, and community consensus — to give you a confident match in seconds. Free 7-day trial with 5 tokens; after that, Premium or a donation keeps it going.",
    items: ["Visual reference compare", "Assemblage detection", "Clarifying questions", "Auto web cross-check"],
  },
  {
    icon: FlaskConical,
    title: "Specimen database",
    desc: "918+ entries with photos, properties, and where-to-find locations. Spot a missing one? Submit photos of your find and approved entries go into the shared database for everyone.",
    items: ["918+ profiles", "Community submissions"],
  },
  {
    icon: Gem,
    title: "Collection & wishlist",
    desc: "Catalog every specimen you bring home with photos, notes, and rarity. Keep a separate wishlist of dream specimens you're hunting next. Heart any specimen to mark it as a personal favorite — liking is independent from your wishlist.",
    items: ["My Rocks", "Wishlist", "Heart favorites", "Specimen map", "Saved images", "Favorite dig spots"],
  },
  {
    icon: BookOpen,
    title: "Field journal & camera",
    desc: "Log daily adventures with auto-weather, photos, and notes. The Field Camera snaps a quick capture without running the full ID tool, so you can save a find anywhere. Stays free after the trial — snaps save to your in-app Saved Images folder at no cost.",
    items: ["Auto-weather entries", "Quick field captures", "Free after trial"],
  },
  {
    icon: MapIcon,
    title: "Dig sites & offline maps",
    desc: "Free, public, and pay-to-dig sites, plus rock, gem & metaphysical shops and gem shows by month. Tap-to-drop-pin tile downloads on every map, with 3-state satellite zoom (16 → 19) for the detail you need.",
    items: ["Nationwide sites", "Shops & shows", "Offline tiles", "Park Here breadcrumb", "3-state zoom"],
  },
  {
    icon: Route,
    title: "Trip Planner",
    desc: "Build multi-stop routes with gear checklists and custom map pins, so the whole crew knows where to meet and what to bring.",
    items: ["Multi-stop routes", "Gear checklists", "Custom pins"],
  },
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
    icon: Trophy,
    title: "Achievements, XP & badges",
    desc: "101 achievements and 31 badges with confetti level-ups. Earn XP for every action and share brag-worthy level-up cards.",
    items: ["101 achievements", "31 badges", "XP & leveling"],
  },
  {
    icon: GraduationCap,
    title: "Educational guides",
    desc: "10 built-in guides: BLM lands, the periodic table, paleontology, and more — no internet required.",
    items: ["10 guides", "Offline-ready"],
  },
  {
    icon: Bell,
    title: "Search, alerts & notifications",
    desc: "Global search across specimens, locations, and guides. A dedicated notification center with deep links, a separate mail icon with unread count, NWS severe weather alerts, and proximity pings when you're near a dig site.",
    items: ["Global search", "Notification center", "Message icon", "NWS weather alerts", "Proximity pings"],
  },
  {
    icon: Backpack,
    title: "Gear, tokens & design",
    desc: "Beginner-to-advanced Gear Guide with 48+ tools and Amazon links. 7-day free trial, $9.99/mo Premium, or one-time donations for tokens. Immersive 3D design with an optional 2GB offline cache mode plus a one-tap bulk download that caches every specimen photo, guide illustration, and hero image (~4 GB) so the whole app works fully offline.",
    items: ["Gear Guide", "Tokens & Premium", "3D design", "2GB cache mode", "Bulk offline download (~4 GB)"],
  },
  {
    icon: HelpCircle,
    title: "Community Q&A board",
    desc: "An app-wide feed where rockhounds post questions, photos, and rock stories. Sort by Newest, Most Loved, or Most Commented. Full threaded replies, image attachments, reposts, and a 14-day auto-expire that keeps the feed fresh — expired posts can be browsed and restored from the Archived Posts popup before they're removed permanently.",
    items: ["Sort feeds", "Threaded replies", "Image attachments", "Repost & report", "Archived posts", "14-day auto-expire"],
  },
  {
    icon: Upload,
    title: "Submit specimens & add locations",
    desc: "Found a specimen that isn't in the database? Submit up to 4 photos plus any info you have — after review it gets added to the Specimen Database or Rocks Are Amazing for every RockScout user to discover. Found a great dig site, rock shop, or gem show not on the map? Use the Submit Location form to add it, reviewed before going live for everyone.",
    items: ["Submit specimens", "Submit locations", "Reviewed by RockScout", "Community-built database"],
  },
  {
    icon: Share2,
    title: "Referrals & community rewards",
    desc: "Share your unique referral link from the Profile screen — when friends sign up, you both earn tokens and XP, with celebration pop-ups when they complete sign-up. A built-in profanity filter and screenshot-based reporting keep the community family-friendly, and the Report button on any message or profile flags inappropriate content for review.",
    items: ["Unique referral link", "Both users earn rewards", "Celebration pop-ups", "Profanity filter", "Screenshot reporting"],
  },
  {
    icon: Database,
    title: "Storage, cache & bulk offline download",
    desc: "Choose Standard (150MB) or Maximum (2GB) cache mode in Storage settings — available to all users, it's your own device storage. The cache covers both specimen photos and map tiles. Want the whole catalog offline? Tap \"Download all images (~4 GB)\" to cache every specimen photo plus all educational/guide illustrations and hero art, so every read-only screen (specimen details, geology guides, BLM guide, periodic table) loads instantly with zero signal. A live progress bar with resume and an offline-ready badge keep you informed.",
    items: ["Standard 150MB", "Maximum 2GB", "~4 GB bulk download", "Resume on restart", "Offline-ready badge", "Map tile caching"],
  },
  {
    icon: Diamond,
    title: "Rocks Are Amazing",
    desc: "A curated gallery of Earth's most stunning formations — enhydros, pseudomorphs, petroleum inclusions, fluorescent minerals, optical phenomena, coprolites, copper-inclusion agates, mineral assemblages, and more. Each wonder opens a full specimen detail page with photos, properties, and where-to-find info. Approved user-submitted specimens can land here alongside the Specimen Database.",
    items: ["Curated wonder gallery", "Swipe-through categories", "User submissions welcome"],
  },
  {
    icon: Library,
    title: "Rock & gem resources",
    desc: "A curated set of trusted external geology, gem, and fossil websites — museums, university mineralogy departments, and reputable reference sites. Links open in your device's browser so you can dig deeper into any topic, making it a perfect research companion alongside the in-app Specimen Database.",
    items: ["Trusted external links", "Museums & universities", "Research companion"],
  },
];

const HowItWorks = () => (
  <section className="relative border-y border-border/40 bg-card/20 py-24">
    <div className="mx-auto max-w-6xl px-4 sm:px-6">
      <div className="max-w-2xl">
        <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
          <span className="h-px w-8 bg-primary/50" /> How it works
        </span>
        <h2 className="mt-4 font-display text-4xl font-bold tracking-tight sm:text-5xl">
          From pocket to collection in 4 taps
        </h2>
      </div>

      <ol className="mt-14 grid gap-6 md:grid-cols-4">
        {STEPS.map((s) => (
          <li key={s.n} className="relative">
            <div className="field-border rounded-3xl bg-background/40 p-6 transition-colors hover:border-primary/40">
              <div className="flex items-center gap-3">
                <span className="step-numeral font-display text-5xl font-black leading-none">{s.n}</span>
                <span className="grid h-10 w-10 place-items-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25">
                  <s.icon className="h-5 w-5" />
                </span>
              </div>
              <h3 className="mt-4 font-display text-xl font-semibold">{s.title}</h3>
              <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{s.body}</p>
            </div>
          </li>
        ))}
      </ol>
    </div>
  </section>
);

const ScreenshotStrip = () => (
  <section className="py-24">
    <div className="mx-auto max-w-6xl px-4 sm:px-6">
      <div className="flex items-end justify-between gap-6">
        <div>
          <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
            <span className="h-px w-8 bg-primary/50" /> In the field
          </span>
          <h2 className="mt-4 font-display text-4xl font-bold tracking-tight sm:text-5xl">See it for real</h2>
          <p className="mt-3 text-muted-foreground">No mockups here — these are actual screens from the Android app.</p>
        </div>
        <span className="hidden shrink-0 items-center gap-1.5 rounded-full border border-border bg-card/50 px-3 py-1.5 text-xs text-muted-foreground sm:inline-flex">
          <Star className="h-3.5 w-3.5 fill-primary text-primary" /> Swipe to explore
        </span>
      </div>
      <div className="mt-10 flex snap-x gap-4 overflow-x-auto pb-6 [scrollbar-width:thin]">
        {SHOTS.map((s, i) => (
          <figure
            key={s.src}
            className="group relative w-[230px] flex-none snap-center overflow-hidden rounded-3xl border border-border/70 bg-card p-2.5 shadow-lg transition-all hover:-translate-y-1.5 hover:border-primary/40 hover:shadow-[0_20px_50px_-20px_hsl(var(--primary))]"
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
    span: "md:col-span-2 md:row-span-2",
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

type GearItem = { emoji: string; name: string; price: string; url: string };

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

const FieldStories = () => (
  <section className="relative py-24 scroll-mt-20" id="supplies">
    <div className="mx-auto max-w-6xl px-4 sm:px-6">
      <div className="mb-14 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div className="max-w-2xl">
          <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
            <span className="h-px w-8 bg-primary/50" /> In the wild
          </span>
          <h2 className="mt-4 font-display text-4xl font-bold tracking-tight sm:text-5xl">
            Built for real rockhounds
          </h2>
          <p className="mt-4 text-balance text-lg text-muted-foreground">
            Campfire IDs, midnight UV hunts, group trips, and field journals — RockScout is made for the moments that happen off-trail.
          </p>
        </div>
        <span className="hidden shrink-0 items-center gap-1.5 rounded-full border border-border bg-card/50 px-3 py-1.5 text-xs text-muted-foreground sm:inline-flex">
          <Star className="h-3.5 w-3.5 fill-primary text-primary" /> Founder & community shots
        </span>
      </div>

      <div className="grid grid-cols-1 gap-4 auto-rows-[260px] sm:grid-cols-2 md:grid-cols-4">
        {FIELD_STORIES.map((story) => (
          <figure
            key={story.type === "gear" ? "gear" : story.src}
            className={`group relative overflow-hidden rounded-3xl border border-border/70 bg-card shadow-md transition-all hover:-translate-y-1 hover:border-primary/40 hover:shadow-xl ${story.span}`}
          >
            {story.type === "gear" ? (
              <>
                <div className="relative flex h-full flex-col p-5">
                  <div className="flex items-center justify-between gap-3">
                    <p className="font-display text-lg font-semibold text-foreground">Gear up for the field</p>
                    <span className="hidden rounded-full border border-primary/30 bg-primary/10 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider text-primary sm:inline-block">
                      Amazon
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-muted-foreground">The same gear guide linked inside the app — curated for every kind of rockhound.</p>
                  <ul className="mt-3 flex-1 space-y-1 overflow-y-auto pr-1 [scrollbar-width:thin]">
                    {GEAR_ITEMS.map((item) => (
                      <li key={item.name}>
                        <a
                          href={item.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="flex items-center gap-2 rounded-xl border border-border/70 bg-card px-2.5 py-1.5 text-xs text-card-foreground transition-colors hover:border-primary/40 hover:bg-primary/5"
                        >
                          <span className="text-sm" aria-hidden>{item.emoji}</span>
                          <span className="flex-1 truncate">{item.name}</span>
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
                  <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/15 to-transparent" />
                </div>
                <figcaption className="absolute bottom-0 left-0 w-full p-4">
                  <p className="font-display text-sm font-semibold leading-snug text-primary drop-shadow">{story.caption}</p>
                </figcaption>
              </>
            )}
          </figure>
        ))}
      </div>
    </div>
  </section>
);

const CTA = () => (
  <section className="mx-auto max-w-6xl px-4 py-24 sm:px-6">
    <div className="relative overflow-hidden rounded-[2rem] border border-primary/30 bg-gradient-to-br from-card via-card to-primary/10 p-10 text-center sm:p-20">
      <div className="pointer-events-none absolute inset-0 geode-gradient opacity-70" aria-hidden />
      {/* Floating sparkles in CTA */}
      <div className="pointer-events-none absolute inset-0" aria-hidden>
        <Sparkle top="18%" left="12%" delay="0s" duration="3s" color="hsl(36 80% 58%)" />
        <Sparkle top="70%" left="84%" delay="1s" duration="4s" color="hsl(20 62% 65%)" />
        <Sparkle top="30%" left="78%" delay="2s" duration="3.5s" color="hsl(172 30% 55%)" />
      </div>
      <div className="relative">
        <h2 className="text-balance font-display text-4xl font-bold tracking-tight sm:text-5xl">
          Ready to find your next specimen?
        </h2>
        <p className="mx-auto mt-5 max-w-md text-balance text-lg text-muted-foreground">
          RockScout is coming soon to Google Play. Be the first to know when it launches.
        </p>
        <div className="mt-9 flex flex-col items-center justify-center gap-3 sm:flex-row">
          <span
            aria-disabled="true"
            className="inline-flex items-center justify-center gap-2.5 rounded-2xl border border-primary/40 bg-primary/10 px-8 py-4 font-semibold text-primary/80"
          >
            <Download className="h-5 w-5" />
            Coming soon to Google Play
          </span>
          <Link
            to="/support"
            className="inline-flex items-center justify-center gap-1.5 rounded-2xl border border-border bg-card/60 px-6 py-4 text-sm font-medium text-foreground transition-colors hover:bg-card"
          >
            Questions? Visit Support <ChevronRight className="h-4 w-4" />
          </Link>
        </div>
        <ul className="mx-auto mt-9 flex max-w-md flex-wrap items-center justify-center gap-x-5 gap-y-2 text-xs text-muted-foreground">
          <li className="inline-flex items-center gap-1.5"><Check className="h-3.5 w-3.5 text-primary" /> Free tier: limited use, but still plenty of useful resources</li>
          <li className="inline-flex items-center gap-1.5"><BadgeCheck className="h-3.5 w-3.5 text-primary" /> Premium: app fully unlocked with Unlimited IDs</li>
          <li className="inline-flex items-center gap-1.5"><Check className="h-3.5 w-3.5 text-primary" /> Account needed to try</li>
          <li className="inline-flex items-center gap-1.5"><ShieldCheck className="h-3.5 w-3.5 text-primary" /> Moderated community</li>
        </ul>
      </div>
    </div>
  </section>
);

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
      <SpecimenMarquee reverse />
      <HowItWorks />
      <SpecimenMarquee />
      <ScreenshotStrip />
      <SpecimenMarquee reverse />
      <FieldStories />
      <SpecimenMarquee />
      <section id="features" className="relative scroll-mt-24 py-24">
        <div className="mx-auto max-w-6xl px-4 sm:px-6">
          <div className="max-w-2xl">
            <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
              <span className="h-px w-8 bg-primary/50" /> Full feature list
            </span>
            <h2 className="mt-4 font-display text-4xl font-bold tracking-tight sm:text-5xl">
              Everything packed into one app
            </h2>
            <p className="mt-4 text-balance text-lg text-muted-foreground">
              Related tools grouped together — so you can see the full picture without scrolling through a wall of boxes.
            </p>
          </div>
          <ul className="mt-14 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
            {FEATURES_LIST.map((f) => (
              <li
                key={f.title}
                className="group flex flex-col gap-3 rounded-2xl border border-border/60 bg-background/40 p-5 transition-all hover:-translate-y-0.5 hover:border-primary/40 hover:bg-card/60"
              >
                <div className="flex items-center gap-3">
                  <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25 transition-transform group-hover:scale-110">
                    <f.icon className="h-5 w-5" />
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
