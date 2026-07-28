/**
 * Generates a stable per-browser fingerprint for premium device tracking.
 *
 * Combines screen resolution, color depth, timezone, platform string, and a
 * random UUID stored in localStorage to produce a consistent identifier
 * across sessions on the same browser.
 */

const STORAGE_KEY = "rockscout_device_fingerprint";

/**
 * Get or create a stable device fingerprint for this browser.
 * The fingerprint is persisted in localStorage and reused across sessions.
 */
export function getDeviceFingerprint(): string {
  // Check localStorage first
  try {
    const existing = localStorage.getItem(STORAGE_KEY);
    if (existing) return existing;
  } catch {
    // localStorage might be unavailable (private mode) — fall through
  }

  // Generate a new fingerprint
  const uuid = generateUuid();
  const screenInfo = `${screen.width}x${screen.height}x${screen.colorDepth}`;
  const tz = Intl.DateTimeFormat().resolvedOptions().timeZone ?? "unknown";
  const platform = navigator.platform ?? "unknown";
  const fingerprint = `${uuid}|${screenInfo}|${tz}|${platform}`;

  try {
    localStorage.setItem(STORAGE_KEY, fingerprint);
  } catch {
    // Best-effort — if localStorage fails, the fingerprint is still
    // valid for this session but won't persist.
  }

  return fingerprint;
}

/**
 * Auto-detect the device type from the user agent and screen width.
 * Returns "Phone", "Tablet", or "PC".
 */
export function detectDeviceType(): "Phone" | "Tablet" | "PC" {
  const ua = navigator.userAgent.toLowerCase();
  const width = window.innerWidth;

  const isMobile = /android|iphone|ipod|windows phone/.test(ua);
  const isTablet = /ipad|tablet|kindle|silk/.test(ua) || (/android/.test(ua) && !/mobile/.test(ua));

  if (isTablet || (!isMobile && width >= 768 && width < 1024)) return "Tablet";
  if (isMobile || width < 768) return "Phone";
  return "PC";
}

/**
 * Get a human-readable device label for the Manage Devices screen.
 */
export function getDeviceLabel(): string {
  const type = detectDeviceType();
  const browser = detectBrowserName();
  return `${type} — ${browser}`;
}

function detectBrowserName(): string {
  const ua = navigator.userAgent;
  if (/edg/i.test(ua)) return "Edge";
  if (/chrome/i.test(ua)) return "Chrome";
  if (/firefox/i.test(ua)) return "Firefox";
  if (/safari/i.test(ua)) return "Safari";
  return "Browser";
}

function generateUuid(): string {
  if (typeof crypto !== "undefined" && crypto.randomUUID) {
    return crypto.randomUUID();
  }
  // Fallback for older browsers
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}
