package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.RockClass
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.Igneous
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900

/**
 * Mock-up screen for the redesigned specimen card.
 * Shows the new layout with a tall image, type pill, two famous-location pills,
 * a colored rarity pill, a 3-line description, and heart/add buttons on the right edge.
 */
@Composable
fun CardMockupScreen(
    navController: NavController,
) {
    val specimens = remember { mockupSpecimens() }

    RockBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Aqua,
                            shadowElevation = 4.dp,
                            circular = true,
                            onClick = { navController.popBackStack() },
                        )
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkTextHigh,
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Card Mock-Up",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Aqua,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(specimens, key = { it.id }) { spec ->
                    NewSpecimenCardMock(specimen = spec)
                }
            }
        }
    }
}

/**
 * New specimen card matching the approved sketch (revised layout):
 * - Image on the far left, taller and wider (bottom edge fixed, grows up/right).
 * - Next to the image (vertical stack, same height as image):
 *     Row 1–2: specimen name (up to 2 lines, larger bold text)
 *     Type pill (rock-class emoji + label)
 *     Two famous-location pills in one horizontal row
 *     Rarity pill — full rarity word, colored by tier, pinned to the bottom
 *     so its bottom edge is even with the bottom of the image.
 * - Below the image row: larger rock-class-colored tagline (up to 3 lines,
 *   vertically centered when shorter) + heart/add buttons on the right edge.
 */
@Composable
private fun NewSpecimenCardMock(
    specimen: Specimen,
) {
    val accent = rockClassAccent(specimen.rockClass)
    val taglineColor = brightenText(accent, 0.45f)
    var liked by remember { mutableStateOf(false) }
    var added by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(20.dp)
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                    )
                )
                .glowingBorder(3.dp, accent.copy(alpha = 0.50f), shape),
        ) {
            // ── Image (left) + 5 evenly-spaced rows (right) ──
            // The image sets a fixed height; the right column fills that height
            // and is split into 5 equal-weight rows so top/bottom align to the
            // image edges and rows stay evenly spaced regardless of name length.
            val imageWidth = 160.dp
            val imageHeight = 210.dp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Tall image — fixed size, bottom edge stays anchored
                Box(
                    modifier = Modifier
                        .width(imageWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(accent.copy(alpha = 0.28f), Color(0xFF1A1812))
                            )
                        )
                        .glowingBorder(2.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    val img = specimen.imageUrls.firstOrNull()
                    if (img != null) {
                        AsyncImage(
                            model = img,
                            contentDescription = specimen.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Text(
                            text = specimen.emoji,
                            style = MaterialTheme.typography.displaySmall,
                        )
                    }
                }

                // Right column: 5 evenly-spaced rows, each weight(1f),
                // content vertically centered within its row.
                // Row 1: name line 1
                // Row 2: name line 2 (blank if name fits on one line)
                // Row 3: type pill
                // Row 4: two location pills
                // Row 5: rarity pill (aligned with image bottom)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    // Row 1 — name line 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = specimen.name.lineOne(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            lineHeight = 22.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // Row 2 — name line 2 (blank if name is one line)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        val second = specimen.name.lineTwo()
                        if (second.isNotEmpty()) {
                            Text(
                                text = second,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                lineHeight = 22.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    // Row 3 — type pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Pill(
                            text = rockClassEmoji(specimen.rockClass) + " " + specimen.rockClass.label,
                            color = accent,
                            filled = false,
                        )
                    }

                    // Row 4 — two famous-location pills in one horizontal row
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            specimen.whereFound.take(2).forEach { loc ->
                                Pill(
                                    text = loc,
                                    color = Aqua,
                                    filled = false,
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = Aqua,
                                            modifier = Modifier.size(13.dp),
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    // Row 5 — rarity pill (aligned with image bottom)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Pill(
                            text = specimen.rarity,
                            color = rarityColor(specimen.rarity),
                            filled = true,
                        )
                    }
                }
            }

            // ── Tagline (left, larger + class-colored) + heart/add (right) ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 10.dp, top = 10.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = specimen.tagline,
                        color = taglineColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 0.1.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CircleIconButton(
                        icon = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        accent = accent,
                        active = liked,
                        onClick = { liked = !liked },
                    )
                    CircleIconButton(
                        icon = Icons.Filled.Add,
                        contentDescription = "Add",
                        accent = accent,
                        active = added,
                        onClick = { added = !added },
                    )
                }
            }
        }
    }
}

