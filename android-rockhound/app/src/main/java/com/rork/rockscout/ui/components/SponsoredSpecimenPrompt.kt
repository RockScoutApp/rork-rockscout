package com.rork.rockscout.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.LocationType
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import kotlinx.coroutines.delay

/** 72 hours in milliseconds. */
private const val COOLDOWN_MS = 72L * 60 * 60 * 1000

/**
 * A sponsored specimen card derived from a nearby shop's inventory.
 *
 * Each card shows the specimen's first photo (or a glyph fallback), name,
 * shop attribution, and a tap-through to the shop's website (or a search
 * URL if the shop has no website). The sponsorship is transparent — the
 * shop name is always visible.
 */
private data class SponsoredSpecimen(
    val specimen: Specimen,
    val shop: DigLocation,
    val linkUrl: String,
)

/**
 * Finds 2–4 sponsored specimens from nearby rock/metaphysical/lapidary shops.
 *
 * Strategy:
 * 1. Filter to shops within 50 miles of the user.
 * 2. If there are none within 50 miles, use the single nearest shop outside that radius.
 * 3. Within the chosen set, wishlist overlap breaks ties.
 *
 * Returns at most 4 specimens, each from a different shop where possible.
 */
private fun findSponsoredSpecimens(
    userLat: Double,
    userLng: Double,
    wishlistIds: List<String>,
): List<SponsoredSpecimen> {
    val shops = SeedData.allLocations.filter {
        it.type == LocationType.ROCK_SHOP || it.type == LocationType.METAPHYSICAL
    }

    val scored = shops.map { shop ->
        val dist = AppRepository.distanceMiles(userLat, userLng, shop.latitude, shop.longitude)
        val overlap = shop.knownFor.count { id -> wishlistIds.contains(id) }
        ScoredShop(shop, dist, overlap)
    }

    // Prefer shops within the 50 mi radius; if none, take the nearest shop outside it.
    val nearby = scored.filter { it.distance <= 50.0 }
    val candidates = if (nearby.isNotEmpty()) nearby else listOf(scored.minByOrNull { it.distance } ?: return emptyList())

    val sorted = candidates
        .sortedWith(compareByDescending<ScoredShop> { it.wishlistOverlap }.thenBy { it.distance })

    val result = mutableListOf<SponsoredSpecimen>()
    val usedShops = mutableSetOf<String>()
    val usedSpecimens = mutableSetOf<String>()

    for (entry in sorted) {
        if (result.size >= 4) break
        val shop = entry.shop
        if (usedShops.contains(shop.id)) continue

        // Pick the first knownFor specimen that resolves and hasn't been used.
        for (specimenId in shop.knownFor) {
            if (usedSpecimens.contains(specimenId)) continue
            val specimen = SeedData.specimenById(specimenId) ?: continue
            val link = shop.website ?: buildSearchUrl(shop, specimen)
            result.add(SponsoredSpecimen(specimen, shop, link))
            usedShops.add(shop.id)
            usedSpecimens.add(specimenId)
            break
        }
    }

    return result
}

private data class ScoredShop(
    val shop: DigLocation,
    val distance: Double,
    val wishlistOverlap: Int,
)

/** Builds a Google search URL for the shop + specimen as a fallback link. */
private fun buildSearchUrl(shop: DigLocation, specimen: Specimen): String {
    val query = "${shop.name} ${specimen.name} rock shop"
    return "https://www.google.com/search?q=${Uri.encode(query)}"
}

/**
 * The opt-in sponsored specimen prompt for paying users (Premium / Pro).
 *
 * Fires once per 72-hour window on the first open of the Dig Sites or
 * Favorite Spots tab. Shows a small, non-interstitial card asking if the
 * user wants to see specimens from nearby shops. On accept, reveals a
 * horizontal carousel of 2–4 shop-attributed specimen cards with
 * tap-through to the shop listing.
 *
 * Free users never see this — they get the Specimen of the Week card and
 * sponsored map pins instead.
 *
 * @param isPayingUser true when the user has Premium or Pro active.
 * @param userLat coarse latitude for proximity matching.
 * @param userLng coarse longitude for proximity matching.
 * @param wishlistIds the user's wishlist specimen IDs for interest matching.
 * @param modifier optional layout modifier.
 */
