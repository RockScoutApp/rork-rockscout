import { Link } from "react-router-dom";
import { Mail, ChevronDown, Download, LifeBuoy, FileText, ShieldAlert, Users, ArrowUpRight, FileText as FileTextIcon } from "lucide-react";
import { Layout } from "@/components/Layout";
import { SITE } from "@/content/legal";

const FAQ = [
  {
    q: "How does the AI rock identification work?",
    a: "Snap a photo of the specimen in good light and RockScout sends it to our identification service, which returns a best-effort suggestion based on visual features. It's a starting point — for high-value or hazardous specimens, confirm with a qualified expert.",
  },
  {
    q: "Is RockScout free?",
    a: "Yes and No. Every user gets a 7-day full-access trial with 5 AI identification tokens. After the trial, the free tier includes (ad supported) rock identification, browsing the full specimen database & geology guides, the field camera (saves to your in-app Saved Images), NWS severe weather alerts, browsing dig sites & offline maps, and the glossary — but it's read-only plus field camera, not full access. AI identification tokens can also be earned free by watching a couple short videos — no weekly cap. Donations unlock social features (Friends, Messenger, Community, Trade Board), My Rocks, Wishlist, Field Captures, Trip Planner, and Field Journal for a limited time. Premium ($5.99/mo) is the only tier that is completely unrestricted — no ads, no token limits, no feature locks. On the web, a free read-only PWA is available for learners and kids — full database, educational guides, and map viewing with no camera, no ID, and no social. The free version does not have access to any of the social aspects, so it stays rated G for younger users. Pricing is shown in the app before any purchase.",
  },
  {
    q: "Can I use RockScout offline?",
    a: "Yes. All specimen text, geology guides, and educational content is bundled in the app and always available offline. For images, enable the Maximum (2 GB) cache mode in Social Settings, then tap \"Download all images (~3.5 GB)\" to bulk-cache every specimen photo, guide illustration, and hero image on-device. Once that completes, every read-only screen loads instantly with zero signal. Map tiles can also be cached per-trip or per-area from any map screen.",
  },
  {
    q: "Do I need an account?",
    a: "A free RockScout account is required to use the app — it's free and your collections, captures, and friends carry over to any device. The free tier includes ad-supported rock identification, the full specimen database, dig site maps, field captures, and all educational guides.",
  },
  {
    q: "Can I trade specimens with other users?",
    a: "Yes — the trade board lets you list specimens you'd like to swap. Trades are strictly peer-to-peer and at your own risk. RockScout is not a party to any trade. Read our Trade & Swap Disclaimer and Safety & Meetup Notice before arranging an exchange.",
  },
  {
    q: "Is my location shared with other users?",
    a: "Only if you opt in. Proximity features show your coarse location on the RockScouts map if you enable them in Social Settings. Background location is off by default. You can revoke location permission from device settings at any time.",
  },
  {
    q: "How do I cancel a subscription?",
    a: "Subscriptions renew automatically through Google Play. Cancel anytime from your Google Play subscriptions settings. You keep access until the end of the current billing period.",
  },
  {
    q: "How do I report a user or post?",
    a: "Every post, comment, message, trade listing, and profile has a report option. Reports go to our moderation team and may result in content removal, warnings, or bans. See our Community Guidelines for details.",
  },
  {
    q: "Someone is harassing me in direct messages. What do I do?",
    a: "Block them from their profile or from the message thread, then report the conversation. Our team reviews reports and takes action under the Community Guidelines.",
  },
  {
    q: "What is the Artifacts & Stone Tools tile?",
    a: "The Artifacts tile is a growing catalog of 100+ authentic prehistoric artifacts, each with its own generated reference image. Families include Arrowheads (20+ types), Spear Points & Dart Tips (12+), Hand Axes & Axe Heads (20+), Flaked Stone Tools (12+), Drill Bits (6+), Native Beads (13+), Stone Effigies (7+), Pipes & Medicine Tubes, Ornaments & Weights, Shell Tools, Bone Tools, Pottery, Game Discs, and Wooden Artifacts. The tile is linked to the Specimen Database via the ARTIFACTS category chip at the top of the specimen list — artifacts never appear inside the main specimen database, only through the Artifacts tile or the category chip. A NEW badge appears on any artifact added within the last 7 days.",
  },
  {
    q: "What are the Natural Wonders?",
    a: "The Natural Wonders tile features 36 world-famous geological sites with stunning photos, formation stories, rocks to find, and visitor tips. From the Grand Canyon and Giant's Causeway to Mount Vesuvius, Salar de Uyuni, and the Zhangjiajie Pillars — including the Naica Crystal Caves in Chihuahua, Mexico, where giant selenite crystals grow from floor to ceiling, some over 36 feet long. Each card opens a full detail page and is a great bucket-list trip-planning resource.",
  },
  {
    q: "What does the NEW badge mean?",
    a: "A NEW badge automatically appears on any specimen or artifact card that was added to the catalog within the last 7 days. It works across the Specimen Database, Artifacts tile, and any category-filtered view. After 7 days the badge disappears automatically — the specimen stays in the catalog but is no longer flagged as new.",
  },
  {
    q: "How do achievement progress bars work?",
    a: "On the All Achievements page, each locked achievement now shows a visual progress bar indicating how close you are to earning it — so you always know what to do next to level up. Earn XP for every action and the bars fill as you make progress toward each goal.",
  },
  {
    q: "What happens when there's a signing conflict during an update?",
    a: "When the app detects that the installed APK was signed with a different key than the new version, a friendly dialog explains that the old version must be uninstalled to update, and offers a button to trigger the system uninstall flow directly. After reinstalling the new version, sign back in with your RockScout account — all your settings (hunter status, cache mode, notification preferences, aurora thresholds, and more) are restored from the cloud exactly as they were. Your collections, captures, friends, and achievements are tied to your account, not your device.",
  },
  {
    q: "Why is there a confirmation dialog when I log out?",
    a: "A confirmation dialog appears when you tap the logout button in Settings to prevent accidental sign-outs. Your data stays safe on your account either way — signing back in restores everything.",
  },
  {
    q: "Is there an image size limit for uploads?",
    a: "Yes. Images selected for upload (specimen submissions, field captures, community posts, profile backgrounds, and trade listings) are automatically checked against a 5 MB size limit. If a photo is too large, you'll get a friendly notification to pick a smaller file, preventing upload failures before they happen.",
  },
  {
    q: "Does the web app work on desktop?",
    a: "Yes — the RockScout web app is a full PWA with desktop-optimized layouts: wider multi-column grids, a split map + location list view on the Dig Sites page, and vim-style keyboard shortcuts. Press \"?\" for the shortcuts overlay, \"/\" to focus search, and \"g\" followed by a letter to jump to any section (g h for Home, g m for Map, etc.). Install the PWA to your desktop from your browser's install prompt for a standalone app window.",
  },
  {
    q: "What is the Aurora Forecaster?",
    a: "The Aurora Forecaster is a real-time space weather tool inside the app. It shows the current Kp index, Bz value, solar wind speed, and aurora visibility status for your latitude, plus 24-hour Kp and 7-day F10.7 trend charts, a 3-day forecast, and active sunspot regions with tappable detail views showing magnetic evolution history. You can save custom coordinates as aurora watching spots, set a custom Kp notification threshold for instant push alerts the moment your Kp is reached, and share your Kp status to social media.",
  },
  {
    q: "What is the Stars & Constellations guide?",
    a: "Accessible from the Aurora Forecaster's Night Sky Guide card, this is a complete astronomy reference with all 88 IAU constellations (including programmatic star charts), 30+ important stars with spectral data, all 8 planets plus dwarf planets, and 40+ deep sky objects (galaxies, nebulae, star clusters). Every page features animated twinkling white stars in the background.",
  },
  {
    q: "Can I plan trips with the Trip Planner and Calendar?",
    a: "Yes — the Trip Planner lets you build multi-stop routes with long-press drag-and-drop stop reordering (the stop swaps to the position under your finger), a connecting polyline on the map, estimated travel times between stops, and gear checklists. Move up buttons are also available for quick single-step reordering. A standalone Calendar screen on the home screen shows all planned trips in a month grid where you can drag and drop trip cards to reschedule them, create and edit trips, and archive completed trips.",
  },
  {
    q: "What are the Common Wildlife tiles?",
    a: "Every BLM state guide, trailhead, campground, dig site, state park, and beach detail screen includes a Common Wildlife tile showing the animals you might encounter in that area — mammals, birds, reptiles, and more, tailored to the region's biome.",
  },
  {
    q: "What is Ask an Expert and the Museum Finder?",
    a: "When an identification result has low confidence or you want a second opinion, tap the \"Ask an Expert\" button on the uncertainty card. RockScout searches for nearby museums and geological institutions using your current location (or your profile region as a fallback). Each result shows the museum name, distance, phone number, website, and directions. You can select multiple museums and compose an email to all of them at once — your captured photo and identification results are attached automatically. Email drafts auto-save, so if you close the app mid-composition, you'll get a prompt to restore your work next time.",
  },
  {
    q: "What are PDF Identification Reports?",
    a: "After running an identification, a small PDF document icon appears on each match card. Tap it to generate a printable 1-2 page report containing your captured photo, all match names with confidence scores and reasoning, the AI analysis summary, assemblage breakdown (if applicable), web references, and a field-capture note with the date and approximate location. The PDF opens in your device's share sheet — email it to a museum expert, save it to your files, or share it with fellow rockhounds.",
  },
  {
    q: "Can I install RockScout on my desktop or laptop?",
    a: "Yes — RockScout's web app is a PWA (Progressive Web App) for desktop, laptop, and tablet browsers. There are two tiers: a free read-only PWA for learning (full 900+ specimen database, educational guides, interactive map, and personal bookmarks — no camera, no AI ID, no social) and a Premium PWA with all features unlocked (AI identify, field camera, social, trade, and more) installable on up to 2 additional devices with email-code confirmation. A free account created on the web carries over to the Android/iOS app. If you upgrade to Premium on any platform, the same account unlocks everywhere.",
  },
  {
    q: "What is the Search Near Me button?",
    a: "On the home screen's Dig Sites & Rock Shops section, a \"Search Near Me\" button appears next to the nearby locations header. Tap it to run a web search for rock-related places — dig sites, rock shops, mineral collecting areas, museums, and metaphysical stores — near your current GPS location. It searches a 50-mile radius first, then automatically expands to 100 miles if nothing is found. Results appear inline right where the nearby locations normally show, each with a name, type badge, description, and an \"Open\" button to visit the source website. Results are also saved for review so approved spots can appear on the Dig Sites map in future updates. Requires Nearby Places to be turned on.",
  },
];

