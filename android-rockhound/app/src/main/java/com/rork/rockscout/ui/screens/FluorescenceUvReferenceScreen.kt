package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Amethyst

@Composable
fun FluorescenceUvReferenceScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "fluorescence_uv") {
        navController.navigate(Routes.PAYWALL)
    }
    ScreenScaffold(title = "Fluorescence & UV Reference", onBack = { navController.popBackStack() }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A1520), Color(0xFF14101A), Color(0xFF0E0A12))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Text(
                        "Some minerals glow under ultraviolet light! This is called fluorescence — the mineral absorbs invisible UV energy and re-emits it as visible light. The colors below tell you which minerals to look for and what color they'll glow.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMid,
                    )
                }
            }

            item { FluorSectionHeader("HOW FLUORESCENCE WORKS") }
            item { HowItWorksCard() }

            item { FluorSectionHeader("UV LIGHT TYPES") }
            item { UvLightTypesCard() }

            item { FluorSectionHeader("FLUORESCENT MINERALS BY COLOR") }
            item { FluorescentMineralsCard() }

            item { FluorSectionHeader("THERMOLUMINESCENT & TRIBOLUMINESCENT") }
            item { SpecialLuminescenceCard() }

            item { FluorSectionHeader("FIELD TIPS FOR UV COLLECTING") }
            item { FieldTipsCard() }
        }
    }
}

@Composable
private fun FluorSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Amethyst,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun HowItWorksCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Amethyst) {
        Text(
            "When UV light hits a fluorescent mineral, electrons in the crystal lattice absorb the energy and jump to a higher energy state. As they fall back down, they release the excess energy as visible light. The color depends on the mineral's chemistry and trace impurities called 'activators'.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "Common activators:",
            style = MaterialTheme.typography.titleSmall,
            color = Amethyst,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        val activators = listOf(
            "Manganese (Mn²⁺)" to "Causes red, pink, and orange fluorescence in calcite, willemite, and others.",
            "Tungsten (W)" to "Causes blue-white fluorescence in scheelite and calcium minerals.",
            "Uranyl (UO₂²⁺)" to "Causes bright green fluorescence in autunite, hyalite, and other uranium minerals.",
            "Rare earth elements" to "Cause diverse fluorescence in some fluorite and apatite specimens.",
            "Organic impurities" to "Cause white/blue fluorescence in some calcite and diamond specimens.",
            "Structural defects" to "Some minerals fluoresce due to crystal lattice damage, not chemical activators.",
        )
        activators.forEach { (name, desc) ->
            Text(
                name,
                style = MaterialTheme.typography.titleSmall,
                color = Amethyst,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
            )
        }
    }
}

