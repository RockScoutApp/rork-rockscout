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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
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
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Success

@Composable
fun MineralCareGuideScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "mineral_care") {
        navController.navigate(Routes.PAYWALL)
    }
    ScreenScaffold(title = "Mineral Care & Cleaning", onBack = { navController.popBackStack() }) {
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
                                listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Text(
                        "You just found a beautiful specimen — now don't ruin it! Many minerals are delicate, water-soluble, or acid-sensitive. This guide covers safe cleaning methods for every type of mineral you'll encounter.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMid,
                    )
                }
            }

            item { SectionHeader("GENERAL RULES") }
            item { GeneralRulesCard() }

            item { SectionHeader("WATER-SOLUBLE MINERALS") }
            item { WaterSolubleCard() }

            item { SectionHeader("ACID-SENSITIVE MINERALS") }
            item { AcidSensitiveCard() }

            item { SectionHeader("SOFT & DELICATE MINERALS") }
            item { SoftDelicateCard() }

            item { SectionHeader("SAFE TO CLEAN WITH WATER") }
            item { WaterSafeCard() }

            item { SectionHeader("MECHANICAL CLEANING") }
            item { MechanicalCleaningCard() }

            item { SectionHeader("STORAGE & DISPLAY") }
            item { StorageDisplayCard() }
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
private fun GeneralRulesCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        val rules = listOf(
            "Always test a small, inconspicuous area first" to "Before cleaning any specimen, test your method on a hidden corner or a loose fragment. Better to damage a tiny bit than the whole specimen.",
            "Know your mineral before you clean" to "Identify the mineral first — its hardness, solubility, and chemical sensitivity determine what's safe. When in doubt, don't clean it at all.",
            "Start with the gentlest method" to "Dry brushing with a soft paintbrush is always step one. Escalate only if needed — water, then mild soap, then specialized methods.",
            "Never use commercial jewelry cleaners" to "Ultrasonic and steam cleaners can shatter delicate crystals, dissolve soluble minerals, and damage treatments. They're designed for hard gemstones, not collector specimens.",
            "Use room-temperature water" to "Hot water can cause thermal shock in some minerals, cracking them instantly. Lukewarm or room-temperature water is always safer.",
            "Pat dry — never air dry" to "Air drying leaves water spots and can allow dissolved minerals to re-deposit on the surface. Gently pat with a soft microfiber cloth.",
        )
        rules.forEach { (title, desc) ->
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
private fun WaterSolubleCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Danger) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Never clean with water!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "These minerals dissolve or degrade in water. Even humidity in the air can damage some of them. Clean only with a dry soft brush or compressed air.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val minerals = listOf(
            "Halite (salt)" to "Dissolves readily in water. Even sweaty fingers can damage it. Store with a desiccant packet.",
            "Sylvite" to "Potassium salt — even more soluble than halite. Handle with dry gloves only.",
            "Gypsum / Selenite" to "Soft (H2) and slightly soluble. Water leaves a frosted, etched surface. Clean with a dry brush only.",
            "Epsomite" to "Magnesium sulfate — highly soluble. Crumbles in humid conditions. Store in an airtight container.",
            "Copper sulfate (Chalcanthite)" to "Dissolves in water and is toxic. Never wash — dust with a dry brush in a ventilated area.",
            "Ulexite (TV rock)" to "Fibrous structure absorbs water and separates. Dry brush only.",
            "Stibnite" to "Not water-soluble but extremely soft (H2) and the crystals are fragile blades. Dry dust only.",
        )
        minerals.forEach { (name, desc) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = Danger, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleSmall, color = Danger, fontWeight = FontWeight.SemiBold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
    }
}

@Composable
private fun AcidSensitiveCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFFE8A33D)) {
        Text(
            "Acid-sensitive minerals",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Carbonate minerals react with even weak acids (vinegar, lemon juice). Acid cleaning will dissolve the surface, ruining the luster and crystal faces. Some cleaning products contain acids — always check labels.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val minerals = listOf(
            "Calcite" to "Fizzes in contact with any acid, even weak vinegar. The acid etches the surface, destroying crystal luster. Clean with distilled water and a soft brush — never acid.",
            "Dolomite" to "Reacts with warm or strong acid. Same care as calcite — avoid all acids.",
            "Aragonite" to "Same chemistry as calcite (CaCO₃) — acid will dissolve it.",
            "Malachite" to "Copper carbonate — acid dissolves the surface and removes the beautiful banding. Clean with mild soapy water only.",
            "Azurite" to "Copper carbonate — extremely acid-sensitive. Even acid fumes can darken and damage the blue color.",
            "Siderite" to "Iron carbonate — reacts with acid. Avoid acidic cleaners.",
            "Smithsonite" to "Zinc carbonate — acid will etch the botryoidal surface.",
        )
        minerals.forEach { (name, desc) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFE8A33D), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleSmall, color = Color(0xFFE8A33D), fontWeight = FontWeight.SemiBold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
    }
}

