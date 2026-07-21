import { Layout } from "@/components/Layout";
import { SITE, type LegalSection } from "@/content/legal";
import { FileText } from "lucide-react";

type LegalPageProps = {
  title: string;
  description: string;
  intro: string;
  sections: LegalSection[];
};

export const LegalPage = ({ title, description, intro, sections }: LegalPageProps) => (
  <Layout title={title} description={description}>
    <article className="mx-auto max-w-3xl px-4 py-16 sm:px-6 sm:py-20">
      <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25">
        <FileText className="h-6 w-6" />
      </div>
      <h1 className="mt-5 text-3xl font-bold tracking-tight sm:text-4xl">{title}</h1>
      <p className="mt-3 text-sm text-muted-foreground">
        Effective {SITE.effectiveDate} · {SITE.jurisdiction}
      </p>
      <p className="mt-6 text-base leading-relaxed text-muted-foreground">{intro}</p>

      <div className="mt-12 space-y-10">
        {sections.map((s, i) => (
          <section key={s.heading} className="scroll-mt-24">
            <h2 className="flex items-baseline gap-3 text-xl font-semibold tracking-tight">
              <span className="text-primary tabular-nums">{String(i + 1).padStart(2, "0")}</span>
              {s.heading}
            </h2>
            <div className="mt-3 space-y-3 pl-8">
              {s.body.map((p, j) => (
                <p key={j} className="text-sm leading-relaxed text-muted-foreground sm:text-[0.95rem]">
                  {p}
                </p>
              ))}
            </div>
          </section>
        ))}
      </div>

      <div className="mt-16 rounded-2xl border border-border bg-card/50 p-6">
        <p className="text-sm text-muted-foreground">
          Questions about this page? Email{" "}
          <a href={`mailto:${SITE.supportEmail}`} className="font-medium text-primary hover:underline">
            {SITE.supportEmail}
          </a>
          .
        </p>
      </div>
    </article>
  </Layout>
);

export default LegalPage;
