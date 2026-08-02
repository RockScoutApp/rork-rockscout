import path from "path";

import react from "@vitejs/plugin-react";
import { defineConfig, loadEnv } from "vite";

const REQUIRED_WEB_ENVS = [
  "EXPO_PUBLIC_SUPABASE_URL",
  "EXPO_PUBLIC_SUPABASE_ANON_KEY",
  "EXPO_PUBLIC_RORK_FUNCTIONS_URL",
  "EXPO_PUBLIC_RORK_APP_KEY",
];

// Fail the production build early if the public env vars are missing.
// This prevents a broken PWA from being deployed with the "missing backend
// configuration" error again.
function verifyProductionEnv(mode: string, root: string): void {
  if (mode !== "production") return;
  // Vite loads env files after the config file is parsed, so use loadEnv
  // to read both .env.production and system env vars at config time.
  const env = loadEnv(mode, root, "EXPO_PUBLIC_");
  const missing = REQUIRED_WEB_ENVS.filter((key) => !env[key]);
  if (missing.length > 0) {
    throw new Error(
      `Missing required public env vars for web production build: ${missing.join(
        ", ",
      )}. Set them in the Rork environment or in a web/.env.production file before deploying.`,
    );
  }
}

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  verifyProductionEnv(mode, __dirname);

  return {
  server: {
    host: "::",
    port: 8080,
    hmr: {
      overlay: false,
    },
  },
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  // Only expose VITE_* prefixed env vars to the client bundle.
  // EXPO_PUBLIC_* vars (which include secrets like the toolkit key) must
  // NEVER be inlined into the web bundle — they're server/app-only.
    envPrefix: ["VITE_", "EXPO_PUBLIC_"],
  };
});
