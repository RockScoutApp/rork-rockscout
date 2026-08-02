import { useState, useCallback, memo } from "react";
import { optimizeImageUrl } from "@/lib/imageOptimize";
import { cn } from "@/lib/utils";

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
