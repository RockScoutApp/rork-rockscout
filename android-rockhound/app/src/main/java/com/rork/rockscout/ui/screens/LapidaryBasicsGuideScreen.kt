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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Amethyst

@Composable
fun LapidaryBasicsGuideScreen(navController: NavController) {
    InterstitialAdTrigger(screenKey = "lapidary_basics") {
        navController.navigate(Routes.PAYWALL)
    }
    ScreenScaffold(title = "Lapidary Basics", onBack = { navController.popBackStack() }) {
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
                        "Lapidary is the art of cutting, shaping, and polishing stones into gems, cabochons, and decorative objects. It's the natural next step for rockhounds who want to turn their finds into jewelry or display pieces. This guide covers the fundamentals — from selecting rough to your first finished cab.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMid,
                    )
                }
            }

            item { LapSectionHeader("GETTING STARTED") }
            item { GettingStartedCard() }

            item { LapSectionHeader("SELECTING ROUGH") }
            item { SelectingRoughCard() }

            item { LapSectionHeader("CUTTING & SLABBING") }
            item { CuttingSlabbingCard() }

            item { LapSectionHeader("MAKING A CABOCHON") }
            item { CabochonCard() }

            item { LapSectionHeader("POLISHING") }
            item { PolishingCard() }

            item { LapSectionHeader("TOOLS & EQUIPMENT") }
            item { ToolsEquipmentCard() }

            item { LapSectionHeader("SAFETY") }
            item { SafetyCard() }
        }
    }
}

