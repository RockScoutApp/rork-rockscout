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
    <article className="mx-auto max-w-3xl px-4 py-12 sm:px-6 sm:py-16 md:py-20">
      <div className="inline-flex h-11 w-11 items-center justify-center rounded-xl bg-primary/15 text-primary ring-1 ring-primary/25 sm:h-12 sm:w-12">
        <FileText className="h-5 w-5 sm:h-6 sm:w-6" />
      </div>
      <h1 className="mt-4 text-2xl font-bold tracking-tight sm:mt-5 sm:text-3xl md:text-4xl">{title}</h1>
      <p className="mt-3 text-sm text-muted-foreground">
        Effective {SITE.effectiveDate} · {SITE.jurisdiction}
      </p>
      <p className="mt-5 text-sm leading-relaxed text-muted-foreground sm:mt-6 sm:text-base">{intro}</p>

      <div className="mt-10 space-y-8 sm:mt-12 sm:space-y-10">
        {sections.map((s, i) => (
          <section key={s.heading} className="scroll-mt-24">
            <h2 className="flex items-baseline gap-3 text-lg font-semibold tracking-tight sm:text-xl">
              <span className="text-primary tabular-nums">{String(i + 1).padStart(2, "0")}</span>
              {s.heading}
            </h2>
            <div className="mt-3 space-y-3 pl-4 sm:pl-8">
              {s.body.map((p, j) => (
                <p key={j} className="text-sm leading-relaxed text-muted-foreground sm:text-[0.95rem]">
                  {p}
                </p>
              ))}
            </div>
          </section>
        ))}
      </div>

      <div className="mt-12 rounded-2xl border border-border bg-card/50 p-4 sm:mt-16 sm:p-6">
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
