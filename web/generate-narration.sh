#!/bin/bash
# Generate all 16 narration chapters with ElevenLabs using user's voice library
# Max 2 concurrent requests (user's ElevenLabs tier limit)
set -e

API_KEY="sk_f42784f19946cae60ba302f658908acdc4470bbcf8e65412"
VOICE_ID="sCfjjOc8ymlZqCKPJLwj"
OUT_DIR="ios-rockscout/RockScout/Resources"
TMP_DIR="/tmp/narration_gen"
mkdir -p "$TMP_DIR"

# Filenames matching composite-video.sh expectations
declare -a FILES=(
  "rockscout_welcome_intro.mp3"
  "rock_identification_voice.mp3"
  "rocks_collection_guide.mp3"
  "field_capture_voice.mp3"
  "treasure_map_voice.mp3"
  "trip_planner_voice.mp3"
  "trade_board_intro_voice.mp3"
  "social_network_voice.mp3"
  "aurora_forecaster_voice.mp3"
  "profile_level_up_voice.mp3"
  "periodic_table_voice_guide.mp3"
  "artifact_catalog_voice.mp3"
  "rockhounding_guide_voice.mp3"
  "educational_guides_intro.mp3"
  "pricing_explanation_voice.mp3"
  "rockscout_voice_intro.mp3"
)

CHAPTER_TEXTS_DIR="$TMP_DIR/chapters"
mkdir -p "$CHAPTER_TEXTS_DIR"

# Chapter texts
cat > "$CHAPTER_TEXTS_DIR/01.txt" << 'ENDCH1'
Hey there. Welcome to RockScout.

If you've ever picked up a rock and wondered what the heck it was — you're in the right place. I've been a rockhounder for over thirty years, and I built this app because I got tired of guessing. This thing does everything — identifies your rocks, maps out dig sites, helps you plan trips, connects you with other rockhounders, and a whole lot more. I packed as much into this thing as I could. No joke.

Let me show you around.
ENDCH1

cat > "$CHAPTER_TEXTS_DIR/02.txt" << 'ENDCH2'
This is the big one. Tap "Identify a Rock" right on the home screen — can't miss it, it's the biggest button up there. Pick a photo from your gallery or snap one with your camera, then hit Identify.

Now here's where it gets serious. Most rock apps use one AI model. RockScout uses three — Claude Haiku, Claude Sonnet, and Gemini 2.5 Pro — all tag-teaming your rock at the same time. The AI actually looks at the database reference images alongside your photo and ranks the best matches visually. It's not just reading a text description and guessing — it sees the rocks.

If the visual match is really confident — ninety-two percent or higher — you get your answer right then. No waiting. Otherwise, it runs a web search cross-check for extra accuracy. And if your rock is being extra stubborn, the AI will ask you a few questions — stuff like hardness, streak color, what kind of environment you found it in. Answer those, and it re-ranks the candidates with even more precision.

Got a rock with multiple minerals in it? It catches that too — breaks down each component so you know exactly what you're holding.

When the results come back, you get the specimen name, a confidence score, the properties, and where to find more of them. And if the AI ain't real sure — you can tap "Ask an Expert" to find nearby museums and shoot them an email with your photo and results attached, right from the app. There's also a PDF report button on every match — tap it and you get a printable report with your photo, all the matches, confidence scores, and reasoning. Email it, save it, share it with your rock club.

Free users get five tokens to start, and there's a seven-day full-access trial. I'll break down the whole token and premium thing at the end — don't worry, it's simpler than it sounds.
ENDCH2

cat > "$CHAPTER_TEXTS_DIR/03.txt" << 'ENDCH3'
This is My Rocks — your personal collection. Every rock you've ever saved lives right here. Each card shows a photo, the name, a quick tagline, the rarity, and where you found it. The colored pill on each card tells you what type of rock it is — silicate, volcanic, carbonate, whatever it is. Tap any card to open the full detail page — properties, photos, locations, the works.