@Composable
private fun LapSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Amethyst,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun GettingStartedCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Amethyst) {
        Text(
            "What is lapidary?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Lapidary is the craft of working with stone to create decorative or functional objects. The three main branches are:",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val branches = listOf(
            "Tumbling" to "The simplest entry point — place rough stones in a rotating barrel with abrasive grit and let it run for weeks. Produces polished nuggets perfect for collecting, gifts, and vase fillers. Great for beginners and kids.",
            "Cabbing (Cabochon cutting)" to "Cutting a stone into a domed shape with a flat back — the classic 'cab.' The most common lapidary skill. Cabs are set in pendants, rings, and belt buckles. Requires a cabbing machine or flat lap.",
            "Faceting" to "Cutting precise flat faces (facets) onto transparent stones to maximize brilliance and fire. The most technical and expensive branch — requires a faceting machine with angular precision. Reserved for transparent gemstones like quartz, topaz, and corundum.",
        )
        branches.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = Amethyst,
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
private fun SelectingRoughCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text(
            "Choosing the right rough",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Not every rock is worth cutting. Before investing time and abrasives, evaluate your rough:",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val tips = listOf(
            "Hardness matters most" to "Stones below H5 are too soft to take a good polish — they scratch easily and look dull. Aim for H6+. Quartz (H7), agate (H7), jasper (H7), and tiger's eye (H7) are ideal for beginners.",
            "Check for fractures and inclusions" to "Stones with internal cracks will break during cutting or polishing. Hold the rough up to a bright light and look for dark lines or veils. Surface fractures can sometimes be ground away, but internal ones are dealbreakers.",
            "Look for interesting patterns" to "Banding (agate), chatoyancy (tiger's eye), color zoning (fluorite), and dendritic inclusions all create striking cabs. Solid color stones are beautiful but patterned stones are more unique.",
            "Avoid porous or crumbly material" to "Rocks that flake, crumble, or absorb water won't polish well. Test by pressing a fingernail into a hidden area — if it leaves a mark, the stone is too soft or porous.",
            "Start with agate and jasper" to "They're hard, tough, take a superb polish, and are inexpensive. You'll learn every fundamental technique on agate before risking expensive rough.",
            "Size matters for cabs" to "A finished cabochon is typically 18–40mm. You need rough at least 50% larger than the finished size to account for cutting and grinding waste. A 30mm cab needs at least 45mm of rough.",
        )
        tips.forEach { (title, desc) ->
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
private fun CuttingSlabbingCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
        Text(
            "Cutting and slabbing",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Before shaping a cab, you need to cut your rough into a slab (a flat slice) and then trim it to a workable preform. This requires a trim saw and optionally a slab saw.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val steps = listOf(
            "1. Slab the rough" to "Use a slab saw (or trim saw for small pieces) to cut the rough into flat slices 5–8mm thick. Cut perpendicular to any banding to show the best patterns. Keep the blade wet — always cut with coolant (water).",
            "2. Select the best slab" to "Look at each slab and choose the one with the most interesting pattern or color. A slab that looks plain wet may look spectacular when polished — wetting a slab gives a preview of the polished appearance.",
            "3. Mark the cab outline" to "Use a template (a metal or plastic cabochon stencil) to trace the desired shape onto the slab with an aluminum pencil or marker. Choose a template size that fits within the slab with a few mm to spare.",
            "4. Trim the preform" to "Use a trim saw to cut the slab close to your marked outline — leave 2–3mm of extra material for grinding. The preform is the rough shape that will be refined on the cabbing machine.",
        )
        steps.forEach { (title, desc) ->
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

@Composable
private fun CabochonCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Success) {
        Text(
            "Making a cabochon — step by step",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "A cabochon has a domed top and a flat back. The process uses progressively finer grits — each step removes the scratches from the previous step until the surface is mirror-smooth.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val steps = listOf(
            "Step 1: Grind the outline (100–220 grit)" to "Use the coarse wheel to grind the preform to your template outline. Keep the stone moving — don't hold it in one spot or you'll create flat spots. Keep a container of water nearby and dip the stone frequently to keep it cool and prevent burns.",
            "Step 2: Grind the flat back (100–220 grit)" to "Flatten the back of the cab on a flat lap or the side of the grinding wheel. The back must be perfectly flat for setting. Keep it wet and check against a straightedge.",
            "Step 3: Create the dome (220–400 grit)" to "Bevel the edges at 45° first, then grind the top into a smooth dome. Work from the edges toward the center. The dome should be a gentle, even curve — no bumps or flat spots. Check by looking at reflections on the surface.",
            "Step 4: Fine grind (600 grit)" to "Switch to a 600-grit wheel. Grind the entire cab surface until all scratches from the 400 grit are gone. This is the most important step — any scratches left now will be visible in the final polish. Hold the stone up to a light to check for scratch shadows.",
            "Step 5: Pre-polish (1200 grit or finer)" to "Use a 1200-grit or pre-polish wheel. The surface should start looking shiny with a satiny finish. This step removes the last visible scratches. If you see any deep scratches, go back to 600 grit.",
            "Step 6: Final polish (polishing compound)" to "Apply a polishing compound (cerium oxide for agate/quartz, aluminum oxide for softer stones) to a felt or leather polishing wheel. Polish the dome until it has a mirror finish. Light pressure and keep it slightly damp.",
            "Step 7: Polish the back" to "Give the flat back a quick polish too — it makes the finished cab look professional and prevents the back from scratching a setting.",
        )
        steps.forEach { (title, desc) ->
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = Success,
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
private fun PolishingCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Color(0xFF9B7BD8)) {
        val accentColor = Color(0xFF9B7BD8)
        Text(
            "Polishing compounds by stone type",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Different stones require different polishing compounds. Using the wrong one can leave a dull finish or even scratch the surface.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextMid,
        )
        Spacer(Modifier.height(14.dp))

        val compounds = listOf(
            "Cerium oxide" to "The gold standard for quartz, agate, jasper, and chalcedony (all H7 silicates). Produces a bright, glassy polish. Use on a felt or leather pad with light pressure.",
            "Aluminum oxide (Al₂O₃)" to "Good all-purpose polish for harder stones (H6+). Works on corundum, topaz, and spinel. Also used as a pre-polish for softer stones.",
            "Tin oxide" to "Excellent for softer stones — malachite, turquoise, lapis, and opal. Gentler than cerium and won't scratch soft minerals. Use on a felt pad.",
            "Diamond paste (50k–100k)" to "For the hardest stones — diamond, sapphire, ruby. Extremely fine diamond grit produces a flawless polish. Expensive but a little goes a long way.",
            "Tripoli" to "A natural abrasive for pre-polishing softer stones. Good for turquoise, howlite, and serpentine. Follow with tin oxide for the final polish.",
            "Linde A / Linde B" to "Synthetic aluminum oxide powders. Linde A for hard stones, Linde B for softer. Very fine, produce excellent polish on challenging materials.",
        )
        compounds.forEach { (name, desc) ->
            Text(
                name,
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

@Composable
private fun ToolsEquipmentCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) {
        Text(
            "Essential tools & equipment",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(14.dp))

        val tools = listOf(
            "Rock tumbler ($80–150)" to "The cheapest way to start. A rotary tumbler with 3 lb barrel, 4 stages of grit (coarse, medium, fine, polish), and ceramic media. Runs for 3–4 weeks per batch.",
            "Trim saw 6\" ($150–300)" to "A small diamond blade saw for cutting slabs and trimming preforms. Essential for cabbing. Uses water as coolant.",
            "Cabbing machine ($400–1200)" to "A machine with 6–8 grinding wheels of progressive grits (100, 220, 400, 600, 1200, polish). The core tool for making cabochons. Cheaper kits use individual wheels on a single arbor.",
            "Dop wax and dop sticks" to "Wax used to attach the stone to a stick (dop stick) for easier handling during fine grinding and polishing. The stone is heated, pressed into melted wax on the stick, then popped off with heat after finishing.",
            "Templates (cabochon stencils)" to "Metal or plastic stencils with standard cabochon shapes (oval, round, teardrop, square). Used to trace the outline on the slab. $10–20 for a full set.",
            "Calipers" to "For measuring the finished cab thickness and ensuring consistency. Digital calipers are $15–25.",
            "Safety glasses and dust mask" to "Mandatory. Stone dust contains silica — inhalation causes silicosis. Always cut wet (which suppresses dust) and wear a mask when dry-sanding.",
        )
        tools.forEach { (name, desc) ->
            Text(
                name,
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
private fun SafetyCard() {
    DarkCard(modifier = Modifier.fillMaxWidth(), accent = Danger) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Lapidary safety",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(14.dp))

        val warnings = listOf(
            "Silica dust causes silicosis — a permanent, incurable lung disease" to "Always cut and grind WET. Water suppresses dust. Never dry-cut stone. Wear a P100 respirator when dry-sanding or cleaning up sludge.",
            "Always wear safety glasses" to "Stone fragments can fly off the grinding wheel. ANSI-rated safety glasses or a face shield are mandatory around any saw or grinder.",
            "Never cut stones containing asbestos" to "Some stones (serpentine, tiger's eye in raw form, some jaspers) may contain asbestos fibers. Research your material before cutting. When in doubt, cut wet and wear a respirator.",
            "Secure your workpiece" to "A stone that slips from your fingers on a grinding wheel becomes a projectile. Use dop sticks for small pieces and keep a firm grip on larger ones. Never force a stone into the wheel.",
            "Keep electrical equipment dry" to "Cabbing machines use water but the motor and electrical connections must stay dry. Use GFCI-protected outlets. Never reach into the water tray with wet hands while the machine is plugged in.",
            "Toxic materials" to "Some lapidary materials are toxic: malachite (copper), cinnabar (mercury), realgar (arsenic), chrysocolla (copper). Cut wet, wash hands after handling, and never eat or drink while working.",
            "Hearing protection" to "Grinding wheels and slab saws are loud enough to cause hearing damage over time. Wear earplugs or earmuffs during extended cutting sessions.",
        )
        warnings.forEach { (title, desc) ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = Danger, fontWeight = FontWeight.SemiBold)
                    Text(desc, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
                }
            }
        }
    }
}
