import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Camera,
  BookMarked,
  Images,
  Repeat,
  MessageSquare,
  Users,
  Map as MapIcon,
  NotebookPen,
  MapPin,
  Search,
  Bell,
  User,
  Heart,
  Download,
  Mountain,
  Globe2,
  Atom,
  School,
  Library,
  Diamond,
  Upload,
  Database,
  X,
  Zap,
  Star,
  Calendar,
  Archive,
  Landmark,
  ShieldCheck,
  Monitor,
  Keyboard,
  Sparkles,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { SITE } from "@/content/legal";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Share2 } from "lucide-react";

type HowToSection = {
  icon: React.ComponentType<{ className?: string }>;
  accent: string;
  title: string;
  shortLabel: string;
  steps: string[];
};

// Mirrors the app's howToSections list (HowToUseScreen.kt) section-for-section.
const HOW_TO_SECTIONS: HowToSection[] = [
  {
    icon: Camera,
    accent: "hsl(48 86% 70%)",
    title: "AI Rock Identification",
    shortLabel: "AI Rock ID",
    steps: [
      `Tap the big "Identify a Rock" hero banner on the home screen.`,
      `Choose a photo from your gallery or snap one with your camera.`,
      `Hit "Identify" — three AI models (Claude Haiku, Claude Sonnet, and Gemini 2.5 Pro) will tag-team the analysis.`,
      `The AI then performs a visual reference comparison — it actually SEES the database reference images alongside your photo and ranks the top matches visually (not just from text descriptions).`,
      `If the visual match is highly confident (92%+), results return instantly — no need for the full pipeline.`,
      `Otherwise, the app runs a web search cross-check for maximum accuracy.`,
      `If the AI needs more information to narrow down the ID, it asks you a few clarification questions (e.g. hardness, streak color, or environment) — answer them and the final result re-ranks the candidates for higher accuracy.`,
      `If your rock has multiple minerals, the AI auto-detects the assemblage and breaks down each component.`,
      `Results show the specimen name, confidence level, properties, and where to find more — the AI has already visually compared your photo against the database reference images to reach that ranking.`,
      `Free users get a 7-day trial with 5 tokens. After that, tokens or a Premium subscription are needed.`,
    ],
  },
  {
    icon: BookMarked,
    accent: "hsl(205 45% 61%)",
    title: "My Collection & Specimen Cards",
    shortLabel: "Collection",
    steps: [
      `Tap "My Rocks" on the home screen to view your collected specimens.`,
      `Each specimen card shows a photo, name, tagline, rarity, and location.`,
      `The colored category pill (silicate, volcanic, carbonate, etc.) uses a unique color per rock type.`,
      `Tap any card to open the full specimen detail page with properties, photos, and locations.`,
      `Use the glowing heart icon on any card to like or wishlist a specimen.`,
      `Use the dropdown menu on the card to add to collection, add to wishlist, share to profile feed, or share to social media.`,
    ],
  },
  {
    icon: Images,
    accent: "hsl(144 44% 58%)",
    title: "Field Captures & Field Camera (Free)",
    shortLabel: "Field Camera",
    steps: [
      `Tap "Field Captures" on the home screen.`,
      `Log photos of rocks you find in the field — no identification needed, just a visual record.`,
      `Each capture stores the photo, date, and optional notes.`,
      `Tap any capture to view it full-screen, long-press to save it to your Saved Images folder.`,
      `Use the separate "Field Camera" tile on the home screen to snap a photo without running the ID tool at all — perfect when you just want to grab a shot.`,
      `The Field Camera stays free after the trial ends — snaps save to your in-app Saved Images folder at no cost. (Saving to My Rocks, Wishlist, or other collection destinations still requires Premium or a donation.)`,
      `After snapping with the Field Camera, pick a destination from the "Save to…" dropdown: Field Captures, Saved Images, My Rocks, My Wishlist, Field Journal Entry, Share to Profile, Change Profile Background, or Submit a Specimen.`,
      `The save form lets you add a name, location, and description, plus drop a pin on a map to mark the exact find spot — the pin's coordinates are stored with the capture card.`,
      `Field Captures has a second swipeable page: a full-page Specimen Map showing every capture that has a pin. Tap any pin to open the full specimen detail page for viewing or editing. Swipe the top pills or the screen to switch between the captures list and the specimen map.`,
      `An Upload pill sits in the same row as the Captures and Specimen Map pills — tap it to submit a new specimen with up to 4 photos, name, date found, location, and description.`,
    ],
  },
  {
    icon: Repeat,
    accent: "hsl(36 80% 58%)",
    title: "Trade Board",
    shortLabel: "Trade Board",
    steps: [
      `Tap the "Trade Board" banner on the home screen.`,
      `Post a specimen you want to swap or sell — add photos, a description, and your trade preferences.`,
      `Browse the Trading Floor to see HAVE/WANT listings from other RockScout users.`,
      `Tap any listing to view details and start a chat with the trader via Messenger.`,
      `Check "My Trades" to manage your active listings and conversations.`,
      `All trades are user-to-user — RockScout facilitates the connection but does not handle shipping or payments.`,
    ],
  },
  {
    icon: MessageSquare,
    accent: "hsl(270 50% 70%)",
    title: "Community Q&A Board",
    shortLabel: "Community",
    steps: [
      `Tap the "Community" banner on the home screen to open the app-wide Q&A feed.`,
      `Post a question, photo, or rock story for the whole RockScout community to see — posts auto-expire after 14 days to keep the feed fresh.`,
      `Sort posts by Newest, Most Loved, or Most Commented using the sort dropdown at the top.`,
      `Tap Love on any post or comment to upvote it, tap Comment to add a comment, and Reply to reply to a comment — full threaded replies are supported.`,
      `Attach an image to a post, comment, or reply by tapping the image icon next to the text field. All images are scanned by the profanity filter.`,
      `Repost a post to share it back to the feed, or use the Report button to flag inappropriate content — a screenshot is captured and a confirmation popup appears before submitting.`,
      `Expired posts appear in an Archived Posts popup where you can browse and restore them before they're permanently removed.`,
    ],
  },
  {
    icon: Users,
    accent: "hsl(270 50% 70%)",
    title: "RockScout Social — Pings, Friends & Messenger",
    shortLabel: "Social",
    steps: [
      `Your RockScout account is required to use the app — it's free and your collections, captures, and friends carry over to any device.`,
      `Open the RockScouts Map to see live pings from other hunters. Drop your own ping to share your location.`,
      `Send friend requests from the RockScout Friends screen or by tapping another user's profile.`,
      `Use the Discover Hunters screen (from your Profile) to browse and search every discoverable RockScout hunter worldwide — filter by status, level, or collection size and tap any result to view their public profile.`,
      `The message icon (envelope, next to the notification bell) shows a count badge for all unread messages and pending message requests. Tap it to open the unified RockScout Friends screen.`,
      `The notification bell shows a count for friend requests and other non-message notifications. Tap it to open the Notification Center, where a summary tile for friend requests appears at the top.`,
      `On the RockScout Friends screen, friend requests and message requests appear at the top under a "Requests" section. Use Accept, Deny, Block, or Report pill buttons on each tile.`,
      `Tap a message request tile to open a full-screen chat view with text-message-style bubbles — your messages on the right, theirs on the left.`,
      `Below the Requests section, active conversations appear under a "Conversations" section. Tap any conversation to open the full chat.`,
      `Below conversations, your connected friends appear under a "Friends" section, sorted by Traders then Hunters.`,
      `Swipe left on a conversation tile to delete it, or swipe left on a friend card to unfriend them. A confirmation popup appears before the action is completed.`,
      `Use the "Preview" button at the top of a chat to read messages without triggering a read receipt (green dot).`,
      `Send images through the messenger by tapping the image icon next to the text field. All images are scanned by the profanity filter.`,
      `Long messages show a "More" pill — tap it to expand and read the full text.`,
      `Use the Report button inside any message tile to flag inappropriate content. A confirmation popup appears before submitting.`,
    ],
  },
  {
    icon: MapIcon,
    accent: "hsl(36 80% 58%)",
    title: "Trip Planner",
    shortLabel: "Trip Planner",
    steps: [
      `Tap the "Trip Planner" banner on the home screen.`,
      `Create a multi-stop hunt route by adding dig sites, rock shops, or custom pins.`,
      `Drop custom pins for gas, food, or any point of interest along the way.`,
      `Flag interesting pins as potential rock locations for the developer to review and add to the database.`,
      `Reorder stops by long-pressing a stop card and dragging it to a new position — the stop swaps to the position under your finger, with the route polyline and travel-time badges updating in real-time. Move up buttons are also available for quick single-step reordering.`,
      `A dashed polyline connects all stops on the route map, with directional arrows at each segment midpoint, so you can visualize the full journey at a glance.`,
      `Estimated travel time appears between consecutive stops based on straight-line distance and an assumed 45 mph average driving speed — shown as a badge between stop rows and as a total in the trip header.`,
      `Use "Download Maps for All Stops" to cache offline satellite tiles and high-res images for every stop on your trip at once — perfect for backcountry trips with no signal.`,
      `Use "Cache Trip Area" on any trip to save satellite tiles for a 3-mile radius around each stop (zoom 10–19) plus a 1-mile radius around every pinned specimen marker (zoom 14–19), so your route AND your find spots work with zero signal. The cache status persists across app restarts, and tiles are freed automatically when you archive or delete a trip.`,
      `A sync-status pill on every trip map shows whether your offline tiles are up to date: green "Up to date" (cached in the last 24h), aqua "Cached" (within 7 days), or warning "Stale • tap to refresh" (older than 7 days). Tap the pill to re-cache the trip area. When you're offline, the pill reports whether the cached tiles are fresh or may be stale.`,
      `Add a gear checklist so you never forget your hammer, loupe, or UV light.`,
      `When editing a trip, drop specimen marker pins on the specimen marker map to mark exactly where you found each rock — pins persist with the trip, appear on the route map, and are included when you cache the trip area for offline use. Use Set Pin / Remove Pin with confirmation popups.`,
      `Expand the trip route map to fullscreen and tap anywhere to drop a pin. Two pill buttons appear: "Add to Route" adds the pin as a stop (auto-approving rock-related locations via web scan), and "Submit a New Location" opens the full location submission form pre-filled with the pinned coordinates.`,
      `Export your planned trip to the in-app Calendar by tapping the "View in Calendar" button in the trip editor — the trip appears on its scheduled date in the month grid.`,
      `Mark a trip as complete with the checkmark button — completed trips can be archived to the "Archived" tab for safekeeping without cluttering your active trip list.`,
      `Share your trip plan with friends via the share button.`,
    ],
  },
  {
    icon: Calendar,
    accent: "hsl(205 45% 61%)",
    title: "Trip Calendar",
    shortLabel: "Calendar",
    steps: [
      `Tap the "Calendar" tile on the home screen to open the standalone Trip Calendar.`,
      `View all planned trips in a month grid — each trip appears inside its scheduled date box with the trip name and first few stop names.`,
      `Navigate between months with the prev/next arrows. Today's date is highlighted with a citrine circle.`,
      `An agenda list below the grid shows upcoming trips sorted by date, with trip name, stop count, first 3 stops, and estimated distance.`,
      `Create new trips directly from the calendar, edit existing ones, share trips, or view trip details — full editing access without going through the Trip Planner.`,
      `Drag and drop trip cards to different dates on the calendar grid to quickly reschedule them — long-press a trip card and drag it to the target date.`,
      `The Trip Planner's "View in Calendar" button links here too, but the calendar is always accessible from the home screen on its own.`,
    ],
  },
  {
    icon: Archive,
    accent: "hsl(142 52% 54%)",
    title: "Archived Trips",
    shortLabel: "Archived",
    steps: [
      `Mark any trip as complete with the checkmark button on its card in the Trip Planner.`,
      `Completed trips can be archived — tap the "Archived" pill button in the Trip Planner header to view them.`,
      `Archived trips are kept safe without cluttering your active trip list.`,
      `Restore an archived trip back to active, or permanently delete it from the Archived screen.`,
      `Archived trip map tiles are automatically freed from the offline cache to save storage.`,
    ],
  },
  {
    icon: Zap,
    accent: "hsl(190 90% 55%)",
    title: "Aurora Forecaster & Space Weather",
    shortLabel: "Aurora",
    steps: [
      `Tap the "Aurora Forecaster" tile on the home screen to check real-time space weather conditions.`,
      `The main card shows the current Kp index, Bz value, solar wind speed, and visibility status for your latitude — color-coded with bright aurora-green and purple theming.`,
      `A 24-hour Kp trend chart and a 7-day F10.7 radio flux chart show how conditions have been changing over time, with a dashed threshold line at your visibility level.`,
      `The 3-day forecast card predicts Kp levels for the next 72 hours so you can plan your aurora viewing nights.`,
      `The Active Sunspot Regions card lists current active regions with their magnetic class and flare probabilities — tap any region for a detail view showing its magnetic evolution history (tracked locally with daily snapshots) and educational content about magnetic classifications.`,
      `Customize your aurora notification threshold in Social Settings — set a minimum Kp level (0.0–9.0) and you'll get an instant push notification the moment that Kp is reached, alerting you that aurora may be within viewing radius.`,
      `When an aurora alert fires, tap the "Share Kp Status" button on the notification to send your current Kp reading and visibility status to social media.`,
      `Use the Saved Spots section to bookmark specific coordinates and track aurora visibility at those locations — drop a pin on the aurora map, enter coordinates manually, or tap "Mark My Location" to use your GPS.`,
      `Every page in the Aurora tab features colorful northern lights backgrounds, bright themed text, and an animated twinkling-stars background that doesn't overlap any text, images, or buttons.`,
    ],
  },
  {
    icon: Star,
    accent: "hsl(190 90% 55%)",
    title: "Stars & Constellations — Night Sky Guide",
    shortLabel: "Night Sky",
    steps: [
      `From the Aurora Forecaster, tap the "Night Sky Guide" card to open the Stars & Constellations landing page.`,
      `Four clickable tiles lead to detailed astronomical info: Constellations, Important Stars, Planets, and Deep Sky Objects.`,
      `Constellations: browse all 88 IAU constellations organized by hemisphere. Tap any constellation for a programmatic star chart drawn with Canvas, mythology/lore, best viewing season, and its major stars with magnitudes. The 12 most famous constellations include hero images.`,
      `Important Stars: explore ~30 notable stars — Sirius, Betelgeuse, Vega, Polaris, Rigel, and more. Tap any star for its spectral class, temperature, luminosity, distance, and visibility info. The 6 most iconic stars include hero images.`,
      `Planets: all 8 planets plus dwarf planets (Pluto, Ceres) with diameter, distance from Sun, orbital period, moons, and detail cards showing physical properties, visibility, and notable features. Each planet has a generated image.`,
      `Deep Sky Objects: ~40 galaxies, nebulae, and star clusters (Andromeda Galaxy, Orion Nebula, Pleiades, Crab Nebula, and more) with catalog numbers, distances, magnitudes, and observing info. The 8 most famous DSOs include hero images.`,
      `Every page in the Night Sky Guide features animated twinkling white stars in the background — purely decorative, they never overlap text, images, or clickable items.`,
    ],
  },
  {
    icon: NotebookPen,
    accent: "hsl(205 45% 61%)",
    title: "Field Journal",
    shortLabel: "Field Journal",
    steps: [
      `Tap the "Field Journal" banner on the home screen.`,
      `Create a new entry for each day in the field — auto-weather, photos, and field notes.`,
      `Each entry captures the date, location, weather conditions, and your personal observations.`,
      `Tap any entry to view details, edit, or share to your profile feed.`,
    ],
  },
  {
    icon: MapPin,
    accent: "hsl(142 52% 54%)",
    title: "Dig Sites, Rock Shops & Locations",
    shortLabel: "Dig Sites",
    steps: [
      `Tap "Dig Sites & Rock Shops" on the home screen to browse the full map.`,
      `Filter by type: free dig sites, pay-to-dig mines, rock shops, metaphysical shops, and gem & mineral shows.`,
      `Enable location monitoring to see nearby hot spots within 100 miles (250 miles with Premium).`,
      `Get proximity pings when you're close to a dig site.`,
      `Tap any location for details, photos, one-tap Google Maps directions, and gear recommendations.`,
      `Open the Gem & Mineral Shows tab from the Dig Sites map to browse recurring gem, mineral, and fossil shows across the US, grouped by upcoming month so you never miss a hunt.`,
      `Download cached satellite and street tiles for offline use in no-signal areas — tap the floating Download button on any map, drop a pin, and cache tiles for a 3-mile radius.`,
      `Export high-resolution satellite map images to your gallery for offline reference — save ultra high-res images at any pin location.`,
      `Use the Park Here button to save your parking spot — satellite tiles are automatically cached around it so you can navigate back to your vehicle with zero signal.`,
      `Tap Navigate to Vehicle to get Google Maps directions back (online) or center the map on your parking spot with cached satellite tiles (offline).`,
      `The satellite button cycles through 3 zoom levels: first tap zooms to 16 (satellite appears), second tap zooms to 19 (deepest detail), third tap restores your original view.`,
      `Every map shows a sync-status pill (top-left) that reports whether the offline tiles for the visible area are up to date, cached, or stale. Tap the pill to refresh the cache for the current area. When offline, it tells you whether the cached tiles are fresh or may be stale so you know if you can trust the map.`,
      `Every map can be expanded to fullscreen with a close button in the top-left corner. The My Location button centers the map directly on your current GPS position. If you accidentally drop a pin by tapping the map, a Cancel/Remove Pin button lets you clear it.`,
      `Found a great dig site not on the map? Use the Upload New Location form to add it after review.`,
    ],
  },
  {
    icon: Search,
    accent: "hsl(190 90% 55%)",
    title: "Search & Discovery",
    shortLabel: "Search",
    steps: [
      `Tap the search icon in the home header.`,
      `Search across the entire database — specimens, locations, and educational guides.`,
      `Results are grouped by category for quick navigation.`,
      `Tap any result to jump straight to the detail page.`,
    ],
  },
  {
    icon: Bell,
    accent: "hsl(4 70% 62%)",
    title: "Notifications, Weather Alerts & Message Requests",
    shortLabel: "Alerts",
    steps: [
      `The notification bell icon (home screen and profile header) shows your unread count for friend requests and other non-message notifications.`,
      `Tap the bell to open the Notification Center. If you have pending friend requests, a summary tile appears at the top — tap it to jump straight to the RockScout Friends screen.`,
      `Below the summary tile, regular notifications appear: trade interests, submission approvals, post likes, comments, and more — each with a tappable deep link.`,
      `Swipe left on any notification to delete it. An "Undo" pill appears for 5 seconds in case you change your mind.`,
      `Use the Select checkbox to enter selection mode, then tap "Delete Selected" to remove multiple notifications at once.`,
      `Enable Severe Weather Alerts in Social Settings to receive instant NWS alerts the moment they're issued for your area — severe thunderstorm, tornado, flash flood, hurricane, tropical storm, tsunami, blizzard, winter storm, ice storm, extreme heat/cold, high wind, dust storm, dense fog, fire weather (Red Flag Warning, Fire Weather Watch), and smoke/air quality alerts.`,
      `Weather alerts and Kp/aurora alerts both fire instantly — they're not batched or delayed by the background polling cycle.`,
      `The message icon (envelope, next to the bell) is separate from the notification bell. It shows a count for all unread messages and pending message requests. Tap it to open the RockScout Friends screen.`,
    ],
  },
  {
    icon: User,
    accent: "hsl(176 80% 60%)",
    title: "Profile, Achievements & Badges",
    shortLabel: "Profile",
    steps: [
      `Tap your avatar on the home screen to open your Profile.`,
      `Your Player Card shows your level, XP progress, hunter status, and earned badges.`,
      `Set your hunter status: Off Grid, Hunting, Digging, or Trading — each shows a color-coded profile border.`,
`Browse 102 achievements and 34 badges on the Achievements page.`,
      `Each locked achievement on the All Achievements page now shows a visual progress bar indicating how close you are to earning it — so you always know what to do next to level up.`,
      `Earn XP for every action — identifying rocks, adding to your collection, capturing field photos, trading, and more.`,
      `Level up to unlock new tiers with unique emojis and brag-worthy celebration pop-ups.`,
      `Share level-up and badge-earn cards to your profile feed or straight to social media.`,
      `Customize your profile with a background image and display name.`,
    ],
  },
  {
    icon: Heart,
    accent: "hsl(270 47% 72%)",
    title: "Wishlist, Favorite Spots & Aurora Saved Spots",
    shortLabel: "Wishlist",
    steps: [
      `Tap the heart icon on any specimen card to add it to your Wishlist.`,
      `View your Wishlist from the home screen — it's your dream-specimen shopping list.`,
      `Tap the bookmark icon on any dig site, state park, BLM location, campground, trailhead, or dig site detail screen to save it to Favorite Spots for quick access.`,
      `Favorite Spots appear on the home screen with a count of saved locations and are fully searchable from the global search.`,
      `In the Aurora Forecaster, use the Saved Spots section to bookmark specific coordinates and track aurora visibility at those locations — drop a pin on the map, enter lat/lng manually, or tap "Mark My Location" to use your GPS.`,
      `Each aurora saved spot shows the name, coordinates, and current visibility status (Aurora visible / unlikely) based on the spot's latitude and current Kp index.`,
    ],
  },
  {
    icon: Download,
    accent: "hsl(195 60% 53%)",
    title: "Saved Images & Photo Interactions (Free)",
    shortLabel: "Saved Images",
    steps: [
      `Tap any photo in the app to view it full-screen.`,
      `Long-press any photo to save it to your personal Saved Images folder.`,
      `Open "My Saved Images" from the home screen to browse, share, or delete saved photos.`,
      `Share saved images to your profile feed or straight to social media.`,
      `Saved Images is a free feature — it's the destination for field-camera snaps after the trial ends, so your photo log never gets locked behind a paywall.`,
    ],
  },
  {
    icon: Mountain,
    accent: "hsl(22 52% 61%)",
    title: "BLM Public Lands Guide",
    shortLabel: "BLM Lands",
    steps: [
      `Tap "BLM Public Lands" in the Field Kit section on the home screen.`,
      `Browse state-by-state rules for rockhounding on Bureau of Land Management land.`,
      `Each state page shows trailheads, campgrounds, and dig sites with tappable detail pages.`,
      `Check regulations before you hunt — rules vary by state and land type.`,
      `Each BLM state guide, trailhead, campground, dig site, and state park detail screen includes a "Common Wildlife" tile showing the animals you might encounter in that area — mammals, birds, reptiles, and more, tailored to the region's biome.`,
      `Beach and coastal dig site detail screens show marine and shorebird wildlife specific to that coast.`,
    ],
  },
  {
    icon: Globe2,
    accent: "hsl(0 0% 75%)",
    title: "Meteorite Hunting",
    shortLabel: "Meteorites",
    steps: [
      `Tap "Finding Meteorites" in the Field Kit section on the home screen.`,
      `Learn how to identify space rocks — fusion crust, magnetic properties, and key visual cues.`,
      `Discover where meteorites are most commonly found and how to hunt them.`,
      `The Specimen Database also includes meteorite entries with full properties.`,
    ],
  },
  {
    icon: Atom,
    accent: "hsl(210 76% 71%)",
    title: "Periodic Table of Elements",
    shortLabel: "Periodic Table",
    steps: [
      `Tap "Periodic Table" in the Explore & Learn section to explore all 118 elements.`,
      `Each element card shows where it appears in rocks and gems and its role in mineral formation.`,
      `Tap any element for a detail page with photos and easy-to-understand explanations.`,
    ],
  },
  {
    icon: School,
    accent: "hsl(40 62% 64%)",
    title: "Educational Guides",
    shortLabel: "Guides",
    steps: [
      `RockScout includes 11 built-in educational guides — all work fully offline once the bulk image download completes.`,
      `Exploring Geology: learn how rocks, minerals, and gems form across the rock cycle.`,
      `Exploring Paleontology: fossils, geologic eras, and deep-time history.`,
      `Exploring Prehistoric Organisms: dinosaurs, ancient birds, prehistoric flora, and more.`,
      `Tectonics & Volcanoes: plate movement, magma, and where rocks are born.`,
      `The Mohs Hardness Scale card on the home screen walks you through scratch testing with infographics, the 10 reference minerals, and field-test steps.`,
      `The Aurora Forecaster and Stars & Constellations guide add space weather and astronomy to your educational toolkit — see the dedicated sections above for details.`,
      `Each guide features stunning photos and easy-to-understand explanations — no geology degree required.`,
    ],
  },
  {
    icon: BookMarked,
    accent: "hsl(195 60% 53%)",
    title: "Rocks Are Amazing",
    shortLabel: "Rocks Are Amazing",
    steps: [
      `Tap "Rocks Are Amazing" in the Explore & Learn section to open a curated gallery of Earth's most stunning formations.`,
      `Swipe through categorized card collections: enhydros, pseudomorphs, petroleum inclusions, fluorescent minerals, optical phenomena, coprolites, copper-inclusion agates, mineral assemblages, and more.`,
      `Each card opens a full specimen detail page with photos, properties, and where-to-find info.`,
      `Approved user-submitted specimens can land here alongside the Specimen Database — your finds might become a wonder.`,
    ],
  },
  {
    icon: Landmark,
    accent: "hsl(30 52% 51%)",
    title: "Artifacts & Stone Tools",
    shortLabel: "Artifacts",
    steps: [
      `Tap the "Artifacts" tile on the home screen to browse a growing catalog of over 100 authentic prehistoric artifacts, each with its own generated reference image on a unique background.`,
      `Families include Arrowheads (20+ types from Clovis and Folsom to Mississippian triangle points), Spear Points & Dart Tips (12+ Paleoindian and Archaic forms), Hand Axes & Axe Heads (20+ Acheulean and Neolithic bifaces), Flaked Stone Tools (12+ scrapers, knives, and gravers), Drill Bits (6+ awl and drill forms), Native Beads (13+ shell, stone, and copper beads), Stone Effigies (7+ animal and human effigies from the Woodland and Mississippian periods), Pipes & Medicine Tubes, Ornaments & Weights, Shell Tools, Bone Tools, Pottery, Game Discs, and Wooden Artifacts.`,
      `Each artifact card shows a reference image, cultural period, material, and a detailed description of how it was made and used.`,
      `The Artifacts tile is linked to the Specimen Database via the ARTIFACTS category chip at the top of the specimen list — tap the chip to filter the full database down to artifacts only. Artifacts never appear inside the main specimen database list; they are only accessible through the Artifacts tile or the category chip.`,
      `A NEW badge appears on any artifact added to the catalog within the last 48 hours, so you can spot the latest additions at a glance.`,
    ],
  },
  {
    icon: Mountain,
    accent: "hsl(176 80% 45%)",
    title: "Natural Wonders of the World",
    shortLabel: "Natural Wonders",
    steps: [
      `Tap the "Natural Wonders" tile on the home screen to explore 36 world-famous geological sites and the rocks and minerals you can find at each one.`,
      `Each wonder includes a stunning photo, location, geological formation story, rocks to find, and fun facts — from the Grand Canyon and Giant's Causeway to Mount Vesuvius, Salar de Uyuni, and the Zhangjiajie Pillars.`,
      `Recent additions include the Naica Crystal Caves (Chihuahua, Mexico) — the Cave of the Crystals, where giant selenite crystals grow from floor to ceiling, some over 36 feet long — plus Sossusvlei Red Dunes, Þingvellir Rift Valley, Tongariro Volcanic Complex, and more.`,
      `Each card opens a full detail page with the formation's geological history, what rocks and minerals are found there, and visitor tips for rockhounds.`,
      `Use Natural Wonders as a bucket-list trip planner — many of these sites are dig-friendly destinations you can add to your Trip Planner.`,
    ],
  },
  {
    icon: Library,
    accent: "hsl(210 76% 71%)",
    title: "Rock & Gem Resources",
    shortLabel: "Resources",
    steps: [
      `Tap "Rock & Gem Resources" in the Field Kit section on the home screen to browse trusted external geology, gem, and fossil websites.`,
      `Links open in your device's browser so you can dig deeper into any topic.`,
      `Curated for rockhounds — museums, university mineralogy departments, and reputable reference sites.`,
      `Use it as a research companion alongside the in-app Specimen Database.`,
    ],
  },
  {
    icon: Diamond,
    accent: "hsl(48 86% 70%)",
    title: "Gear Guide",
    shortLabel: "Gear Guide",
    steps: [
      `Tap the "Gear Guide" banner (right below the Calendar tile) on the home screen to browse 48+ curated tools with Amazon links.`,
      `Kits are organized from beginner to advanced — from a first loupe and rock hammer to lapidary equipment and UV lights.`,
      `Each item shows a price range and a quick link so you can stock up before your next dig.`,
      `As an Amazon Associate, RockScout earns from qualifying purchases — at no extra cost to you.`,
    ],
  },
  {
    icon: Users,
    accent: "hsl(142 52% 54%)",
    title: "Referrals & Community",
    shortLabel: "Referrals",
    steps: [
      `Open the Referral screen from your Profile to get your unique referral link.`,
      `Share the link with friends — when they sign up, you both earn tokens and XP.`,
      `Watch for referral reward celebration pop-ups when your friends complete sign-up.`,
      `The app includes a profanity filter and screenshot-based reporting to keep the community family-friendly.`,
      `Use the Report button on any message or profile to flag inappropriate content.`,
      `Rate & review RockScout from the home screen to help fellow rockhounders discover the app.`,
    ],
  },
  {
    icon: Upload,
    accent: "hsl(195 60% 53%)",
    title: "Submit Specimens & Add Locations",
    shortLabel: "Submit",
    steps: [
      `Found a specimen that isn't in the database? Use the Upload Specimen pill (found on the Specimen Database screen, the Field Captures screen, and specimen detail pages).`,
      `Submit up to 4 photos plus a name, date found, location, and description. Images are automatically checked against a 5 MB size limit — if a photo is too large, you'll get a friendly notification to pick a smaller file, preventing upload failures before they happen.`,
      `If the specimen already exists in the database and your location is a common find spot for it, you'll get a small pop-up letting you know it's already included. If the location is unusual for that specimen, it goes to developer review instead.`,
      `Found a dig site, rock shop, or gem show that isn't on the map? Use the Add Location form on the Dig Sites map or any map screen.`,
      `After review, submitted locations appear on the Dig Sites map for every RockScout user to discover.`,
      `Help build the most thorough rock database and the most complete hunting map on the app market!`,
    ],
  },
  {
    icon: Share2,
    accent: "hsl(190 90% 55%)",
    title: "Share-a-Spot Deep Links",
    shortLabel: "Share a Spot",
    steps: [
      `Open the Field Captures Specimen Map (the swipeable second page of Field Captures) or the Trip Planner specimen marker map.`,
      `Tap any specimen marker pin on the map to open that specimen's detail view.`,
      `A new "Share a Spot" card appears on the detail page when you arrived from a marker pin. Tap Share a Spot to generate a rockscout://spot deep link encoding the spot's coordinates and specimen name.`,
      `The system share sheet opens so you can send the link via Messenger, text, email, or any app you like.`,
      `Friends who tap the link on a device with RockScout installed jump straight to a Shared Spot screen showing the spot on a map with a Close button.`,
      `This is the easiest way to point fellow rockhounds at an exact find spot — no typing coordinates by hand.`,
    ],
  },
  {
    icon: Database,
    accent: "hsl(210 76% 71%)",
    title: "Storage, Cache & Bulk Offline Download",
    shortLabel: "Storage",
    steps: [
      `Open Social Settings and scroll to the Storage section to choose your cache size.`,
      `Standard (150MB) stores recently viewed specimen photos and map tiles — automatically manages itself by removing older items as new ones come in.`,
      `Maximum (2GB) raises the ceiling so a bulk download can fit alongside your map tiles. Available to all users — it's your own device storage, not Premium-gated.`,
      `The cache size applies to both the image cache (specimen photos, field captures) and the map tile cache (satellite and street tiles).`,
      `Want the whole catalog offline? Tap the "Download all images (~3.5 GB)" button — found as a small pill in the Specimen Database header AND as a full card next to the 2GB toggle in Storage settings.`,
      `The bulk download caches every specimen photo plus all educational/guide illustrations and hero art on-device, so every read-only screen (specimen details, geology guides, BLM guide, periodic table, etc.) loads instantly with zero signal.`,
      `Requires the Maximum (2GB) cache mode first — if it's off, the card links you straight to the toggle.`,
      `On cellular, you'll get a confirmation dialog showing the ~3.5 GB estimate before anything downloads. On WiFi, it starts immediately.`,
      `A live progress bar shows "X / Y images · Z%" with a Cancel button. If you cancel or close the app, progress is remembered — re-open and tap "Resume download" to pick up where you left off.`,
      `When the download finishes, an "All images saved offline" banner shows the actual bytes cached, and an offline-ready badge appears in the Specimen Database header so you can see at a glance that the catalog is fully cached.`,
    ],
  },
  {
    icon: NotebookPen,
    accent: "hsl(40 62% 64%)",
    title: "Tokens, Premium & Donations — What's Free vs Paid",
    shortLabel: "Premium",
    steps: [
      `Free for 7 days: full app access including AI identification (5 tokens to spend at your own pace), RockScout Friends, Trade Board, My Rocks, Wishlist, Field Captures, Trip Planner, and Field Journal.`,
      `After the trial, these stay FREE forever: browsing the full specimen database & geology guides, the Field Camera (saves to Saved Images), NWS severe weather alerts, and browsing dig sites & offline maps.`,
      `After the trial, these require Premium or a donation: AI identification & ID tokens, RockScout Friends/Messenger/Community, Trade Board & Discover Hunters, My Rocks/Wishlist/Field Captures/Favorite Spots, and Trip Planner/Field Journal/specimen submissions.`,
      `Two ways to keep identifying free: watch 2 short rock-related videos to earn 1 ID token (no weekly cap), or make a donation of any amount for tokens plus a temporary full-feature unlock (2 days up to 1 month).`,
      `Premium ($5.99/mo) unlocks everything: unlimited AI identifies with all 3 models (Haiku + Sonnet + Gemini 2.5 Pro tie-breaker), ad-free, 250-mile nearby radius, 24-hour pings, premium gem badge, and early access to new features.`,
      `Check your token balance anytime via the Token Bank icon in the home header.`,
      `Tap the Token Bank to open the Token Info screen for details and purchasing options.`,
    ],
  },
  {
    icon: Sparkles,
    accent: "hsl(48 86% 65%)",
    title: "NEW Badges & Recently Added Specimens",
    shortLabel: "NEW Badge",
    steps: [
      `A NEW badge automatically appears on any specimen or artifact card that was added to the catalog within the last 48 hours.`,
      `Spot the latest database additions at a glance — no need to hunt through the full list to find what's new.`,
      `The badge works across the Specimen Database, Artifacts tile, and any category-filtered view.`,
      `After 48 hours, the badge disappears automatically — the specimen stays in the catalog but is no longer flagged as new.`,
    ],
  },
  {
    icon: ShieldCheck,
    accent: "hsl(142 52% 50%)",
    title: "App Updates, Sign-in & Safety",
    shortLabel: "Updates & Safety",
    steps: [
      `When the app detects a signing conflict during an update (the installed APK was signed with a different key than the new version), a friendly dialog explains that the old version must be uninstalled to update — and offers a button to trigger the system uninstall flow directly.`,
      `After reinstalling the new version, just sign back in with your RockScout account. All your settings — hunter status, cache mode, notification preferences, aurora thresholds, and more — are restored from the cloud, exactly as they were before.`,
      `A confirmation dialog appears when you tap the logout button in Settings, preventing accidental sign-outs.`,
      `Your collections, captures, friends, and achievements are tied to your account, not your device — they carry over to any device when you sign back in.`,
    ],
  },
  {
    icon: Monitor,
    accent: "hsl(210 76% 65%)",
    title: "Desktop Web App & Keyboard Shortcuts",
    shortLabel: "Desktop & Keys",
    steps: [
      `RockScout's web app is a full PWA that works on desktop browsers — wider multi-column layouts, split views for the map and location list, and grids that use your screen space efficiently.`,
      `On large screens, the Home, Reference Library, Specimens, Collection, Trade Board, Achievements, Gear Guide, and Gem Shows grids expand to 4–6 columns so you can browse more at once.`,
      `The Dig Sites map page features a desktop split view — a scrollable, selectable location list sits beside the map so you can browse and navigate without switching tabs.`,
      `Keyboard shortcuts: press \"?\" to toggle the keyboard shortcuts overlay, \"/\" to focus the search bar, and \"g\" followed by a letter to jump to a section (g h for Home, g m for Map, g s for Specimens, g c for Collection, g r for Reference Library, g t for Trade Board, g a for Achievements, g g for Gear Guide). Press Esc to close overlays or blur the search.`,
      `Install the PWA to your desktop from your browser's install prompt for a standalone app window with its own icon.`,
    ],
  },
];

