/**
 * Auto-detect the device type from the user agent and screen width.
 * Returns "Phone", "Tablet", or "PC".
 */
export type DeviceType = "Phone" | "Tablet" | "PC";

export function detectDeviceType(): DeviceType {
  const ua = navigator.userAgent.toLowerCase();
  const width = window.innerWidth;

  const isMobile = /android|iphone|ipod|windows phone/.test(ua);
  const isTablet =
    /ipad|tablet|kindle|silk/.test(ua) ||
    (/android/.test(ua) && !/mobile/.test(ua));

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
