import { useState, useCallback, memo } from "react";
import { optimizeImageUrl } from "@/lib/imageOptimize";
import { cn } from "@/lib/utils";
import { SculptedCard } from "@/components/sculpted";

interface OptimizedImageProps {
  src: string | undefined | null;
  alt: string;
  className?: string;
  loading?: "lazy" | "eager";
  /** Override the default skeleton background color. */
  skeletonClassName?: string;
}

/**
 * Image component with skeleton loading state, fade-in animation, and
 * URL optimization via the edge caching proxy.
 *
 * - Rewrites r2-pub.rork.com URLs through the Worker proxy for edge caching
 * - Shows a subtle shimmering skeleton while loading
 * - Fades in smoothly once decoded
 * - Uses async decoding to avoid blocking the main thread
 */
function OptimizedImageImpl({
  src,
  alt,
  className,
  loading = "lazy",
  skeletonClassName,
}: OptimizedImageProps) {
  const [loaded, setLoaded] = useState(false);
  const [errored, setErrored] = useState(false);

  const optimizedSrc = optimizeImageUrl(src);

  const handleLoad = useCallback(() => {
    setLoaded(true);
  }, []);

  const handleError = useCallback(() => {
    setErrored(true);
    setLoaded(true);
  }, []);

  if (!optimizedSrc || errored) {
    return (
      <div
        className={cn(
          "flex h-full w-full items-center justify-center bg-muted/40",
          className,
        )}
        aria-label={alt}
      >
        <svg
          className="h-8 w-8 text-muted-foreground/40"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={1.5}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909M3.75 3.75h16.5a2.25 2.25 0 012.25 2.25v12a2.25 2.25 0 01-2.25 2.25H3.75A2.25 2.25 0 011.5 18V6a2.25 2.25 0 012.25-2.25z"
          />
        </svg>
      </div>
    );
  }

  return (
    <>
      {!loaded && (
        <div
          className={cn(
            "absolute inset-0 animate-pulse bg-gradient-to-br from-muted/40 via-muted/60 to-muted/40",
            skeletonClassName,
          )}
          aria-hidden="true"
        />
      )}
      <img
        src={optimizedSrc}
        alt={alt}
        loading={loading}
        decoding="async"
        onLoad={handleLoad}
        onError={handleError}
        className={cn(
          "transition-opacity duration-300",
          loaded ? "opacity-100" : "opacity-0",
          className,
        )}
      />
    </>
  );
}

export const OptimizedImage = memo(OptimizedImageImpl);

interface SpecimenGalleryProps {
  images: string[];
  alt: string;
  accent?: "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";
}

/**
 * Multi-image gallery for specimen detail pages.
 * Shows a hero image with a thumbnail strip below.
 * Clicking the hero opens a full-screen lightbox with arrow navigation.
 * Falls back to a single image display when only 1 image is available.
 */
export function SpecimenGallery({ images, alt, accent = "citrine" }: SpecimenGalleryProps) {
  const [activeIndex, setActiveIndex] = useState(0);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  const validImages = images.filter(Boolean);
  if (validImages.length === 0) return null;
  if (validImages.length === 1) {
    return (
      <SculptedCard accent={accent} glowing className="overflow-hidden">
        <div className="relative overflow-hidden">
          <OptimizedImage
            src={validImages[0]}
            alt={alt}
            loading="eager"
            className="max-h-[400px] w-full object-cover"
          />
        </div>
      </SculptedCard>
    );
  }

  return (
    <>
      <div className="space-y-3">
        {/* Main hero image */}
        <SculptedCard accent={accent} glowing className="overflow-hidden">
          <button
            type="button"
            onClick={() => setLightboxOpen(true)}
            className="relative block w-full overflow-hidden"
            aria-label="Open full-size image"
          >
            <OptimizedImage
              src={validImages[activeIndex]}
              alt={alt}
              loading="eager"
              className="max-h-[400px] w-full object-cover"
            />
            <div className="absolute bottom-2 right-2 rounded-full bg-black/60 px-2 py-1 text-xs text-white/90 backdrop-blur-sm">
              {activeIndex + 1} / {validImages.length}
            </div>
          </button>
        </SculptedCard>

        {/* Thumbnail strip */}
        <div className="flex gap-2 overflow-x-auto pb-1">
          {validImages.map((img, i) => (
            <button
              key={`${img}-${i}`}
              type="button"
              onClick={() => setActiveIndex(i)}
              className={cn(
                "relative h-16 w-16 shrink-0 overflow-hidden rounded-lg border-2 transition-all",
                i === activeIndex
                  ? "border-primary ring-2 ring-primary/30"
                  : "border-muted/30 opacity-70 hover:opacity-100",
              )}
              aria-label={`View image ${i + 1}`}
              aria-pressed={i === activeIndex}
            >
              <OptimizedImage
                src={img}
                alt={`${alt} — view ${i + 1}`}
                loading="lazy"
                className="h-full w-full object-cover"
              />
            </button>
          ))}
        </div>
      </div>

      {/* Lightbox overlay */}
      {lightboxOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 backdrop-blur-sm"
          onClick={() => setLightboxOpen(false)}
        >
          <button
            type="button"
            className="absolute right-4 top-4 rounded-full bg-white/10 p-2 text-white transition hover:bg-white/20"
            onClick={() => setLightboxOpen(false)}
            aria-label="Close image viewer"
          >
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
          <img
            src={optimizeImageUrl(validImages[activeIndex])}
            alt={alt}
            className="max-h-[90vh] max-w-[90vw] rounded-lg object-contain"
            onClick={(e) => e.stopPropagation()}
          />
          {validImages.length > 1 && (
            <>
              <button
                type="button"
                className="absolute left-4 top-1/2 -translate-y-1/2 rounded-full bg-white/10 p-3 text-white transition hover:bg-white/20"
                onClick={(e) => {
                  e.stopPropagation();
                  setActiveIndex((prev) => (prev - 1 + validImages.length) % validImages.length);
                }}
                aria-label="Previous image"
              >
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
                </svg>
              </button>
              <button
                type="button"
                className="absolute right-4 top-1/2 -translate-y-1/2 rounded-full bg-white/10 p-3 text-white transition hover:bg-white/20"
                onClick={(e) => {
                  e.stopPropagation();
                  setActiveIndex((prev) => (prev + 1) % validImages.length);
                }}
                aria-label="Next image"
              >
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
                </svg>
              </button>
            </>
          )}
        </div>
      )}
    </>
  );
}
