package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border as foundationBorder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedIconButton
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

/** A single how-to section with icon, title, and step-by-step instructions. */
private data class HowToSection(
    val icon: ImageVector,
    val accent: Color,
    val title: String,
    val steps: List<String>,
)

/** All how-to sections — covers every major feature in RockScout. */
private val howToSections: List<HowToSection> = listOf(
    HowToSection(
        icon = Icons.Filled.CameraAlt,
        accent = Citrine,
        title = "AI Rock Identification",
        steps = listOf(
            "Tap the big \"Identify a Rock\" hero banner on the home screen.",
            "Choose a photo from your gallery or snap one with your camera.",
            "Hit \"Identify\" — three AI models (Claude Haiku, Claude Sonnet, and Gemini 2.5 Pro) will tag-team the analysis.",
            "The AI then performs a visual reference comparison — it actually SEES the database reference images alongside your photo and ranks the top matches visually (not just from text descriptions).",
            "If the visual match is highly confident (92%+), results return instantly — no need for the full pipeline.",
            "Otherwise, the app runs a web search cross-check for maximum accuracy.",
            "If the AI needs more information to narrow down the ID, it asks you a few clarification questions (e.g. hardness, streak color, or environment) — answer them and the final result re-ranks the candidates for higher accuracy.",
            "If your rock has multiple minerals, the AI auto-detects the assemblage and breaks down each component.",
            "Results show the specimen name, confidence level, properties, and where to find more. A \"verified against database images\" badge appears when the visual reference step was used.",
            "Free users get a 7-day trial with 5 tokens. After that, tokens or a Premium subscription are needed.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CollectionsBookmark,
        accent = Color(0xFF6FA8C7),
        title = "My Collection & Specimen Cards",
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
        steps = listOf(
            "Tap \"Field Captures\" on the home screen.",
            "Log photos of rocks you find in the field — no identification needed, just a visual record.",
            "Each capture stores the photo, date, and optional notes.",
            "Tap any capture to view it full-screen, long-press to save it to your Saved Images folder.",
            "Use the separate \"Field Camera\" tile on the home screen to snap a photo without running the ID tool at all — perfect when you just want to grab a shot.",
            "The Field Camera stays free after the trial ends — snaps save to your in-app Saved Images folder at no cost. (Saving to My Rocks, Wishlist, or other collection destinations still requires Premium or a donation.)",
            "After snapping with the Field Camera, pick a destination from the \"Save to…\" dropdown: Field Captures, Saved Images, My Rocks, My Wishlist, Field Journal Entry, Share to Profile, Change Profile Background, or Submit a Specimen.",
            "The save form lets you add a name, location, and description, plus drop a pin on a map to mark the exact find spot — the pin's coordinates are stored with the capture card.",
            "Field Captures has a second swipeable page: a full-page Specimen Map showing every capture that has a pin. Tap any pin for a read-only view of that capture card. Swipe the top pills or the screen to switch between the captures list and the specimen map.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.SwapHoriz,
        accent = Color(0xFFE8A33D),
        title = "Trade Board",
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
        steps = listOf(
            "Tap the \"Trip Planner\" banner on the home screen.",
            "Create a multi-stop hunt route by adding dig sites, rock shops, or custom pins.",
            "Drop custom pins for gas, food, or any point of interest along the way.",
            "Flag interesting pins as potential rock locations for the developer to review and add to the database.",
            "Use \"Download Maps for All Stops\" to cache offline satellite tiles and high-res images for every stop on your trip at once — perfect for backcountry trips with no signal.",
            "Use \"Cache Trip Area\" on any trip to save satellite tiles for a 3-mile radius around each stop (zoom 10–19) plus a 1-mile radius around every pinned specimen marker (zoom 14–19), so your route AND your find spots work with zero signal. The cache status persists across app restarts, and tiles are freed automatically when you archive or delete a trip.",
            "A sync-status pill on every trip map shows whether your offline tiles are up to date: green \"Up to date\" (cached in the last 24h), aqua \"Cached\" (within 7 days), or warning \"Stale • tap to refresh\" (older than 7 days). Tap the pill to re-cache the trip area. When you're offline, the pill reports whether the cached tiles are fresh or may be stale.",
            "Add a gear checklist so you never forget your hammer, loupe, or UV light.",
            "When editing a trip, drop specimen marker pins on the specimen marker map to mark exactly where you found each rock — pins persist with the trip, appear on the route map, and are included when you cache the trip area for offline use. Use Set Pin / Remove Pin with confirmation popups.",
            "Expand the trip route map to fullscreen and tap anywhere to drop a pin. Two pill buttons appear: \"Add to Route\" adds the pin as a stop (auto-approving rock-related locations via web scan), and \"Submit a New Location\" opens the full location submission form pre-filled with the pinned coordinates.",
            "Share your trip plan with friends via the share button.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.MenuBook,
        accent = Color(0xFF6FA8C7),
        title = "Field Journal",
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
            "Found a great dig site not on the map? Use the Submit Location form to add it after review.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Search,
        accent = Cyan,
        title = "Search & Discovery",
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
        steps = listOf(
            "The notification bell icon (home screen and profile header) shows your unread count for friend requests and other non-message notifications.",
            "Tap the bell to open the Notification Center. If you have pending friend requests, a summary tile appears at the top — tap it to jump straight to the RockScout Friends screen.",
            "Below the summary tile, regular notifications appear: trade interests, submission approvals, post likes, comments, and more — each with a tappable deep link.",
            "Swipe left on any notification to delete it. An \"Undo\" pill appears for 5 seconds in case you change your mind.",
            "Use the Select checkbox to enter selection mode, then tap \"Delete Selected\" to remove multiple notifications at once.",
            "Enable Severe Weather Alerts in Social Settings to receive instant NWS alerts for your area — severe thunderstorm, tornado, flash flood, hurricane, tropical storm, tsunami, blizzard, winter storm, ice storm, extreme heat/cold, high wind, dust storm, dense fog, fire weather (Red Flag Warning, Fire Weather Watch), and smoke/air quality alerts.",
            "Weather alerts monitor your location for weather only — no need to enable location monitoring.",
            "The message icon (envelope, next to the bell) is separate from the notification bell. It shows a count for all unread messages and pending message requests. Tap it to open the RockScout Friends screen.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Person,
        accent = Aqua,
        title = "Profile, Achievements & Badges",
        steps = listOf(
            "Tap your avatar on the home screen to open your Profile.",
            "Your Player Card shows your level, XP progress, hunter status, and earned badges.",
            "Set your hunter status: Off Grid, Hunting, Digging, or Trading — each shows a color-coded profile border.",
            "Browse 100 achievements and 30 badges on the Achievements page.",
            "Earn XP for every action — identifying rocks, adding to your collection, capturing field photos, trading, and more.",
            "Level up to unlock new tiers with unique emojis and brag-worthy celebration pop-ups.",
            "Share level-up and badge-earn cards to your profile feed or straight to social media.",
            "Customize your profile with a background image and display name.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.FavoriteBorder,
        accent = Color(0xFF9B7BD8),
        title = "Wishlist & Favorite Spots",
        steps = listOf(
            "Tap the heart icon on any specimen card to add it to your Wishlist.",
            "View your Wishlist from the home screen — it's your dream-specimen shopping list.",
            "Tap the bookmark icon on any dig site or location to save it to Favorite Spots for quick access.",
            "Favorite Spots appear on the home screen with a count of saved locations.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Download,
        accent = Color(0xFF44AACC),
        title = "Saved Images & Photo Interactions (Free)",
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
        steps = listOf(
            "Tap \"BLM Public Lands\" in the Explore & Learn section.",
            "Browse state-by-state rules for rockhounding on Bureau of Land Management land.",
            "Each state page shows trailheads, campgrounds, and dig sites with tappable detail pages.",
            "Check regulations before you hunt — rules vary by state and land type.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFFC0C0C0),
        title = "Meteorite Hunting",
        steps = listOf(
            "Tap \"Finding Meteorites\" in the Explore & Learn section.",
            "Learn how to identify space rocks — fusion crust, magnetic properties, and key visual cues.",
            "Discover where meteorites are most commonly found and how to hunt them.",
            "The Specimen Database also includes meteorite entries with full properties.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Science,
        accent = Color(0xFF7CB5EC),
        title = "Periodic Table of Elements",
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
        steps = listOf(
            "RockScout includes 10 built-in educational guides — all work fully offline once the bulk image download completes.",
            "Exploring Geology: learn how rocks, minerals, and gems form across the rock cycle.",
            "Exploring Paleontology: fossils, geologic eras, and deep-time history.",
            "Exploring Prehistoric Organisms: dinosaurs, ancient birds, prehistoric flora, and more.",
            "Tectonics & Volcanoes: plate movement, magma, and where rocks are born.",
            "The Mohs Hardness Scale card on the home screen walks you through scratch testing with infographics, the 10 reference minerals, and field-test steps.",
            "Each guide features stunning photos and easy-to-understand explanations — no geology degree required.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.CollectionsBookmark,
        accent = Color(0xFF44AACC),
        title = "Rocks Are Amazing",
        steps = listOf(
            "Tap \"Rocks Are Amazing\" in the Explore & Learn section to open a curated gallery of Earth's most stunning formations.",
            "Swipe through categorized card collections: enhydros, pseudomorphs, petroleum inclusions, fluorescent minerals, optical phenomena, coprolites, copper-inclusion agates, mineral assemblages, and more.",
            "Each card opens a full specimen detail page with photos, properties, and where-to-find info.",
            "Approved user-submitted specimens can land here alongside the Specimen Database — your finds might become a wonder.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Public,
        accent = Color(0xFF7CB5EC),
        title = "Rock & Gem Resources",
        steps = listOf(
            "Tap \"Rock & Gem Resources\" in the Explore & Learn section to browse trusted external geology, gem, and fossil websites.",
            "Links open in your device's browser so you can dig deeper into any topic.",
            "Curated for rockhounds — museums, university mineralogy departments, and reputable reference sites.",
            "Use it as a research companion alongside the in-app Specimen Database.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Diamond,
        accent = Citrine,
        title = "Gear Guide",
        steps = listOf(
            "Tap the \"Gear Guide\" banner on the home screen to browse 40+ curated tools with Amazon links.",
            "Kits are organized from beginner to advanced — from a first loupe and rock hammer to lapidary equipment and UV lights.",
            "Each item shows a price range and a quick link so you can stock up before your next dig.",
            "As an Amazon Associate, RockScout earns from qualifying purchases — at no extra cost to you.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Group,
        accent = Success,
        title = "Referrals & Community",
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
        icon = Icons.Filled.CollectionsBookmark,
        accent = Color(0xFF44AACC),
        title = "Submit Specimens & Add Locations",
        steps = listOf(
            "Found a specimen that isn't in the database? Use the Submit Specimen button (found on specimen detail pages and the database screen).",
            "Submit up to 4 photos plus any info you have — after review, it gets added to the Specimen Database or Rocks Are Amazing collection.",
            "Found a great dig site, rock shop, or gem show that isn't on the map? Use the Submit Location / Add Location form on the Dig Sites map or any map screen.",
            "After review, submitted locations appear on the Dig Sites map for every RockScout user to discover.",
            "Help build the most thorough rock database and the most complete hunting map on the app market!",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.Download,
        accent = Color(0xFF7CB5EC),
        title = "Storage, Cache & Bulk Offline Download",
        steps = listOf(
            "Open Social Settings and scroll to the Storage section to choose your cache size.",
            "Standard (150MB) stores recently viewed specimen photos and map tiles — automatically manages itself by removing older items as new ones come in.",
            "Maximum (2GB) raises the ceiling so a bulk download can fit alongside your map tiles. Available to all users — it's your own device storage, not Premium-gated.",
            "The cache size applies to both the image cache (specimen photos, field captures) and the map tile cache (satellite and street tiles).",
            "Want the whole catalog offline? Tap the \"Download all images (~4 GB)\" button — found as a small pill in the Specimen Database header AND as a full card next to the 2GB toggle in Storage settings.",
            "The bulk download caches every specimen photo plus all educational/guide illustrations and hero art on-device, so every read-only screen (specimen details, geology guides, BLM guide, periodic table, etc.) loads instantly with zero signal.",
            "Requires the Maximum (2GB) cache mode first — if it's off, the card links you straight to the toggle.",
            "On cellular, you'll get a confirmation dialog showing the ~4 GB estimate before anything downloads. On WiFi, it starts immediately.",
            "A live progress bar shows \"X / Y images · Z%\" with a Cancel button. If you cancel or close the app, progress is remembered — re-open and tap \"Resume download\" to pick up where you left off.",
            "When the download finishes, an \"All images saved offline\" banner shows the actual bytes cached, and an offline-ready badge appears in the Specimen Database header so you can see at a glance that the catalog is fully cached.",
        ),
    ),
    HowToSection(
        icon = Icons.Filled.MenuBook,
        accent = Color(0xFFD9B26A),
        title = "Tokens, Premium & Donations — What's Free vs Paid",
        steps = listOf(
            "Free for 7 days: full app access including AI identification (5 tokens to spend at your own pace), RockScout Friends, Trade Board, My Rocks, Wishlist, Field Captures, Trip Planner, and Field Journal.",
            "After the trial, these stay FREE forever: browsing the full specimen database & geology guides, the Field Camera (saves to Saved Images), NWS severe weather alerts, and browsing dig sites & offline maps.",
            "After the trial, these require Premium or a donation: AI identification & ID tokens, RockScout Friends/Messenger/Community, Trade Board & Discover Hunters, My Rocks/Wishlist/Field Captures/Favorite Spots, and Trip Planner/Field Journal/specimen submissions.",
            "Two ways to keep identifying free: watch 2 short rock-related videos to earn 1 ID token (no weekly cap), or make a donation of any amount for tokens plus a temporary full-feature unlock (2 days up to 1 month).",
            "Premium ($9.99/mo) unlocks everything: unlimited AI identifies with all 3 models (Haiku + Sonnet + Gemini 2.5 Pro tie-breaker), ad-free, 250-mile nearby radius, 24-hour pings, premium gem badge, and early access to new features.",
            "Check your token balance anytime via the Token Bank icon in the home header.",
            "Tap the Token Bank to open the Token Info screen for details and purchasing options.",
        ),
    ),
)

@Composable
fun HowToUseScreen(navController: NavController) {
    BackHandler { navController.popBackStack() }

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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
                        text = "Welcome to RockScout! This guide walks you through every feature of the app — from AI rock identification to trading, social features, trip planning, and more. Scroll through to learn how to get the most out of your rockhounding adventures.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMid,
                            lineHeight = 22.sp,
                        ),
                    )
                }
            }
            items(howToSections) { section ->
                HowToSectionCard(section = section)
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
    }
}

@Composable
private fun HowToSectionCard(section: HowToSection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Slate800.copy(alpha = 0.85f), Slate900.copy(alpha = 0.95f)),
                ),
            )
            .glowingBorder(2.dp, section.accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Title row with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                text = section.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextHigh,
                ),
            )
        }
        // Steps
        section.steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = section.accent,
                    ),
                    modifier = Modifier.width(20.dp),
                )
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMid,
                        lineHeight = 19.sp,
                    ),
                    modifier = Modifier.weight(1f),
                )
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
