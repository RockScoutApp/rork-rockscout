/**
 * App version check endpoint.
 * Returns the latest published version so the app can compare
 * against its installed versionCode and fire an update notification.
 *
 * Update LATEST_VERSION_CODE each time you publish a new build.
 * Update LATEST_VERSION_NAME with the user-facing version string.
 * Update STORE_URL to your Play Store listing once published.
 */

const LATEST_VERSION_CODE = 2;
const LATEST_VERSION_NAME = "1.1";
const STORE_URL = "https://play.google.com/store/apps/details?id=com.rork.rockscout";
// Direct APK download URL for in-app self-update (installs over the existing
// app, no uninstall needed). Leave empty to fall back to the Play Store URL.
// Update this + LATEST_VERSION_CODE each time you publish a new build.
const APK_URL = "";
const CHANGELOG = "Enhanced AI identifier now returns 5 ranked results with web cross-referencing, clarification questions for low-confidence matches, and 800+ specimen database.";

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
  "Content-Type": "application/json",
};

export function handleAppVersion(request: Request): Response {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: CORS });
  }

  return new Response(
    JSON.stringify({
      latestVersionCode: LATEST_VERSION_CODE,
      latestVersionName: LATEST_VERSION_NAME,
      storeUrl: STORE_URL,
      apkUrl: APK_URL,
      changelog: CHANGELOG,
      checkedAt: new Date().toISOString(),
    }),
    { status: 200, headers: CORS },
  );
}