@Composable
private fun UvLightTypesCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text(
            "Three UV wavelengths",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        val types = listOf(
            Triple("Short-wave (SW / 254 nm)", "The most common for mineral fluorescence. Causes the strongest reaction in most fluorescent minerals. Requires a dedicated SW UV lamp — sunlight and blacklights don't emit this wavelength.", Color(0xFF6FA8C7)),
            Triple("Mid-wave (MW / 312 nm)", "Less commonly used but can produce different colors than SW. Some fluorite specimens respond only to MW. Found in specialty UV lamps.", Color(0xFFD9B26A)),
            Triple("Long-wave (LW / 365 nm)", "Also called 'blacklight.' Cheaper and safer. Causes fluorescence in willemite, some fluorite, scapolite, and sodalite. Common in UV flashlights for night collecting.", Color(0xFF9B7BD8)),
        )
        types.forEach { (name, desc, color) ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Safety: Never shine UV light directly into eyes. SW UV is especially hazardous — wear UV-blocking safety glasses when using short-wave lamps.",
            style = MaterialTheme.typography.bodySmall,
            color = Citrine,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private data class FluorescentMineral(
    val name: String,
    val swColor: String,
    val swHex: Long,
    val lwColor: String,
    val notes: String,
)

private val fluorescentMinerals = listOf(
    FluorescentMineral("Fluorite", "Blue / purple / green", 0xFF4488FF, "Blue / green / white", "The most variable fluorescent mineral — color depends on locality and activator. Blue SW is the most common. Some fluorite is fluorescent only under MW. 'Blue John' fluorite from England phosphoresces brightly."),
    FluorescentMineral("Calcite", "Red / pink / orange", 0xFFFF4444, "White / blue / yellow", "Red SW fluorescence from manganese activators. Some calcite phosphoresces (keeps glowing after UV is removed). The most common fluorescent mineral worldwide."),
    FluorescentMineral("Willemite", "Bright green", 0xFF44FF44, "Green", "The signature green of the Franklin, New Jersey fluorescent deposits. So bright it's used to find ore at night with UV lamps."),
    FluorescentMineral("Autunite", "Bright green", 0xFF44FF44, "Green", "Radioactive uranium mineral — glows intense green under SW. Handle with care; wash hands after handling."),
    FluorescentMineral("Hyalite (Opal-AN)", "Green", 0xFF44FF44, "Green", "Uranium-bearing opal from Spruce Pine, NC and other localities. Glows vivid green under SW — a collector favorite."),
    FluorescentMineral("Scheelite", "Bright blue-white", 0xFFEEEEFF, "White", "Tungsten ore mineral. Bright blue-white under SW. Used historically for prospecting tungsten deposits at night."),
    FluorescentMineral("Scapolite", "Yellow / orange", 0xFFFFDD44, "Yellow", "Common in metamorphic rocks. Yellow SW fluorescence is distinctive."),
    FluorescentMineral("Sodalite", "Orange / red", 0xFFFF6600, "Orange", "Some sodalite from Greenland fluoresces bright orange. Hackmanite (a sodalite variety) shows tenebrescence — it changes color in sunlight."),
    FluorescentMineral("Gypsum / Selenite", "White / blue-white", 0xFFEEEEFF, "White", "Often fluoresces white under SW. 'Selenite roses' from Oklahoma can glow beautifully."),
    FluorescentMineral("Diamagnetic (Diamond)", "Blue / green / red", 0xFF44AAFF, "Blue", "About 30% of diamonds fluoresce, usually blue under LW. This is how jewelers screen for diamonds in mixed gravel."),
    FluorescentMineral("Zircon", "Bright yellow / orange", 0xFFFFCC22, "Yellow", "Often fluorescent yellow under SW. Useful for identifying zircon in heavy mineral sands."),
    FluorescentMineral("Wernerite (Scapolite var.)", "Bright yellow", 0xFFFFDD00, "Yellow", "Classic fluorescent mineral from Canada. Intense yellow under SW."),
    FluorescentMineral("Benitoite", "Blue", 0xFF4488FF, "Red", "California's state gemstone. Blue SW, red LW — one of the few minerals with a strong LW response that differs from SW."),
    FluorescentMineral("Ruby (Corundum)", "Red", 0xFFFF0000, "Red", "Natural ruby fluoresces red under LW UV due to chromium. A field test to distinguish ruby from red spinel (which is usually inert)."),
    FluorescentMineral("Adamite", "Bright green", 0xFF44FF44, "Green", "Zinc arsenate — bright green SW fluorescence. Found in oxidized zinc deposits."),
    FluorescentMineral("Margnasite", "Green / white", 0xFF88FF88, "White", "Often found with calcite. Green SW fluorescence distinguishes it from non-fluorescent pyrite."),
    FluorescentMineral("Hydrozincite", "White / blue-white", 0xFFEEEEFF, "White", "Bright white under SW. Found as an oxidation product of sphalerite deposits."),
    FluorescentMineral("Cerussite", "Yellow / white", 0xFFFFFFCC, "White", "Lead carbonate — often fluoresces yellow-white. Sometimes phosphoresces."),
    FluorescentMineral("Powellite", "Yellow / cream", 0xFFFFEEAA, "Yellow", "Calcium molybdate — yellow SW fluorescence. Often found with scheelite."),
    FluorescentMineral("Laumontite", "White", 0xFFEEEEFF, "White", "Zeolite group mineral — white SW fluorescence. Common in basalt vugs."),
    FluorescentMineral("Hardystonite", "Blue-violet", 0xFF8844FF, "Violet", "From Franklin, NJ — a classic fluorescent locality. Distinctive blue-violet SW response."),
    FluorescentMineral("Esperite", "Yellow", 0xFFFFFF44, "Yellow", "Another Franklin, NJ mineral. Bright yellow under SW, distinguishes it from willemite."),
    FluorescentMineral("Margarosanite", "White / blue", 0xFFEEDDFF, "White", "Lead calcium silicate from Franklin, NJ. White-blue SW fluorescence."),
    FluorescentMineral("Pectolite", "Orange / pink", 0xFFFFAA66, "Orange", "Often fluoresces orange-pink under SW. Found in basalt cavities."),
    FluorescentMineral("Axinite", "Red", 0xFFFF4444, "Red", "Complex borosilicate — red SW fluorescence. Sometimes only under SW, not LW."),
)

@Composable
private fun FluorescentMineralsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Amethyst) {
        Text(
            "Fluorescent minerals and their glow colors",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "SW = Short-wave UV (254 nm) · LW = Long-wave UV (365 nm). Colors shown are typical but can vary by locality and activator content.",
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))
        fluorescentMinerals.forEach { mineral ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                // Glow color swatch
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(mineral.swHex).copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(mineral.swHex)),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        mineral.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "SW: ${mineral.swColor} · LW: ${mineral.lwColor}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Amethyst,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        mineral.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextMid,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialLuminescenceCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text(
            "Other types of luminescence",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(14.dp))

        val types = listOf(
            "Phosphorescence" to "The mineral keeps glowing after the UV light is removed. Calcite, willemite, and some fluorite specimens phosphoresce. Some can glow for minutes. A spectacular party trick.",
            "Thermoluminescence" to "The mineral glows when heated (gently!). Some fluorite, apatite, and calcite specimens emit light when warmed to about 100°C. Do NOT test this on valuable specimens — heating can crack them.",
            "Triboluminescence" to "The mineral flashes light when crushed or scratched. Sphalerite, fluorite, and quartz can do this. Try scratching sphalerite in a dark room — you may see brief yellow flashes.",
            "Tenebrescence" to "The mineral changes color when exposed to UV or sunlight, and the change reverses in the dark. Hackmanite (sodalite variety) turns pink in sunlight and fades back to white in darkness. Also called 'reversible photochromism'.",
        )
        types.forEach { (title, desc) ->
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
                color = DarkTextMid,
            )
        }
    }
}