const HowToUse = () => {
  const navigate = useNavigate();
  const [selectedIdx, setSelectedIdx] = useState<number | null>(null);
  const selected = selectedIdx !== null ? HOW_TO_SECTIONS[selectedIdx] : null;

  return (
    <Layout
      title={`How to use ${SITE.name}`}
      description={`A complete walkthrough of every feature in ${SITE.name} — from AI rock identification to trading, social, trip planning, and offline maps.`}
      canonical="https://rockscout.net/how-to-use"
    >
      <article className="relative">
        {/* Geode backdrop */}
        <div className="geode-gradient absolute inset-x-0 top-0 -z-10 h-72" aria-hidden />

        <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6 sm:py-12 md:py-16">
          {/* Header row with close button */}
          <div className="flex items-start justify-between gap-4">
            <div className="fade-rise">
              <span className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-primary">
                <span className="h-px w-8 bg-primary/50" /> Guide
              </span>
              <h1 className="mt-3 font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl">
                How to use {SITE.name}
              </h1>
              <p className="mt-3 max-w-xl text-balance text-sm text-muted-foreground sm:text-base">
                Every feature, step by step — the same walkthrough that lives inside the app.
              </p>
            </div>
            <button
              type="button"
              onClick={() => navigate("/")}
              aria-label="Close and return home"
              className="grid h-11 w-11 shrink-0 place-items-center rounded-full border border-border bg-card/60 text-muted-foreground transition-all hover:-translate-y-0.5 hover:border-primary/60 hover:text-primary"
            >
              <X className="h-5 w-5" />
            </button>
          </div>

          {/* Intro card */}
          <div className="mt-8 rounded-2xl border border-border/60 bg-card/40 p-4 fade-rise sm:mt-10 sm:p-6">
            <p className="text-sm leading-relaxed text-muted-foreground">
              Welcome to {SITE.name}! This guide walks you through every feature of the app — from AI rock identification to trading, social features, trip planning, and more. Tap any section below to read its step-by-step instructions.
            </p>
          </div>

          {/* Section pills grid */}
          <div className="mt-8 grid grid-cols-2 gap-2.5 sm:mt-10 sm:grid-cols-3 sm:gap-3 lg:grid-cols-4">
            {HOW_TO_SECTIONS.map((section, idx) => (
              <button
                key={section.title}
                type="button"
                onClick={() => setSelectedIdx(idx)}
                className="group flex items-center gap-2.5 rounded-xl border px-3 py-3 text-left transition-all hover:-translate-y-0.5 hover:shadow-md sm:px-4"
                style={{
                  borderColor: `color-mix(in srgb, ${section.accent} 30%, transparent)`,
                  backgroundColor: `color-mix(in srgb, ${section.accent} 8%, transparent)`,
                }}
              >
                <span
                  className="grid h-8 w-8 shrink-0 place-items-center rounded-lg ring-1 transition-transform group-hover:scale-110 sm:h-9 sm:w-9"
                  style={{
                    backgroundColor: `color-mix(in srgb, ${section.accent} 15%, transparent)`,
                    color: section.accent,
                    boxShadow: `inset 0 0 0 1px color-mix(in srgb, ${section.accent} 30%, transparent)`,
                  }}
                >
                  <section.icon className="h-4 w-4 sm:h-5 sm:w-5" />
                </span>
                <span className="flex flex-col gap-0.5 min-w-0">
                  <span className="text-[10px] font-bold tabular-nums text-muted-foreground/60">
                    {String(idx + 1).padStart(2, "0")}
                  </span>
                  <span className="truncate text-xs font-semibold text-foreground sm:text-sm">
                    {section.shortLabel}
                  </span>
                </span>
              </button>
            ))}
          </div>

          {/* Sign-off */}
          <div className="mt-10 text-center sm:mt-14">
            <p className="font-display text-xl font-bold text-primary">Happy Hunting!</p>
            <p className="mt-1 text-xs text-muted-foreground/70">
              {SITE.name} · {SITE.tagline}
            </p>
          </div>
        </div>
      </article>

      {/* Section popup dialog */}
      <Dialog open={selectedIdx !== null} onOpenChange={(open) => { if (!open) setSelectedIdx(null); }}>
        <DialogContent className="max-w-2xl max-h-[85vh] overflow-hidden p-0 sm:rounded-2xl">
          {selected && (
            <div className="flex flex-col h-[85vh] sm:h-[85vh]">
              {/* Header */}
              <DialogHeader className="flex-row items-center gap-3 border-b border-border/60 px-5 py-4 sm:px-6">
                <span
                  className="grid h-10 w-10 shrink-0 place-items-center rounded-xl ring-1"
                  style={{
                    backgroundColor: `color-mix(in srgb, ${selected.accent} 15%, transparent)`,
                    color: selected.accent,
                    boxShadow: `inset 0 0 0 1px color-mix(in srgb, ${selected.accent} 30%, transparent)`,
                  }}
                >
                  <selected.icon className="h-5 w-5" />
                </span>
                <DialogTitle className="font-display text-base font-bold text-foreground sm:text-lg">
                  {selected.title}
                </DialogTitle>
              </DialogHeader>

              {/* Scrollable steps */}
              <div className="flex-1 overflow-y-auto px-5 py-5 sm:px-6">
                <ol className="flex flex-col gap-3">
                  {selected.steps.map((step, i) => (
                    <li key={i} className="flex gap-3 text-sm leading-relaxed text-muted-foreground">
                      <span
                        className="mt-0.5 shrink-0 font-display text-xs font-bold tabular-nums"
                        style={{ color: selected.accent }}
                      >
                        {i + 1}.
                      </span>
                      <span>{step}</span>
                    </li>
                  ))}
                </ol>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </Layout>
  );
};

export default HowToUse;
