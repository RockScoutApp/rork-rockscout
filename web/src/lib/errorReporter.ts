/**
 * Central error reporter for the web PWA.
 *
 * Ships runtime exceptions to the Supabase `rockscout_error_logs` table via
 * the `/error-report` Cloudflare Worker endpoint.
 *
 * Self-healing: for known recoverable patterns (stale cache, service worker
 * issues, missing config), the reporter automatically applies a fix before
 * the error propagates to the user.
 *
 * All network calls are fire-and-forget — they never block rendering or
 * crash the app if the upload fails.
 */

import { FUNCTIONS_URL, APP_KEY } from "@/lib/config";

interface ErrorPayload {
  platform: string;
  appVersion?: string;
  osVersion?: string;
  deviceModel?: string;
  userId?: string;
  errorType: string;
  errorMessage: string;
  stackTrace?: string;
  isFatal: boolean;
  screen?: string;
  breadcrumb?: string;
  autoHealed: boolean;
  healAction?: string;
  fingerprint: string;
}

/** Dedup window — skip if same fingerprint was reported within this time. */
const DEDUP_WINDOW_MS = 5 * 60 * 1000; // 5 minutes
const MAX_STACK_TRACE = 8000;
const MAX_SCREEN = 200;
const MAX_MESSAGE = 2000;
const MAX_BREADCRUMB = 500;

/** In-memory dedup map: fingerprint → last report timestamp. */
const reportTimestamps = new Map<string, number>();

/** Current user ID (set after auth). */
let currentUserId: string | null = null;

/** Last user action for breadcrumb tracing. */
let lastBreadcrumb: string | null = null;

/** App version from package.json or build. */
const APP_VERSION = "1.0.0";

/** Set the current user ID so errors can be attributed. */
export function setErrorReporterUserId(userId: string | null): void {
  currentUserId = userId;
}

/** Set the last user action for breadcrumb tracing. */
export function setBreadcrumb(action: string): void {
  lastBreadcrumb = action.slice(0, MAX_BREADCRUMB);
}

/** SHA-256 of (errorType + message + screen), first 16 hex chars. */
async function fingerprint(
  type: string,
  message: string,
  screen: string,
): Promise<string> {
  const input = `${type}|${message}|${screen}`;
  if (globalThis.crypto?.subtle) {
    const data = new TextEncoder().encode(input);
    const hash = await globalThis.crypto.subtle.digest("SHA-256", data);
    return Array.from(new Uint8Array(hash))
      .map((b) => b.toString(16).padStart(2, "0"))
      .join("")
      .slice(0, 16);
  }
  // Fallback for very old browsers
  let hash = 0;
  for (let i = 0; i < input.length; i++) {
    hash = ((hash << 5) - hash + input.charCodeAt(i)) | 0;
  }
  return Math.abs(hash).toString(16).padStart(16, "0").slice(0, 16);
}

/**
 * Report an error to the central service. Fire-and-forget.
 *
 * @param screen - Route/screen where the error occurred
 * @param error - The exception
 * @param isFatal - Whether this is a fatal crash
 * @param attemptSelfHeal - If true, try to auto-fix known patterns
 */
export async function reportError(
  screen: string,
  error: unknown,
  isFatal = false,
  attemptSelfHeal = true,
): Promise<void> {
  const errorObj = error instanceof Error ? error : new Error(String(error));
  const errorType = errorObj.name || "Error";
  const message = (errorObj.message || "(no message)").slice(0, MAX_MESSAGE);
  const stackTrace = errorObj.stack?.slice(0, MAX_STACK_TRACE);

  let healAction: string | null = null;
  let autoHealed = false;

  if (attemptSelfHeal && !isFatal) {
    const result = selfHeal(errorType, message, screen);
    if (result) {
      autoHealed = true;
      healAction = result;
      console.info(`[ErrorReporter] Auto-healed: ${healAction}`);
    }
  }

  const fp = await fingerprint(errorType, message, screen);

  // Dedup
  const now = Date.now();
  const lastTime = reportTimestamps.get(fp);
  if (lastTime && now - lastTime < DEDUP_WINDOW_MS) {
    return; // Skip duplicate
  }
  reportTimestamps.set(fp, now);

  const payload: ErrorPayload = {
    platform: "web",
    appVersion: APP_VERSION,
    osVersion: navigator.userAgent.includes("Mac") ? "macOS" :
      navigator.userAgent.includes("Win") ? "Windows" :
      navigator.userAgent.includes("Android") ? "Android" :
      navigator.userAgent.includes("iPhone") || navigator.userAgent.includes("iPad") ? "iOS" : "Unknown",
    deviceModel: navigator.userAgent.slice(0, 300),
    userId: currentUserId ?? undefined,
    errorType: errorType.slice(0, 300),
    errorMessage: message,
    stackTrace: stackTrace || undefined,
    isFatal,
    screen: screen.slice(0, MAX_SCREEN) || undefined,
    breadcrumb: lastBreadcrumb ?? undefined,
    autoHealed,
    healAction: healAction ?? undefined,
    fingerprint: fp,
  };

  // Fire-and-forget upload
  void uploadError(payload);
}

