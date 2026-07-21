package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.launch

/**
 * Current version of the mandatory legal disclaimer. Bump this when clauses
 * change materially — users with an older (or null) accepted version will be
 * re-prompted with the flow on next launch / sign-in.
 */
const val DISCLAIMER_CURRENT_VERSION = "2026-07-20-v1"

/**
 * Returns true if the current user has accepted the latest version of the
 * mandatory disclaimer. Used by [com.rork.rockscout.ui.navigation.AppNavigation]
 * to gate access to the rest of the app until the flow is completed.
 */
fun isDisclaimerAccepted(): Boolean =
    PersistenceManager.loadDisclaimerAcceptedVersion() == DISCLAIMER_CURRENT_VERSION

private data class DisclaimerPage(
    val title: String,
    val icon: Color,
    val sections: List<Pair<String, List<String>>>,
)

// Mirrors the clause text on the website (web/src/content/legal.ts) so the
// public-facing pages and the in-app consent record stay in sync.
private val DISCLAIMER_PAGES: List<DisclaimerPage> = listOf(
    DisclaimerPage(
        title = "Privacy Notice",
        icon = Citrine,
        sections = listOf(
            "Overview" to listOf(
                "RockScout collects only the data needed to run its features. We do not sell your personal information.",
            ),
            "Account information" to listOf(
                "When you sign up, we collect your email address and a display name you choose. Authentication is handled by Supabase Auth, which stores your hashed credentials. Your email is used for account recovery, important service notices, and support replies.",
            ),
            "Camera and photos" to listOf(
                "RockScout uses your device camera to capture images for AI rock & mineral identification, for saving captures to your collection, and for posting to community features. Images submitted for identification are sent to our backend identification service for analysis.",
                "Identification images may be retained to improve model quality unless you request deletion. Captures you save to your collection are stored in your account until you delete them.",
            ),
            "Location" to listOf(
                "With your permission, RockScout uses your device's fine location to show nearby dig sites, gem shows, and other rockhounds on the map, to drop pins on your trip planner, and to surface relevant field guides.",
                "Optional background location and nearby-place alerts allow the app to notify you of interesting locations while you are out in the field. Background location is off by default and only enabled when you explicitly turn it on in settings.",
                "Your coarse location may be shown on the RockScouts proximity map if you opt in. You can switch this off at any time from Social Settings.",
            ),
            "Push notifications" to listOf(
                "With your permission, we send push notifications for friend requests, messages, trade interest, marked-traded updates, location-approval notices, and optional engagement summaries. You can fine-tune which notifications you receive in Settings.",
            ),
            "Subscriptions and billing" to listOf(
                "RockScout offers subscriptions managed by RevenueCat. Payment is processed by Google Play Billing. We receive anonymous purchase state and entitlement information. Your full payment card details never reach RockScout — they stay with Google Play.",
            ),
            "Advertising" to listOf(
                "The free tier of RockScout may be supported by advertising served by Google AdMob. AdMob may collect a device advertising identifier and coarse diagnostic data to serve and measure ads. You can reset the advertising identifier in your device settings at any time.",
            ),
            "User-generated content" to listOf(
                "Posts, comments, direct messages, trade listings, profile details, and images you upload are stored in our Supabase database and storage. You control this content and can delete it from the app.",
                "All images uploaded to community features pass through automated moderation to detect prohibited content (explicit, violent, or illegal imagery) before they are visible to others.",
            ),
            "Crash and diagnostic logs" to listOf(
                "If the app crashes, a crash log may be written to your device's internal storage and, with your opt-in, uploaded to our diagnostic pipeline. These logs may include device model, OS version, app version, and a stack trace — never your specimen photos, location coordinates at rest, or message contents.",
            ),
            "Children" to listOf(
                "RockScout is not intended for children under 13. We do not knowingly collect personal information from children under 13. If you believe a child has provided us personal information, contact support@rockscout.net and we will delete it.",
            ),
            "Your choices and rights" to listOf(
                "You can review or delete your account, collection, captures, posts, and messages from within the app. You can revoke camera, location, and notification permissions from your device settings at any time.",
                "Depending on where you live, you may have rights to access, correct, export, or delete your personal data. To exercise any of these rights, email support@rockscout.net.",
            ),
        ),
    ),
    DisclaimerPage(
        title = "Terms of Service",
        icon = Aqua,
        sections = listOf(
            "Agreement to terms" to listOf(
                "By creating a RockScout account or using any RockScout feature, you agree to these Terms of Service and the RockScout Privacy Policy. If you do not agree, do not create an account or use the app.",
            ),
            "Eligibility" to listOf(
                "You must be at least 13 years old to use RockScout. Users between 13 and 18 must have a parent or guardian's permission. RockScout does not knowingly allow users under 13.",
            ),
            "Your account" to listOf(
                "You are responsible for keeping your account credentials secure and for all activity under your account. Provide accurate information at sign-up and keep it current. You may delete your account at any time from the app.",
            ),
            "Acceptable use" to listOf(
                "You agree not to: (a) post content that is illegal, hateful, threatening, harassing, sexually explicit, or that promotes violence; (b) impersonate another person or entity; (c) spam, scam, or mislead other users; (d) attempt to access another user's account or data; (e) reverse-engineer, scrape, or overload the service; (f) use RockScout for any commercial purpose without our written permission; or (g) violate any applicable law.",
                "RockScout uses automated profanity and image moderation, and we may remove content or restrict accounts that violate these rules.",
            ),
            "User-generated content" to listOf(
                "You retain ownership of content you create in RockScout. By posting, you grant RockScout a worldwide, non-exclusive, royalty-free license to host, display, and process that content within the app for the purpose of operating the service.",
                "You are solely responsible for your content and for ensuring you have the rights to post it. We may remove any content that violates these Terms or that we determine is harmful to the community.",
            ),
            "Subscriptions, renewals, and cancellations" to listOf(
                "RockScout offers auto-renewing subscriptions and one-time in-app purchases. Subscriptions renew automatically through Google Play Billing until you cancel. You can cancel anytime from your Google Play subscriptions settings. Cancellation stops renewal at the end of the current billing period; you keep access until then.",
                "Refunds are governed by Google Play's refund policy. RockScout does not issue refunds directly. Pricing and plan details are shown in the app before any purchase.",
            ),
            "Trade board and peer-to-peer exchanges" to listOf(
                "RockScout offers a trade board where users can list specimens they would like to swap and message each other to arrange exchanges. RockScout is not a party to any trade, does not facilitate payment, does not hold specimens in escrow, and does not guarantee the authenticity, value, or condition of any item.",
                "All trades are strictly between users and at your own risk. See the Trade & Swap Disclaimer and Safety & Meetup Notice before arranging any in-person exchange.",
            ),
            "AI identification" to listOf(
                "RockScout's AI identification feature provides a best-effort suggestion based on the image you submit. Results are not a guarantee of identity, value, or safety. Always confirm identifications with a qualified expert before handling, cutting, or selling any specimen, and never handle specimens you suspect may be toxic, radioactive, or otherwise hazardous based solely on an AI result.",
            ),
            "Disclaimers" to listOf(
                "RockScout is provided \"as is\" and \"as available\" without warranties of any kind, express or implied. We do not warrant that the app will be uninterrupted, error-free, secure, or that identification results are accurate.",
                "Use RockScout at your own risk. You are responsible for your own safety, compliance with local collecting laws, and the condition and authenticity of any specimen you acquire, trade, or handle.",
            ),
            "Limitation of liability" to listOf(
                "To the maximum extent permitted by law, RockScout and its operators are not liable for any indirect, incidental, special, consequential, or punitive damages, or any loss of data, specimens, money, or goodwill, arising out of or related to your use of the app — including any trade, meetup, identification, or collection decision you make based on the app.",
                "Our total liability for any claim arising out of these Terms is limited to the amount you paid us in the 12 months preceding the claim, or \$50, whichever is greater.",
            ),
            "Governing law" to listOf(
                "These Terms are governed by the laws of the United States, without regard to conflict-of-law principles. Disputes will be resolved in the courts located in the United States.",
            ),
        ),
    ),
    DisclaimerPage(
        title = "Community Guidelines",
        icon = Citrine,
        sections = listOf(
            "Our community standard" to listOf(
                "RockScout is a community of rockhounds, collectors, and learners. These guidelines apply to all user-generated content: posts, comments, direct messages, trade listings, profile details, and images. They work alongside our Terms of Service.",
            ),
            "Be respectful and helpful" to listOf(
                "Treat other users with respect. Disagreements about an identification or a trade are fine; personal attacks, slurs, bullying, and harassment are not.",
                "Help newcomers. Share what you know. Credit other collectors and sources for photos and information that are not yours.",
            ),
            "Keep it family-friendly" to listOf(
                "RockScout is open to users 13 and up and is used by collectors of all ages. No nudity, sexual content, graphic violence, or illegal activity. RockScout uses automated image moderation on every upload and a profanity filter on text content.",
                "Content that slips past automation and is reported by users is reviewed and removed if it violates these guidelines.",
            ),
            "No spam, scams, or misleading listings" to listOf(
                "Do not post repetitive, promotional, or off-topic content. Do not misrepresent a specimen's identity, origin, condition, or value in a trade listing or post. Do not use RockScout to coordinate off-platform scams.",
            ),
            "Direct messages" to listOf(
                "Direct messages follow the same rules as public posts. Unsolicited harassment, sales pressure, or abusive messages are not allowed and are reportable. You can block any user from their profile or from a message thread.",
            ),
            "Reporting and enforcement" to listOf(
                "Every post, comment, message, trade listing, and profile has a report option. Reports go to our moderation team and may result in content removal, warnings, temporary suspensions, or permanent bans.",
                "When you accumulate active reports, you will see an in-app warning with the count and a path to appeal. Repeated or serious violations result in escalating suspensions.",
            ),
            "Appeals" to listOf(
                "If you believe a moderation action against you was wrong, use the Contact Us screen in the app (or email support@rockscout.net) to appeal. Include what happened and why you think the action was incorrect. We review appeals and respond within 5 business days.",
            ),
        ),
    ),
    DisclaimerPage(
        title = "Trade & Swap Disclaimer",
        icon = Aqua,
        sections = listOf(
            "Trades are between you and the other user" to listOf(
                "The RockScout trade board is a place to list specimens you would like to swap and to find other collectors interested in trading. Every trade that results is a private arrangement between two users.",
                "RockScout is not a party to any trade. RockScout is not a marketplace, not an auction site, not an escrow service, and not a payment processor.",
            ),
            "No escrow, no guarantee" to listOf(
                "RockScout does not hold specimens, does not verify authenticity or condition, does not mediate disputes, and does not guarantee that either party will follow through on a trade. Any trade you arrange is at your own risk.",
                "If a trade goes wrong, you may report the other user via the app and we may take moderation action under our Community Guidelines, but RockScout cannot recover specimens, money, or time on your behalf.",
            ),
            "No fees from RockScout" to listOf(
                "RockScout does not charge a fee for listing or completing a trade through the trade board. If anyone asks you to pay RockScout to facilitate a trade, that is a scam — report it.",
            ),
            "Verify before you trade" to listOf(
                "Ask for additional photos, ask about provenance, ask about condition, and ask questions before agreeing to a trade. Use the app's AI identification and the specimen database as a starting point, not as proof of identity or value. For high-value specimens, seek a third-party appraisal.",
            ),
            "Shipping versus in-person" to listOf(
                "Some trades are arranged to ship; others are arranged to meet in person. Either way, follow the Safety & Meetup Notice. For shipped trades, agree on shipping method, insurance, and timing in writing inside the app before you send anything.",
            ),
            "Prohibited items" to listOf(
                "Do not list items that are illegal to own, sell, or ship in your jurisdiction — including but not limited to protected fossils, artifacts taken from public or tribal land, conflict minerals, and items that require permits you do not have. You are responsible for knowing and following the law where you collect, own, and trade.",
            ),
        ),
    ),
    DisclaimerPage(
        title = "Safety & Meetup Notice",
        icon = Citrine,
        sections = listOf(
            "In-person meetups carry risk" to listOf(
                "RockScout's trade board, trip planner, and community features can lead to in-person meetings with people you do not know. Meeting a stranger to exchange specimens carries real risk. This notice is here so you can make safe choices.",
            ),
            "Meet in public, well-lit places" to listOf(
                "Always arrange to meet in a busy, public, well-lit location — a coffee shop, a public library, a police station lobby, or a public park during daylight. Many police stations have a designated \"safe exchange zone\". If the other person refuses to meet in public, that is a red flag — do not proceed.",
            ),
            "Bring a friend and tell someone" to listOf(
                "Bring a friend or family member to the meeting if you can. If you cannot, tell someone you trust where you are going, who you are meeting, and when you expect to be back. Share your live location with that person from your phone.",
            ),
            "Do not share your home address" to listOf(
                "Never share your home address, your workplace, or other identifying location details in posts, comments, trade listings, or direct messages. Do not invite strangers to your home and do not agree to meet at a private residence for a first exchange.",
            ),
            "Inspect before you trade" to listOf(
                "Inspect the specimen in person before you hand anything over. Check for damage, repairs, or misrepresentation. If something does not match what was described in the listing, you can walk away — you are not obligated to complete the trade.",
            ),
            "Trust your instincts" to listOf(
                "If a situation feels off — pressure tactics, last-minute location changes, requests for cash beyond the agreed trade, or anything that makes you uncomfortable — leave. You can report the user in the app afterwards.",
            ),
            "Minors" to listOf(
                "RockScout is for users 13 and older, but minors should not arrange in-person meetups without a parent or guardian's permission and presence. If you are a minor, do not agree to meet a stranger from RockScout without your parent or guardian involved.",
                "Adults: never arrange to meet a minor one-on-one. If you discover the other user is a minor, end the conversation and do not proceed with a meetup.",
            ),
            "If something goes wrong" to listOf(
                "If you are the victim of theft, fraud, assault, or any crime during a meetup, contact local emergency services first, then report the user in the app and email support@rockscout.net. We will cooperate with law enforcement as required.",
            ),
        ),
    ),
)