const legalLinks = [
  { to: "/privacy", label: "Privacy Policy", icon: FileText, desc: "What we collect and why." },
  { to: "/terms", label: "Terms of Service", icon: FileText, desc: "The rules for using RockScout." },
  { to: "/community-guidelines", label: "Community Guidelines", icon: Users, desc: "How we keep the community respectful and family-friendly." },
  { to: "/trade-disclaimer", label: "Trade & Swap Disclaimer", icon: ShieldAlert, desc: "Trades are peer-to-peer and at your own risk." },
  { to: "/safety", label: "Safety & Meetup Notice", icon: ShieldAlert, desc: "How to stay safe meeting another user in person." },
  { to: "/cookies", label: "Cookies & SDKs", icon: FileText, desc: "The cookies and SDK identifiers we use." },
];

const Support = () => (
  <Layout
    title="Support"
    description={`Frequently asked questions about ${SITE.name}, plus links to every legal page and how to reach our team.`}
  >
    <section className="mx-auto max-w-4xl px-4 py-12 sm:px-6 sm:py-16 md:py-20">
      <div className="inline-flex h-11 w-11 items-center justify-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25 sm:h-12 sm:w-12">
        <LifeBuoy className="h-5 w-5 sm:h-6 sm:w-6" />
      </div>
      <h1 className="mt-4 text-2xl font-bold tracking-tight sm:mt-5 sm:text-3xl md:text-4xl">Support</h1>
      <p className="mt-3 max-w-xl text-sm text-muted-foreground sm:text-base">
        Quick answers, legal links, and how to reach us.
      </p>

      <div className="mt-10 grid gap-4 sm:mt-12 sm:grid-cols-2">
        <a
          href={`mailto:${SITE.supportEmail}`}
          className="group flex items-center justify-between rounded-2xl border border-border bg-card/50 p-5 transition-colors hover:border-primary/40 hover:bg-card"
        >
          <div>
            <p className="text-sm font-semibold">Email our team</p>
            <p className="mt-1 text-sm text-muted-foreground">{SITE.supportEmail}</p>
            <p className="mt-2 text-xs text-muted-foreground">Replies within 5 business days.</p>
          </div>
          <Mail className="h-5 w-5 text-primary transition-transform group-hover:-translate-y-0.5" />
        </a>
        <div className="group flex items-center justify-between rounded-2xl border border-border bg-card/50 p-5">
          <div>
            <p className="text-sm font-semibold">Coming soon to Android, iOS (Beta) &amp; PC</p>
            <p className="mt-1 text-sm text-muted-foreground">Not available to download yet</p>
            <p className="mt-2 text-xs text-muted-foreground">Native iOS app coming soon to the App Store.</p>
          </div>
          <Download className="h-5 w-5 text-primary/70" />
        </div>
      </div>

      <h2 className="mt-12 text-xl font-semibold tracking-tight sm:mt-16 sm:text-2xl">Frequently asked questions</h2>
      <div className="mt-5 divide-y divide-border/60 rounded-2xl border border-border bg-card/40 sm:mt-6">
        {FAQ.map((item, i) => (
          <details key={i} className="group p-4 sm:p-5 [&_summary::-webkit-details-marker]:hidden">
            <summary className="flex cursor-pointer items-center justify-between gap-4 text-sm font-medium">
              {item.q}
              <ChevronDown className="h-4 w-4 flex-none text-muted-foreground transition-transform group-open:rotate-180" />
            </summary>
            <p className="mt-3 text-sm leading-relaxed text-muted-foreground">{item.a}</p>
          </details>
        ))}
      </div>

      <h2 className="mt-12 text-xl font-semibold tracking-tight sm:mt-16 sm:text-2xl">Legal & policies</h2>
      <div className="mt-5 grid gap-4 sm:mt-6 sm:grid-cols-2">
        {legalLinks.map((l) => (
          <Link
            key={l.to}
            to={l.to}
            className="group flex items-start justify-between gap-4 rounded-2xl border border-border bg-card/50 p-4 transition-colors hover:border-primary/40 hover:bg-card sm:p-5"
          >
            <div className="flex items-start gap-3">
              <span className="mt-0.5 inline-flex h-8 w-8 flex-none items-center justify-center rounded-lg bg-primary/15 text-primary">
                <l.icon className="h-4 w-4" />
              </span>
              <div>
                <p className="text-sm font-semibold">{l.label}</p>
                <p className="mt-1 text-xs text-muted-foreground">{l.desc}</p>
              </div>
            </div>
            <ArrowUpRight className="h-4 w-4 flex-none text-muted-foreground transition-all group-hover:-translate-y-0.5 group-hover:translate-x-0.5 group-hover:text-primary" />
          </Link>
        ))}
      </div>
    </section>
  </Layout>
);

export default Support;