See that glowing heart on the card? Tap it to add a specimen to your wishlist. That's your dream-rock shopping list. And the little dropdown menu lets you add to your collection, add to your wishlist, share it to your profile feed, or share it straight to social media.

Down here is Saved Images — tap any photo in the whole app to view it full-screen, long-press to save it to your personal folder. That one's free, by the way. Your photo log never gets locked behind a paywall.
ENDCH3

cat > "$CHAPTER_TEXTS_DIR/04.txt" << 'ENDCH4'
Field Captures is where you log photos of rocks you find out in the field — no identification needed, just a visual record. Each one stores the photo, the date, and any notes you want to add. Tap a capture to view it full-screen, long-press to save it to your Saved Images.

And check this out — Field Captures has a second page you swipe to. It's a full-page map showing a pin for every capture that has coordinates. So every rock you found and marked? It's all on one map. Tap any pin to jump back to that specimen's detail page.

There's also a separate Field Camera tile on the home screen. That one's for when you just want to grab a quick shot without running the whole ID pipeline. Snap a photo, then pick where to save it — Field Captures, Saved Images, My Rocks, Wishlist, a journal entry, your profile feed, your profile background, or even submit it as a new specimen. All from one dropdown.

There's an Upload pill too — tap it to submit a new specimen with up to four photos, a name, the date, location, and a description.
ENDCH4

cat > "$CHAPTER_TEXTS_DIR/05.txt" << 'ENDCH5'
This is your treasure map. Tap "Dig Sites and Rock Shops" on the home screen and you get the full map — free dig sites, pay-to-dig mines, rock shops, metaphysical shops, all of it. Filter by type if you know what you're looking for. Turn on location monitoring and you'll see what's nearby within a hundred miles — two hundred fifty if you're on Premium. You even get a proximity ping when you're close to a dig site. Tap any pin for details, photos, one-tap directions, and gear recommendations.

And if you're into shows — there's a Gem and Mineral Shows tab. Recurring shows across the country, grouped by upcoming month so you never miss one.
ENDCH5

cat > "$CHAPTER_TEXTS_DIR/06.txt" << 'ENDCH6'
The Trip Planner is where you build your hunt route. Add dig sites, rock shops, custom pins — whatever stops you want. Drop a pin for gas, food, or that creek crossing that doesn't have a name. You can even flag pins as potential rock locations for review.

Need to reorder your stops? Long-press a card and drag it — the route polyline and travel-time badges update in real-time. Move-up buttons are there too if you just need to shuffle one stop. The dashed line on the map connects everything with directional arrows so you can see the whole journey at a glance.

Heading somewhere with no signal? "Download Maps for All Stops" caches offline satellite tiles for every stop at once. And "Cache Trip Area" saves a three-mile radius around each stop plus a one-mile radius around every specimen marker pin — so your route AND your find spots work with zero signal.

There's a standalone Calendar screen too — view all your trips in a month grid, drag and drop trip cards to reschedule them, create new ones right there. Completed trips get archived to their own tab so they don't clutter your active list.

And the Field Journal — that's where you log your daily adventures. Each entry grabs the date, location, weather conditions automatically, and your personal notes. It's your hunting chronicle.
ENDCH6

cat > "$CHAPTER_TEXTS_DIR/07.txt" << 'ENDCH7'
The Trade Board is where you post specimens to swap, sell, or trade. Add photos, a description, your trade preferences — done. Then hop over to the Trading Floor to browse HAVE and WANT listings from other RockScout users. Tap any listing to see the details and start a chat with the trader through Messenger. Check "My Trades" to manage your active listings and conversations. All trades are user-to-user — RockScout makes the connection, but it doesn't handle shipping or payments. Work that out between yourselves.

