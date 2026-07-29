import { chromium } from "playwright";
import { join } from "path";

const BASE = "http://localhost:5180";
const SHOT_DIR = join(process.cwd(), "screenshots");

// Only the captures that failed in the first run
const missing: Array<{ name: string; path: string; scrollDown?: number }> = [
  { name: "01_home", path: "/app", scrollDown: 600 },
  { name: "02_identify", path: "/app/identify" },
  { name: "05_collection", path: "/app/collection", scrollDown: 400 },
  { name: "06_specimen_detail", path: "/app/specimens/quartz", scrollDown: 400 },
  { name: "11_dig_sites_map", path: "/app/map" },
  { name: "15_field_journal", path: "/app/journal" },
  { name: "19_community", path: "/app/community", scrollDown: 400 },
  { name: "21_friends", path: "/app/friends" },
  { name: "27_achievements", path: "/app/achievements", scrollDown: 400 },
  { name: "28_periodic_table", path: "/app/periodic-table" },
  { name: "29_specimen_database", path: "/app/specimens", scrollDown: 400 },
  { name: "30_search", path: "/app/search" },
  { name: "36_gear_guide", path: "/app/gear", scrollDown: 400 },
];

async function main() {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({
    viewport: { width: 414, height: 896 },
    deviceScaleFactor: 2,
    isMobile: true,
    hasTouch: true,
  });

  let count = 0;
  for (const cap of missing) {
    const url = `${BASE}${cap.path}?screenshot=1`;
    const page = await context.newPage();
    try {
      console.log(`Capturing ${cap.name}...`);
      await page.goto(url, { waitUntil: "networkidle", timeout: 30000 });
      await page.waitForTimeout(3000);
      if (cap.scrollDown) {
        await page.evaluate((s) => window.scrollBy(0, s), cap.scrollDown);
        await page.waitForTimeout(500);
      }
      await page.screenshot({ path: join(SHOT_DIR, `${cap.name}.png`), fullPage: false });
      count++;
      console.log(`  ✓ ${cap.name}.png`);
    } catch (err) {
      console.error(`  ✗ ${cap.name}: ${(err as Error).message}`);
    } finally {
      await page.close();
    }
  }

  await browser.close();
  console.log(`\nDone: ${count}/${missing.length} captured`);
}

main().catch(console.error);
