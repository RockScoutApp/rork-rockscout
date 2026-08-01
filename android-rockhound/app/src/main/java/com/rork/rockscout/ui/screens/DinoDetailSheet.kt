package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.DinoDiet
import com.rork.rockscout.data.DinoEntry
import com.rork.rockscout.data.DinoEra
import com.rork.rockscout.data.DinoExtraFacts
import com.rork.rockscout.data.DinoImageMap
import com.rork.rockscout.data.DinoLifeImageMap
import com.rork.rockscout.ui.components.DinoSilhouette
import com.rork.rockscout.ui.components.DinoSizeComparison
import com.rork.rockscout.ui.components.MetricText
import com.rork.rockscout.ui.components.PronunciationRow
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextMid

/** Opaque page background — nothing behind the popup may show through. */
private val SheetBackground = Color(0xFF0C0F0B)
private val CardBackground = Color(0xFF161A14)
private val ImageMatte = Color(0xFF1D2219)

/** Era accent colors */
private val eraColors: Map<DinoEra, Color> = mapOf(
    DinoEra.TRIASSIC to Color(0xFFE8A33D),
    DinoEra.JURASSIC to Color(0xFF8BBF6A),
    DinoEra.CRETACEOUS to Color(0xFF6FBF8A),
    DinoEra.PALEOGENE to Color(0xFF9B7BD8),
    DinoEra.NEOGENE to Color(0xFFE2574C),
    DinoEra.QUATERNARY to Color(0xFF5090B0),
    DinoEra.OTHER to Color(0xFF7CB5EC),
)

private val dietColors: Map<DinoDiet, Color> = mapOf(
    DinoDiet.CARNIVORE to Color(0xFFE2574C),
    DinoDiet.HERBIVORE to Color(0xFF8BBF6A),
    DinoDiet.OMNIVORE to Color(0xFFE8A33D),
    DinoDiet.PISCIVORE to Color(0xFF5090B0),
    DinoDiet.FILTER_FEEDER to Color(0xFF6FBF8A),
    DinoDiet.INSECTIVORE to Color(0xFF9B7BD8),
    DinoDiet.SCAVENGER to Color(0xFF8B7A60),
)

/** Kid-friendly one-liners for each diet. */
private val dietBlurbs: Map<DinoDiet, String> = mapOf(
    DinoDiet.CARNIVORE to "Ate meat — it hunted other animals.",
    DinoDiet.HERBIVORE to "Ate plants — leaves, ferns and branches.",
    DinoDiet.OMNIVORE to "Ate both plants and meat, whatever it could find.",
    DinoDiet.PISCIVORE to "Ate fish, snatched straight out of the water.",
    DinoDiet.FILTER_FEEDER to "Strained tiny food out of the water like a whale.",
    DinoDiet.INSECTIVORE to "Ate bugs — beetles, grubs and creepy-crawlies.",
    DinoDiet.SCAVENGER to "Ate leftovers from animals that had already died.",
)

/** Estimate a length in feet from strings such as "85 ft" or "10–20 ft". */
private fun lengthInFeet(length: String): Float {
    Regex("""([\d.]+)\s*[–-]\s*([\d.]+)\s*ft""").find(length)?.let {
        return it.groupValues[2].toFloatOrNull() ?: 0f
    }
    Regex("""([\d.]+)\s*ft""").find(length)?.let {
        return it.groupValues[1].toFloatOrNull() ?: 0f
    }
    Regex("""([\d.]+)\s*in""").find(length)?.let {
        return (it.groupValues[1].toFloatOrNull() ?: 0f) / 12f
    }
    return 0f
}

/**
 * Full-page detail popup for a dinosaur entry.
 *
 * Shows the life reconstruction ("what it looked like alive"), a scale chart
 * against a human, a single fossil photo, and kid-friendly facts. The page is
 * fully opaque and the system back gesture closes only this popup.
 *
 * @param entry The dinosaur entry to display
 * @param onDismiss Callback when the popup is closed
 */