@Composable
private fun FieldTipsCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
        Text(
            "Field tips for UV night collecting",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(14.dp))

        val tips = listOf(
            "Use a SW (254 nm) lamp for the best results" to "SW UV causes the strongest and most colorful fluorescence. A dedicated SW lamp is the single best investment for UV collecting. LW-only flashlights miss many minerals.",
            "Let your eyes dark-adapt for 20 minutes" to "Your eyes need time to adjust to the dark before you can see the fluorescence clearly. Use the Night / Red Light Mode in RockScout to preserve your night vision while using the app.",
            "Scan mine tailings and old dumps" to "Fluorescent minerals are often concentrated in mine waste. The Franklin, NJ area, Spruce Pine, NC, and many uranium mines are famous for UV-collecting spots.",
            "Some minerals only fluoresce when dry" to "Moisture can quench fluorescence. If a specimen isn't glowing, let it dry completely and try again.",
            "Photograph fluorescence in total darkness" to "Use a long exposure with your camera on a tripod. UV-pass filters on your phone camera can help block visible light reflections. The glow colors photograph beautifully.",
            "Label your UV-reactive specimens" to "Write the UV color and wavelength (SW/LW) on the label. A specimen that glows green under SW could be willemite, autunite, or hyalite — the label helps you remember.",
            "Wear UV-protective glasses" to "Especially with SW lamps. UV damage to eyes is cumulative and irreversible. Wrap-around UV-blocking glasses are cheap insurance.",
        )
        tips.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = Aqua,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
        }
    }
}
