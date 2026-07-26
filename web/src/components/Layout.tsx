import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Menu, X } from "lucide-react";
import { SITE } from "@/content/legal";
import { InstallAppButton } from "@/components/InstallAppButton";

type NavLink = { to: string; label: string };

const navLinks: NavLink[] = [
  { to: "/", label: "Home" },
  { to: "/support", label: "Support" },
  { to: "/privacy", label: "Privacy" },
  { to: "/terms", label: "Terms" },
];

const footerLinks: NavLink[] = [
  { to: "/support", label: "Support" },
  { to: "/privacy", label: "Privacy Policy" },
  { to: "/terms", label: "Terms of Service" },
  { to: "/community-guidelines", label: "Community Guidelines" },
  { to: "/trade-disclaimer", label: "Trade Disclaimer" },
  { to: "/safety", label: "Safety & Meetup" },
  { to: "/cookies", label: "Cookies & SDKs" },
  { to: "/press", label: "Press Kit" },
];

export const Logo = ({ className = "" }: { className?: string }) => (
  <Link to="/" className={`group flex items-center gap-2 ${className}`} aria-label={`${SITE.name} home`}>
    <span className="relative h-8 w-8 overflow-hidden rounded-xl ring-1 ring-primary/30 transition-transform group-hover:scale-105 sm:h-9 sm:w-9">
      <img
        src="/images/app-icon.webp"
        alt=""
        className="h-full w-full object-cover"
        loading="eager"
      />
    </span>
    <span className="text-base font-semibold tracking-tight sm:text-lg">
      Rock<span className="text-primary">Scout</span>
    </span>
  </Link>
);

export const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-40 border-b border-border/60 bg-background/80 backdrop-blur-xl">
      <nav className="mx-auto flex h-14 max-w-6xl items-center justify-between px-4 sm:h-16 sm:px-6">
        <Logo />
        <div className="hidden items-center gap-1 md:flex">
          {navLinks.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              className="rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted/60 hover:text-foreground"
            >
              {l.label}
            </Link>
          ))}
        </div>
        <div className="flex items-center gap-2">
          <span
            aria-disabled
            className="hidden cursor-default items-center gap-2 rounded-full border border-primary/40 bg-primary/10 px-3 py-1.5 text-xs font-semibold text-primary/80 sm:inline-flex sm:px-4 sm:py-2 sm:text-sm"
          >
            Coming soon to Android
          </span>
          {/* Mobile hamburger */}
          <button
            type="button"
            onClick={() => setMenuOpen(!menuOpen)}
            aria-label={menuOpen ? "Close menu" : "Open menu"}
            aria-expanded={menuOpen}
            className="grid h-10 w-10 place-items-center rounded-lg border border-border bg-card/60 text-muted-foreground transition-colors hover:border-primary/40 hover:text-primary md:hidden"
          >
            {menuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>
      </nav>

      {/* Mobile slide-down menu */}
      {menuOpen && (
        <div className="border-t border-border/60 bg-background/95 backdrop-blur-xl md:hidden">
          <div className="mx-auto max-w-6xl px-4 py-4">
            <div className="flex flex-col gap-1">
              {navLinks.map((l) => (
                <Link
                  key={l.to}
                  to={l.to}
                  onClick={() => setMenuOpen(false)}
                  className="rounded-lg px-4 py-3 text-base font-medium text-muted-foreground transition-colors hover:bg-muted/60 hover:text-foreground"
                >
                  {l.label}
                </Link>
              ))}
              <span
                aria-disabled
                onClick={() => setMenuOpen(false)}
                className="mt-2 inline-flex cursor-default items-center justify-center gap-2 rounded-full border border-primary/40 bg-primary/10 px-4 py-3 text-sm font-semibold text-primary/80"
              >
                Coming soon to Android
              </span>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};

export const Footer = () => (
  <footer className="mt-12 border-t border-border/60 bg-card/30 sm:mt-24">
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 sm:py-12">
      <div className="grid gap-8 sm:gap-10 md:grid-cols-[1.5fr_2fr]">
        <div>
          <Logo />
          <p className="mt-3 max-w-xs text-sm leading-relaxed text-muted-foreground sm:mt-4">
            {SITE.tagline}. The field companion for rockhounds, collectors, and curious explorers.
          </p>
          <p className="mt-3 text-xs text-muted-foreground sm:mt-4">
            © {new Date().getFullYear()} {SITE.name}. All rights reserved.
          </p>
        </div>
        <div className="grid grid-cols-2 gap-x-4 gap-y-2.5 sm:gap-x-6 sm:gap-y-3 sm:grid-cols-4">
          {footerLinks.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              className="text-sm text-muted-foreground transition-colors hover:text-primary"
            >
              {l.label}
            </Link>
          ))}
        </div>
      </div>
      <div className="mt-8 flex flex-col items-start justify-between gap-3 border-t border-border/40 pt-6 text-xs text-muted-foreground sm:mt-10 sm:flex-row sm:items-center">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:gap-5">
          <InstallAppButton />
          <span>Effective {SITE.effectiveDate}. Jurisdiction: {SITE.jurisdiction}.</span>
        </div>
        <a href={`mailto:${SITE.supportEmail}`} className="hover:text-primary">
          {SITE.supportEmail}
        </a>
      </div>
    </div>
  </footer>
);

type LayoutProps = {
  children: React.ReactNode;
  title: string;
  description?: string;
  ogImage?: string;
  noIndex?: boolean;
  canonical?: string;
};

export const Layout = ({
  children,
  title,
  description,
  ogImage = "/images/og-share-card.webp",
  noIndex = false,
  canonical,
}: LayoutProps) => {
  const fullTitle = title.includes(SITE.name) ? title : `${title} · ${SITE.name}`;
  const desc = description ?? SITE.description;
  // DOM mutations must happen in useEffect, not during render, to avoid
  // side effects during the render phase and React strict-mode double-render.
  useEffect(() => {
    if (typeof document === "undefined") return;
    document.title = fullTitle;
    const setMeta = (name: string, content: string, attr: "name" | "property" = "name") => {
      let el = document.querySelector(`meta[${attr}="${name}"]`) as HTMLMetaElement | null;
      if (!el) {
        el = document.createElement("meta");
        el.setAttribute(attr, name);
        document.head.appendChild(el);
      }
      el.setAttribute("content", content);
    };
    setMeta("description", desc);
    setMeta("og:title", fullTitle, "property");
    setMeta("og:description", desc, "property");
    setMeta("og:image", ogImage, "property");
    setMeta("og:type", "website", "property");
    setMeta("og:url", window.location.href, "property");
    setMeta("twitter:card", "summary_large_image");
    setMeta("twitter:title", fullTitle);
    setMeta("twitter:description", desc);
    setMeta("twitter:image", ogImage);
    if (noIndex) {
      setMeta("robots", "noindex,nofollow");
    } else {
      const robots = document.querySelector('meta[name="robots"]');
      if (robots) robots.remove();
    }
    const canonicalHref = canonical ?? window.location.href.split("#")[0].split("?")[0];
    let link = document.querySelector('link[rel="canonical"]') as HTMLLinkElement | null;
    if (!link) {
      link = document.createElement("link");
      link.setAttribute("rel", "canonical");
      document.head.appendChild(link);
    }
    link.setAttribute("href", canonicalHref);
  }, [fullTitle, desc, ogImage, noIndex, canonical]);
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">{children}</main>
      <Footer />
    </div>
  );
};
