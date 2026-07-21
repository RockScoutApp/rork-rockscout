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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rork.rockscout.data.PrehistoricOrganisms
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.FullScreenImageViewer
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.BookStyleImage
import com.rork.rockscout.ui.components.PREHISTORIC_IMG_ARCHAEOPTERYX
import com.rork.rockscout.ui.components.PREHISTORIC_IMG_TIKTAALIK
import com.rork.rockscout.ui.components.PREHISTORIC_IMG_SKELETON
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.Igneous
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.glowingBorder

private val categoryColors = mapOf(
    "Dinosaur" to Color(0xFF8BBF6A),
    "Pterosaur" to Color(0xFF6FBF8A),
    "Marine Reptile" to Color(0xFF5090B0),
    "Prehistoric Mammal" to Color(0xFFC9A87C),
    "Prehistoric Bird" to Color(0xFFC97070),
    "Prehistoric Arthropod" to Color(0xFF7080A0),
    "Prehistoric Insect" to Color(0xFF70A0C9),
    "Prehistoric Fish" to Color(0xFF6090B0),
    "Prehistoric Amphibian" to Color(0xFF90B070),
    "Prehistoric Flora" to Color(0xFF70B870),
    "Single-Celled Organism" to Color(0xFFB0A0C0),
)

@Composable
fun PrehistoricOrganismsScreen(navController: NavController) {
    val organisms = PrehistoricOrganisms.specimens
    val groupedByCategory = organisms.groupBy { it.category }
    val categoryOrder = listOf(
        "Single-Celled Organism", "Prehistoric Flora", "Prehistoric Arthropod", "Prehistoric Insect",
        "Prehistoric Amphibian", "Prehistoric Fish", "Prehistoric Bird",
        "Prehistoric Mammal", "Marine Reptile", "Pterosaur", "Dinosaur"
    )

    var viewerUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    ScreenScaffold(title = "Exploring Prehistoric Organisms", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Column {
                        Text(
                            "Journey through 3.7 billion years of life — from single-celled microbes to the giants of the Mesozoic. Each organism tells a story of evolution, adaptation, and survival across Earth's deepest time.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextMid,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagChip("${organisms.size} organisms", color = Fossil)
                            TagChip("3.7 billion years", color = Fossil)
                            TagChip("10 categories", color = Fossil)
                        }
                    }
                }
            }

            // === EVOLUTION OF LIFE ===
            item { SectionHeader("EVOLUTION OF LIFE") }
            item { EvolutionOfLifeCard() }

            // === DINOSAUR FACTS ===
            item { SectionHeader("DINOSAUR FACTS") }
            item { DinosaurFactsCard() }

            categoryOrder.forEach { category ->
                val items = groupedByCategory[category] ?: return@forEach
                item {
                    CategoryHeader(
                        category = category,
                        count = items.size,
                        accent = categoryColors[category] ?: Fossil,
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(items) { organism ->
                            OrganismCard(
                                organism = organism,
                                accent = categoryColors[category] ?: Fossil,
                                onClick = { navController.navigate(Routes.specimen(organism.id)) },
                                onPhotoClick = { urls, page ->
                                    viewerUrls = urls
                                    viewerInitialPage = page
                                },
                            )
                        }
                    }
                }
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
private fun EvolutionOfLifeCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF70B870)) {
        val accentColor = Color(0xFF70B870)
        Text(
            "The Tree of Life",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Life on Earth began as single-celled organisms in the Archean seas and evolved over 3.7 billion years into the incredible diversity we see today. Each major innovation opened new possibilities.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val milestones = listOf(
            "3.7 bya: First Life" to "Single-celled prokaryotes (bacteria and archaea) appear in the oceans. No oxygen in the atmosphere yet.",
            "3.5 bya: Stromatolites" to "Layered microbial mats build reef-like structures. The oldest fossils on Earth are stromatolites from Western Australia.",
            "2.4 bya: Great Oxidation Event" to "Cyanobacteria produce oxygen as waste, transforming the atmosphere. Many anaerobic organisms go extinct.",
            "2.0 bya: First Eukaryotes" to "Complex cells with a nucleus evolve — the foundation for all plants, animals, and fungi.",
            "1.2 bya: Sexual Reproduction" to "Recombination of genes accelerates evolution by creating more genetic variation.",
            "600 mya: First Animals" to "Soft-bodied multicellular animals appear — the Ediacaran biota. No shells or skeletons yet.",
            "541 mya: Cambrian Explosion" to "Almost all modern animal body plans appear in just 20 million years. Shells and hard parts make fossilization common.",
            "470 mya: First Land Plants" to "Plants colonize land, transforming barren continents into green ecosystems.",
            "370 mya: First Tetrapods" to "Fish evolve limbs and crawl onto land. Tiktaalik is the iconic transitional fossil.",
            "230 mya: First Dinosaurs" to "Small bipedal predators like Herrerasaurus appear. Dinosaurs will dominate for 165 million years.",
            "150 mya: First Birds" to "Archaeopteryx evolves from theropod dinosaurs — feathers, wings, but still teeth and a bony tail.",
            "65 mya: First Primates" to "After the asteroid, small tree-dwelling primates evolve. The lineage that leads to humans begins.",
            "300 kya: Homo sapiens" to "Modern humans appear in Africa — the newest species in a 3.7-billion-year story.",
        )
        milestones.forEach { (title, desc) ->
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
            // Book-style illustration: Tiktaalik after First Tetrapods
            if (title == "370 mya: First Tetrapods") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = PREHISTORIC_IMG_TIKTAALIK,
                        contentDescription = "Tiktaalik fossil showing early limb bones in fin structure",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 Tiktaalik had wrist bones in its fins \u2014 the first step toward walking on land.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // Book-style illustration: Archaeopteryx after First Birds
            if (title == "150 mya: First Birds") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "\u2192 Archaeopteryx had feathers and wings but also teeth and a long bony tail.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextLow,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                    )
                    BookStyleImage(
                        imageUrl = PREHISTORIC_IMG_ARCHAEOPTERYX,
                        contentDescription = "Archaeopteryx fossil slab showing feather impressions",
                    )
                }
            }
        }
    }
}