Then there's the Community board — app-wide Q&A feed. Post a question, a photo, a rock story. Sort by newest, most loved, or most commented. Tap Love to upvote, tap Comment to reply, full threaded replies supported. Attach images to posts, comments, and replies. Posts auto-expire after fourteen days to keep the feed fresh, and expired ones land in an Archived Posts popup where you can restore them before they're gone for good. There's a profanity filter and screenshot-based reporting to keep it family-friendly.
ENDCH7

cat > "$CHAPTER_TEXTS_DIR/08.txt" << 'ENDCH8'
RockScout's got a whole social network built in. The RockScouts Map shows live pings from other hunters — drop your own pin to share where you are. Send friend requests from the Friends screen or by tapping someone's profile. The Discover Hunters screen lets you browse and search every discoverable RockScout hunter worldwide — filter by status, level, or collection size.

The Friends screen puts everything in one place — friend requests and message requests at the top, active conversations in the middle, your connected friends below. Tap any conversation to open the full chat with text-message-style bubbles. Swipe left to delete or unfriend. There's a Preview button to read messages without triggering a read receipt. Send images through the messenger too. The notification bell and the message icon are separate — the bell handles friend requests and other notifications, the envelope handles all your messages.
ENDCH8

cat > "$CHAPTER_TEXTS_DIR/09.txt" << 'ENDCH9'
This is your personal space weather station. The Aurora Forecaster shows real-time Kp index, Bz value, solar wind speed, and whether aurora's visible from your latitude. There's a twenty-four-hour Kp trend chart, a seven-day radio flux chart, and a three-day forecast so you can plan your viewing nights. The Active Sunspot Regions card lists current regions with magnetic classes and flare probabilities — tap any region for its evolution history.

Set a custom Kp notification threshold in Social Settings and you'll get an instant push alert the moment that level hits. You can save custom coordinates as aurora watching spots and track visibility at each one.

And from the Aurora tab, tap the Night Sky Guide to open the stars section. Four tiles — Constellations, Important Stars, Planets, and Deep Sky Objects. All eighty-eight IAU constellations with programmatic star charts, thirty-plus important stars, all eight planets plus dwarf planets, and forty-plus deep sky objects. Every page has animated twinkling stars in the background. It's a whole astronomy guide tucked inside your rock app.
ENDCH9

cat > "$CHAPTER_TEXTS_DIR/10.txt" << 'ENDCH10'
Tap your avatar to open your Profile — your Player Card. It shows your level, XP progress, hunter status, and earned badges. Set your status to Off Grid, Hunting, Digging, or Trading — each one gives you a different colored profile border so everyone knows if you're around or out in the field.

The Achievements page has over one hundred achievements and over thirty badges. Each locked achievement shows a progress bar so you know exactly what to do next to unlock it. Earn XP for every action — identifying, collecting, capturing, trading. Level up and you get a celebration pop-up you can share to your feed or social media.
ENDCH10

cat > "$CHAPTER_TEXTS_DIR/11.txt" << 'ENDCH11'
The Periodic Table — all one hundred eighteen elements, each one showing where it appears in rocks and gems and its role in mineral formation. Tap any element for a detail page with photos and plain-English explanations.

The Specimen Database is a massive encyclopedia of over nine hundred specimens with stunning photos, detailed properties, and where-to-find locations. Use the filter chips to narrow by category or family, scroll through the list, and watch for the NEW badge on anything added in the last seven days. There's a Recently Added filter chip too — tap it to see only the latest additions.

And the global search — tap the search icon in the header and search across the entire database. Specimens, locations, educational guides, and your favorite spots. Results are grouped by category so you can jump straight to what you need.
ENDCH11

cat > "$CHAPTER_TEXTS_DIR/12.txt" << 'ENDCH12'
The Artifacts tile takes you to a growing catalog of over one hundred authentic prehistoric artifacts — arrowheads, spear points, hand axes, scrapers, beads, effigies, and more. Each one has its own reference image, cultural period, material, and a description of how it was made and used. There's a NEW badge on anything added in the last week. You can also filter the Specimen Database down to artifacts only with the ARTIFACTS category chip.