@Composable
private fun SoftDelicateCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        Text(
            "Soft & delicate minerals (Mohs ≤ 3)",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Minerals with hardness 3 or below scratch easily — even a fingernail (H2.5) can damage them. Never scrub, never use abrasive cloths, and avoid ultrasonic cleaners entirely.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val minerals = listOf(
            "Talc (H1)" to "The softest mineral — scratched by everything. Brush gently with a soft artist brush. Do not wash — it's also slightly water-sensitive.",
            "Gypsum / Selenite (H2)" to "Scratch with a fingernail. Clean with a dry brush. Water causes surface frosting.",
            "Stibnite (H2)" to "Bladed crystals snap easily. Dust with a soft brush — never touch the crystal blades directly.",
            "Mica group (H2–2.5)" to "Perfect cleavage — the sheets peel apart. Brush dust off gently; never use water pressure.",
            "Galena (H2.5)" to "Heavy but soft — dents easily. Clean with mild soapy water and pat dry. Avoid scrubbing.",
            "Chlorite (H2–2.5)" to "Soft and flaky. Dry brush only — water can cause the plates to separate.",
            "Calcite (H3)" to "Soft and cleaves easily. Mild soapy water is fine but avoid scrubbing and all acids.",
            "Barite (H3–3.5)" to "Brittle and can cleave. Mild soapy water with gentle dabbing is safe.",
        )
        minerals.forEach { (name, desc) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFF9B7BD8), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleSmall, color = Color(0xFF9B7BD8), fontWeight = FontWeight.SemiBold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
    }
}

@Composable
private fun WaterSafeCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Success) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Safe to clean with water",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "These hard, stable minerals can be safely cleaned with mild soapy water and a soft brush. Use distilled or filtered water when possible — tap water can leave mineral deposits.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val minerals = listOf(
            "Quartz (H7)" to "Very durable — soapy water, soft brush, even mild ultrasonic for heavily soiled specimens. Rinse with distilled water and pat dry.",
            "Corundum (H9)" to "Extremely hard and chemically stable. Any cleaning method is safe.",
            "Topaz (H8)" to "Hard and durable, but has perfect cleavage — avoid thermal shock (no hot water).",
            "Beryl (H7.5–8)" to "Durable and water-safe. Mild soap and a soft brush work well.",
            "Garnet (H6.5–7.5)" to "Tough and stable — safe with soapy water and brushing.",
            "Tourmaline (H7–7.5)" to "Durable but can be heat-sensitive. Room-temperature soapy water only.",
            "Feldspar group (H6–6.5)" to "Safe with mild soapy water. Avoid strong acids which can weather the surface.",
            "Diamond (H10)" to "The most durable mineral — any cleaning method is safe, including ultrasonic.",
        )
        minerals.forEach { (name, desc) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Success, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleSmall, color = Success, fontWeight = FontWeight.SemiBold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
    }
}

@Composable
private fun MechanicalCleaningCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
        Text(
            "Mechanical cleaning tools",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "When water isn't enough, mechanical methods can remove stubborn dirt, clay, and matrix. Use these carefully — they can scratch soft minerals.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val tools = listOf(
            "Soft paintbrush (1–2 inch)" to "The #1 tool for every collector. Removes loose dust and dirt without scratching anything. Start here always.",
            "Dental pick / probe" to "Great for picking clay out of crystal crevices. Use gently — the tip is steel and will scratch anything softer than H5.",
            "Wooden toothpicks" to "Softer than metal — safe for delicate work around crystal faces. Snap them to create a custom angled tip.",
            "Compressed air (canned air)" to "Blows dust out of deep vugs and crystal pockets. Use short bursts and hold the can upright to avoid freezing propellant.",
            "Water pik (dental flosser)" to "A gentle water pik on low pressure can remove clay from crystal clusters without scrubbing. Use distilled water.",
            "Brass brush" to "Softer than steel — safe on quartz and harder minerals (H7+). Never use on soft minerals; brass will scratch them.",
            "Sonic cleaning (NOT ultrasonic)" to "A gentle vibrating pen can loosen dirt without the intense cavitation of an ultrasonic bath. Safer for fragile specimens.",
        )
        tools.forEach { (name, desc) ->
            Text(
                name,
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

@Composable
private fun StorageDisplayCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF6FA8C7)) {
        val accentColor = Color(0xFF6FA8C7)
        Text(
            "Long-term storage & display",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Proper storage is just as important as careful cleaning. Many beautiful specimens have been ruined by bad display choices.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val tips = listOf(
            "Keep specimens out of direct sunlight" to "UV light fades amethyst, fluorite, citrine, and many other colored minerals over time. Display in indirect light or use UV-filtering glass.",
            "Control humidity" to "Water-soluble minerals (halite, sylvite, gypsum) need a dry environment. Store with silica gel desiccant packets and check them monthly.",
            "Avoid touching specimens with bare hands" to "Skin oils leave fingerprints that attract dust and can etch some minerals over time. Handle by the matrix or wear cotton gloves.",
            "Use individual containers or egg crate foam" to "Specimens rubbing against each other causes scratches and chip. Egg crate foam is cheap, soft, and perfect for shallow drawers.",
            "Label everything immediately" to "Write the specimen name, location found, and date on a small card or label tape. You will forget — every collector does. Keep labels with specimens permanently.",
            "Acid fumes from wood cabinets" to "Some woods (oak, cedar) release acidic vapors over time that can damage carbonates and metals. Line wooden shelves with inert foam or use metal/glass cabinets.",
            "Separate soft and hard minerals" to "Don't store a H1 talc specimen next to a H7 quartz — if they touch, the quartz will scratch the talc.",
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
        }
    }
}