@Composable
fun SponsoredSpecimenPrompt(
    isPayingUser: Boolean,
    userLat: Double,
    userLng: Double,
    wishlistIds: List<String>,
    modifier: Modifier = Modifier,
) {
    if (!isPayingUser) return

    val context = LocalContext.current
    var showPrompt by remember { mutableStateOf(false) }
    var showCarousel by remember { mutableStateOf(false) }
    var specimens by remember { mutableStateOf<List<SponsoredSpecimen>>(emptyList()) }

    // Check cooldown on first composition — only show if 72h have elapsed.
    LaunchedEffect(Unit) {
        val lastShown = PersistenceManager.loadLastSponsoredPromptAt()
        val now = System.currentTimeMillis()
        if (now - lastShown >= COOLDOWN_MS) {
            // Pre-compute specimens so the carousel is instant on accept.
            specimens = findSponsoredSpecimens(userLat, userLng, wishlistIds)
            if (specimens.isNotEmpty()) {
                showPrompt = true
            }
        }
    }

    // Prompt card
    AnimatedVisibility(
        visible = showPrompt && !showCarousel,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier,
    ) {
        PromptCard(
            onAccept = {
                showCarousel = true
            },
            onDismiss = {
                showPrompt = false
                PersistenceManager.saveLastSponsoredPromptAt(System.currentTimeMillis())
            },
        )
    }

    // Specimen carousel
    AnimatedVisibility(
        visible = showCarousel,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier,
    ) {
        CarouselContent(
            specimens = specimens,
            onOpenLink = { url ->
                SafeLinkOpener.openUrl(context, url)
            },
            onClose = {
                showCarousel = false
                showPrompt = false
                PersistenceManager.saveLastSponsoredPromptAt(System.currentTimeMillis())
            },
        )
    }
}

@Composable
private fun PromptCard(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = shape, accent = Amethyst, shadowElevation = 5.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(3.dp, Amethyst.copy(alpha = 0.55f), shape)
            .padding(18.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Amethyst.copy(alpha = 0.30f))
                        .glowingBorder(2.dp, Amethyst.copy(alpha = 0.55f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Storefront,
                        contentDescription = null,
                        tint = DarkTextHigh,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nearby shops have specimens for you",
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "A couple of shops near you have specimens they think you'd like to see. Want to take a look?",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SculptedOutlinedButton(
                    text = "Not today",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(44.dp),
                    accent = Aqua,
                    textColor = DarkTextMid,
                )
                SculptedButton(
                    text = "Why not!",
                    onClick = onAccept,
                    modifier = Modifier.weight(1f).height(44.dp),
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = DarkTextHigh,
                )
            }
        }
    }
}

@Composable
private fun CarouselContent(
    specimens: List<SponsoredSpecimen>,
    onOpenLink: (String) -> Unit,
    onClose: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16))
                )
            )
            .glowingBorder(3.dp, Amethyst.copy(alpha = 0.55f), shape)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Storefront,
                contentDescription = null,
                tint = Amethyst,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Specimens from nearby shops",
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .sculpted(
                        shape = RoundedCornerShape(8.dp),
                        accent = Citrine,
                        shadowElevation = 2.dp,
                        circular = true,
                        onClick = onClose,
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = DarkTextMid,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
        ) {
            items(specimens) { item ->
                SpecimenShopCard(
                    specimen = item.specimen,
                    shopName = item.shop.name,
                    imageUrl = item.specimen.imageUrls.firstOrNull(),
                    onClick = { onOpenLink(item.linkUrl) },
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Links will open separately in your browser",
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun SpecimenShopCard(
    specimen: Specimen,
    shopName: String,
    imageUrl: String?,
    onClick: () -> Unit,
) {
    val cardShape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .width(160.dp)
            .sculpted(shape = cardShape, accent = Amethyst, shadowElevation = 4.dp, onClick = onClick)
            .clip(cardShape)
            .background(Color(0xFF12110D))
            .glowingBorder(2.dp, Amethyst.copy(alpha = 0.40f), cardShape)
            .padding(10.dp),
    ) {
        // Specimen photo or glyph fallback
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Amethyst.copy(alpha = 0.20f), Color(0xFF1E1C16))
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = specimen.name,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                )
            } else {
                Text(
                    text = specimen.emoji,
                    style = MaterialTheme.typography.displaySmall,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = specimen.name,
            style = MaterialTheme.typography.titleSmall,
            color = DarkTextHigh,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Storefront,
                contentDescription = null,
                tint = Amethyst.copy(alpha = 0.80f),
                modifier = Modifier.size(12.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = shopName,
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextMid,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "View listing",
                style = MaterialTheme.typography.labelMedium,
                color = Amethyst,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = null,
                tint = Amethyst,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
