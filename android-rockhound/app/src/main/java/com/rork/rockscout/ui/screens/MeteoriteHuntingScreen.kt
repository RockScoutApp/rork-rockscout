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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.data.MeteoriteSpecimens
import com.rork.rockscout.data.RocksAreAmazingSpecimens
import com.rork.rockscout.data.SeedData
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.InlineContentImage
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.METEORITE_HERO_URL
import com.rork.rockscout.ui.components.METEORITE_IMG_CHONDRITE
import com.rork.rockscout.ui.components.METEORITE_IMG_WIDMANSTATTEN
import com.rork.rockscout.ui.components.METEORITE_IMG_PALLASITE
import com.rork.rockscout.ui.components.METEORITE_IMG_DRY_LAKE_BED
import com.rork.rockscout.ui.components.METEORITE_IMG_MAGNET_CANE
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SpecimenListItem
import com.rork.rockscout.ui.components.RarityIndicator
import com.rork.rockscout.ui.components.shortCategoryLabel
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineSoft
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import androidx.compose.ui.text.style.TextOverflow
import com.rork.rockscout.ui.components.glowingBorder

@Composable
fun MeteoriteHuntingScreen(navController: NavController) {
    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    val guideSpec = remember {
        SeedData.specimenById("amazing-meteorite-hunting")
            ?: RocksAreAmazingSpecimens.allAmazing.firstOrNull { it.id == "amazing-meteorite-hunting" }
    }
    val guideImages = remember {
        SpecimenImages.urls["amazing-meteorite-hunting"] ?: SpecimenImages.urls["meteorite-hunting"] ?: emptyList()
    }
    val meteorites = remember { MeteoriteSpecimens.specimens }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    RockBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(800)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 52.dp, bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = TextMid,
                    )
                    Text(
                        text = "Finding Meteorites",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
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
                        model = METEORITE_HERO_URL,
                        contentDescription = "Iron meteorite on desert ground",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    // Gradient overlay for text legibility
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
                            "How to find, identify, and verify space rocks — from strewn-field hunting to the magnet test and window-cutting. The full field guide for meteorite hunters.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Hero guide card
            if (guideSpec != null) {
                item {
                    DarkCard(
                        accent = Color(0xFFC0C0C0),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFFC0C0C0).copy(alpha = 0.35f), Color(0xFF1A1812))
                                        )
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Public, contentDescription = null, tint = Color(0xFFC0C0C0))
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    guideSpec.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                )
                                Text(
                                    guideSpec.tagline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC0C0C0).copy(alpha = 0.85f),
                                    fontStyle = FontStyle.Italic,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        if (guideImages.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.8f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1812))
                                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                    .clickable {
                                        viewerUrls = guideImages
                                        viewerInitialPage = 0
                                    },
                            ) {
                                AsyncImage(
                                    model = guideImages.first(),
                                    contentDescription = guideSpec.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            guideSpec.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Where to Search",
                            style = MaterialTheme.typography.titleMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        guideSpec.whereFound.forEach { location ->
                            Text(
                                "• $location",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Field Tips",
                            style = MaterialTheme.typography.titleMedium,
                            color = Citrine,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        guideSpec.funFacts.forEach { fact ->
                            Text(
                                "• $fact",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }

            // === METEORITE CLASSIFICATION ===
            item { SectionHeader("METEORITE CLASSIFICATION") }
            item { MeteoriteClassificationCard() }

            // === IDENTIFICATION GUIDE ===
            item { SectionHeader("FIELD IDENTIFICATION") }
            item { MeteoriteIDCard() }

            // === HUNTING TIPS ===
            item { SectionHeader("HUNTING TIPS & TECHNIQUES") }
            item { HuntingTipsCard() }

            // === FAMOUS FALLS ===
            item { SectionHeader("FAMOUS METEORITES") }
            item { FamousMeteoritesCard() }

            item {
                Text(
                    "METEORITE TYPES IN THE DATABASE",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            items(meteorites) { specimen ->
                val imageUrls = SpecimenImages.urls[specimen.id] ?: specimen.imageUrls
                val accent = Color(specimen.colorHex)
                SpecimenListItem(
                    specimen = specimen,
                    accent = accent,
                    onClick = { navController.navigate(Routes.specimen(specimen.id)) },
                    onImageClick = {
                        viewerUrls = imageUrls
                        viewerInitialPage = 0
                    },
                    imageSize = 113.dp,
                    showCategory = true,
                    categoryLabel = shortCategoryLabel(specimen.category),
                )
            }
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
private fun MeteoriteClassificationCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFC0C0C0)) {
        val accentColor = Color(0xFFC0C0C0)
        Text(
            "Meteorite Classification",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Meteorites are classified into three main groups based on their composition and origin. Each type tells a different story about the early solar system.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val types = listOf(
            Triple("Stony Meteorites", "~94% of all falls", "Made mostly of silicate minerals. Divided into chondrites (with chondrules — ancient spherical grains from the solar nebula) and achondrites (without chondrules, from differentiated parent bodies like asteroids and planets). Chondrites are the most primitive material in the solar system — 4.56 billion years old, unchanged since formation.") to Color(0xFF8B7D6B),
            Triple("Iron Meteorites", "~5% of all falls", "Made of nearly pure iron-nickel alloy. When cut, polished, and etched with acid, they show Widmanstätten patterns — interlocking crystal bands that can only form by cooling over millions of years in the core of a planetesimal. These are literal pieces of planetary cores.") to Color(0xFFC0C0C0),
            Triple("Stony-Iron Meteorites", "~1% of all falls", "The rarest type — a mix of silicate minerals and iron-nickel metal. Two subgroups: pallasites (olivine crystals embedded in an iron-nickel matrix — possibly from the core-mantle boundary of a destroyed planet) and mesosiderites (a breccia of metal and rock). Among the most beautiful meteorites.") to Color(0xFFD4A017),
        )
        types.forEach { (data, color) ->
            Text(
                data.first,
                style = MaterialTheme.typography.titleSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 2.dp),
            )
            Text(
                data.second,
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextLow,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                data.third,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            // Insert image after the first type (stony — chondrite cross-section)
            if (data.first == "Stony Meteorites") {
                Spacer(Modifier.height(10.dp))
                InlineContentImage(
                    imageUrl = METEORITE_IMG_CHONDRITE,
                    contentDescription = "Polished chondrite meteorite cross-section showing chondrules",
                    caption = "Chondrite cross-section with visible chondrules",
                )
            }
            // Insert image after the second type (iron — Widmanstatten)
            if (data.first == "Iron Meteorites") {
                Spacer(Modifier.height(10.dp))
                InlineContentImage(
                    imageUrl = METEORITE_IMG_WIDMANSTATTEN,
                    contentDescription = "Etched iron meteorite showing Widmanstatten patterns",
                    caption = "Widmanstatten patterns in an etched iron meteorite",
                )
            }
            // Insert image after the third type (stony-iron — pallasite)
            if (data.first == "Stony-Iron Meteorites") {
                Spacer(Modifier.height(10.dp))
                InlineContentImage(
                    imageUrl = METEORITE_IMG_PALLASITE,
                    contentDescription = "Pallasite meteorite with olivine crystals in iron-nickel matrix",
                    caption = "Pallasite — olivine crystals in an iron-nickel matrix",
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Lunar and Martian meteorites are extremely rare achondrites — rocks blasted off the Moon or Mars by asteroid impacts that landed on Earth. They are identified by matching their isotope ratios to rocks brought back by Apollo missions (Moon) or by trapped gas bubbles matching Mars' atmosphere (Mars).",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextMid,
        )
    }
}

@Composable
private fun MeteoriteIDCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE5683C)) {
        val accentColor = Color(0xFFE5683C)
        Text(
            "How to Identify a Meteorite in the Field",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Most rocks that people think are meteorites aren't. These are the key tests to tell a real meteorite from a meteorwrong:",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val tests = listOf(
            "1. Magnetic Test" to "Most meteorites contain iron-nickel and will attract a magnet. A strong neodymium magnet will stick firmly to a genuine meteorite. If it's not magnetic at all, it's almost certainly not a meteorite. However, many Earth rocks are also magnetic (magnetite, hematite), so this is a necessary but not sufficient test.",
            "2. Density Test" to "Meteorites are typically denser than ordinary Earth rocks. Iron meteorites are extremely heavy (7.5+ g/cm³), and even stony meteorites feel heavier than expected for their size. If a rock feels unusually heavy, that's a clue.",
            "3. Fusion Crust" to "When a meteorite passes through the atmosphere, the surface melts and forms a thin dark crust. On fresh falls, this crust is black and glassy. On older finds, it weathers to brown. The crust is usually smoother than the interior and may show flow lines from molten material streaming backward during flight.",
            "4. Regmaglypts (Thumbprints)" to "Many meteorites have regmaglypts — shallow, thumbprint-like depressions on the surface caused by ablation during atmospheric passage. These look like someone pressed their thumb into soft clay. Not all meteorites have them, but they're a strong indicator.",
            "5. Metal Flakes" to "If you cut or grind a corner off the specimen, look for tiny metallic flakes or specks of silver-colored iron-nickel metal. Most Earth rocks don't have visible metal. Use a file and look for shiny spots in the cut surface.",
            "6. Chondrules" to "If the specimen is a chondrite, cutting it open may reveal tiny spherical grains (chondrules) — usually 0.5-2mm in diameter. These are some of the oldest solid material in the solar system and are only found in chondritic meteorites.",
            "7. The Streak Test" to "Rub the specimen on an unglazed porcelain plate. A meteorite will leave a faint gray or brown streak. Magnetite (a common meteorwrong) leaves a black streak. Hematite leaves a red-brown streak. If you get a colored streak, it's probably an Earth mineral.",
            "8. Fusion Crust vs. Coating" to "A true fusion crust is thin (1-2mm) and formed by melting. If the dark exterior is thick or extends throughout the rock, it's likely a desert varnish, manganese coating, or weathering rind — not a fusion crust.",
        )
        tests.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            // Book-style illustration: chondrite after Chondrules test
            if (title == "6. Chondrules") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = METEORITE_IMG_CHONDRITE,
                        contentDescription = "Chondrite cross-section with visible chondrules",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 Chondrules are 4.56 billion years old \u2014 older than any Earth rock.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // Book-style illustration: Widmanstatten after Metal Flakes test
            if (title == "5. Metal Flakes") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 Etching an iron meteorite reveals Widmanst\u00e4tten patterns \u2014 impossible to fake.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = METEORITE_IMG_WIDMANSTATTEN,
                        contentDescription = "Etched iron meteorite showing Widmanstatten patterns",
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text(
                "⚠️ ",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFE2574C),
            )
            Text(
                "If you think you've found a meteorite, don't grind, cut, or damage it extensively. Take photos with a scale reference, record the GPS coordinates, and contact a university geology department or a meteorite expert for verification.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE2574C),
            )
        }
    }
}

@Composable
private fun HuntingTipsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF6FA8C7)) {
        val accentColor = Color(0xFF6FA8C7)
        Text(
            "Hunting Tips & Techniques",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        val tips = listOf(
            "Best Hunting Grounds: Dry Lake Beds" to "Dry lake beds in the desert Southwest (Nevada, Arizona, California, Utah) are ideal. The flat, light-colored surface makes dark meteorites easy to spot. Meteorites are preserved by the dry climate and accumulated over thousands of years.",
            "Best Hunting Grounds: Ice Fields" to "Antarctica is the premier meteorite hunting ground on Earth. The blue ice fields preserve meteorites perfectly and the moving ice concentrates them in stranding zones. Over 50,000 meteorites have been found there — more than the rest of the world combined.",
            "Best Hunting Grounds: Plowed Fields" to "Farmers plowing fields in the Midwest occasionally turn up meteorites. The freshly turned soil exposes dark rocks. Kansas, Iowa, and Nebraska have produced many finds. Ask farmers for permission to search after plowing.",
            "Use a Magnet Cane" to "Sweep a strong neodymium magnet on a stick (a 'meteorite cane') just above the ground surface. Iron meteorites and most stony meteorites will jump to the magnet. This dramatically speeds up searching large areas.",
            "Search After Rains" to "Rain washes away dust and reveals freshly exposed rocks on desert surfaces. The dark fusion crust of meteorites contrasts sharply with wet, light-colored desert pavement. Search in the morning before things dry out.",
            "Grid Searching" to "Walk in systematic grid patterns — back and forth in parallel lines about 5-10 feet apart. This ensures you don't miss areas. Mark your GPS track so you know where you've been. Many hunters use ATV-mounted GPS for this.",
            "Look for Fresh Falls" to "When a fireball is reported, the strewn field (area where fragments land) can be calculated from trajectory data. Fresh falls are the most valuable — they have unweathered fusion crusts and haven't been altered by terrestrial weathering.",
            "What to Bring" to "Strong neodymium magnet, GPS device or phone, camera, notebook, sturdy boots, sun protection, plenty of water (desert hunting), and a 10x hand lens for examining fusion crust and metal flakes. A metal detector can also help for buried meteorites.",
            "Recording Your Find" to "Photograph the meteorite in place before moving it. Record exact GPS coordinates. Note the surrounding terrain and any other fragments nearby. Bag it carefully — don't clean it with water (can alter the fusion crust). Label with date, coordinates, and any other notes.",
        )
        tips.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            // Insert dry lake bed image after that section
            if (title == "Best Hunting Grounds: Dry Lake Beds") {
                Spacer(Modifier.height(10.dp))
                InlineContentImage(
                    imageUrl = METEORITE_IMG_DRY_LAKE_BED,
                    contentDescription = "Dry lake bed in the desert Southwest — ideal meteorite hunting ground",
                    caption = "Dry lake bed — dark meteorites stand out on the light playa surface",
                )
            }
            // Insert magnet cane image after that section
            if (title == "Use a Magnet Cane") {
                Spacer(Modifier.height(10.dp))
                InlineContentImage(
                    imageUrl = METEORITE_IMG_MAGNET_CANE,
                    contentDescription = "Neodymium magnet on a stick being used to test a meteorite",
                    caption = "A magnet cane — the fastest way to cover ground while hunting",
                )
            }
        }
    }
}

