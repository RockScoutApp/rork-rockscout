import { Link } from "react-router-dom";
import { Home, Compass } from "lucide-react";
import { Layout } from "@/components/Layout";

const NotFound = () => (
  <Layout title="Page not found" noIndex>
    <section className="mx-auto flex max-w-xl flex-col items-center px-4 py-24 text-center sm:px-6 sm:py-32">
      <div className="relative">
        <span className="text-7xl font-bold tracking-tight text-primary/30">404</span>
        <span className="absolute inset-0 -z-10 blur-3xl bg-primary/10" aria-hidden />
      </div>
      <h1 className="mt-4 text-2xl font-bold tracking-tight sm:text-3xl">This vein ran out</h1>
      <p className="mt-3 max-w-sm text-balance text-muted-foreground">
        The page you were looking for isn't here. It may have moved, been renamed, or never existed.
      </p>
      <div className="mt-8 flex flex-col gap-3 sm:flex-row">
        <Link
          to="/"
          className="inline-flex items-center justify-center gap-2 rounded-2xl bg-primary px-5 py-3 font-semibold text-primary-foreground transition-transform hover:scale-[1.03] active:scale-95"
        >
          <Home className="h-4 w-4" /> Back to home
        </Link>
        <Link
          to="/support"
          className="inline-flex items-center justify-center gap-2 rounded-2xl border border-border bg-card/50 px-5 py-3 font-medium text-foreground transition-colors hover:bg-card"
        >
          <Compass className="h-4 w-4" /> Visit support
        </Link>
      </div>
    </section>
  </Layout>
);

export default NotFound;
