/**
 * App version check endpoint.
 *
 * Returns the latest published version so each client can compare it against
 * its own installed build and offer an update.
 *
 * IMPORTANT — keep these in lockstep with the shipped builds:
 *   - LATEST_VERSION_CODE  ==  android `versionCode` in app/build.gradle.kts
 *   - LATEST_VERSION_NAME  ==  android `versionName` / iOS marketing version
 *
 * If LATEST_VERSION_CODE is ever LOWER than what users already have installed,
 * the in-app updater goes permanently silent (it only offers an update when the
 * server code is strictly higher), which looks like "updates are broken".
 *
 * Every value can be overridden at runtime with a Cloudflare environment
 * variable of the same name, so a new APK can be published without a code
 * deploy — set APP_LATEST_VERSION_CODE / APP_LATEST_VERSION_NAME / APP_APK_URL
 * / APP_CHANGELOG in the Worker settings.
 */

const LATEST_VERSION_CODE = 8;
const LATEST_VERSION_NAME = "1.1.6";
const STORE_URL = "https://play.google.com/store/apps/details?id=com.rork.rockscout";
const IOS_STORE_URL = "https://apps.apple.com/app/rockscout/id0000000000";
/**
 * Direct APK download URL for the in-app self-update on sideloaded builds.
 * Must point at the SAME signed APK the users were given, hosted somewhere
 * that serves it with a stable URL and correct byte length. Leave empty to
 * fall back to the Play Store listing.
 */
const APK_URL = "";
const CHANGELOG =
  "1.1.6: Fixed a critical issue where updates could fail with 'App not installed' on some devices. All builds now use a consistent signing key so updates install seamlessly over existing installs. Improved install error reporting and reliability.";

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
  "Content-Type": "application/json",
  // Never let a CDN or browser pin a stale version payload — a cached old
  // version code would hide a real update from every client behind it.
  "Cache-Control": "no-store, max-age=0",
};

/** Environment overrides so releases don't require a code deploy. */
type AppVersionEnv = {
  APP_LATEST_VERSION_CODE?: string;
  APP_LATEST_VERSION_NAME?: string;
  APP_APK_URL?: string;
  APP_STORE_URL?: string;
  APP_IOS_STORE_URL?: string;
  APP_CHANGELOG?: string;
};

export function handleAppVersion(request: Request, env?: AppVersionEnv): Response {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: CORS });
  }

  const parsedCode = Number.parseInt(env?.APP_LATEST_VERSION_CODE ?? "", 10);
  const latestVersionCode = Number.isFinite(parsedCode) && parsedCode > 0
    ? parsedCode
    : LATEST_VERSION_CODE;

  const url = new URL(request.url);
  const platform = (url.searchParams.get("platform") ?? "android").toLowerCase();
  const androidStoreUrl = env?.APP_STORE_URL?.trim() || STORE_URL;
  const iosStoreUrl = env?.APP_IOS_STORE_URL?.trim() || IOS_STORE_URL;

  return new Response(
    JSON.stringify({
      latestVersionCode,
      latestVersionName: env?.APP_LATEST_VERSION_NAME?.trim() || LATEST_VERSION_NAME,
      storeUrl: platform === "ios" ? iosStoreUrl : androidStoreUrl,
      androidStoreUrl,
      iosStoreUrl,
      apkUrl: env?.APP_APK_URL?.trim() || APK_URL,
      changelog: env?.APP_CHANGELOG?.trim() || CHANGELOG,
      checkedAt: new Date().toISOString(),
    }),
    { status: 200, headers: CORS },
  );
}
