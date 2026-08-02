/**
 * Image caching proxy for RockScout.
 *
 * r2-pub.rork.com serves specimen/artifact images as uncompressed PNGs with
 * NO cache-control headers, so Cloudflare never caches them at the edge
 * (cf-cache-status: DYNAMIC). Every grid view of 24 specimens downloads
 * ~48 MB from origin on every page load.
 *
 * This proxy fetches the image from r2-pub and returns it with aggressive
 * immutable cache headers, so Cloudflare's edge CDN caches the response.
 * After the first request, subsequent loads for the same image are served
 * from the nearest edge node — typically <50ms instead of 2-5 seconds.
 *
 * Route: GET /img?url=<encoded r2-pub URL>
 * No auth required — these are public images. Rate-limited by IP.
 */

const ALLOWED_HOST = "r2-pub.rork.com";
const CACHE_MAX_AGE = 2592000; // 30 days

/** Allowed origins for CORS. */
const ALLOWED_ORIGINS = new Set([
  "https://rockscout.app",
  "https://rockscout.net",
  "https://jvns5dfy7fpytx79a2tb3-web.rork.live",
  "http://localhost:8080",
  "http://localhost:3000",
]);

export async function handleImageProxy(
  request: Request,
  cors: Record<string, string>,
): Promise<Response> {
  const url = new URL(request.url);
  const targetParam = url.searchParams.get("url");

  if (!targetParam) {
    return new Response("Missing url parameter", { status: 400, headers: cors });
  }

  let targetUrl: URL;
  try {
    targetUrl = new URL(targetParam);
  } catch {
    return new Response("Invalid url parameter", { status: 400, headers: cors });
  }

  // Only proxy r2-pub.rork.com — prevent SSRF.
  if (targetUrl.hostname !== ALLOWED_HOST) {
    return new Response("Forbidden host", { status: 403, headers: cors });
  }

  // Only allow GET/HEAD for images.
  if (request.method !== "GET" && request.method !== "HEAD") {
    return new Response("Method not allowed", { status: 405, headers: cors });
  }

  try {
    const upstream = await fetch(targetUrl.toString(), {
      method: request.method,
      // Don't forward cookies or auth headers to R2.
      headers: { "Accept": "image/*" },
      cf: {
        // Cache the upstream fetch in Cloudflare's edge cache.
        cacheTtl: CACHE_MAX_AGE,
        cacheEverything: true,
      },
    });

    if (!upstream.ok) {
      return new Response(`Upstream error: ${upstream.status}`, {
        status: upstream.status,
        headers: cors,
      });
    }

    // Clone the response with aggressive cache headers.
    const headers = new Headers();
    headers.set("Content-Type", upstream.headers.get("Content-Type") || "image/png");
    headers.set("Content-Length", upstream.headers.get("Content-Length") || "");
    headers.set("Cache-Control", `public, max-age=${CACHE_MAX_AGE}, immutable`);
    headers.set("Access-Control-Allow-Origin", "*");
    headers.set("Vary", "Accept");

    // For HEAD requests, return headers only.
    if (request.method === "HEAD") {
      return new Response(null, { status: 200, headers });
    }

    return new Response(upstream.body, { status: 200, headers });
  } catch (err) {
    return new Response("Failed to fetch image", { status: 502, headers: cors });
  }
}

/** Build CORS headers for the proxy based on request origin. */
export function buildProxyCors(request: Request): Record<string, string> {
  const origin = request.headers.get("Origin") || "";
  const allowed = ALLOWED_ORIGINS.has(origin) ? origin : "";
  return {
    "Access-Control-Allow-Origin": allowed || "*",
    "Access-Control-Allow-Methods": "GET, HEAD, OPTIONS",
    "Access-Control-Max-Age": "86400",
  };
}