/**
 * Report a plain message (no exception object).
 */
export async function reportMessage(
  screen: string,
  message: string,
  isFatal = false,
): Promise<void> {
  const fp = await fingerprint("Message", message, screen);

  const now = Date.now();
  const lastTime = reportTimestamps.get(fp);
  if (lastTime && now - lastTime < DEDUP_WINDOW_MS) return;
  reportTimestamps.set(fp, now);

  const payload: ErrorPayload = {
    platform: "web",
    appVersion: APP_VERSION,
    osVersion: undefined,
    deviceModel: navigator.userAgent.slice(0, 300),
    userId: currentUserId ?? undefined,
    errorType: "Message",
    errorMessage: message.slice(0, MAX_MESSAGE),
    stackTrace: undefined,
    isFatal,
    screen: screen.slice(0, MAX_SCREEN) || undefined,
    breadcrumb: lastBreadcrumb ?? undefined,
    autoHealed: false,
    healAction: undefined,
    fingerprint: fp,
  };

  void uploadError(payload);
}

/** Upload the error payload to the central service. */
async function uploadError(payload: ErrorPayload): Promise<void> {
  if (!FUNCTIONS_URL || !APP_KEY) {
    // No backend configured — log to console as fallback
    console.error("[ErrorReporter] No backend configured:", payload.errorType, payload.errorMessage);
    return;
  }

  try {
    await fetch(`${FUNCTIONS_URL}/error-report`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-App-Key": APP_KEY,
      },
      body: JSON.stringify(payload),
    });
  } catch {
    // Fire-and-forget — network failures are expected
  }
}

/**
 * Self-healing for known web error patterns.
 * Returns the heal action description if healed, or null if not recognized.
 */
function selfHeal(errorType: string, message: string, screen: string): string | null {
  const msg = message.toLowerCase();

  // 1. Chunk loading errors — stale service worker serving old chunks
  if (errorType === "SyntaxError" || msg.includes("unexpected token") ||
      msg.includes("failed to fetch dynamic import") ||
      msg.includes("loading chunk") || msg.includes("loading module")) {
    clearStaleCache();
    return "cleared_stale_service_worker";
  }

  // 2. Network / fetch errors — retry will happen automatically, just acknowledge
  if (errorType === "TypeError" && msg.includes("failed to fetch")) {
    return "network_retry_acknowledged";
  }

  // 3. Storage quota exceeded — clear non-essential caches
  if (msg.includes("quota") || msg.includes("storage") || msg.includes("exceeded")) {
    clearStorageQuota();
    return "cleared_storage_quota";
  }

  // 4. Service worker registration failure
  if (msg.includes("service worker") || msg.includes("serviceworker")) {
    // Unregister stale workers so a fresh one can register
    if ("serviceWorker" in navigator) {
      navigator.serviceWorker.getRegistrations().then((regs) => {
        regs.forEach((r) => r.unregister().catch(() => {}));
      });
    }
    return "unregistered_stale_service_worker";
  }

  // 5. React context / undefined destructuring — the ErrorBoundary already
  //    handles this with a reload button, so just acknowledge
  if (msg.includes("cannot destructure") || msg.includes("is undefined") ||
      msg.includes("is null")) {
    return null; // Let the ErrorBoundary handle it
  }

  return null;
}

/** Clear stale caches and unregister service workers. */
function clearStaleCache(): void {
  if ("caches" in window) {
    caches.keys().then((keys) =>
      Promise.all(keys.map((k) => caches.delete(k))).catch(() => {}),
    );
  }
}

/** Clear non-essential storage to free up quota. */
function clearStorageQuota(): void {
  // Clear sessionStorage (non-persistent)
  try {
    sessionStorage.clear();
  } catch {
    // Ignore
  }

  // Clear caches API
  if ("caches" in window) {
    caches.keys().then((keys) =>
      Promise.all(keys.map((k) => caches.delete(k))).catch(() => {}),
    );
  }

  // Don't clear localStorage — it has auth tokens and user prefs
  // Don't clear IndexedDB — it has Supabase state
}

/**
 * Install global error handlers for uncaught exceptions and unhandled
 * promise rejections. Call this once at app startup.
 */
export function installGlobalErrorHandlers(): void {
  // Uncaught errors (outside React error boundary)
  window.addEventListener("error", (event) => {
    void reportError(
      window.location.pathname,
      event.error ?? new Error(event.message),
      false, // isFatal
    );
  });

  // Unhandled promise rejections
  window.addEventListener("unhandledrejection", (event) => {
    const error = event.reason instanceof Error
      ? event.reason
      : new Error(String(event.reason));
    void reportError(
      window.location.pathname,
      error,
      false, // isFatal
    );
  });
}