@Composable
private fun FamousMeteoritesCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFD4A017)) {
        val accentColor = Color(0xFFD4A017)
        Text(
            "Famous Meteorites",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        val meteorites = listOf(
            "Allende (Mexico, 1969)" to "A carbonaceous chondrite that fell the same year as the Apollo Moon landings. Contains interstellar grains older than the solar system itself — 4.567 billion years old. One of the most studied meteorites ever.",
            "Sikhote-Alin (Russia, 1947)" to "The largest witnessed iron meteorite fall in recorded history. Over 23 tons of material scattered across a 0.7-square-mile strewn field in Siberia. Individuals saw the fireball and heard explosions 200 miles away. Specimens show spectacular regmaglypts.",
            "Hoba (Namibia, ~80,000 years)" to "The largest known meteorite on Earth — 60 tons of iron-nickel, still lying where it fell. It's so heavy it has never been moved. Now a national monument. Measuring ~3 meters across, it's been a tourist destination for decades.",
            "Willamette (Oregon, ~10,000 years)" to "The largest meteorite found in the United States (15.5 tons). Discovered by settlers in 1902, though known to Native Americans for centuries. Now at the American Museum of Natural History in New York. Oregon has requested its return.",
            "ALH 84001 (Antarctica, 1984)" to "A Martian meteorite found in Antarctica. In 1996, NASA announced it contained possible fossilized Martian bacteria — a claim still debated. It proved that rocks can travel from Mars to Earth, carrying potential evidence of life.",
            "Chelyabinsk (Russia, 2013)" to "A 20-meter asteroid that exploded over Chelyabinsk with the force of 30 Hiroshima bombs, injuring 1,500 people (mostly from flying glass). The most damaging meteorite event in modern history. Fragments were recovered from a frozen lake.",
            "Cape York (Greenland, ~10,000 years)" to "An iron meteorite used by Inuit people for centuries as a source of tool-making iron — before they had contact with Europeans. Several large masses were removed by explorer Robert Peary in the 1890s and sold to museums.",
            "Ensisheim (France, 1492)" to "The oldest recorded meteorite fall with a preserved specimen. A 127-kg stone fell in a wheat field and was witnessed by a boy. Emperor Maximilian I declared it a good omen. The meteorite is still on display in the town hall.",
        )
        meteorites.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
            // Book-style illustration: pallasite after Hoba (largest meteorite)
            if (title == "Hoba (Namibia, ~80,000 years)") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = METEORITE_IMG_PALLASITE,
                        contentDescription = "Pallasite meteorite with olivine crystals in iron-nickel matrix",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 Pallasites like this may come from the core-mantle boundary of a destroyed planet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