@Composable
fun DinoDetailSheet(
    entry: DinoEntry,
    onDismiss: () -> Unit,
) {
    // Back press/gesture closes the popup instead of leaving the dictionary.
    BackHandler(enabled = true, onBack = onDismiss)

    val eraColor = eraColors[entry.era] ?: Color(0xFF7CB5EC)
    val dietColor = dietColors[entry.diet] ?: eraColor
    val silhouetteColor = Color(entry.accentColor)
    val lifeImage = DinoLifeImageMap.imageUri(entry)
    val fossilImage = DinoImageMap.imageUri(entry)
    val extras = remember(entry.id) { DinoExtraFacts.forEntry(entry) }
    val feet = remember(entry.length) { lengthInFeet(entry.length) }
    val sizeLine = remember(feet) { DinoExtraFacts.sizeComparison(feet) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SheetBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // ── Hero: what it looked like alive ────────────────────────────
            item {
                HeroHeader(
                    entry = entry,
                    lifeImage = lifeImage,
                    eraColor = eraColor,
                    dietColor = dietColor,
                    silhouetteColor = silhouetteColor,
                    pronunciation = extras?.pronunciation.orEmpty(),
                )
            }

            // ── Quick stats ────────────────────────────────────────────────
            item {
                StatStrip(
                    entry = entry,
                    eraColor = eraColor,
                    weightColor = silhouetteColor,
                    ageColor = dietColor,
                )
            }

            // ── Fun "what does that mean" cards ────────────────────────────
            if (extras != null && (extras.meaning.isNotBlank() || extras.locomotion.isNotBlank())) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (extras.meaning.isNotBlank()) {
                            FactRow("Name means", "\u201C${extras.meaning}\u201D", eraColor)
                        }
                        if (extras.locomotion.isNotBlank()) {
                            FactRow("Got around", "Walked ${extras.locomotion}", eraColor)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Diet in plain language ─────────────────────────────────────
            item {
                DietCard(entry = entry, dietColor = dietColor)
            }

            // ── Size comparison silhouette ─────────────────────────────────
            item {
                SectionTitle("How Big Was It?", eraColor)
                if (sizeLine.isNotBlank()) {
                    Text(
                        text = sizeLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardBackground),
                ) {
                    DinoSizeComparison(
                        entry = entry,
                        accentColor = silhouetteColor,
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── About ──────────────────────────────────────────────────────
            item {
                InfoSection("The Story", entry.description, eraColor)
            }

            // ── Where it lived ─────────────────────────────────────────────
            item {
                InfoSection("Where It Lived", entry.habitat, eraColor)
            }

            // ── Fun facts ──────────────────────────────────────────────────
            if (entry.funFacts.isNotEmpty()) {
                item { SectionTitle("Cool Facts", eraColor) }
                itemsIndexed(entry.funFacts) { index, fact ->
                    FunFactCard(index = index, fact = fact, accent = eraColor)
                }
                item { Spacer(Modifier.height(20.dp)) }
            }

            // ── The one fossil photo ───────────────────────────────────────
            if (fossilImage != null) {
                item {
                    SectionTitle("Its Fossils", eraColor)
                    Text(
                        text = "Everything we know about ${entry.name} comes from bones like these.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    FittedImageCard(
                        model = fossilImage,
                        contentDescription = "${entry.name} fossil",
                        ratio = 1.5f,
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Fossil locations ───────────────────────────────────────────
            if (entry.foundIn.isNotEmpty()) {
                item { SectionTitle("Dug Up In", eraColor) }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        entry.foundIn.forEach { location ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(dietColor.copy(alpha = 0.8f)),
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextMid,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Timeline footer ────────────────────────────────────────────
            item {
                TimelineCard(entry = entry, eraColor = eraColor)
                Spacer(Modifier.height(28.dp))
            }
        }

        // Close button floats above the scrolling content, clear of the status bar.
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(12.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.62f))
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.22f), CircleShape),
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.White,
            )
        }
    }
}

/**
 * Hero header showing the life reconstruction on a matte panel so the whole
 * artwork is visible (never cropped), with the name and badges below it.
 */
@Composable
private fun HeroHeader(
    entry: DinoEntry,
    lifeImage: String?,
    eraColor: Color,
    dietColor: Color,
    silhouetteColor: Color,
    pronunciation: String,
) {
    var loaded by remember(entry.id) { mutableStateOf(false) }
    val artAlpha by animateFloatAsState(
        targetValue = if (loaded || lifeImage == null) 1f else 0f,
        animationSpec = tween(durationMillis = 420),
        label = "heroFade",
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.45f)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            silhouetteColor.copy(alpha = 0.22f),
                            ImageMatte,
                            SheetBackground,
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (lifeImage != null) {
                AsyncImage(
                    model = lifeImage,
                    contentDescription = "${entry.name} as it looked when alive",
                    // Fit keeps the full illustration on screen — no cropped heads or tails.
                    contentScale = ContentScale.Fit,
                    onSuccess = { loaded = true },
                    onError = { loaded = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                )
            } else {
                DinoSilhouette(
                    bodyPlan = entry.bodyPlan,
                    color = silhouetteColor.copy(alpha = 0.9f),
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .aspectRatio(1.8f),
                )
            }
            if (lifeImage != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "Artist's impression",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 9.sp,
                    )
                }
            }
        }

        // Name block on solid background so text is always fully legible.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SheetBackground)
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 12.dp),
        ) {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.height(6.dp))
            // Tap to hear the species name read aloud.
            PronunciationRow(
                name = entry.name,
                pronunciation = pronunciation,
                accent = eraColor,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(entry.era.label, color = eraColor)
                TagChip(entry.diet.label, color = dietColor)
            }
        }
    }
}

