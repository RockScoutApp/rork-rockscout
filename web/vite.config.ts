import path from "path";

import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
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
}));
