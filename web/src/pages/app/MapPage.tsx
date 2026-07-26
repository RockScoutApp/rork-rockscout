import { MapPin, Loader2, Navigation } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function MapPage() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Maps & Dig Sites
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Find nearby collecting sites, mines, and gem shows
        </p>
      </div>

      <div className="flex flex-col items-center justify-center gap-4 rounded-xl border-2 border-dashed border-border bg-card/30 py-16 text-center">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
          <MapPin className="h-8 w-8 text-primary" />
        </div>
        <div>
          <p className="font-medium text-foreground">Interactive map coming soon</p>
          <p className="mt-1 max-w-sm text-sm text-muted-foreground">
            Leaflet field maps with dig site markers, BLM collecting areas, gem
            shows, and offline tile caching will be added in Phase 2.
          </p>
        </div>
        <Button variant="outline" size="sm" className="gap-2">
          <Navigation className="h-4 w-4" />
          Explore nearby sites
        </Button>
      </div>
    </div>
  );
}
