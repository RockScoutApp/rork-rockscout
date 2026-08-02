/**
 * Image URL optimization utilities for RockScout.
 *
 * r2-pub.rork.com images are served with no cache-control headers, so
 * Cloudflare never caches them at the edge. This rewrites image URLs to
 * route through our Worker proxy (/img?url=...) which adds immutable
 * cache headers, enabling edge caching.
 */

/** The Worker proxy endpoint. */
const PROXY_BASE = "https://rockscout-finder-backend.rork.app/img?url=";

/** Check if a URL points to r2-pub.rork.com (needs proxying). */
function isR2PubUrl(url: string): boolean {
  return url.includes("r2-pub.rork.com");
}

/**
 * Rewrite an image URL to go through the caching proxy if needed.
 * Returns the original URL if it's already optimized or not an r2-pub URL.
 */
export function optimizeImageUrl(url: string | undefined | null): string | undefined {
  if (!url) return undefined;
  if (!isR2PubUrl(url)) return url;
  return `${PROXY_BASE}${encodeURIComponent(url)}`;
}

/**
 * Batch-optimized image URLs for grid views.
 * Returns original URLs for non-r2-pub images, proxied URLs for r2-pub.
 */
export function optimizeImageUrls(urls: (string | undefined | null)[]): (string | undefined)[] {
  return urls.map(optimizeImageUrl);
}
