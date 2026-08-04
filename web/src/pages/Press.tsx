import { Download, Mail } from "lucide-react";
import { Layout } from "@/components/Layout";
import { SITE } from "@/content/legal";

const BOILERPLATE = `${SITE.name} is the field companion for rockhounds — 5-source AI rock & mineral identification (database visual comparison + three AI models + web cross-check), a growing specimen database, dig-site maps with offline caching, a collection tracker, and a moderated community with a peer-to-peer trade board. Founded in ${SITE.foundedYear}, ${SITE.name} is built by a small team of collectors and engineers. Available on Android, iOS, and web; an optional Pro subscription is available.`;

const SHOTS = [
  { src: "/images/screenshots/01_home_dashboard.webp", alt: "Home dashboard" },
  { src: "/images/screenshots/02_specimen_detail.webp", alt: "Specimen detail" },
  { src: "/images/screenshots/03_specimen_database.webp", alt: "Specimen database" },
  { src: "/images/screenshots/04_explore_learn.webp", alt: "Explore & Learn" },
  { src: "/images/screenshots/05_home_features.webp", alt: "Home features" },
  { src: "/images/screenshots/06_trade_board.webp", alt: "Trade board" },
  { src: "/images/screenshots/07_field_kit.webp", alt: "Field kit" },
  { src: "/images/screenshots/08_specimen_detail_photos.webp", alt: "Specimen detail photos" },
  { src: "/images/screenshots/09_profile.webp", alt: "Profile" },
  { src: "/images/screenshots/10_social_friends.webp", alt: "Social friends" },
];

const FACTS = [
  { label: "Product", value: SITE.name },
  { label: "Tagline", value: SITE.tagline },
  { label: "Platform", value: "Android, iOS & web" },
  { label: "Category", value: "Lifestyle / Outdoor / Education" },
  { label: "Pricing", value: "Free trial, then limited free tier; donations unlock temporarily; Premium is fully unrestricted" },
  { label: "Founded", value: String(SITE.foundedYear) },
  { label: "Website", value: SITE.domain },
  { label: "Press contact", value: SITE.pressEmail },
];

const Press = () => (
  <Layout
    title="Press Kit"
    description={`App icon, screenshots, boilerplate, and facts about ${SITE.name} for journalists and reviewers.`}
  >
    <section className="mx-auto max-w-4xl px-4 py-16 sm:px-6 sm:py-20">
      <h1 className="text-3xl font-bold tracking-tight sm:text-4xl">Press Kit</h1>
      <p className="mt-3 max-w-xl text-muted-foreground">
        Everything you need to write about {SITE.name}. For interviews or review access, email{" "}
        <a href={`mailto:${SITE.pressEmail}`} className="font-medium text-primary hover:underline">
          {SITE.pressEmail}
        </a>
        .
      </p>

      <div className="mt-12 grid gap-8 md:grid-cols-[1fr_1.4fr]">
        {/* App icon */}
        <div className="flex flex-col items-center justify-start rounded-2xl border border-border bg-card/40 p-6">
          <img
            src="/icon.png"
            alt={`${SITE.name} app icon`}
            className="h-32 w-32 rounded-3xl ring-1 ring-primary/25"
          />
          <a
            href="/icon.png"
            download="rockscout-icon.png"
            className="mt-4 inline-flex items-center gap-2 text-sm font-medium text-primary hover:underline"
          >
            <Download className="h-4 w-4" /> Download icon
          </a>
        </div>

        {/* Fact sheet */}
        <div className="rounded-2xl border border-border bg-card/40 p-6">
          <h2 className="text-lg font-semibold">Fact sheet</h2>
          <dl className="mt-4 divide-y divide-border/60">
            {FACTS.map((f) => (
              <div key={f.label} className="grid grid-cols-[120px_1fr] gap-4 py-2.5 text-sm">
                <dt className="text-muted-foreground">{f.label}</dt>
                <dd className="font-medium">{f.value}</dd>
              </div>
            ))}
          </dl>
        </div>
      </div>

      <h2 className="mt-16 text-2xl font-semibold tracking-tight">Boilerplate</h2>
      <p className="mt-4 max-w-2xl text-sm leading-relaxed text-muted-foreground">{BOILERPLATE}</p>

      <h2 className="mt-16 text-2xl font-semibold tracking-tight">Screenshots</h2>
      <p className="mt-2 text-xs text-muted-foreground">
        Right-click any screenshot and choose "Save image as" to download.
      </p>
      <div className="mt-6 grid grid-cols-2 gap-4 sm:grid-cols-3">
        {SHOTS.map((s) => (
          <figure
            key={s.src}
            className="overflow-hidden rounded-xl border border-border/70 bg-card p-1.5"
          >
            <img src={s.src} alt={s.alt} className="aspect-[9/19.5] w-full rounded-lg object-cover" loading="lazy" onError={(e) => { (e.target as HTMLImageElement).src = "/placeholder.svg"; }} />
          </figure>
        ))}
      </div>

      <div className="mt-16 flex flex-col gap-3 rounded-2xl border border-border bg-card/40 p-6 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold">Review access or interview?</p>
          <p className="mt-1 text-xs text-muted-foreground">
            Email {SITE.pressEmail} — we respond within 36 hours.
          </p>
        </div>
        <div className="flex gap-3">
          <a
            href={`mailto:${SITE.pressEmail}`}
            className="inline-flex items-center gap-2 rounded-xl border border-border px-4 py-2.5 text-sm font-medium transition-colors hover:bg-muted/40"
          >
            <Mail className="h-4 w-4" /> Email press
          </a>
          <span
            aria-disabled
            className="inline-flex cursor-default items-center gap-2 rounded-xl border border-primary/40 bg-primary/10 px-4 py-2.5 text-sm font-semibold text-primary/80"
          >
            <Download className="h-4 w-4" /> Available on Android, iOS &amp; web
          </span>
        </div>
      </div>
    </section>
  </Layout>
);

export default Press;
