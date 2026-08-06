/**
 * Email verification helpers for the web app.
 *
 * Talks to the Cloudflare Worker `/email-verification` endpoint, which sends a
 * 6-digit code via Resend and — once the code checks out — marks the Supabase
 * email as confirmed so the account can sign in immediately.
 *
 * The endpoint is app-key guarded: requests without the `X-App-Key` header are
 * rejected with 401, which is why every call here goes through `postAction`.
 */

import { FUNCTIONS_URL, APP_KEY } from "@/lib/config";

export interface VerificationOutcome {
  ok: boolean;
  /** True only for a successful `verify` call. */
  verified?: boolean;
  /** True when the backend also confirmed the Supabase email. */
  emailConfirmed?: boolean;
  /** Backend reason when emailConfirmed is false (e.g. admin_401). */
  confirmReason?: string;
  /** User-facing hint from the backend when confirm failed. */
  confirmHint?: string;
  /** User-facing message when `ok` is false. */
  error?: string;
}

interface WorkerResponse {
  ok?: boolean;
  verified?: boolean;
  emailConfirmed?: boolean;
  confirmReason?: string;
  confirmHint?: string;
  error?: string;
  reason?: string;
}

async function postAction(
  payload: Record<string, unknown>,
): Promise<VerificationOutcome> {
  if (!FUNCTIONS_URL || !APP_KEY) {
    console.error("Email verification is not configured for this build");
    return {
      ok: false,
      error: "Verification is unavailable right now. Please try again later.",
    };
  }

  try {
    const res = await fetch(`${FUNCTIONS_URL}/email-verification`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-App-Key": APP_KEY,
      },
      body: JSON.stringify(payload),
    });

    let data: WorkerResponse = {};
    try {
      data = (await res.json()) as WorkerResponse;
    } catch {
      // Non-JSON response — fall through to the generic error below.
    }

    if (data.ok) {
      return {
        ok: true,
        verified: data.verified ?? false,
        emailConfirmed: data.emailConfirmed ?? false,
        confirmReason: data.confirmReason,
        confirmHint: data.confirmHint,
      };
    }

    if (res.status === 429) {
      return { ok: false, error: "Too many attempts. Wait a moment and retry." };
    }

    return {
      ok: false,
      error: data.error ?? "Verification failed. Please try again.",
    };
  } catch {
    return {
      ok: false,
      error: "Network error. Check your connection and try again.",
    };
  }
}

/** Emails a fresh 6-digit verification code to `email`. */
export function sendVerificationCode(email: string): Promise<VerificationOutcome> {
  return postAction({ action: "send", email });
}

/**
 * Checks a 6-digit `code` for `email`. On success the backend also confirms
 * the Supabase account so the user can sign in straight away.
 */
export function verifyEmailCode(
  email: string,
  code: string,
  supabaseUserId?: string,
): Promise<VerificationOutcome> {
  return postAction({
    action: "verify",
    email,
    code: code.trim(),
    ...(supabaseUserId ? { supabaseUserId } : {}),
  });
}
