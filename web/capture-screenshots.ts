import { chromium } from "playwright";
import { mkdirSync } from "fs";
import { join } from "path";

const BASE = "http://localhost:5180";
const SHOT_DIR = join(process.cwd(), "screenshots");
const CLIP_DIR = join(process.cwd(), "video-clips");
mkdirSync(SHOT_DIR, { recursive: true });
mkdirSync(CLIP_DIR, { recursive: true });

const VW = 414;
const VH = 896;

interface Capture {
  name: string;
  path: string;
  type: "screenshot" | "clip";
  waitMs?: number;
  scrollDown?: number;
  clickSelector?: string;
}

const captures: Capture[] = [
  // Chapter 1 — Welcome
  { name: "01_home", path: "/app", type: "clip", waitMs: 3000, scrollDown: 600 },

  // Chapter 2 — AI Rock ID
  { name: "02_identify", path: "/app/identify", type: "clip", waitMs: 3000 },
  { name: "03_scan", path: "/app/scan", type: "screenshot", waitMs: 3000 },
  { name: "04_pdf_report", path: "/app/identify", type: "screenshot", waitMs: 3000 },

  // Chapter 3 — Your Collection
  { name: "05_collection", path: "/app/collection", type: "clip", waitMs: 3000, scrollDown: 400 },
  { name: "06_specimen_detail", path: "/app/specimens/quartz", type: "clip", waitMs: 3000, scrollDown: 400 },
  { name: "07_saved_images", path: "/app/saved-images", type: "screenshot", waitMs: 3000 },

  // Chapter 4 — Field Tools
  { name: "08_field_captures", path: "/app/captures", type: "screenshot", waitMs: 3000 },
  { name: "09_field_camera", path: "/app", type: "screenshot", waitMs: 3000 },
  { name: "10_upload_pill", path: "/app/captures", type: "screenshot", waitMs: 3000 },

  // Chapter 5 — Dig Sites & Gem Shows
  { name: "11_dig_sites_map", path: "/app/map", type: "clip", waitMs: 4000 },
  { name: "12_gem_shows", path: "/app/gem-shows", type: "screenshot", waitMs: 3000 },

  // Chapter 6 — Trip Planning
  { name: "13_trip_planner", path: "/app/trips", type: "screenshot", waitMs: 3000 },
  { name: "14_archived_trips", path: "/app/archived-trips", type: "screenshot", waitMs: 3000 },
  { name: "15_field_journal", path: "/app/journal", type: "clip", waitMs: 3000 },

  // Chapter 7 — Trading & Community
  { name: "16_trade_board", path: "/app/trade", type: "screenshot", waitMs: 3000 },
  { name: "17_trading_floor", path: "/app/trading-floor", type: "screenshot", waitMs: 3000 },
  { name: "18_my_trades", path: "/app/my-trades", type: "screenshot", waitMs: 3000 },
  { name: "19_community", path: "/app/community", type: "clip", waitMs: 3000, scrollDown: 400 },

  // Chapter 8 — Social
  { name: "20_rockscouts_map", path: "/app/rockscouts-map", type: "screenshot", waitMs: 3000 },
  { name: "21_friends", path: "/app/friends", type: "clip", waitMs: 3000 },
  { name: "22_discover_hunters", path: "/app/discover-hunters", type: "screenshot", waitMs: 3000 },

  // Chapter 9 — Aurora & Night Sky
  { name: "23_aurora", path: "/app/aurora", type: "screenshot", waitMs: 4000 },
  { name: "24_stars_landing", path: "/app/stars", type: "screenshot", waitMs: 3000 },
  { name: "25_constellations", path: "/app/stars", type: "screenshot", waitMs: 3000 },

  // Chapter 10 — Your Profile
  { name: "26_profile", path: "/app/profile", type: "screenshot", waitMs: 3000 },
  { name: "27_achievements", path: "/app/achievements", type: "clip", waitMs: 3000, scrollDown: 400 },

  // Chapter 11 — Reference Library
  { name: "28_periodic_table", path: "/app/periodic-table", type: "clip", waitMs: 3000 },
  { name: "29_specimen_database", path: "/app/specimens", type: "clip", waitMs: 3000, scrollDown: 400 },
  { name: "30_search", path: "/app/search", type: "clip", waitMs: 3000 },

  // Chapter 12 — Artifacts & Wonders
  { name: "31_artifacts", path: "/app/artifacts", type: "screenshot", waitMs: 3000 },
  { name: "32_natural_wonders", path: "/app/natural-wonders", type: "screenshot", waitMs: 3000 },

  // Chapter 13 — Field Kit
  { name: "33_blm_guide", path: "/app/blm-guide", type: "screenshot", waitMs: 3000 },
  { name: "34_state_parks", path: "/app/state-parks", type: "screenshot", waitMs: 3000 },
  { name: "35_meteorite_hunting", path: "/app/meteorite-hunting", type: "screenshot", waitMs: 3000 },
  { name: "36_gear_guide", path: "/app/gear", type: "clip", waitMs: 3000, scrollDown: 400 },
  { name: "37_resource_links", path: "/app/resource-links", type: "screenshot", waitMs: 3000 },

  // Chapter 14 — Learn & Explore
  { name: "38_reference_hub", path: "/app/reference", type: "screenshot", waitMs: 3000 },
  { name: "39_crystal_systems", path: "/app/crystal-systems", type: "screenshot", waitMs: 3000 },
  { name: "40_fluorescence", path: "/app/fluorescence", type: "screenshot", waitMs: 3000 },
  { name: "41_rock_types", path: "/app/rock-types", type: "screenshot", waitMs: 3000 },
  { name: "42_rock_cycle", path: "/app/rock-cycle", type: "screenshot", waitMs: 3000 },
  { name: "43_glossary", path: "/app/glossary", type: "screenshot", waitMs: 3000 },
  { name: "44_mineral_id", path: "/app/mineral-id", type: "screenshot", waitMs: 3000 },
  { name: "45_lapidary", path: "/app/lapidary", type: "screenshot", waitMs: 3000 },

  // Chapter 15 — Premium & Free Tier
  { name: "46_paywall", path: "/app/paywall", type: "screenshot", waitMs: 3000 },
];