/**
 * Mandatory swipe-through legal disclaimer flow.
 *
 * @param isGate When true (after sign-up / when acceptance is stale), the flow
 *  is not dismissable — no back gesture, no system back, no swipe-down. Only the
 *  in-screen buttons advance or exit. When false (review mode from the Home
 *  pill button), a back arrow is shown and the Decline button is hidden.
 */
@Composable
fun DisclaimerScreen(
    navController: NavController,
    isGate: Boolean = false,
    onAccepted: () -> Unit = {},
) {
    val pageCount = DISCLAIMER_PAGES.size
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }
    val scope = rememberCoroutineScope()

    // Block system back in gate mode — the user must accept or decline via the
    // on-screen buttons, not by gesturing out of the flow.
    BackHandler(enabled = isGate) { /* swallow */ }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Slate900,
                        0.6f to Slate800,
                        1.0f to Obsidian,
                    ),
                ),
            )
            .statusBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                if (!isGate) {
                    androidx.compose.material3.IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextHigh,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isGate) "Before you begin" else "Legal & Disclaimers",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextHigh,
                        ),
                    )
                    Text(
                        text = "Swipe to review · ${pagerState.currentPage + 1} of $pageCount",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextLow),
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
            }

            // Pager — one full-page legal document per page, scrollable inside.
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true,
            ) { pageIndex ->
                val page = DISCLAIMER_PAGES[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(page.icon.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = null,
                                tint = page.icon,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextHigh,
                            ),
                        )
                    }
                    page.sections.forEachIndexed { index, (heading, paragraphs) ->
                        Text(
                            text = "${index + 1}. $heading",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Citrine,
                            ),
                            modifier = Modifier.padding(top = if (index == 0) 0.dp else 18.dp, bottom = 8.dp),
                        )
                        paragraphs.forEach { p ->
                            Text(
                                text = p,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextMid,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.25f,
                                ),
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Page indicator dots
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                repeat(pageCount) { i ->
                    val selected = i == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (selected) Citrine else TextLow.copy(alpha = 0.35f),
                            ),
                    )
                }
            }

            // Action buttons — only on the last page.
            if (pagerState.currentPage == pageCount - 1) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                ) {
                    if (isGate) {
                        // Decline — outlined, signs the user out and returns to sign-in.
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(Slate800)
                                .clickable {
                                    scope.launch {
                                        // Sign the user out. The auth gate in
                                        // AppNavigation observes sessionStatus and
                                        // will show the SignInScreen on its own —
                                        // no navigation call needed (and none is
                                        // possible: the NavHost isn't composed
                                        // while the disclaimer gate is showing).
                                        AuthRepository.instance.signOut()
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = Danger, modifier = Modifier.size(18.dp))
                                Text("Decline", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextHigh))
                            }
                        }
                    } else {
                        // Review mode — back button instead of decline.
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(Slate800)
                                .clickable { navController.popBackStack() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Close", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextHigh))
                        }
                    }
                    // Accept — Citrine filled.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Brush.horizontalGradient(listOf(Citrine, CitrineDeep)))
                            .clickable {
                                PersistenceManager.saveDisclaimerAcceptedVersion(DISCLAIMER_CURRENT_VERSION)
                                if (isGate) {
                                    // Don't navigate — the NavHost isn't composed
                                    // while the disclaimer gate overlay is showing.
                                    // Flip the gate state so AppNavigation
                                    // recomposes and drops the overlay, revealing
                                    // the NavHost with HOME as its start.
                                    onAccepted()
                                } else {
                                    navController.popBackStack()
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Ink, modifier = Modifier.size(18.dp))
                            Text(
                                "I Accept",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Ink),
                            )
                        }
                    }
                }
            } else {
                // Mid-flow: gentle hint to swipe forward. No accept/decline available.
                Text(
                    text = "Swipe to continue →",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextLow),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .navigationBarsPadding(),
                )
            }
        }
    }
}
