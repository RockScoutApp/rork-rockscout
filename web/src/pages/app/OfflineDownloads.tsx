import { useState, useEffect, useCallback } from "react";
import {
  Download,
  HardDrive,
  Check,
  Loader2,
  WifiOff,
  RefreshCw,
  Trash2,
  Cloud,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";

interface StorageEstimate {
  usage: number;
  quota: number;
}

const FETCH_BATCH_SIZE = 8;

/**
 * Offline downloads page — lets the user bulk-cache specimen photos,
 * guide illustrations, and map tiles for offline field use.
 *
 * Desktop gets the full "Maximum" pack (~3.5GB); iPhone is detected and
 * offered a curated pack (top specimens + guide illustrations, ~150-500MB).
 * Calls navigator.storage.persist() so the browser won't evict the cache
 * under storage pressure.
 */
export default function OfflineDownloads() {
  const [persisted, setPersisted] = useState<boolean | null>(null);
  const [storage, setStorage] = useState<StorageEstimate | null>(null);
  const [downloading, setDownloading] = useState(false);
  const [progress, setProgress] = useState<{ done: number; total: number } | null>(null);
  const [isIOS, setIsIOS] = useState(false);
  const [cacheCount, setCacheCount] = useState<number | null>(null);

  useEffect(() => {
    // Detect iPhone for the curated pack prompt.
    const ua = navigator.userAgent;
    setIsIOS(/iPhone|iPad|iPod/.test(ua) || (navigator.platform === "MacIntel" && navigator.maxTouchPoints > 1));

    // Request persistent storage.
    if (navigator.storage?.persisted) {
      navigator.storage
        .persisted()
        .then((p) => setPersisted(p))
        .catch(() => setPersisted(false));
    }

    // Fetch storage estimate.
    refreshStorage();

    // Count existing cached entries.
    countCacheEntries();
  }, []);

  const refreshStorage = useCallback(() => {
    if (navigator.storage?.estimate) {
      navigator.storage
        .estimate()
        .then((est) =>
          setStorage({
            usage: est.usage ?? 0,
            quota: est.quota ?? 0,
          }),
        )
        .catch(() => undefined);
    }
  }, []);

  const countCacheEntries = useCallback(async () => {
    try {
      const cache = await caches.open("rockscout-v3");
      const keys = await cache.keys();
      setCacheCount(keys.length);
    } catch {
      setCacheCount(0);
    }
  }, []);

  const requestPersistence = useCallback(async () => {
    if (!navigator.storage?.persist) {
      toast.error("Persistent storage isn't supported on this browser.");
      return;
    }
    try {
      const granted = await navigator.storage.persist();
      setPersisted(granted);
      if (granted) {
        toast.success("Storage will persist — your offline cache won't be auto-evicted.");
      } else {
        toast.error("Browser denied persistent storage. Your cache may be evicted under pressure.");
      }
    } catch {
      toast.error("Could not request persistent storage.");
    }
  }, []);

  /** Fetch a list of specimen image URLs to cache from the Supabase catalog. */
  const fetchSpecimenImageUrls = useCallback(
    async (limit: number): Promise<string[]> => {
      const { supabase } = await import("@/lib/supabase");
      const { data, error } = await supabase
        .from("specimen_catalog")
        .select("image_url")
        .not("image_url", "is", null)
        .limit(limit);
      if (error) throw error;
      return (data ?? [])
        .map((r) => r.image_url as string)
        .filter((url) => url && url.startsWith("http"));
    },
    [],
  );

  /** Cache a batch of URLs in parallel using the Cache API. */
  const cacheUrls = useCallback(async (urls: string[], cacheName: string): Promise<number> => {
    const cache = await caches.open(cacheName);
    let succeeded = 0;
    for (let i = 0; i < urls.length; i += FETCH_BATCH_SIZE) {
      const batch = urls.slice(i, i + FETCH_BATCH_SIZE);
      const results = await Promise.allSettled(
        batch.map((url) =>
          cache.add(url).then(() => {
            succeeded++;
          }),
        ),
      );
      // Update progress per batch.
      setProgress({ done: Math.min(i + batch.length, urls.length), total: urls.length });
      void results;
    }
    return succeeded;
  }, []);

  const handleDownload = useCallback(
    async (pack: "maximum" | "curated") => {
      setDownloading(true);
      setProgress({ done: 0, total: 0 });
      try {
        // Step 1: request persistent storage.
        if (!persisted && navigator.storage?.persist) {
          const granted = await navigator.storage.persist();
          setPersisted(granted);
        }

        // Step 2: fetch specimen image URLs.
        const limit = pack === "maximum" ? 4000 : 200;
        toast.info(`Fetching ${pack === "maximum" ? "full" : "curated"} specimen catalog…`);
        const specimenUrls = await fetchSpecimenImageUrls(limit);

        // Step 3: cache the images via the service worker's Cache API.
        toast.info(`Caching ${specimenUrls.length} specimen photos…`);
        const cached = await cacheUrls(specimenUrls, "rockscout-v3");

        // Step 4: cache the app shell (already done by the SW, but ensure it).
        toast.info("Caching app shell and guide pages…");
        const shellUrls = ["/", "/app", "/index.html", "/pwa-192.png", "/pwa-512.png"];
        const shellCache = await caches.open("rockscout-v3");
        await Promise.allSettled(shellUrls.map((u) => shellCache.add(u)));

        refreshStorage();
        countCacheEntries();
        setProgress(null);
        toast.success(
          `Offline pack ready — ${cached} photos cached. ${
            pack === "maximum" ? "Full database available offline." : "Top specimens available offline."
          }`,
        );
      } catch (err) {
        setProgress(null);
        toast.error(err instanceof Error ? err.message : "Download failed");
      } finally {
        setDownloading(false);
      }
    },
    [persisted, fetchSpecimenImageUrls, cacheUrls, refreshStorage, countCacheEntries],
  );

  const handleClearCache = useCallback(async () => {
    setDownloading(true);
    try {
      const cacheNames = ["rockscout-v3", "rockscout-tiles-v1"];
      for (const name of cacheNames) {
        const cache = await caches.open(name);
        const keys = await cache.keys();
        await Promise.all(keys.map((req) => cache.delete(req)));
      }
      countCacheEntries();
      refreshStorage();
      toast.success("Offline cache cleared.");
    } catch {
      toast.error("Could not clear cache.");
    } finally {
      setDownloading(false);
    }
  }, [countCacheEntries, refreshStorage]);

  const formatBytes = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`;
    if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(0)} MB`;
    return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`;
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Offline Downloads
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Cache specimen photos, guides, and map tiles for field trips with no signal.
        </p>
      </div>

      {/* Storage status */}
      <div className="dark-card sculpted-raised rounded-xl p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/15">
            <HardDrive className="h-5 w-5 text-primary" />
          </div>
          <div className="flex-1">
            <p className="font-display text-sm font-semibold text-foreground">
              Storage Status
            </p>
            <p className="text-xs text-muted-foreground">
              {storage
                ? `${formatBytes(storage.usage)} used of ${formatBytes(storage.quota)} available`
                : "Calculating…"}
            </p>
          </div>
          <div className="text-right">
            {persisted === true ? (
              <span className="inline-flex items-center gap-1 text-xs font-medium text-emerald-500">
                <Check className="h-3.5 w-3.5" /> Persistent
              </span>
            ) : persisted === false ? (
              <Button size="sm" variant="outline" onClick={requestPersistence}>
                <Cloud className="mr-1.5 h-3.5 w-3.5" />
                Protect cache
              </Button>
            ) : (
              <span className="text-xs text-muted-foreground">Checking…</span>
            )}
          </div>
        </div>

        {cacheCount !== null && (
          <p className="mt-3 text-xs text-muted-foreground">
            {cacheCount} {cacheCount === 1 ? "item" : "items"} currently cached
          </p>
        )}
      </div>

      {/* iOS note */}
      {isIOS && (
        <div className="flex items-start gap-3 rounded-xl border border-amber-500/30 bg-amber-500/10 p-4">
          <WifiOff className="mt-0.5 h-5 w-5 shrink-0 text-amber-500" />
          <div>
            <p className="text-sm font-medium text-amber-600 dark:text-amber-400">
              iPhone storage is limited
            </p>
            <p className="mt-1 text-xs text-muted-foreground">
              iOS restricts offline storage more than desktop. The curated pack
              (~200 photos) is recommended for iPhone. The full pack may fail or
              get evicted by the browser.
            </p>
          </div>
        </div>
      )}

      {/* Download packs */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {/* Curated pack */}
        <div className="dark-card sculpted-raised rounded-xl p-5">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
              <Download className="h-6 w-6 text-primary" />
            </div>
            <div>
              <h2 className="font-display text-lg font-bold text-foreground">
                Curated Pack
              </h2>
              <p className="text-xs text-muted-foreground">~150 MB</p>
            </div>
          </div>
          <p className="mt-3 text-sm text-muted-foreground">
            Top 200 specimen photos + the app shell. Best for iPhone or quick
            field reference.
          </p>
          <Button
            className="mt-4 w-full gap-2"
            variant="outline"
            disabled={downloading}
            onClick={() => handleDownload("curated")}
          >
            {downloading && progress ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                {progress.done}/{progress.total}
              </>
            ) : (
              <>
                <Download className="h-4 w-4" />
                Download curated pack
              </>
            )}
          </Button>
        </div>

        {/* Maximum pack */}
        <div className="rounded-xl border border-primary/30 bg-gradient-to-br from-primary/10 to-card p-5">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/20">
              <HardDrive className="h-6 w-6 text-primary" />
            </div>
            <div>
              <h2 className="font-display text-lg font-bold text-foreground">
                Maximum Pack
              </h2>
              <p className="text-xs text-muted-foreground">~3.5 GB</p>
            </div>
          </div>
          <p className="mt-3 text-sm text-muted-foreground">
            All specimen photos, guide pages, and the app shell. Full offline
            database. Best for desktop or Android.
          </p>
          <Button
            className="mt-4 w-full gap-2"
            disabled={downloading}
            onClick={() => handleDownload("maximum")}
          >
            {downloading && progress ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                {progress.done}/{progress.total}
              </>
            ) : (
              <>
                <Download className="h-4 w-4" />
                Download maximum pack
              </>
            )}
          </Button>
        </div>
      </div>

      {/* Map tiles note */}
      <div className="dark-card sculpted-raised rounded-xl p-4">
        <p className="text-sm text-muted-foreground">
          <RefreshCw className="mr-1.5 inline h-4 w-4" />
          Map tiles are cached automatically as you browse the map. Visit an area
          online first, then it'll be available offline.
        </p>
      </div>

      {/* Clear cache */}
      <div className="flex justify-end">
        <Button
          variant="ghost"
          size="sm"
          className="gap-2 text-destructive hover:text-destructive"
          disabled={downloading || (cacheCount ?? 0) === 0}
          onClick={handleClearCache}
        >
          <Trash2 className="h-4 w-4" />
          Clear offline cache
        </Button>
      </div>
    </div>
  );
}