Natural Wonders — over thirty-five world-famous geological sites and the rocks and minerals you can find at each one. The Grand Canyon, Giant's Causeway, Mount Vesuvius, the Naica Crystal Caves with selenite crystals over thirty-six feet long. Each one opens a detail page with the geological history, what rocks are there, and visitor tips. Use it as a bucket-list trip planner.
ENDCH12

cat > "$CHAPTER_TEXTS_DIR/13.txt" << 'ENDCH13'
The BLM Public Lands Guide breaks down rockhounding rules for Bureau of Land Management land, state by state. Each state page shows trailheads, campgrounds, and dig sites with tappable detail pages. Every BLM guide, trailhead, campground, and national or state park detail screen includes a Common Wildlife tile showing the animals you might run into out there.

National and State Parks — over fifty of them, dig-friendly or geologically significant. Each park shows what rocks and minerals you can find, visitor info, and common wildlife. Tap the bookmark to save any park to your Favorite Spots.

Meteorite Hunting teaches you how to identify space rocks — fusion crust, magnetic properties, the key visual cues. Where meteorites are most commonly found and how to hunt them. The Specimen Database also includes meteorite entries with full properties.

And the Gear Guide — over forty-five curated tools with Amazon links, organized from beginner to advanced. From your first loupe and rock hammer to lapidary equipment and UV lights. Each item shows a price range so you know what you're getting into. Contextual gear recommendations show up on specimen details, dig sites, and identification results too, so you always know what to bring.
ENDCH13

cat > "$CHAPTER_TEXTS_DIR/14.txt" << 'ENDCH14'
The Educational Guides hub is where you go to learn the science behind what you're finding. Over ten built-in guides, all working fully offline once you've done the bulk image download.

Exploring Geology, Paleontology, Prehistoric Organisms, Tectonics and Volcanoes. The Rock Cycle explorer showing how igneous, sedimentary, and metamorphic rocks transform into each other. Rock Types deep dive — basalt versus granite, shale versus sandstone, marble versus quartzite. The Mineral ID Guide — a step-by-step identification key using hardness, streak, luster, cleavage, and specific gravity.

Crystal Systems — the seven systems with visual examples. Fluorescence and UV Reference — which minerals glow under UV light and what colors. The Glossary — every geological term in the app defined in plain English. Lapidary Basics — cutting, polishing, and cabbing your finds into jewelry. Mass Extinctions, the Geo Time Scale, Geologic Periods, and Fossil Types — for understanding the deep-time context of what you find.

The Mohs Hardness Scale card walks you through scratch testing with infographics and the ten reference minerals. And "Rocks Are Amazing" is a curated gallery of Earth's most stunning formations — enhydros, pseudomorphs, fluorescent minerals, coprolites, and more. It's like a museum in your pocket.
ENDCH14

cat > "$CHAPTER_TEXTS_DIR/15.txt" << 'ENDCH15'
Alright, let's talk about the money side of things — and I'm gonna be straight with you here.

You get a seven-day free trial when you sign up. Full access — AI identification with five tokens, all the social features, the Trade Board, your collections, trip planning, everything. After that trial ends, here's what stays free forever: browsing the entire specimen database of over nine hundred entries, all the educational guides, the Field Camera saving to Saved Images, severe weather alerts, and browsing dig sites and offline maps. That's a lot of free content.

After the trial, the premium features need a subscription or a donation. That includes AI identification, the social network, Trade Board, collections, trip planning, and specimen submissions. But here's the thing — you don't necessarily need a subscription to get your money's worth. You can watch two short rock-related videos to earn one identification token — no weekly cap on that. Or make a donation of any amount and you get tokens plus a temporary full-feature unlock, from two days up to a month.

