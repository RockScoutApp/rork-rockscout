package com.rork.rockscout.data

import com.rork.rockscout.ui.components.BLACK_ROCK_BACKGROUND_URL
import com.rork.rockscout.ui.components.ELEMENT_CATEGORY_BG
import com.rork.rockscout.ui.components.METEORITE_HERO_URL
import com.rork.rockscout.ui.components.METEORITE_IMG_CHONDRITE
import com.rork.rockscout.ui.components.METEORITE_IMG_WIDMANSTATTEN
import com.rork.rockscout.ui.components.METEORITE_IMG_PALLASITE
import com.rork.rockscout.ui.components.METEORITE_IMG_DRY_LAKE_BED
import com.rork.rockscout.ui.components.METEORITE_IMG_MAGNET_CANE
import com.rork.rockscout.ui.components.BLM_HERO_URL
import com.rork.rockscout.ui.components.BLM_IMG_ROCKHOUND
import com.rork.rockscout.ui.components.BLM_IMG_CANYON
import com.rork.rockscout.ui.components.BLM_IMG_CAMPING
import com.rork.rockscout.ui.components.BLM_IMG_TRAILHEAD
import com.rork.rockscout.ui.components.BLM_IMG_PANORAMIC
import com.rork.rockscout.ui.components.BLM_IMG_ROCK_HAMMER
import com.rork.rockscout.ui.components.BLM_IMG_DESERT_VARNISH
import com.rork.rockscout.ui.components.TECTONIC_HERO_URL
import com.rork.rockscout.ui.components.TECTONIC_IMG_DIVERGENT
import com.rork.rockscout.ui.components.TECTONIC_IMG_SUBDUCTION
import com.rork.rockscout.ui.components.TECTONIC_IMG_TRANSFORM
import com.rork.rockscout.ui.components.TECTONIC_IMG_HOTSPOT
import com.rork.rockscout.ui.components.TECTONIC_IMG_BASALT
import com.rork.rockscout.ui.components.GEM_MINERAL_HERO_URL
import com.rork.rockscout.ui.components.GEM_IMG_CUT_GEMS
import com.rork.rockscout.ui.components.GEM_IMG_MUSEUM
import com.rork.rockscout.ui.components.GEM_IMG_LAPIDARY
import com.rork.rockscout.ui.components.GEM_IMG_AMMONITE
import com.rork.rockscout.ui.components.GEM_IMG_PEGMATITE
import com.rork.rockscout.ui.components.GEO_IMG_STREAK_TEST
import com.rork.rockscout.ui.components.GEO_IMG_CRYSTAL_HABIT
import com.rork.rockscout.ui.components.GEO_IMG_MOHS_SCRATCH
import com.rork.rockscout.ui.components.GEO_IMG_ACID_TEST
import com.rork.rockscout.ui.components.PALEO_IMG_TRILOBITE
import com.rork.rockscout.ui.components.PALEO_IMG_AMMONITE
import com.rork.rockscout.ui.components.PALEO_IMG_DINO_TRACK
import com.rork.rockscout.ui.components.PALEO_IMG_PETRIFIED_WOOD
import com.rork.rockscout.ui.components.PALEO_IMG_STROMATOLITE
import com.rork.rockscout.ui.components.PREHISTORIC_IMG_ARCHAEOPTERYX
import com.rork.rockscout.ui.components.PREHISTORIC_IMG_TIKTAALIK
import com.rork.rockscout.ui.components.PREHISTORIC_IMG_SKELETON
import com.rork.rockscout.ui.screens.TECTONIC_IMAGES
import com.rork.rockscout.ui.screens.SPLASH_ICON_URL
import com.rork.rockscout.ui.screens.IDENTIFY_BACKGROUND_URL
import com.rork.rockscout.ui.screens.IDENTIFY_HERO_BACKGROUND_URL

/**
 * Registry of every non-specimen image URL used across the read-only
 * educational/guide/home screens of RockScout.
 *
 * The bulk "Download all images for offline" job pulls both
 * [SpecimenImages.urls] (the specimen photos) AND [all] (these educational
 * illustrations + hero/background images), so the offline claim covers every
 * read-only screen — not just the specimen database.
 *
 * Sources aggregated here:
 * - Home screen tiles + hero backgrounds (Identify, Community, Trip Planner,
 *   Gear Guide banners).
 * - Splash icon.
 * - Periodic-table element category backgrounds + the black rock background.
 * - Rock Info / Geology inline images (streak, crystal habit, Mohs, acid test)
 *   and the four rock-cycle stage illustrations.
 * - Paleontology + Prehistoric Organisms inline illustrations.
 * - Meteorite Hunting hero + inline photos.
 * - BLM Guide hero + inline photos.
 * - Tectonics & Volcanoes hero + gallery + inline photos.
 * - Rock & Gem Resources hero + inline photos.
 * - Achievement tile background illustrations.
 * - Mohs hardness reference mineral photos and infographics (these are defined
 *   on [SpecimenImages] as public `IMG_MOHS_*` constants — they're reference
 *   imagery, not specimen-card photos, so they're listed here too).
 *
 * URLs are deduped by [all] at access time; many sources (e.g. the black rock
 * background used as the "Unknown" element category) overlap.
 */
object EducationalImages {

    /** Home screen banner / tile image URLs (Community, Trip Planner, Gear Guide). */
    private val HOME_BANNERS: List<String> = listOf(
        "https://r2-pub.rork.com/attachments/r6r3hon86cegy20yrqaxy.jpg",
        "https://r2-pub.rork.com/attachments/78k8yy4tgahby3o9opb6j.png",
        "https://r2-pub.rork.com/attachments/abxrhqw66vap6ksxlr670.png",
        // Home tile thumbnails for the educational tabs (Tectonics + Periodic).
        "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/13d72c22-f574-47c4-a23c-a6a9ae6b65bb.png",
        "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/040be3bf-71ab-46d0-b6be-4598df22a18b.png",
    )

