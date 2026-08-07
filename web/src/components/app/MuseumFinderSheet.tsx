/**
 * Museum Finder — web equivalent of Android's MuseumFinderSheet.
 *
 * Opens as a dialog, requests geolocation, calls the /museums backend
 * endpoint (Overpass API), and shows a scrollable list of nearby
 * artifact-relevant museums sorted by distance.
 *
 * Each card shows name, type badge, address, distance, phone (tappable),
 * website (tappable), directions link, and an "Email Expert" button.
 * Multi-select with "Compose Email" button for emailing several at once.
 */

import { useState, useEffect, useCallback, useMemo } from "react";
import {
  Loader2,
  MapPin,
  X,
  Phone,
  Globe,
  Navigation,
  Mail,
  AlertCircle,
  Check,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { cn } from "@/lib/utils";
import { FUNCTIONS_URL as BACKEND_URL, APP_KEY } from "@/lib/config";

export interface Museum {
  id: string;
  name: string;
  type: string;
  lat: number;
  lon: number;
  phone: string | null;
  website: string | null;
  email: string | null;
  address: string;
  distanceMiles: number;
}

interface MuseumResponse {
  museums: Museum[];
  expandedRadius: boolean;
  searchRadiusMiles: number;
}

interface MuseumFinderSheetProps {
  open: boolean;
  onDismiss: () => void;
  onEmailExpert: (museum: Museum) => void;
  onEmailExperts: (museums: Museum[]) => void;
  matchNames?: string[];
  matchConfidences?: number[];
  aiSummary?: string;
}

export function MuseumFinderSheet({
  open,
  onDismiss,
  onEmailExpert,
  onEmailExperts,
  matchNames = [],
  matchConfidences = [],
  aiSummary = "",
}: MuseumFinderSheetProps) {
  const [isLoading, setIsLoading] = useState(true);
  const [museums, setMuseums] = useState<Museum[]>([]);
  const [expandedRadius, setExpandedRadius] = useState(false);
  const [searchRadius, setSearchRadius] = useState(50);
  const [error, setError] = useState<string | null>(null);
  const [selectedMuseums, setSelectedMuseums] = useState<Set<string>>(
    new Set(),
  );

  const fetchMuseums = useCallback(async (lat: number, lon: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await fetch(`${BACKEND_URL}/museums`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-App-Key": APP_KEY,
        },
        body: JSON.stringify({ lat, lon, radius: 50 }),
      });
      if (!response.ok) {
        throw new Error(`Museum search failed (${response.status})`);
      }
      const data = (await response.json()) as MuseumResponse;
      setMuseums(data.museums);
      setExpandedRadius(data.expandedRadius);
      setSearchRadius(data.searchRadiusMiles);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to find museums. Please try again.",
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!open) return;
    setIsLoading(true);
    setError(null);
    setMuseums([]);
    setSelectedMuseums(new Set());

    if (!("geolocation" in navigator)) {
      setError("Geolocation is not supported by your browser.");
      setIsLoading(false);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        fetchMuseums(pos.coords.latitude, pos.coords.longitude);
      },
      (err) => {
        const messages: Record<number, string> = {
          1: "Location permission denied. Enable location access to find nearby museums.",
          2: "Could not determine your location. Please try again.",
          3: "Location request timed out. Please try again.",
        };
        setError(messages[err.code] ?? "Could not get your location.");
        setIsLoading(false);
      },
      { enableHighAccuracy: false, timeout: 15000, maximumAge: 300000 },
    );
  }, [open, fetchMuseums]);

  const toggleSelect = useCallback((museum: Museum) => {
    setSelectedMuseums((prev) => {
      const next = new Set(prev);
      const key = `${museum.name}|${museum.address}`;
      if (next.has(key)) {
        next.delete(key);
      } else {
        next.add(key);
      }
      return next;
    });
  }, []);

  const isMuseumSelected = useCallback(
    (museum: Museum) =>
      selectedMuseums.has(`${museum.name}|${museum.address}`),
    [selectedMuseums],
  );

  const selectedList = useMemo(
    () => museums.filter((m) => isMuseumSelected(m)),
    [museums, isMuseumSelected],
  );

  const handleComposeEmail = useCallback(() => {
    onEmailExperts(selectedList);
  }, [selectedList, onEmailExperts]);

  return (
    <Dialog open={open} onOpenChange={(v) => !v && onDismiss()}>
      <DialogContent className="max-h-[85vh] max-w-lg overflow-hidden p-0">
        <DialogHeader className="flex-row items-center justify-between space-y-0 border-b border-border px-5 py-4">
          <DialogTitle className="text-lg font-bold text-primary">
            Ask an Expert
          </DialogTitle>
        </DialogHeader>

        <div className="px-5 pb-2">
          <p className="text-sm text-muted-foreground">
            Nearby museums and cultural centers that can help identify your
            find.
          </p>
        </div>

        <div className="max-h-[calc(85vh-120px)] overflow-y-auto px-5 pb-5">
          {isLoading && (
            <div className="flex h-48 flex-col items-center justify-center gap-3">
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
              <p className="text-sm text-muted-foreground">
                Finding museums near you...
              </p>
            </div>
          )}

          {!isLoading && error && (
            <div className="flex flex-col items-center gap-3 p-4 text-center">
              <AlertCircle className="h-8 w-8 text-warning" />
              <p className="text-sm text-muted-foreground">{error}</p>
            </div>
          )}

          {!isLoading && !error && museums.length === 0 && (
            <div className="flex flex-col items-center gap-2 p-4 text-center">
              <p className="font-semibold text-foreground">
                No museums found nearby.
              </p>
              <p className="text-sm text-muted-foreground">
                Try searching online for "museum near me" or contact a regional
                university geology department.
              </p>
            </div>
          )}

          {!isLoading && !error && museums.length > 0 && (
            <div className="space-y-3">
              {expandedRadius && (
                <div className="flex items-center gap-2 rounded-lg bg-warning/15 p-3">
                  <MapPin className="h-4 w-4 shrink-0 text-warning" />
                  <p className="text-xs text-muted-foreground">
                    No museums within 50 miles — showing the nearest options up
                    to {searchRadius} miles away.
                  </p>
                </div>
              )}

              {museums.map((museum) => (
                <MuseumCard
                  key={museum.id}
                  museum={museum}
                  isSelected={isMuseumSelected(museum)}
                  onToggleSelect={() => toggleSelect(museum)}
                  onEmailExpert={() => onEmailExpert(museum)}
                />
              ))}

              {selectedList.length > 0 && (
                <Button
                  onClick={handleComposeEmail}
                  className="w-full gap-2"
                  size="lg"
                >
                  <Mail className="h-4 w-4" />
                  Compose Email ({selectedList.length})
                </Button>
              )}
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

function MuseumCard({
  museum,
  isSelected,
  onToggleSelect,
  onEmailExpert,
}: {
  museum: Museum;
  isSelected: boolean;
  onToggleSelect: () => void;
  onEmailExpert: () => void;
}) {
  return (
    <div
      className={cn(
        "dark-card sculpted-raised cursor-pointer rounded-lg p-4 transition-colors",
        isSelected && "ring-2 ring-primary",
      )}
      onClick={onToggleSelect}
    >
      <div className="flex items-start justify-between gap-2">
        <p className="font-semibold text-foreground">{museum.name}</p>
        <span className="shrink-0 rounded-md bg-primary/20 px-2 py-0.5 text-xs font-medium text-primary">
          {museum.type}
        </span>
      </div>

      {museum.address && (
        <p className="mt-1.5 text-sm text-muted-foreground">{museum.address}</p>
      )}

      <div className="mt-1 flex items-center gap-1.5">
        <Navigation className="h-3.5 w-3.5 text-sky-400" />
        <span className="text-sm font-medium text-sky-400">
          {museum.distanceMiles.toFixed(1)} miles away
        </span>
      </div>

      <div className="mt-3 flex flex-wrap items-center gap-2">
        <Button
          onClick={(e) => {
            e.stopPropagation();
            onEmailExpert();
          }}
          size="sm"
          className="gap-1.5"
        >
          <Mail className="h-3.5 w-3.5" />
          Email Expert
        </Button>

        <a
          href={`https://www.google.com/maps/dir/?api=1&destination=${museum.lat},${museum.lon}`}
          target="_blank"
          rel="noopener noreferrer"
          onClick={(e) => e.stopPropagation()}
          className="inline-flex h-9 items-center justify-center rounded-md border border-border bg-card px-3 text-sm text-sky-400 transition-colors hover:bg-muted"
          aria-label="Directions"
        >
          <Navigation className="h-4 w-4" />
        </a>

        {museum.phone && (
          <a
            href={`tel:${museum.phone}`}
            onClick={(e) => e.stopPropagation()}
            className="inline-flex h-9 items-center justify-center rounded-md border border-border bg-card px-3 text-sm text-sky-400 transition-colors hover:bg-muted"
            aria-label={`Call ${museum.phone}`}
          >
            <Phone className="h-4 w-4" />
          </a>
        )}

        {museum.website && (
          <a
            href={museum.website}
            target="_blank"
            rel="noopener noreferrer"
            onClick={(e) => e.stopPropagation()}
            className="inline-flex h-9 items-center justify-center rounded-md border border-border bg-card px-3 text-sm text-sky-400 transition-colors hover:bg-muted"
            aria-label="Website"
          >
            <Globe className="h-4 w-4" />
          </a>
        )}

        {isSelected && (
          <span className="ml-auto flex items-center gap-1 text-xs font-medium text-primary">
            <Check className="h-3.5 w-3.5" />
            Selected
          </span>
        )}
      </div>
    </div>
  );
}
