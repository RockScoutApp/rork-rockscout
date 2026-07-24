import { Link } from "react-router-dom";
import { Mail, ChevronDown, Download, LifeBuoy, FileText, ShieldAlert, Users, ArrowUpRight } from "lucide-react";
import { Layout } from "@/components/Layout";
import { SITE } from "@/content/legal";

const FAQ = [
  {
    q: "How does the AI rock identification work?",
    a: "Snap a photo of the specimen in good light and RockScout sends it to our identification service, which returns a best-effort suggestion based on visual features. It's a starting point — for high-value or hazardous specimens, confirm with a qualified expert.",
  },
  {
    q: "Is RockScout free?",
    a: "Yes. Every user gets a 7-day full-access trial with 5 AI identification tokens. After the trial, these features stay free forever: browsing the full specimen database & geology guides, the field camera (saves to your in-app Saved Images), NWS severe weather alerts, and browsing dig sites & offline maps. AI identification, social features (Friends, Messenger, Community, Trade Board), My Rocks, Wishlist, Field Captures, Trip Planner, and Field Journal require Premium ($9.99/mo) or a donation. You can also earn free ID tokens by watching short rock-related videos. Pricing is shown in the app before any purchase.",
  },
  {
    q: "Can I use RockScout offline?",
    a: "Yes. All specimen text, geology guides, and educational content is bundled in the app and always available offline. For images, enable the Maximum (2 GB) cache mode in Social Settings, then tap \"Download all images (~3.5 GB)\" to bulk-cache every specimen photo, guide illustration, and hero image on-device. Once that completes, every read-only screen loads instantly with zero signal. Map tiles can also be cached per-trip or per-area from any map screen.",
  },
  {
    q: "Do I need an account?",
    a: "You can try identification without an account, but saving to your collection, joining the community, messaging, and using the trade board require a free account so other users know who they're talking to.",
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
    <section className="mx-auto max-w-4xl px-4 py-16 sm:px-6 sm:py-20">
      <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25">
        <LifeBuoy className="h-6 w-6" />
      </div>
      <h1 className="mt-5 text-3xl font-bold tracking-tight sm:text-4xl">Support</h1>
      <p className="mt-3 max-w-xl text-muted-foreground">
        Quick answers, legal links, and how to reach us.
      </p>

      <div className="mt-12 grid gap-4 sm:grid-cols-2">
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
            <p className="text-sm font-semibold">Coming soon to Google Play</p>
            <p className="mt-1 text-sm text-muted-foreground">Android app in review</p>
            <p className="mt-2 text-xs text-muted-foreground">{SITE.appStoreNote}.</p>
          </div>
          <Download className="h-5 w-5 text-primary/70" />
        </div>
      </div>

      <h2 className="mt-16 text-2xl font-semibold tracking-tight">Frequently asked questions</h2>
      <div className="mt-6 divide-y divide-border/60 rounded-2xl border border-border bg-card/40">
        {FAQ.map((item, i) => (
          <details key={i} className="group p-5 [&_summary::-webkit-details-marker]:hidden">
            <summary className="flex cursor-pointer items-center justify-between gap-4 text-sm font-medium">
              {item.q}
              <ChevronDown className="h-4 w-4 flex-none text-muted-foreground transition-transform group-open:rotate-180" />
            </summary>
            <p className="mt-3 text-sm leading-relaxed text-muted-foreground">{item.a}</p>
          </details>
        ))}
      </div>

      <h2 className="mt-16 text-2xl font-semibold tracking-tight">Legal & policies</h2>
      <div className="mt-6 grid gap-4 sm:grid-cols-2">
        {legalLinks.map((l) => (
          <Link
            key={l.to}
            to={l.to}
            className="group flex items-start justify-between gap-4 rounded-2xl border border-border bg-card/50 p-5 transition-colors hover:border-primary/40 hover:bg-card"
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