    /** Rock-cycle stage illustrations on the Exploring Geology screen. */
    private val ROCK_CYCLE_STAGES: List<String> = listOf(
        "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1377a35e-b615-40f7-90d1-15b9310b1af8.png",
        "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/53df6c87-ab76-4fdd-b590-a792920dc46a.png",
        "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/6876799c-c3c0-4c3f-9587-d861bafc0c72.png",
        "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d58a650b-99fa-4e8a-86bc-d458f5e430c0.png",
    )

    /** Mohs hardness reference minerals + infographics (public on SpecimenImages). */
    private val MOHS_REFERENCE: List<String> = listOf(
        SpecimenImages.IMG_MOHS_1_TALC,
        SpecimenImages.IMG_MOHS_2_GYPSUM,
        SpecimenImages.IMG_MOHS_3_CALCITE,
        SpecimenImages.IMG_MOHS_4_FLUORITE,
        SpecimenImages.IMG_MOHS_5_APATITE,
        SpecimenImages.IMG_MOHS_6_ORTHOCLASE,
        SpecimenImages.IMG_MOHS_7_QUARTZ,
        SpecimenImages.IMG_MOHS_8_TOPAZ,
        SpecimenImages.IMG_MOHS_9_CORUNDUM,
        SpecimenImages.IMG_MOHS_10_DIAMOND,
        SpecimenImages.IMG_MOHS_SCALE_INFOGRAPHIC,
        SpecimenImages.IMG_MOHS_SCRATCH_TEST_CHART,
        SpecimenImages.IMG_MOHS_ABSOLUTE_HARDNESS,
        SpecimenImages.IMG_MOHS_FIELD_TEST_KIT,
        SpecimenImages.IMG_MOHS_SCRATCH_TEST_STEPS,
        SpecimenImages.IMG_MOHS_COMMON_ITEMS,
        SpecimenImages.IMG_MOHS_ALL_MINERALS_GRID,
    )

    /** Every inline / hero / background image URL used across the educational
     *  and guide screens, plus the home banners, splash, and identify
     *  backgrounds. Deduped and filtered to http(s) URLs only. */
    val all: List<String> by lazy {
        buildList {
            // Home banners + tile thumbnails + hero backgrounds + splash.
            addAll(HOME_BANNERS)
            add(IDENTIFY_HERO_BACKGROUND_URL)
            add(IDENTIFY_BACKGROUND_URL)
            add(SPLASH_ICON_URL)

            // Periodic table element-category backgrounds (map values) + the
            // shared black rock background constant.
            addAll(ELEMENT_CATEGORY_BG.values)
            add(BLACK_ROCK_BACKGROUND_URL)

            // Geology inline images + rock-cycle stages.
            add(GEO_IMG_STREAK_TEST)
            add(GEO_IMG_CRYSTAL_HABIT)
            add(GEO_IMG_MOHS_SCRATCH)
            add(GEO_IMG_ACID_TEST)
            addAll(ROCK_CYCLE_STAGES)

            // Paleontology + Prehistoric Organisms.
            add(PALEO_IMG_TRILOBITE)
            add(PALEO_IMG_AMMONITE)
            add(PALEO_IMG_DINO_TRACK)
            add(PALEO_IMG_PETRIFIED_WOOD)
            add(PALEO_IMG_STROMATOLITE)
            add(PREHISTORIC_IMG_ARCHAEOPTERYX)
            add(PREHISTORIC_IMG_TIKTAALIK)
            add(PREHISTORIC_IMG_SKELETON)

            // Meteorite Hunting.
            add(METEORITE_HERO_URL)
            add(METEORITE_IMG_CHONDRITE)
            add(METEORITE_IMG_WIDMANSTATTEN)
            add(METEORITE_IMG_PALLASITE)
            add(METEORITE_IMG_DRY_LAKE_BED)
            add(METEORITE_IMG_MAGNET_CANE)

            // BLM Guide.
            add(BLM_HERO_URL)
            add(BLM_IMG_ROCKHOUND)
            add(BLM_IMG_CANYON)
            add(BLM_IMG_CAMPING)
            add(BLM_IMG_TRAILHEAD)
            add(BLM_IMG_PANORAMIC)
            add(BLM_IMG_ROCK_HAMMER)
            add(BLM_IMG_DESERT_VARNISH)

            // Tectonics & Volcanoes.
            add(TECTONIC_HERO_URL)
            addAll(TECTONIC_IMAGES)
            add(TECTONIC_IMG_DIVERGENT)
            add(TECTONIC_IMG_SUBDUCTION)
            add(TECTONIC_IMG_TRANSFORM)
            add(TECTONIC_IMG_HOTSPOT)
            add(TECTONIC_IMG_BASALT)

            // Rock & Gem Resources.
            add(GEM_MINERAL_HERO_URL)
            add(GEM_IMG_CUT_GEMS)
            add(GEM_IMG_MUSEUM)
            add(GEM_IMG_LAPIDARY)
            add(GEM_IMG_AMMONITE)
            add(GEM_IMG_PEGMATITE)

            // Mohs reference minerals + infographics.
            addAll(MOHS_REFERENCE)

            // Achievement tile background illustrations.
            addAll(AchievementBackgrounds.urls)
        }
            .distinct()
            .filter { it.isNotBlank() && it.startsWith("http") }
    }
}
