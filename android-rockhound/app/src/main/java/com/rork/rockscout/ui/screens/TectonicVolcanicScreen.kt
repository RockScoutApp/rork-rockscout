package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.InlineContentImage
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.ScreenScaffold
import androidx.compose.ui.text.font.FontStyle
import com.rork.rockscout.ui.components.TECTONIC_HERO_URL
import com.rork.rockscout.ui.components.TECTONIC_IMG_DIVERGENT
import com.rork.rockscout.ui.components.TECTONIC_IMG_SUBDUCTION
import com.rork.rockscout.ui.components.TECTONIC_IMG_TRANSFORM
import com.rork.rockscout.ui.components.TECTONIC_IMG_HOTSPOT
import com.rork.rockscout.ui.components.TECTONIC_IMG_BASALT
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

/** Inline gallery images for the Tectonics & Volcanoes tab. Public so the
 *  offline bulk-download registry can include them. */
val TECTONIC_IMAGES = listOf(
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/abad4a4c-ec17-43a6-a13b-3eb5abf02bbe.png",
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/13d72c22-f574-47c4-a23c-a6a9ae6b65bb.png",
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/2a3324dd-76fa-4a3c-89d2-643770b88a1e.png",
)

private data class TectonicTopic(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
    val summary: String,
    val details: List<String>,
)