@Composable
private fun DinosaurFactsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF8BBF6A)) {
        val accentColor = Color(0xFF8BBF6A)
        Text(
            "Everything You Should Know About Dinosaurs",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        val facts = listOf(
            "Dinosaurs are NOT extinct" to "Birds are living dinosaurs — literally descended from small theropod dinosaurs. So dinosaurs are all around you. T. rex is more closely related to a chicken than to Stegosaurus.",
            "Dinosaurs ruled for 165 million years" to "From the Late Triassic (~230 mya) to the end-Cretaceous extinction (~66 mya). Humans have existed for less than 0.1% of that time.",
            "Not everything old is a dinosaur" to "Pterosaurs (flying reptiles), plesiosaurs, mosasaurs, and ichthyosaurs (marine reptiles), and Dimetrodon (a synapsid/mammal ancestor) are NOT dinosaurs. They lived alongside them but are separate groups.",
            "Two main groups" to "Saurischia (lizard-hipped: theropods like T. rex + sauropods like Brachiosaurus) and Ornithischia (bird-hipped: Triceratops, Stegosaurus, Iguanodon). Ironically, birds evolved from the lizard-hipped group.",
            "Warm-blooded debate" to "Most paleontologists now believe dinosaurs were warm-blooded (endothermic) or at least mesothermic — somewhere between reptile and mammal metabolisms. This allowed active hunting and sustained growth.",
            "Feathers were common" to "Many theropods had feathers — not just for flight, but for display, insulation, and brooding. Even T. rex likely had feathered young. Velociraptor was fully feathered.",
            "The biggest ever" to "Argentinosaurus may have reached 100+ feet and 70-100 tons. The largest predator was Spinosaurus at ~50 feet — larger than T. rex.",
            "The smallest" to "Anchiornis and Microraptor were about the size of a crow. Some dinosaurs were smaller than many birds alive today.",
            "How they died" to "The Chicxulub asteroid (6 miles wide) hit Mexico 66 mya, creating the 110-mile crater. The impact winter lasted years, killing 76% of species. Only small theropods (birds) survived.",
        )
        facts.forEach { (title, desc) ->
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
            // Book-style illustration: dinosaur skeleton after "Dinosaurs ruled"
            if (title == "Dinosaurs ruled for 165 million years") {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Top) {
                    BookStyleImage(
                        imageUrl = PREHISTORIC_IMG_SKELETON,
                        contentDescription = "Dinosaur skeleton mounted in a natural history museum",
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "\u2192 165 million years of dominance \u2014 mammals existed the entire time but stayed small.",
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

@Composable
private fun CategoryHeader(category: String, count: Int, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accent)
                .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelMedium,
            color = TextMid,
        )
    }
}

@Composable
private fun OrganismCard(
    organism: Specimen,
    accent: Color,
    onClick: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit = { _, _ -> },
) {
    val imageUrls = SpecimenImages.urls[organism.id] ?: organism.imageUrls

    DarkCard(
        accent = accent,
        modifier = Modifier
            .width(160.dp)
            .height(260.dp)
            .clickable(onClick = onClick),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.radialGradient(listOf(accent.copy(alpha = 0.28f), Color(0xFF1A1812)))
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = imageUrls.first(),
                    contentDescription = organism.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onPhotoClick(imageUrls, 0) },
                    contentScale = ContentScale.Crop,
                )
                if (imageUrls.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .glowingBorder(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text("${imageUrls.size}", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            } else {
                Text(text = organism.emoji, style = MaterialTheme.typography.displaySmall)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = organism.name,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        Text(
            text = organism.tagline,
            style = MaterialTheme.typography.labelSmall,
            color = com.rork.rockscout.ui.components.brightenForText(accent, amount = 0.5f),
            fontStyle = FontStyle.Italic,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            organism.geologicalPeriod?.let {
                TagChip(it, color = accent)
            }
        }
    }
}
