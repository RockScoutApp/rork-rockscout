import { render } from "vitest-browser-react";

import { Providers } from "@/App";
import { InstallAppButton } from "@/components/InstallAppButton";

/**
 * Regression guard for the marketing-page black screen.
 *
 * The marketing `Layout` footer renders `InstallAppButton`, which calls
 * `useTier()`. `TierProvider` used to be scoped to the authenticated `/app`
 * subtree, so every marketing route ("/", "/how-to-use", "/support", "/press",
 * 404) crashed with "Cannot destructure property 'isPremium' of undefined" and
 * rendered a black screen. The provider now lives at the root.
 */
test("renders inside the root provider stack without crashing", async () => {
  const screen = await render(
    <Providers>
      <InstallAppButton />
    </Providers>,
  );

  await expect
    .element(screen.getByRole("button", { name: /install free pwa/i }))
    .toBeInTheDocument();
});

/**
 * Headless Chromium never fires `beforeinstallprompt`, which is the same
 * situation as iOS Safari and Firefox. The button must surface manual
 * "Add to Home Screen" steps rather than silently doing nothing.
 */
test("falls back to manual install steps when no native prompt exists", async () => {
  const screen = await render(
    <Providers>
      <InstallAppButton />
    </Providers>,
  );

  await screen.getByRole("button", { name: /install free pwa/i }).click();

  await expect.element(screen.getByText(/Install RockScout from/i)).toBeInTheDocument();
  await expect.element(screen.getByRole("list")).toBeInTheDocument();
});