async function main() {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({
    viewport: { width: VW, height: VH },
    deviceScaleFactor: 2,
    isMobile: true,
    hasTouch: true,
  });

  let screenshotCount = 0;
  let clipCount = 0;
  const errors: string[] = [];

  for (const cap of captures) {
    const url = `${BASE}${cap.path}?screenshot=1`;
    const page = await context.newPage();

    try {
      console.log(`Capturing ${cap.name} from ${cap.path}...`);
      await page.goto(url, { waitUntil: "networkidle", timeout: 30000 });
      await page.waitForTimeout(cap.waitMs ?? 2000);

      if (cap.scrollDown) {
        await page.evaluate((scrollAmt) => {
          window.scrollBy(0, scrollAmt);
        }, cap.scrollDown);
        await page.waitForTimeout(500);
      }

      if (cap.type === "screenshot") {
        const filePath = join(SHOT_DIR, `${cap.name}.png`);
        await page.screenshot({ path: filePath, fullPage: false });
        screenshotCount++;
        console.log(`  ✓ Screenshot saved: ${cap.name}.png`);
      } else {
        // Record a short video clip
        const filePath = join(CLIP_DIR, `${cap.name}.webm`);
        const recorder = await page.video?.path();
        // Playwright records video at context level, so we use a different approach
        // For simplicity, capture a screenshot for now (video recording requires context-level setup)
        const shotPath = join(SHOT_DIR, `${cap.name}.png`);
        await page.screenshot({ path: shotPath, fullPage: false });

        // Try to do a scroll animation and capture another frame
        if (cap.scrollDown) {
          await page.evaluate(() => {
            window.scrollTo({ top: 0, behavior: "smooth" });
          });
          await page.waitForTimeout(800);
          const shot2Path = join(SHOT_DIR, `${cap.name}_scrolled.png`);
          await page.screenshot({ path: shot2Path, fullPage: false });
        }

        screenshotCount++;
        console.log(`  ✓ Clip capture (screenshot pair) saved: ${cap.name}.png`);
      }
    } catch (err) {
      const msg = `  ✗ Error capturing ${cap.name}: ${(err as Error).message}`;
      console.error(msg);
      errors.push(msg);
    } finally {
      await page.close();
    }
  }

  await browser.close();

  console.log(`\n=== Capture Complete ===`);
  console.log(`Screenshots: ${screenshotCount}`);
  console.log(`Errors: ${errors.length}`);
  if (errors.length > 0) {
    console.log("Errors:");
    errors.forEach((e) => console.log(e));
  }
}

main().catch(console.error);