Premium is five ninety-nine a month and it unlocks everything. Unlimited AI identifies with all three models, ad-free, two hundred fifty-mile nearby radius, twenty-four-hour pings, a premium gem badge, and early access to new features.

And the best part? Premium costs less than what you'd pay for any streaming service, and its gonna be a lot more fun than bingeing the same show for the 6th time.
ENDCH15

cat > "$CHAPTER_TEXTS_DIR/16.txt" << 'ENDCH16'
That's RockScout. I built it for rockhounders, because I am one. There's a referral system — share your link, earn tokens and XP when your friends sign up. There's a free read-only PWA you can install on your computer too — browse the whole database on a bigger screen. And if you've got feedback, a bug to report, or a specimen you want added, tap Contact Us and it goes straight to me.

Happy Hunting.
ENDCH16

# Generate function
generate_chapter() {
  local idx=$1
  local text_file="$CHAPTER_TEXTS_DIR/$(printf '%02d' $idx).txt"
  local out_file="$TMP_DIR/${FILES[$((idx-1))]}"
  local text=$(cat "$text_file")

  echo "[$(date +%H:%M:%S)] Generating Chapter $idx -> ${FILES[$((idx-1))]}"

  local http_code
  http_code=$(curl -s -w "%{http_code}" \
    "https://api.elevenlabs.io/v1/text-to-speech/${VOICE_ID}" \
    -H "xi-api-key: ${API_KEY}" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg text "$text" '{text: $text, model_id: "eleven_v3", output_format: "mp3_44100_128"}')" \
    -o "$out_file" 2>&1)

  if [ "$http_code" = "200" ] && [ -f "$out_file" ]; then
    local dur
    dur=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "$out_file" 2>/dev/null || echo "0")
    local chars=${#text}
    echo "[$(date +%H:%M:%S)] OK Ch$idx: ${dur}s, ${chars} chars, $(du -h "$out_file" | cut -f1)"
  else
    echo "[$(date +%H:%M:%S)] FAIL Ch$idx: HTTP $http_code"
    if [ -f "$out_file" ]; then
      head -c 300 "$out_file"
      echo ""
    fi
    return 1
  fi
}

echo "=== Generating 16 chapters with voice $VOICE_ID (golden age broadcast) ==="
echo "=== Max 2 concurrent (user ElevenLabs tier limit) ==="
echo ""

# Generate in pairs (2 concurrent max)
for batch_start in 1 3 5 7 9 11 13 15; do
  pids=()
  for i in 0 1; do
    idx=$((batch_start + i))
    if [ $idx -le 16 ]; then
      generate_chapter $idx &
      pids+=($!)
    fi
  done
  for pid in "${pids[@]}"; do
    wait $pid
  done
  echo "--- Pair complete (chapters $batch_start-$((batch_start+1))) ---"
done

echo ""
echo "=== Moving successful files to Resources ==="
TOTAL_DUR=0
SUCCESS=0
FAIL=0
for i in "${!FILES[@]}"; do
  idx=$((i+1))
  src="$TMP_DIR/${FILES[$i]}"
  dst="$OUT_DIR/${FILES[$i]}"
  if [ -f "$src" ]; then
    dur=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "$src" 2>/dev/null || echo "0")
    mv "$src" "$dst"
    TOTAL_DUR=$(awk "BEGIN {printf \"%.2f\", $TOTAL_DUR + $dur}")
    SUCCESS=$((SUCCESS+1))
    echo "  Ch$idx: ${FILES[$i]} (${dur}s)"
  else
    echo "  Ch$idx: MISSING!"
    FAIL=$((FAIL+1))
  fi
done

echo ""
echo "=== Results: $SUCCESS success, $FAIL fail ==="
echo "=== Total narration duration: ${TOTAL_DUR}s ($(awk "BEGIN {printf \"%.1f\", $TOTAL_DUR/60}") min) ==="