/** Split a name into its first line for the 2-row name layout. */
private fun String.lineOne(): String {
    val trimmed = trim()
    // If short enough to fit on one line, show the whole thing on line 1.
    return if (trimmed.length <= 14) trimmed else trimmed.substringBefore(' ', trimmed)
}

/** Second line of the name for the 2-row name layout (empty if it fits on one line). */
private fun String.lineTwo(): String {
    val trimmed = trim()
    if (trimmed.length <= 14) return ""
    val first = trimmed.substringBefore(' ', trimmed)
    val rest = trimmed.substring(first.length).trim()
    return if (rest.isEmpty()) "" else rest
}

/** Compact pill chip. */
@Composable
private fun Pill(
    text: String,
    color: Color,
    filled: Boolean,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (filled) color else color.copy(alpha = 0.16f))
            .glowingBorder(1.dp, color.copy(alpha = if (filled) 0.9f else 0.55f), shape)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) leadingIcon()
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (filled) Ink else brightenText(color),
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Circular icon button for heart / add. */
@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    accent: Color,
    active: Boolean,
    onClick: () -> Unit,
) {
    val size = 38.dp
    Box(
        modifier = Modifier
            .size(size)
            .sculpted(
                shape = RoundedCornerShape(size / 2),
                accent = accent,
                shadowElevation = 3.dp,
                circular = true,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(size / 2))
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (active) accent else Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** Map rock class to accent color. */
private fun rockClassAccent(rockClass: RockClass): Color = when (rockClass) {
    RockClass.IGNEOUS -> Igneous
    RockClass.SEDIMENTARY -> Color(0xFFD9B26A)
    RockClass.METAMORPHIC -> Color(0xFF6FA8C7)
    RockClass.MINERAL -> Citrine
    RockClass.CRYSTAL -> Amethyst
    RockClass.FOSSIL -> Fossil
}

/** Map rarity string to color. */
private fun rarityColor(rarity: String): Color {
    val r = rarity.lowercase()
    return when {
        r.contains("very rare") -> Color(0xFFB71C1C)
        r.contains("rare") && !r.contains("uncommon") -> Color(0xFFE2574C)
        r.contains("uncommon") -> Color(0xFFE8A33D)
        else -> Color(0xFF5CC98C)
    }
}

/** Rock-class emoji used in front of the type pill text. */
private fun rockClassEmoji(rockClass: RockClass): String = when (rockClass) {
    RockClass.IGNEOUS -> "\uD83C\uDF0B"
    RockClass.SEDIMENTARY -> "\uD83C\uDFD7\uFE0F"
    RockClass.METAMORPHIC -> "\u26F0\uFE0F"
    RockClass.MINERAL -> "\uD83E\uDEA8"
    RockClass.CRYSTAL -> "\uD83D\uDC8E"
    RockClass.FOSSIL -> "\uD83E\uDEA5"
}

/** Brighten a color for legible text on dark backgrounds. */
private fun brightenText(color: Color, amount: Float = 0.35f): Color {
    val lum = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (lum < 0.55f) {
        Color(
            color.red + (1f - color.red) * amount,
            color.green + (1f - color.green) * amount,
            color.blue + (1f - color.blue) * amount,
        )
    } else color
}

/** Real specimen data for the mock-up. */
private fun mockupSpecimens(): List<Specimen> {
    val q = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets"
    return listOf(
        Specimen(
            id = "amethyst",
            name = "Amethyst",
            rockClass = RockClass.CRYSTAL,
            category = "Silicate — Quartz variety",
            tagline = "Ancient Greeks wore it to stay sober. (It didn't work.)",
            emoji = "\uD83D\uDC8E",
            colorHex = 0xFF9B7BD8,
            description = "",
            formation = "",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO\u2082",
            commonColors = emptyList(),
            whereFound = listOf("Brazil", "Uruguay", "Zambia", "Thunder Bay, Canada"),
            funFacts = emptyList(),
            uses = "",
            rarity = "Common",
            imageUrls = listOf("$q/83b2bd7a-36bd-4a87-9872-e784d4a3872a.png"),
        ),
        Specimen(
            id = "pyrite",
            name = "Pyrite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide mineral",
            tagline = "Fool's Gold \u2014 perfect metallic cubes that sparkle like treasure.",
            emoji = "\uD83D\uDFE8",
            colorHex = 0xFFC9A227,
            description = "",
            formation = "",
            hardness = "6\u20136.5",
            luster = "Metallic",
            streak = "Greenish-black",
            crystalSystem = "Cubic",
            chemicalFormula = "FeS\u2082",
            commonColors = emptyList(),
            whereFound = listOf("Spain", "Peru", "Illinois, USA", "Italy"),
            funFacts = emptyList(),
            uses = "",
            rarity = "Common",
            imageUrls = listOf("$q/ce54d4e8-b66c-4431-a33a-9bcae71ff5a5.png"),
        ),
        Specimen(
            id = "aquamarine",
            name = "Beryl, Aquamarine",
            rockClass = RockClass.CRYSTAL,
            category = "Silicate — Beryl variety",
            tagline = "Seawater frozen into a gem you can wear.",
            emoji = "\uD83C\uDF0A",
            colorHex = 0xFF7EC8E3,
            description = "",
            formation = "",
            hardness = "7.5\u20138",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Hexagonal",
            chemicalFormula = "Be\u2083Al\u2082Si\u2086O\u2081\u2088",
            commonColors = emptyList(),
            whereFound = listOf("Brazil", "Pakistan", "Colorado, USA", "Russia"),
            funFacts = emptyList(),
            uses = "",
            rarity = "Uncommon",
            imageUrls = listOf("$q/2e3906e3-b0ac-42bd-aa1c-34248c1ebd08.png"),
        ),
        Specimen(
            id = "emerald",
            name = "Beryl, Emerald",
            rockClass = RockClass.CRYSTAL,
            category = "Silicate — Beryl variety",
            tagline = "Cleopatra's obsession \u2014 green fire from the deep.",
            emoji = "\uD83D\uDC9A",
            colorHex = 0xFF2D8B57,
            description = "",
            formation = "",
            hardness = "7.5\u20138",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Hexagonal",
            chemicalFormula = "Be\u2083Al\u2082Si\u2086O\u2081\u2088",
            commonColors = emptyList(),
            whereFound = listOf("Colombia", "Zambia", "Brazil", "North Carolina, USA"),
            funFacts = emptyList(),
            uses = "",
            rarity = "Rare",
            imageUrls = listOf("$q/3adca516-772e-4e90-8086-74f9be1837a8.png"),
        ),
        Specimen(
            id = "ammonite",
            name = "Ammonite",
            rockClass = RockClass.FOSSIL,
            category = "Fossil — Cephalopod",
            tagline = "Coiled shell of a sea creature that ruled the oceans for 350 million years.",
            emoji = "\uD83D\uDC1D",
            colorHex = 0xFFC9A87C,
            description = "",
            formation = "",
            hardness = "\u2014",
            luster = "\u2014",
            streak = "\u2014",
            crystalSystem = "N/A",
            chemicalFormula = "CaCO\u2083",
            commonColors = emptyList(),
            whereFound = listOf("Morocco", "Madagascar", "Alberta, Canada", "England"),
            funFacts = emptyList(),
            uses = "",
            rarity = "Common",
            imageUrls = listOf("$q/43091c8c-862a-4f1d-b342-84ee86e12783.png"),
        ),
        Specimen(
            id = "amber",
            name = "Amber",
            rockClass = RockClass.FOSSIL,
            category = "Biogenic gem — Fossil resin",
            tagline = "Tree resin turned to stone \u2014 sometimes with insects frozen inside for 40 million years.",
            emoji = "\uD83D\uDCAB",
            colorHex = 0xFFE8A33D,
            description = "",
            formation = "",
            hardness = "2\u20132.5",
            luster = "Resinous",
            streak = "\u2014",
            crystalSystem = "Amorphous",
            chemicalFormula = "C\u2081\u2080H\u2081\u2086O",
            commonColors = emptyList(),
            whereFound = listOf("Baltic Sea", "Dominican Republic", "Myanmar", "Mexico"),
            funFacts = emptyList(),
            uses = "",
            rarity = "Uncommon",
            imageUrls = listOf("$q/88071d9b-beb7-4ce2-85a3-42aee192e254.png"),
        ),
    )
}
