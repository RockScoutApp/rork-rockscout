package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.LocalVideoPlayerState
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Cyan
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid

/** A single how-to section with icon, title, short label, and step-by-step instructions. */
private data class HowToSection(
    val icon: ImageVector,
    val accent: Color,
    val title: String,
    val shortLabel: String,
    val steps: List<String>,
)

/** All how-to sections — covers every major feature in RockScout. */
private val howToSections: List<HowToSection> = listOf(
    HowToSection(
        icon = Icons.Filled.CameraAlt,
        accent = Citrine,
        title = "AI Rock Identification",
        shortLabel = "AI Rock ID",
        steps = listOf(
            "Tap the big \"Identify a Rock\" hero banner on the home screen.",
            "Choose a photo from your gallery or snap one with your camera.",
            "The 5-source pipeline starts: first the database visual reference comparison — your photo is compared side by side against reference images in the specimen database and ranked by visual similarity (not just from text descriptions).",
            "Then three AI models tag-team the analysis — Claude Haiku, Claude Sonnet, and Gemini 2.5 Pro — all looking at the reference images alongside your photo.",
            "If the visual match is highly confident (92%+), results return instantly — no need for the full pipeline.",
            "Otherwise, the fifth source runs: a web search cross-check for maximum accuracy.",
            "If the AI needs more information to narrow down the ID, it asks you a few clarification questions (e.g. hardness, streak color, or environment) — answer them and the final result re-ranks the candidates for higher accuracy.",
            "If your rock has multiple minerals, the AI auto-detects the assemblage and breaks down each component.",
            "Results show the specimen name, confidence level, properties, and where to find more — the AI has already visually compared your photo against the database reference images to reach that ranking.",
            "Free users get a 7-day trial with 5 tokens. After that, tokens or a Premium subscription are needed.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CollectionsBookmark,
        accent = Color(0xFF6FA8C7),
        title = "My Collection & Specimen Cards",
        shortLabel = "Collection",
        steps = listOf(
            "Tap \"My Rocks\" on the home screen to view your collected specimens.",
            "Each specimen card shows a photo, name, tagline, rarity, and location.",
            "The colored category pill (silicate, volcanic, carbonate, etc.) uses a unique color per rock type.",
            "Tap any card to open the full specimen detail page with properties, photos, and locations.",
            "Use the glowing heart icon on any card to like or wishlist a specimen.",
            "Use the dropdown menu on the card to add to collection, add to wishlist, share to profile feed, or share to social media.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.PhotoLibrary,
        accent = Color(0xFF5CC98C),
        title = "Field Captures & Field Camera (Free)",
        shortLabel = "Field Camera",
        steps = listOf(
            "Tap \"Field Captures\" on the home screen.",
            "Log photos of rocks you find in the field — no identification needed, just a visual record.",
            "Each capture stores the photo, date, and optional notes.",
            "Tap any capture to view it full-screen, long-press to save it to your Saved Images folder.",
            "Use the separate \"Field Camera\" tile on the home screen to snap a photo without running the ID tool at all — perfect when you just want to grab a shot.",
            "The Field Camera stays free after the trial ends — snaps save to your in-app Saved Images folder at no cost. (Saving to My Rocks, Wishlist, or other collection destinations still requires Premium or a donation.)",
            "After snapping with the Field Camera, pick a destination from the \"Save to…\" dropdown: Field Captures, Saved Images, My Rocks, My Wishlist, Field Journal Entry, Share to Profile, Change Profile Background, or Submit a Specimen.",
            "The save form lets you add a name, location, and description, plus drop a pin on a map to mark the exact find spot — the pin's coordinates are stored with the capture card.",
            "Field Captures has a second swipeable page: a full-page Specimen Map showing every capture that has a pin. Tap any pin to open the full specimen detail page for viewing or editing. Swipe the top pills or the screen to switch between the captures list and the specimen map.",
            "An Upload pill sits in the same row as the Captures and Specimen Map pills — tap it to submit a new specimen with up to 10 photos, name, date found, location, and description.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.SwapHoriz,
        accent = Color(0xFFE8A33D),
        title = "Trade Board",
        shortLabel = "Trade Board",
        steps = listOf(
            "Tap the \"Trade Board\" banner on the home screen.",
            "Post a specimen you want to swap or sell — add photos, a description, and your trade preferences.",
            "Browse the Trading Floor to see HAVE/WANT listings from other RockScout users.",
            "Tap any listing to view details and start a chat with the trader via Messenger.",
            "Check \"My Trades\" to manage your active listings and conversations.",
            "All trades are user-to-user — RockScout facilitates the connection but does not handle shipping or payments.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Forum,
        accent = Amethyst,
        title = "Community Q&A Board",
        shortLabel = "Community",
        steps = listOf(
            "Tap the \"Community\" banner on the home screen to open the app-wide Q&A feed.",
            "Post a question, photo, or rock story for the whole RockScout community to see — posts auto-expire after 14 days to keep the feed fresh.",
            "Sort posts by Newest, Most Loved, or Most Commented using the sort dropdown at the top.",
            "Tap Love on any post or comment to upvote it, tap Comment to add a comment, and Reply to reply to a comment — full threaded replies are supported.",
            "Attach an image to a post, comment, or reply by tapping the image icon next to the text field. All images are scanned by the profanity filter.",
            "Repost a post to share it back to the feed, or use the Report button to flag inappropriate content — a screenshot is captured and a confirmation popup appears before submitting.",
            "Expired posts appear in an Archived Posts popup where you can browse and restore them before they're permanently removed.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Group,
        accent = Amethyst,
        title = "RockScout Social — Pings, Friends & Messenger",
        shortLabel = "Social",
        steps = listOf(
            "Your RockScout account is required to use the app — it's free and your collections, captures, and friends carry over to any device.",
            "Open the RockScouts Map to see live pings from other hunters. Drop your own ping to share your location.",
            "Send friend requests from the RockScout Friends screen or by tapping another user's profile.",
            "Use the Discover Hunters screen (from your Profile) to browse and search every discoverable RockScout hunter worldwide — filter by status, level, or collection size and tap any result to view their public profile.",
            "The message icon (envelope, next to the notification bell) shows a count badge for all unread messages and pending message requests. Tap it to open the unified RockScout Friends screen.",
            "The notification bell shows a count for friend requests and other non-message notifications. Tap it to open the Notification Center, where a summary tile for friend requests appears at the top.",
            "On the RockScout Friends screen, friend requests and message requests appear at the top under a \"Requests\" section. Use Accept, Deny, Block, or Report pill buttons on each tile.",
            "Tap a message request tile to open a full-screen chat view with text-message-style bubbles — your messages on the right, theirs on the left.",
            "Below the Requests section, active conversations appear under a \"Conversations\" section. Tap any conversation to open the full chat.",
            "Below conversations, your connected friends appear under a \"Friends\" section, sorted by Traders then Hunters.",
            "Swipe left on a conversation tile to delete it, or swipe left on a friend card to unfriend them. A confirmation popup appears before the action is completed.",
            "Use the \"Preview\" button at the top of a chat to read messages without triggering a read receipt (green dot).",
            "Send images through the messenger by tapping the image icon next to the text field. All images are scanned by the profanity filter.",
            "Long messages show a \"More\" pill — tap it to expand and read the full text.",
            "Use the Report button inside any message tile to flag inappropriate content. A confirmation popup appears before submitting.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Map,
        accent = Color(0xFFE8A33D),
        title = "Trip Planner",
        shortLabel = "Trip Planner",
        steps = listOf(
            "Tap the \"Trip Planner\" banner on the home screen.",
            "Create a multi-stop hunt route by adding dig sites, rock shops, or custom pins.",
            "Drop custom pins for gas, food, or any point of interest along the way.",
            "Flag interesting pins as potential rock locations for the developer to review and add to the database.",
            "Reorder stops by long-pressing a stop card and dragging it to a new position — the stop swaps to the position under your finger, with the route polyline and travel-time badges updating in real-time. Move up buttons are also available for quick single-step reordering.",
            "A dashed polyline connects all stops on the route map, with directional arrows at each segment midpoint, so you can visualize the full journey at a glance.",
            "Estimated travel time appears between consecutive stops based on straight-line distance and an assumed 45 mph average driving speed — shown as a badge between stop rows and as a total in the trip header.",
            "Use \"Download Maps for All Stops\" to cache offline satellite tiles and high-res images for every stop on your trip at once — perfect for backcountry trips with no signal.",
            "Use \"Cache Trip Area\" on any trip to save satellite tiles for a 3-mile radius around each stop (zoom 10–19) plus a 1-mile radius around every pinned specimen marker (zoom 14–19), so your route AND your find spots work with zero signal. The cache status persists across app restarts, and tiles are freed automatically when you archive or delete a trip.",
            "A sync-status pill on every trip map shows whether your offline tiles are up to date: green \"Up to date\" (cached in the last 24h), aqua \"Cached\" (within 7 days), or warning \"Stale • tap to refresh\" (older than 7 days). Tap the pill to re-cache the trip area. When you're offline, the pill reports whether the cached tiles are fresh or may be stale.",
            "Add a gear checklist so you never forget your hammer, loupe, or UV light.",
            "When editing a trip, drop specimen marker pins on the specimen marker map to mark exactly where you found each rock — pins persist with the trip, appear on the route map, and are included when you cache the trip area for offline use. Use Set Pin / Remove Pin with confirmation popups.",
            "Expand the trip route map to fullscreen and tap anywhere to drop a pin. Two pill buttons appear: \"Add to Route\" adds the pin as a stop (auto-approving rock-related locations via web scan), and \"Upload New Location\" opens the full location upload form pre-filled with the pinned coordinates.",
            "Export your planned trip to the in-app Calendar by tapping the \"View in Calendar\" button in the trip editor — the trip appears on its scheduled date in the month grid.",
            "Mark a trip as complete with the checkmark button — completed trips can be archived to the \"Archived\" tab for safekeeping without cluttering your active trip list.",
            "Share your trip plan with friends via the share button.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CalendarMonth,
        accent = Color(0xFF6FA8C7),
        title = "Trip Calendar",
        shortLabel = "Calendar",
        steps = listOf(
            "Tap the \"Calendar\" tile on the home screen to open the standalone Trip Calendar.",
            "View all planned trips in a month grid — each trip appears inside its scheduled date box with the trip name and first few stop names.",
            "Navigate between months with the prev/next arrows. Today's date is highlighted with a Citrine circle.",
            "An agenda list below the grid shows upcoming trips sorted by date, with trip name, stop count, first 3 stops, and estimated distance.",
            "Create new trips directly from the calendar, edit existing ones, share trips, or view trip details — full editing access without going through the Trip Planner.",
            "Drag and drop trip cards to different dates on the calendar grid to quickly reschedule them — long-press a trip card and drag it to the target date.",
            "The Trip Planner's \"View in Calendar\" button links here too, but the calendar is always accessible from the home screen on its own.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Archive,
        accent = Success,
        title = "Archived Trips",
        shortLabel = "Archived",
        steps = listOf(
            "Mark any trip as complete with the checkmark button on its card in the Trip Planner.",
            "Completed trips can be archived — tap the \"Archived\" pill button in the Trip Planner header to view them.",
            "Archived trips are kept safe without cluttering your active trip list.",
            "Restore an archived trip back to active, or permanently delete it from the Archived screen.",
            "Archived trip map tiles are automatically freed from the offline cache to save storage.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Bolt,
        accent = Color(0xFF4FC3F7),
        title = "Aurora Forecaster & Space Weather",
        shortLabel = "Aurora",
        steps = listOf(
            "Tap the \"Aurora Forecaster\" tile on the home screen to check real-time space weather conditions.",
            "The main card shows the current Kp index, Bz value, solar wind speed, and visibility status for your latitude — color-coded with bright aurora-green and purple theming.",
            "A 24-hour Kp trend chart and a 7-day F10.7 radio flux chart show how conditions have been changing over time, with a dashed threshold line at your visibility level.",
            "The 3-day forecast card predicts Kp levels for the next 72 hours so you can plan your aurora viewing nights.",
            "The Active Sunspot Regions card lists current active regions with their magnetic class and flare probabilities — tap any region for a detail view showing its magnetic evolution history (tracked locally with daily snapshots) and educational content about magnetic classifications.",
            "Customize your aurora notification threshold in Social Settings — set a minimum Kp level (0.0–9.0) and you'll get an instant push notification the moment that Kp is reached, alerting you that aurora may be within viewing radius.",
            "When an aurora alert fires, tap the \"Share Kp Status\" button on the notification to send your current Kp reading and visibility status to social media.",
            "Use the Saved Spots section to bookmark specific coordinates and track aurora visibility at those locations — drop a pin on the aurora map, enter coordinates manually, or tap \"Mark My Location\" to use your GPS.",
            "Every page in the Aurora tab features colorful northern lights backgrounds, bright themed text, and an animated twinkling-stars background that doesn't overlap any text, images, or buttons.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Star,
        accent = Color(0xFF4FC3F7),
        title = "Stars & Constellations — Night Sky Guide",
        shortLabel = "Night Sky",
        steps = listOf(
            "From the Aurora Forecaster, tap the \"Night Sky Guide\" card to open the Stars & Constellations landing page.",
            "Four clickable tiles lead to detailed astronomical info: Constellations, Important Stars, Planets, and Deep Sky Objects.",
            "Constellations: browse all 88 IAU constellations organized by hemisphere. Tap any constellation for a programmatic star chart drawn with Canvas, mythology/lore, best viewing season, and its major stars with magnitudes. The 12 most famous constellations include hero images.",
            "Important Stars: explore ~30 notable stars — Sirius, Betelgeuse, Vega, Polaris, Rigel, and more. Tap any star for its spectral class, temperature, luminosity, distance, and visibility info. The 6 most iconic stars include hero images.",
            "Planets: all 8 planets plus dwarf planets (Pluto, Ceres) with diameter, distance from Sun, orbital period, moons, and detail cards showing physical properties, visibility, and notable features. Each planet has a generated image.",
            "Deep Sky Objects: ~40 galaxies, nebulae, and star clusters (Andromeda Galaxy, Orion Nebula, Pleiades, Crab Nebula, and more) with catalog numbers, distances, magnitudes, and observing info. The 8 most famous DSOs include hero images.",
            "Every page in the Night Sky Guide features animated twinkling white stars in the background — purely decorative, they never overlap text, images, or clickable items.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.MenuBook,
        accent = Color(0xFF6FA8C7),
        title = "Field Journal",
        shortLabel = "Field Journal",
        steps = listOf(
            "Tap the \"Field Journal\" banner on the home screen.",
            "Create a new entry for each day in the field — auto-weather, photos, and field notes.",
            "Each entry captures the date, location, weather conditions, and your personal observations.",
            "Tap any entry to view details, edit, or share to your profile feed.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.LocationOn,
        accent = Success,
        title = "Dig Sites, Rock Shops & Locations",
        shortLabel = "Dig Sites",
        steps = listOf(
            "Tap \"Dig Sites & Rock Shops\" on the home screen to browse the full map.",
            "Filter by type: free dig sites, pay-to-dig mines, rock shops, metaphysical shops, and gem & mineral shows.",
            "Enable location monitoring to see nearby hot spots within 100 miles (250 miles with Premium).",
            "Get proximity pings when you're close to a dig site.",
            "Tap any location for details, photos, one-tap Google Maps directions, and gear recommendations.",
            "Open the Gem & Mineral Shows tab from the Dig Sites map to browse recurring gem, mineral, and fossil shows across the US, grouped by upcoming month so you never miss a hunt.",
            "Download cached satellite and street tiles for offline use in no-signal areas — tap the floating Download button on any map, drop a pin, and cache tiles for a 3-mile radius.",
            "Export high-resolution satellite map images to your gallery for offline reference — save ultra high-res images at any pin location.",
            "Use the Park Here button to save your parking spot — satellite tiles are automatically cached around it so you can navigate back to your vehicle with zero signal.",
            "Tap Navigate to Vehicle to get Google Maps directions back (online) or center the map on your parking spot with cached satellite tiles (offline).",
            "The satellite button cycles through 3 zoom levels: first tap zooms to 16 (satellite appears), second tap zooms to 19 (deepest detail), third tap restores your original view.",
            "Every map shows a sync-status pill (top-left) that reports whether the offline tiles for the visible area are up to date, cached, or stale. Tap the pill to refresh the cache for the current area. When offline, it tells you whether the cached tiles are fresh or may be stale so you know if you can trust the map.",
            "Every map can be expanded to fullscreen with a close button in the top-left corner. The My Location button centers the map directly on your current GPS position. If you accidentally drop a pin by tapping the map, a Cancel/Remove Pin button lets you clear it.",
            "Found a dig site not on the map? Use the Add Location form to add it after review.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Search,
        accent = Cyan,
        title = "Search & Discovery",
        shortLabel = "Search",
        steps = listOf(
            "Tap the search icon in the home header.",
            "Search across the entire database — specimens, locations, and educational guides.",
            "Results are grouped by category for quick navigation.",
            "Tap any result to jump straight to the detail page.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Notifications,
        accent = Color(0xFFE2574C),
        title = "Notifications, Weather Alerts & Message Requests",
        shortLabel = "Alerts",
        steps = listOf(
            "The notification bell icon (home screen and profile header) shows your unread count for friend requests and other non-message notifications.",
            "Tap the bell to open the Notification Center. If you have pending friend requests, a summary tile appears at the top — tap it to jump straight to the RockScout Friends screen.",
            "Below the summary tile, regular notifications appear: trade interests, submission approvals, post likes, comments, and more — each with a tappable deep link.",
            "Swipe left on any notification to delete it. An \"Undo\" pill appears for 5 seconds in case you change your mind.",
            "Use the Select checkbox to enter selection mode, then tap \"Delete Selected\" to remove multiple notifications at once.",
            "Enable Severe Weather Alerts in Social Settings to receive instant NWS alerts the moment they're issued for your area — severe thunderstorm, tornado, flash flood, hurricane, tropical storm, tsunami, blizzard, winter storm, ice storm, extreme heat/cold, high wind, dust storm, dense fog, fire weather (Red Flag Warning, Fire Weather Watch), and smoke/air quality alerts.",
            "Weather alerts and Kp/aurora alerts both fire instantly — they're not batched or delayed by the background polling cycle.",
            "The message icon (envelope, next to the bell) is separate from the notification bell. It shows a count for all unread messages and pending message requests. Tap it to open the RockScout Friends screen.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Person,
        accent = Aqua,
        title = "Profile, Achievements & Badges",
        shortLabel = "Profile",
        steps = listOf(
            "Tap your avatar on the home screen to open your Profile.",
            "Your Player Card shows your level, XP progress, hunter status, and earned badges.",
            "Set your hunter status: Off Grid, Hunting, Digging, or Trading — each shows a color-coded profile border.",
            "Browse over 100 achievements and over 30 badges on the Achievements page.",
            "Each locked achievement on the All Achievements page now shows a visual progress bar indicating how close you are to earning it — so you always know what to do next to level up.",
            "Earn XP for every action — identifying rocks, adding to your collection, capturing field photos, trading, and more.",
            "Level up to unlock new tiers with unique emojis and brag-worthy celebration pop-ups.",
            "Share level-up and badge-earn cards to your profile feed or straight to social media.",
            "Customize your profile with a background image and display name.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.FavoriteBorder,
        accent = Color(0xFF9B7BD8),
        title = "Wishlist, Favorite Spots & Aurora Saved Spots",
        shortLabel = "Wishlist",
        steps = listOf(
            "Tap the heart icon on any specimen card to add it to your Wishlist.",
            "View your Wishlist from the home screen — it's your dream-specimen shopping list.",
            "Tap the bookmark icon on any dig site, national or state park, BLM location, campground, trailhead, or dig site detail screen to save it to Favorite Spots for quick access.",
            "Favorite Spots appear on the home screen with a count of saved locations and are fully searchable from the global search.",
            "In the Aurora Forecaster, use the Saved Spots section to bookmark specific coordinates and track aurora visibility at those locations — drop a pin on the map, enter lat/lng manually, or tap \"Mark My Location\" to use your GPS.",
            "Each aurora saved spot shows the name, coordinates, and current visibility status (Aurora visible / unlikely) based on the spot's latitude and current Kp index.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Download,
        accent = Color(0xFF44AACC),
        title = "Saved Images & Photo Interactions (Free)",
        shortLabel = "Saved Images",
        steps = listOf(
            "Tap any photo in the app to view it full-screen.",
            "Long-press any photo to save it to your personal Saved Images folder.",
            "Open \"My Saved Images\" from the home screen to browse, share, or delete saved photos.",
            "Share saved images to your profile feed or straight to social media.",
            "Saved Images is a free feature — it's the destination for field-camera snaps after the trial ends, so your photo log never gets locked behind a paywall.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Terrain,
        accent = Color(0xFFC97B4A),
        title = "BLM Public Lands Guide",
        shortLabel = "BLM Lands",
        steps = listOf(
            "Tap \"BLM Public Lands\" in the Field Kit section on the home screen.",
            "Browse state-by-state rules for rockhounding on Bureau of Land Management land.",
            "Each state page shows trailheads, campgrounds, and dig sites with tappable detail pages.",
            "Check regulations before you hunt — rules vary by state and land type.",
            "Each BLM state guide, trailhead, campground, dig site, and national or state park detail screen includes a \"Common Wildlife\" tile showing the animals you might encounter in that area — mammals, birds, reptiles, and more, tailored to the region's biome.",
            "Beach and coastal dig site detail screens show marine and shorebird wildlife specific to that coast.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFFC0C0C0),
        title = "Meteorite Hunting",
        shortLabel = "Meteorites",
        steps = listOf(
            "Tap \"Finding Meteorites\" in the Field Kit section on the home screen.",
            "Learn how to identify space rocks — fusion crust, magnetic properties, and key visual cues.",
            "Discover where meteorites are most commonly found and how to hunt them.",
            "The Specimen Database also includes meteorite entries with full properties.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Science,
        accent = Color(0xFF7CB5EC),
        title = "Periodic Table of Elements",
        shortLabel = "Periodic Table",
        steps = listOf(
            "Tap \"Periodic Table\" in the Explore & Learn section to explore all 118 elements.",
            "Each element card shows where it appears in rocks and gems and its role in mineral formation.",
            "Tap any element for a detail page with photos and easy-to-understand explanations.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.School,
        accent = Color(0xFFD9B26A),
        title = "Educational Guides",
        shortLabel = "Guides",
        steps = listOf(
            "RockScout includes over 10 built-in educational guides — all work fully offline once the bulk image download completes.",
            "Exploring Geology: learn how rocks, minerals, and gems form across the rock cycle.",
            "Exploring Paleontology: fossils, geologic eras, and deep-time history.",
            "Exploring Prehistoric Organisms: dinosaurs, ancient birds, prehistoric flora, and more.",
            "Tectonics & Volcanoes: plate movement, magma, and where rocks are born.",
            "The Mohs Hardness Scale card on the home screen walks you through scratch testing with infographics, the 10 reference minerals, and field-test steps.",
            "The Aurora Forecaster and Stars & Constellations guide add space weather and astronomy to your educational toolkit — see the dedicated sections above for details.",
            "Each guide features stunning photos and easy-to-understand explanations — no geology degree required.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CollectionsBookmark,
        accent = Color(0xFF44AACC),
        title = "Rocks Are Amazing",
        shortLabel = "Rocks Are Amazing",
        steps = listOf(
            "Tap \"Rocks Are Amazing\" in the Explore & Learn section to open a curated gallery of Earth's most stunning formations.",
            "Swipe through categorized card collections: enhydros, pseudomorphs, petroleum inclusions, fluorescent minerals, optical phenomena, coprolites, copper-inclusion agates, mineral assemblages, and more.",
            "Each card opens a full specimen detail page with photos, properties, and where-to-find info.",
            "Approved user-submitted specimens can land here alongside the Specimen Database — your finds might become a wonder.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.AccountBox,
        accent = Color(0xFFC97B4A),
        title = "Artifacts & Stone Tools",
        shortLabel = "Artifacts",
        steps = listOf(
            "Tap the \"Artifacts\" tile on the home screen to browse a growing catalog of over 100 authentic prehistoric artifacts, each with its own generated reference image on a unique background.",
            "Families include Arrowheads (20+ types from Clovis and Folsom to Mississippian triangle points), Spear Points & Dart Tips (12+ Paleoindian and Archaic forms), Hand Axes & Axe Heads (20+ Acheulean and Neolithic bifaces), Flaked Stone Tools (12+ scrapers, knives, and gravers), Drill Bits (6+ awl and drill forms), Native Beads (13+ shell, stone, and copper beads), Stone Effigies (7+ animal and human effigies from the Woodland and Mississippian periods), Pipes & Medicine Tubes, Ornaments & Weights, Shell Tools, Bone Tools, Pottery, Game Discs, and Wooden Artifacts.",
            "Each artifact card shows a reference image, cultural period, material, and a detailed description of how it was made and used.",
            "The Artifacts tile is linked to the Specimen Database via the ARTIFACTS category chip at the top of the specimen list — tap the chip to filter the full database down to artifacts only. Artifacts never appear inside the main specimen database list; they are only accessible through the Artifacts tile or the category chip.",
            "A NEW badge appears on any artifact added to the catalog within the last 7 days, so you can spot the latest additions at a glance.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFF2EC4B6),
        title = "Natural Wonders of the World",
        shortLabel = "Natural Wonders",
        steps = listOf(
            "Tap the \"Natural Wonders\" tile on the home screen to explore over 35 world-famous geological sites and the rocks and minerals you can find at each one.",
            "Each wonder includes a stunning photo, location, geological formation story, rocks to find, and fun facts — from the Grand Canyon and Giant's Causeway to Mount Vesuvius, Salar de Uyuni, and the Zhangjiajie Pillars.",
            "Recent additions include the Naica Crystal Caves (Chihuahua, Mexico) — the Cave of the Crystals, where giant selenite crystals grow from floor to ceiling, some over 36 feet long — plus Sossusvlei Red Dunes, Þingvellir Rift Valley, Tongariro Volcanic Complex, and more.",
            "Each card opens a full detail page with the formation's geological history, what rocks and minerals are found there, and visitor tips for rockhounds.",
            "Use Natural Wonders as a bucket-list trip planner — many of these sites are dig-friendly destinations you can add to your Trip Planner.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFF7CB5EC),
        title = "Rock & Gem Resources",
        shortLabel = "Resources",
        steps = listOf(
            "Tap \"Rock & Gem Resources\" in the Field Kit section on the home screen to browse trusted external geology, gem, and fossil websites.",
            "Links open in your device's browser so you can dig deeper into any topic.",
            "Curated for rockhounds — museums, university mineralogy departments, and reputable reference sites.",
            "Use it as a research companion alongside the in-app Specimen Database.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Diamond,
        accent = Citrine,
        title = "Gear Guide",
        shortLabel = "Gear Guide",
        steps = listOf(
            "Tap the \"Gear Guide\" banner (right below the Calendar tile) on the home screen to browse over 45 curated tools with Amazon links.",
            "Kits are organized from beginner to advanced — from a first loupe and rock hammer to lapidary equipment and UV lights.",
            "Each item shows a price range and a quick link so you can stock up before your next dig.",
            "As an Amazon Associate, RockScout earns from qualifying purchases — at no extra cost to you.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Group,
        accent = Success,
        title = "Referrals & Community",
        shortLabel = "Referrals",
        steps = listOf(
            "Open the Referral screen from your Profile to get your unique referral link.",
            "Share the link with friends — when they sign up, you both earn tokens and XP.",
            "Watch for referral reward celebration pop-ups when your friends complete sign-up.",
            "The app includes a profanity filter and screenshot-based reporting to keep the community family-friendly.",
            "Use the Report button on any message or profile to flag inappropriate content.",
            "Rate & review RockScout from the home screen to help fellow rockhounders discover the app.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Upload,
        accent = Color(0xFF44AACC),
        title = "Submit Specimens & Add Locations",
        shortLabel = "Submit",
        steps = listOf(
            "Found a specimen that isn't in the database? Use the Upload Specimen pill (found on the Specimen Database screen, the Field Captures screen, and specimen detail pages).",
            "Submit up to 10 photos plus a name, date found, location, and description. Images are automatically checked against a 5 MB size limit — if a photo is too large, you'll get a friendly notification to pick a smaller file, preventing upload failures before they happen.",
            "If the specimen already exists in the database and your location is a common find spot for it, you'll get a small pop-up letting you know it's already included. If the location is unusual for that specimen, it goes to developer review instead.",
            "Found a dig site, rock shop, or gem show that isn't on the map? Use the Add Location form on the Dig Sites map or any map screen.",
            "After review, submitted locations appear on the Dig Sites map for every RockScout user to discover.",
            "Help build the most thorough rock database and the most complete hunting map on the app market!",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Share,
        accent = Aqua,
        title = "Share-a-Spot Deep Links",
        shortLabel = "Share a Spot",
        steps = listOf(
            "Open the Field Captures Specimen Map (the swipeable second page of Field Captures) or the Trip Planner specimen marker map.",
            "Tap any specimen marker pin on the map to open that specimen's detail view.",
            "A new \"Share a Spot\" card appears on the detail page when you arrived from a marker pin. Tap Share a Spot to generate a rockscout://spot deep link encoding the spot's coordinates and specimen name.",
            "The system share sheet opens so you can send the link via Messenger, text, email, or any app you like.",
            "Friends who tap the link on a device with RockScout installed jump straight to a Shared Spot screen showing the spot on a map with a Close button.",
            "This is the easiest way to point fellow rockhounds at an exact find spot — no typing coordinates by hand.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Storage,
        accent = Color(0xFF7CB5EC),
        title = "Storage, Cache & Bulk Offline Download",
        shortLabel = "Storage",
        steps = listOf(
            "Open Social Settings and scroll to the Storage section to choose your cache size.",
            "Standard (150MB) stores recently viewed specimen photos and map tiles — automatically manages itself by removing older items as new ones come in.",
            "Maximum (2GB) raises the ceiling so a bulk download can fit alongside your map tiles. Available to all users — it's your own device storage, not Premium-gated.",
            "The cache size applies to both the image cache (specimen photos, field captures) and the map tile cache (satellite and street tiles).",
            "Want the whole catalog offline? Tap the \"Download all images (~3.5 GB)\" button — found as a small pill in the Specimen Database header AND as a full card next to the 2GB toggle in Storage settings.",
            "The bulk download caches every specimen photo plus all educational/guide illustrations and hero art on-device, so every read-only screen (specimen details, geology guides, BLM guide, periodic table, etc.) loads instantly with zero signal.",
            "Requires the Maximum (2GB) cache mode first — if it's off, the card links you straight to the toggle.",
            "On cellular, you'll get a confirmation dialog showing the ~3.5 GB estimate before anything downloads. On WiFi, it starts immediately.",
            "A live progress bar shows \"X / Y images · Z%\" with a Cancel button. If you cancel or close the app, progress is remembered — re-open and tap \"Resume download\" to pick up where you left off.",
            "When the download finishes, an \"All images saved offline\" banner shows the actual bytes cached, and an offline-ready badge appears in the Specimen Database header so you can see at a glance that the catalog is fully cached.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.MenuBook,
        accent = Color(0xFFD9B26A),
        title = "Tokens, Premium & Donations — What's Free vs Paid",
        shortLabel = "Premium",
        steps = listOf(
            "Free for 7 days: full app access including AI identification (5 tokens to spend at your own pace), RockScout Friends, Trade Board, My Rocks, Wishlist, Field Captures, Trip Planner, and Field Journal.",
            "After the trial, these stay FREE forever: browsing the full specimen database & geology guides, the Field Camera (saves to Saved Images), NWS severe weather alerts, and browsing dig sites & offline maps.",
            "After the trial, these require Premium or a donation: AI identification & ID tokens, RockScout Friends/Messenger/Community, Trade Board & Discover Hunters, My Rocks/Wishlist/Field Captures/Favorite Spots, and Trip Planner/Field Journal/specimen submissions.",
            "Two ways to keep identifying free: watch 2 short rock-related videos to earn 1 ID token (no weekly cap), or make a donation of any amount for tokens plus a temporary full-feature unlock (2 days up to 1 month).",
            "Premium ($5.99/mo) unlocks everything: unlimited AI identifies with all 5 sources (database comparison + Haiku + Sonnet + Gemini 2.5 Pro + web cross-check), ad-free, 250-mile nearby radius, 24-hour pings, premium gem badge, and early access to new features.",
            "The free tier is recommended for everyone. Premium is recommended for users 18+ because it unlocks the social layer (friends, messaging, trade, community). Safety is the first, second, and third rule.",
            "Check your token balance anytime via the Token Bank icon in the home header.",
            "Tap the Token Bank to open the Token Info screen for details and purchasing options.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Star,
        accent = Color(0xFFE8C547),
        title = "NEW Badges & Recently Added Specimens",
        shortLabel = "NEW Badge",
        steps = listOf(
            "A NEW badge automatically appears on any specimen or artifact card that was added to the catalog within the last 7 days.",
            "Spot the latest database additions at a glance — no need to hunt through the full list to find what's new.",
            "The badge works across the Specimen Database, Artifacts tile, and any category-filtered view.",
            "After 7 days, the badge disappears automatically — the specimen stays in the catalog but is no longer flagged as new.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Security,
        accent = Color(0xFF45B56A),
        title = "App Updates, Sign-in & Safety",
        shortLabel = "Updates & Safety",
        steps = listOf(
            "When the app detects a signing conflict during an update (the installed APK was signed with a different key than the new version), a friendly dialog explains that the old version must be uninstalled to update — and offers a button to trigger the system uninstall flow directly.",
            "After reinstalling the new version, just sign back in with your RockScout account. All your settings — hunter status, cache mode, notification preferences, aurora thresholds, and more — are restored from the cloud, exactly as they were before.",
            "A confirmation dialog appears when you tap the logout button in Settings, preventing accidental sign-outs.",
            "Your collections, captures, friends, and achievements are tied to your account, not your device — they carry over to any device when you sign back in.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Mail,
        accent = Color(0xFF4A9FE0),
        title = "Ask an Expert & Museum Finder",
        shortLabel = "Ask an Expert",
        steps = listOf(
            "When an identification result has low confidence or you want a second opinion, tap the \"Ask an Expert\" button on the uncertainty card.",
            "RockScout searches for nearby museums and geological institutions using your current location (or your profile region as a fallback). Each result shows the museum name, distance, phone number, website, and directions.",
            "Tap a museum card to see its details, call them directly, visit their website, or get directions via your maps app.",
            "When you're ready, compose an email to the museum directly from the app — your captured photo and identification results are attached automatically so the expert has everything they need.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Description,
        accent = Color(0xFFE8C547),
        title = "PDF Identification Reports",
        shortLabel = "PDF Reports",
        steps = listOf(
            "After running an identification, a small PDF document icon appears on each match card. Tap it to generate a printable 1–2 page report.",
            "The report includes your captured photo, all match names with confidence scores and reasoning, the AI analysis summary, assemblage breakdown (if applicable), web references, and a field-capture note with the date and approximate location.",
            "The PDF opens in your device's share sheet — email it to a museum expert, save it to your files, or share it with fellow rockhounds.",
            "Reports use the RockScout amber accent and color-coded confidence indicators (green = high, amber = medium, red = low) for quick visual scanning.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Group,
        accent = Color(0xFF45B56A),
        title = "Multi-Recipient Expert Email & Draft Persistence",
        shortLabel = "Multi-Email",
        steps = listOf(
            "In the Museum Finder sheet, tap to select multiple museums at once — each selected museum gets a checkmark and Citrine highlight.",
            "A \"Compose Email (N)\" button appears at the bottom showing how many recipients you've selected. Tap it to open the email composer with all selected museums as recipients.",
            "Recipient chips at the top of the composer show each museum — tap the X on a chip to remove a recipient without leaving the composer.",
            "Your email draft auto-saves as you type — if you close the app mid-composition, you'll get a \"Restore unsaved draft?\" prompt next time with your recipients, photos, and message body intact. Drafts expire after 7 days.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Schedule,
        accent = Color(0xFF2EC4B6),
        title = "Recently Added Filter",
        shortLabel = "Recently Added",
        steps = listOf(
            "On both the Specimen Database and Artifacts screens, a \"Recently Added\" filter chip appears in the filter row.",
            "Tap the chip to narrow the list to only items added within the last 7 days — the same window the NEW badge covers.",
            "The filter composes with existing category, family, and search filters — all filters apply together (AND logic). Tap again to turn it off.",
            "If no items were added in the last 7 days, a friendly empty state appears: \"No specimens added in the last 7 days. Check back soon!\"",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CloudUpload,
        accent = Color(0xFF4A9FE0),
        title = "Settings Cloud Backup",
        shortLabel = "Cloud Backup",
        steps = listOf(
            "RockScout automatically backs up your settings — hunter status, cache mode, notification preferences, aurora thresholds, and more — to the cloud every 12 hours when you're signed in and online.",
            "For a manual backup, go to Profile → Data & Sync and tap \"Back Up Data Now.\" A progress bar shows the backup in progress, and a success message confirms when your data is synced.",
            "If you ever need to reinstall the app (for example, during a signing-conflict update), your settings are restored automatically when you sign back in — everything comes back exactly as it was.",
            "The backup also fires automatically when you background the app, debounced to once per hour to avoid redundant uploads.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Smartphone,
        accent = Color(0xFF45B56A),
        title = "Free Read-Only PWA & Premium PWA Install",
        shortLabel = "Free & Premium PWA",
        steps = listOf(
            "RockScout's web app is available as a PWA (Progressive Web App) for desktop, laptop, and tablet browsers. There are two tiers.",
            "Free tier: Install a read-only PWA on any device — browse the full specimen database of over 900 entries, all educational guides, the interactive map, and your personal bookmarks (Collection, Wishlist, Favorite Spots). No camera, no AI identification, no social features. Perfect for kids and learners.",
            "Premium tier: Install the full PWA with all features unlocked — AI identify, field camera, social, trade, messenger, field journal, trips, and more — on up to 2 additional devices with email-code confirmation. Manage your registered devices from the Manage Devices page.",
            "A free account created on the web carries over to the Android/iOS app. If you upgrade to Premium on any platform, the same account unlocks everywhere — no new login needed.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.NearMe,
        accent = Color(0xFFE8C547),
        title = "Search Near Me — Web Search for Local Rock Spots",
        shortLabel = "Search Near Me",
        steps = listOf(
            "On the home screen's Dig Sites & Rock Shops section, a \"Search Near Me\" button appears next to the nearby locations header.",
            "Tap it to run a web search for rock-related places — dig sites, rock shops, mineral collecting areas, museums, and metaphysical stores — near your current GPS location.",
            "The search starts with a 50-mile radius. If nothing is found, it automatically expands to 100 miles. Results appear inline right where the nearby locations normally show.",
            "Each result shows the name, type badge (Dig Site, Rock Shop, Museum, etc.), description, and an \"Open\" button to visit the source website. Results are also saved for review so approved spots can appear on the Dig Sites map in future updates. Requires Nearby Places to be turned on.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CleaningServices,
        accent = Color(0xFF5CC98C),
        title = "Mineral Care & Cleaning Guide",
        shortLabel = "Mineral Care",
        steps = listOf(
            "Tap \"Mineral Care & Cleaning\" in the Explore & Learn section on the home screen.",
            "Browse safe cleaning methods for every mineral type — water, mild soap, ultrasonic, or \"don't clean at all.\"",
            "Each guide covers what solvents to avoid, how to store delicate specimens, and how to prevent damage.",
            "Don't ruin your finds — a quick check here before cleaning can save a priceless specimen.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.MenuBook,
        accent = Color(0xFF6FA8C7),
        title = "Glossary",
        shortLabel = "Glossary",
        steps = listOf(
            "Tap \"Glossary\" in the Field Kit section on the home screen.",
            "Every rock, mineral, and space term used in the app is defined here in plain English.",
            "Search for a term or browse alphabetically.",
            "Tap any term to see its full definition with related terms.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Category,
        accent = Color(0xFFE5683C),
        title = "Exploring Geology — Rock Info Hub",
        shortLabel = "Rock Info",
        steps = listOf(
            "Tap \"Exploring Geology\" in the Explore & Learn section on the home screen to open the Rock Info hub.",
            "Four tiles lead to the core geology reference guides: Rock Types, Mineral ID, Crystal Systems, and Rock Cycle & Tools.",
            "This is the starting point for understanding how rocks form, how to identify minerals in the field, and how the rock cycle connects everything together.",
            "Each tile opens its own detailed guide — see the individual sections below for Rock Types, Mineral ID Guide, Crystal Systems, and Rock Cycle Tools.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Diamond,
        accent = Color(0xFF6FA8C7),
        title = "Crystal System Reference",
        shortLabel = "Crystal Systems",
        steps = listOf(
            "Tap \"Crystal Systems\" in the Explore & Learn section on the home screen.",
            "Learn the 7 crystal systems — cubic, tetragonal, orthorhombic, monoclinic, triclinic, and hexagonal/trigonal.",
            "Each system includes visual examples of minerals that form in that shape.",
            "Tap any crystal system for detailed info about its axes, angles, and representative minerals.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Lightbulb,
        accent = Color(0xFF9B7BD8),
        title = "Fluorescence & UV Reference",
        shortLabel = "Fluorescence",
        steps = listOf(
            "Tap \"Fluorescence & UV\" in the Explore & Learn section on the home screen.",
            "Discover which minerals glow under UV light and what colors they produce.",
            "Browse by mineral name or by UV wavelength — shortwave, midwave, and longwave.",
            "Essential for night collecting with a UV lamp — some minerals only reveal themselves in the dark.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Construction,
        accent = Color(0xFFE8A33D),
        title = "Lapidary Basics Guide",
        shortLabel = "Lapidary",
        steps = listOf(
            "Tap \"Lapidary Basics\" in the Explore & Learn section on the home screen.",
            "Learn the fundamentals of cutting, polishing, and cabbing your finds into jewelry.",
            "Covers equipment basics — saws, grinders, polishers, and cabochon machines.",
            "Step-by-step guidance from rough rock to finished cabochon.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.WaterDrop,
        accent = Color(0xFF4FC3F7),
        title = "Rock Cycle Tools",
        shortLabel = "Rock Cycle",
        steps = listOf(
            "Tap \"Rock Cycle Tools\" from the Educational Guides section.",
            "Explore the interactive rock cycle — see how igneous, sedimentary, and metamorphic rocks transform into each other.",
            "Tap any stage of the cycle for detailed explanations and examples.",
            "Understand how the rocks you collect got to be the way they are.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Terrain,
        accent = Color(0xFFC97B4A),
        title = "Rock Types Deep Dive",
        shortLabel = "Rock Types",
        steps = listOf(
            "Tap \"Rock Types\" from the Educational Guides section.",
            "Deep dive into the three main rock families: igneous, sedimentary, and metamorphic.",
            "Each type includes formation processes, common examples, and where to find them.",
            "Learn the differences between basalt and granite, shale and sandstone, marble and quartzite.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Search,
        accent = Color(0xFF7CB5EC),
        title = "Mineral ID Guide",
        shortLabel = "Mineral ID",
        steps = listOf(
            "Tap \"Mineral ID Guide\" from the Educational Guides section.",
            "Use the step-by-step identification key to narrow down any mineral.",
            "Tests include hardness, streak, luster, cleavage, and specific gravity.",
            "Each step narrows your options until you reach a positive ID.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFFE2574C),
        title = "Mass Extinctions",
        shortLabel = "Extinctions",
        steps = listOf(
            "Tap \"Mass Extinctions\" from the Educational Guides section.",
            "Learn about the 5 big extinction events that reshaped life on Earth.",
            "Each event includes the timeline, cause, and which species were lost.",
            "Understand the deep-time context of the fossils you find.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Schedule,
        accent = Color(0xFFD9B26A),
        title = "Geo Time Scale",
        shortLabel = "Geo Time Scale",
        steps = listOf(
            "Tap \"Geo Time Scale\" from the Educational Guides section.",
            "Explore an interactive geologic timeline from the Hadean to the present.",
            "Tap any era or period for details about the life, climate, and geology of that time.",
            "See where your fossil finds fit into Earth's 4.6-billion-year story.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CalendarMonth,
        accent = Color(0xFF6FA8C7),
        title = "Geologic Periods",
        shortLabel = "Geologic Periods",
        steps = listOf(
            "Tap \"Geologic Periods\" from the Educational Guides section.",
            "Browse period-by-period through the Paleozoic, Mesozoic, and Cenozoic eras.",
            "Each period includes key events, dominant life forms, and major rock formations.",
            "Tap any period for a detailed breakdown.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Nature,
        accent = Color(0xFF8BBF6A),
        title = "Fossil Types Guide",
        shortLabel = "Fossil Types",
        steps = listOf(
            "Tap \"Fossil Types\" from the Educational Guides section.",
            "Learn the different ways fossils form — permineralization, replacement, carbonization, molds and casts, and more.",
            "Browse examples of each fossil type with photos and descriptions.",
            "Essential for understanding what you're looking at when you find a fossil.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Map,
        accent = Color(0xFFE8A33D),
        title = "Trip Journal — Combined Trip Planner & Field Journal",
        shortLabel = "Trip Journal",
        steps = listOf(
            "Tap the \"Trip Planner & Field Journal\" banner on the home screen to open the combined Trip Journal.",
            "The Trip Journal combines your Trip Planner and Field Journal in one tabbed view — switch between trips and journal entries with the tab selector.",
            "The trips tab shows all your planned and active trips; the journal tab shows your field journal entries.",
            "This is the same data as the standalone Trip Planner and Field Journal — just combined for convenience.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Analytics,
        accent = Color(0xFF7CB5EC),
        title = "Collection Statistics Dashboard",
        shortLabel = "Collection Stats",
        steps = listOf(
            "Open \"My Rocks\" on the home screen and tap the statistics icon to view your Collection Statistics Dashboard.",
            "See charts and stats for your collection — breakdown by category, rarity, location, and more.",
            "Track your collecting progress over time with visual graphs.",
            "A fun way to see what you've been finding and where the gaps are.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Mail,
        accent = Color(0xFF4A9FE0),
        title = "Contact Us",
        shortLabel = "Contact Us",
        steps = listOf(
            "Tap \"Contact Us\" from the home screen tagline section.",
            "Send feedback, report issues, or suggest new features directly to the developer.",
            "Your message goes straight to the RockScout team — no bots, no tickets.",
            "Have a question about a feature or found a bug? This is the place.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFF2EC4B6),
        title = "National & State Parks",
        shortLabel = "Parks",
        steps = listOf(
            "Tap \"National & State Parks\" from the Field Kit section on the home screen.",
            "Browse over 250 national and state parks — including all 62 US National Parks — that are dig-friendly or geologically significant.",
            "Each park shows what rocks and minerals you can find there, plus visitor info and common wildlife.",
            "Tap the bookmark icon on any park to save it to Favorite Spots for quick access.",
        ),
    ),
)

@Composable
fun HowToUseScreen(navController: NavController) {
    BackHandler { navController.popBackStack() }

    var selectedSectionIndex by remember { mutableIntStateOf(-1) }
    val videoPlayerState = LocalVideoPlayerState.current

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Tutorial Video pill button at the very top
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate900.copy(alpha = 0.85f))
                        .glowingBorder(2.dp, Citrine, RoundedCornerShape(16.dp))
                        .clickable { videoPlayerState?.showFullscreen() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = null,
                        tint = Citrine,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "Watch Tutorial Video",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Citrine,
                        ),
                    )
                }
            }
            item {
                // Header row with back button and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Aqua,
                        iconTint = Aqua,
                        size = 44.dp,
                        shadowElevation = 5.dp,
                    )
                    Text(
                        text = "How to Use RockScout",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextHigh,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                // Intro card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Slate800, Slate900),
                            ),
                        )
                        .glowingBorder(2.dp, Aqua.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Welcome to RockScout! This guide walks you through every feature of the app — from AI rock identification to trading, social features, trip planning, and more. Tap any section below to read its step-by-step instructions.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMid,
                            lineHeight = 22.sp,
                        ),
                    )
                }
            }
            // Section pills grid — manual rows to avoid nesting lazy composables
            val chunked = howToSections.chunked(2)
            chunked.forEach { rowSections ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        rowSections.forEachIndexed { colIdx, section ->
                            val globalIdx = howToSections.indexOf(section)
                            HowToPillButton(
                                section = section,
                                index = globalIdx,
                                onClick = { selectedSectionIndex = globalIdx },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        // Fill empty slot if odd number in last row
                        if (rowSections.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Happy Hunting!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Citrine,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "RockScout v${getAppVersion()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextLow.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                )
            }
        }

        // Full-screen section popup dialog
        if (selectedSectionIndex >= 0) {
            HowToSectionDialog(
                section = howToSections[selectedSectionIndex],
                index = selectedSectionIndex,
                onDismiss = { selectedSectionIndex = -1 },
            )
        }
    }
}

@Composable
private fun HowToPillButton(
    section: HowToSection,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Slate800.copy(alpha = 0.85f))
            .glowingBorder(1.5.dp, section.accent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(section.accent.copy(alpha = 0.15f))
                .glowingBorder(1.dp, section.accent.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = section.icon,
                contentDescription = null,
                tint = section.accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = String.format("%02d", index + 1),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextLow.copy(alpha = 0.6f),
            ),
        )
        Text(
            text = section.shortLabel,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextHigh,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HowToSectionDialog(
    section: HowToSection,
    index: Int,
    onDismiss: () -> Unit,
) {
    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            shape = RoundedCornerShape(28.dp),
            color = Slate900,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Header with icon, title, and close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate800)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(section.accent.copy(alpha = 0.15f))
                            .glowingBorder(1.5.dp, section.accent.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = section.accent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Text(
                        text = "${String.format("%02d", index + 1)} · ${section.title}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextHigh,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    SculptedIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        accent = section.accent,
                        iconTint = section.accent,
                        size = 40.dp,
                        shadowElevation = 4.dp,
                    )
                }

                // Scrollable steps
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    section.steps.forEachIndexed { stepIdx, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "${stepIdx + 1}.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = section.accent,
                                ),
                                modifier = Modifier.width(24.dp),
                            )
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextMid,
                                    lineHeight = 22.sp,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun getAppVersion(): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember {
        runCatching {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
        }.getOrDefault("1.1") ?: "1.1"
    }
}