/**
 * Three evenly-weighted stat blocks. Each column takes an equal share of the
 * row so long values such as "30–80 tons" wrap onto a second line instead of
 * being squeezed into a single vertical character column.
 */
@Composable
private fun StatStrip(
    entry: DinoEntry,
    eraColor: Color,
    weightColor: Color,
    ageColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        MetricStatBlock(
            label = "Length",
            value = entry.length,
            color = eraColor,
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        MetricStatBlock(
            label = "Weight",
            value = entry.weight,
            color = weightColor,
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        StatBlock(
            label = "Lived",
            value = entry.age,
            color = ageColor,
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(34.dp)
            .background(Color.White.copy(alpha = 0.08f)),
    )
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 17.sp,
            maxLines = 3,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextLow,
        )
    }
}

/**
 * Stat block that shows an imperial measurement with long-press metric
 * conversion popup. Long-press the value to see the metric equivalent.
 */
@Composable
private fun MetricStatBlock(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MetricText(
            text = value,
            color = color,
            fontSize = 14,
            lineHeight = 17,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextLow,
        )
    }
}

@Composable
private fun FactRow(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextLow,
            modifier = Modifier.width(96.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = accent,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DietCard(entry: DinoEntry, dietColor: Color) {
    val blurb = dietBlurbs[entry.diet] ?: ""
    if (blurb.isBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(dietColor.copy(alpha = 0.12f))
            .glowingBorder(1.dp, dietColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dietColor),
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = entry.diet.label,
                style = MaterialTheme.typography.titleSmall,
                color = dietColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = blurb,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        }
    }
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun FunFactCard(index: Int, fact: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = fact,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMid,
            lineHeight = 20.sp,
        )
    }
}

/** Image card that shows the whole picture on a matte, never cropping it. */
@Composable
private fun FittedImageCard(
    model: String,
    contentDescription: String,
    ratio: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(ImageMatte)
            .aspectRatio(ratio),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
        )
    }
}

@Composable
private fun TimelineCard(entry: DinoEntry, eraColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(eraColor.copy(alpha = 0.18f), CardBackground)
                )
            )
            .padding(16.dp),
    ) {
        Text(
            text = "When On Earth?",
            style = MaterialTheme.typography.titleSmall,
            color = eraColor,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = entry.period,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = entry.age,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMid,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${entry.era.label} \u2022 ${entry.era.subtitle}",
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextLow,
        )
    }
}

@Composable
private fun SectionTitle(title: String, accent: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = accent,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
    )
}

@Composable
private fun InfoSection(title: String, body: String, accent: Color) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = accent,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMid,
            lineHeight = 21.sp,
        )
    }
    Spacer(Modifier.height(20.dp))
}