@Composable
fun TectonicVolcanicScreen(navController: NavController) {
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    ScreenScaffold(title = "Tectonics & Volcanoes", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Hero banner image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
                ) {
                    AsyncImage(
                        model = TECTONIC_HERO_URL,
                        contentDescription = "Volcanic eruption with flowing lava",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.7f))
                                )
                            ),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Text(
                            "Earth's surface is a mosaic of moving tectonic plates. Their collisions, separations, and slips create the heat, pressure, and magma that form almost every rock and mineral you'll ever collect.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            item { SectionHeader("VISUAL GALLERY") }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                ) {
                    items(TECTONIC_IMAGES) { url ->
                        TectonicImageCard(
                            imageUrl = url,
                            onClick = {
                                viewerUrls = TECTONIC_IMAGES
                                viewerInitialPage = TECTONIC_IMAGES.indexOf(url)
                            },
                        )
                    }
                }
            }

            item { SectionHeader("PLATE BOUNDARIES & ROCK FORMATION") }
            items(topics.size) { index ->
                TectonicTopicCard(topic = topics[index])
            }
            // Inline images for each plate boundary type
            item {
                InlineContentImage(
                    imageUrl = TECTONIC_IMG_DIVERGENT,
                    contentDescription = "Mid-ocean ridge with lava flowing from a rift valley",
                    caption = "Divergent boundary — new crust forms at mid-ocean ridges",
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                InlineContentImage(
                    imageUrl = TECTONIC_IMG_SUBDUCTION,
                    contentDescription = "Subduction zone cross-section with volcanic arc",
                    caption = "Convergent boundary — subduction creates volcanic arcs",
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                InlineContentImage(
                    imageUrl = TECTONIC_IMG_TRANSFORM,
                    contentDescription = "Aerial view of the San Andreas transform fault",
                    caption = "Transform boundary — plates grind past each other",
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                InlineContentImage(
                    imageUrl = TECTONIC_IMG_HOTSPOT,
                    contentDescription = "Mantle plume hotspot creating volcanic island chain",
                    caption = "Hotspot — a stationary magma plume burns through a moving plate",
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                InlineContentImage(
                    imageUrl = TECTONIC_IMG_BASALT,
                    contentDescription = "Columnar basalt formation with hexagonal columns",
                    caption = "Columnar basalt — formed when thick lava cools and contracts",
                )
            }

            item { SectionHeader("ROCKHOUNDING BY GEOLOGY") }
            item { RockLocationCard() }
        }

        if (viewerUrls.isNotEmpty()) {
            FullScreenImageViewer(
                imageUrls = viewerUrls,
                initialPage = viewerInitialPage,
                onDismiss = { viewerUrls = emptyList() },
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Aqua,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun TectonicImageCard(imageUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1C16))
            .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Tectonic or volcanic process",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(
                "Tap to enlarge",
                style = MaterialTheme.typography.labelSmall,
                color = TextHigh,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

@Composable
private fun TectonicTopicCard(topic: TectonicTopic) {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = topic.accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(topic.accent.copy(alpha = 0.22f))
                    .glowingBorder(1.dp, topic.accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(topic.icon, contentDescription = null, tint = topic.accent, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    topic.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    topic.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        topic.details.forEach { detail ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = topic.accent,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun RockLocationCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text(
            "Where the action is",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Some of the best rockhounding happens where plates meet or where ancient volcanoes once stood. Use these geologic clues to plan your next hunt:",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))
        val locations = listOf(
            "Rift valleys & flood basalts" to "Look for agates, zeolites, and thunder eggs in basalt flows (Pacific Northwest, Deccan Traps).",
            "Volcanic arcs & calderas" to "Obsidian, pumice, opalized wood, and fire agate form near active and ancient volcanoes.",
            "Subduction-zone mountain belts" to "Metamorphic gems like garnet, kyanite, and staurolite form under heat and pressure.",
            "Hotspot chains" to "Basalt columns, peridot xenoliths, and gem-quality olivine can weather out of hotspot lavas (Hawaiian Islands).",
            "Ancient cratons & shields" to "Old, stable cores expose granites, pegmatites, and diamond-bearing kimberlite pipes.",
            "Sedimentary basins" to "Fossils, petrified wood, and agate nodules concentrate in ancient lake and river deposits.",
        )
        locations.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = Citrine,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
            // Book-style illustration: basalt after rift valleys
            if (title == "Rift valleys & flood basalts") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = TECTONIC_IMG_BASALT,
                        contentDescription = "Columnar basalt formation from cooled lava flow",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 Columnar basalt forms when thick lava cools and contracts \u2014 agates grow in the gaps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // Book-style illustration: hotspot after hotspot chains
            if (title == "Hotspot chains") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 Hotspots are stationary \u2014 the plate moves over them, creating island chains.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = TECTONIC_IMG_HOTSPOT,
                        contentDescription = "Mantle plume hotspot creating volcanic island chain",
                    )
                }
            }
        }
    }
}

private val topics = listOf(
    TectonicTopic(
        title = "Divergent Boundaries",
        icon = Icons.Filled.Waves,
        accent = Color(0xFFFF6B4A),
        summary = "Plates pull apart, magma rises, and new oceanic crust forms.",
        details = listOf(
            "Mid-ocean ridges produce basalt — the most common volcanic rock on Earth.",
            "Rift valleys on land (East African Rift) expose young volcanic rocks and mineral deposits.",
            "Hydrothermal vents at ridges concentrate sulfide minerals, including copper and zinc ores.",
            "Famous finds: agates in flood basalts, zeolite cavities, and opalized wood.",
        ),
    ),
    TectonicTopic(
        title = "Convergent Boundaries",
        icon = Icons.Filled.Terrain,
        accent = Color(0xFF6FA8C7),
        summary = "Plates collide, one sinks, and the other crumples into mountains.",
        details = listOf(
            "Subduction zones generate magma that rises into volcanic arcs (Andes, Cascades, Japan).",
            "Andesite, dacite, and rhyolite dominate these volcanic chains.",
            "Deep burial creates metamorphic rocks: schist, gneiss, marble, and eclogite.",
            "Famous finds: emerald and gold in shear zones, garnet in schist, jade near subduction complexes.",
        ),
    ),
    TectonicTopic(
        title = "Transform Boundaries",
        icon = Icons.Filled.Landscape,
        accent = Color(0xFFD9B26A),
        summary = "Plates slide past each other, grinding rock into fault zones.",
        details = listOf(
            "Fault movement crushes rock into breccia, gouge, and mylonite.",
            "Pressure and heat along faults can locally recrystallize minerals.",
            "These zones often expose older, deeper rock formations at the surface.",
            "Famous finds: serpentinite bodies, jasper, and quartz veins carried up by fault systems.",
        ),
    ),
    TectonicTopic(
        title = "Hotspots & Mantle Plumes",
        icon = Icons.Filled.LocalFireDepartment,
        accent = Color(0xFFE8A33D),
        summary = "Stationary magma plumes burn through moving plates, building island chains.",
        details = listOf(
            "Hawaii, Yellowstone, Iceland, and the Galápagos all sit above hotspots.",
            "Basaltic lava flows can pile into shield volcanoes and columnar basalt formations.",
            "Mantle plumes sometimes carry deep minerals like peridot and diamond to the surface.",
            "Famous finds: Hawaiian peridot, Oregon sunstone in basalts, Yellowstone agates.",
        ),
    ),
    TectonicTopic(
        title = "Volcanic Eruptions & Igneous Rocks",
        icon = Icons.Filled.Public,
        accent = Color(0xFF8BBF6A),
        summary = "Eruptions cool magma into igneous rocks with distinct textures and minerals.",
        details = listOf(
            "Extrusive rocks (obsidian, pumice, basalt) cool quickly at the surface.",
            "Intrusive rocks (granite, gabbro, diorite) cool slowly underground, growing larger crystals.",
            "Pegmatites form in the last stages of magma crystallization and can host giant crystals of tourmaline, beryl, and spodumene.",
            "Volcanic glass and lapilli deposits preserve rapid geologic events.",
        ),
    ),
)
