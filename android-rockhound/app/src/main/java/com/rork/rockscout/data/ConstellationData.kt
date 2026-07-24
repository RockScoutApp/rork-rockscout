package com.rork.rockscout.data

/**
 * All 88 IAU constellations with key data for star charts, mythology, and viewing.
 * The 12 most famous constellations include star chart positions and hero image URLs.
 */

data class ConstellationEntry(
    val name: String,
    val abbr: String,
    val hemisphere: String,       // "Northern", "Southern", "Equatorial"
    val brightestStar: String,
    val description: String,
    val mythology: String = "",
    val bestSeason: String = "",
    val rightAscension: String = "",
    val declination: String = "",
    val starChartStars: List<com.rork.rockscout.ui.components.ChartStar> = emptyList(),
    val starChartLines: List<com.rork.rockscout.ui.components.StarLine> = emptyList(),
    val heroImageUrl: String? = null,
)

object ConstellationData {

    // ── Hero image URLs for the 12 famous constellations ──
    const val ORION_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1a0ae7eb-e084-4492-b95f-bf6f04b2a2f4.png"
    const val URSA_MAJOR_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/3edc2a9b-b492-4cf0-ac90-e21379295681.png"
    const val CASSIOPEIA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/f616fdaa-501d-495b-bc67-e6ecd0610d65.png"
    const val LEO_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1ef2f674-e0af-4e88-8ae9-c23417c40983.png"
    const val SCORPIUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e24b4049-96f9-4f0f-b108-d0ff8a09b02b.png"
    const val CYGNUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/cb3f54d8-01f4-4c6f-ae35-20fdd9853d0a.png"
    const val LYRA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/033ab0e2-688b-4531-8875-14bda3d7fd2e.png"
    const val TAURUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/43d7e12d-4f47-4069-9a9a-5ca06d487f4f.png"
    const val GEMINI_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/179fbb4c-d77c-4595-9654-e172cc587b37.png"
    const val CANIS_MAJOR_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/5e064532-e383-4aa7-827d-c6cfca928f78.png"
    const val ANDROMEDA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/26caf60f-7783-4df3-9e0b-d85efc3020f7.png"
    const val SAGITTARIUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/53b72794-f7f4-4e09-ae22-57a0d36168ac.png"

    // ── Star chart helper ──
    private fun s(x: Float, y: Float, mag: Float, name: String = "") =
        com.rork.rockscout.ui.components.ChartStar(x, y, mag, name)
    private fun l(from: Int, to: Int) = com.rork.rockscout.ui.components.StarLine(from, to)

    // ── 12 famous constellations with star charts ──
    private val orion = ConstellationEntry(
        name = "Orion", abbr = "Ori", hemisphere = "Equatorial", brightestStar = "Rigel (β)",
        description = "The Hunter — one of the most recognizable constellations, featuring the iconic Belt of Orion (three bright stars in a row).",
        mythology = "In Greek mythology, Orion was a great hunter. He was placed in the sky by Zeus, where he hunts with his two dogs (Canis Major and Minor) across the winter sky.",
        bestSeason = "Winter (Nov–Mar)", rightAscension = "5h", declination = "+5°",
        starChartStars = listOf(
            s(0.35f, 0.2f, 0.1f, "Betelgeuse"), s(0.65f, 0.25f, 1.6f, "Bellatrix"),
            s(0.40f, 0.5f, 1.7f, "Alnitak"), s(0.50f, 0.5f, 1.8f, "Alnilam"), s(0.60f, 0.5f, 2.2f, "Mintaka"),
            s(0.30f, 0.8f, 0.1f, "Rigel"), s(0.70f, 0.75f, 2.1f, "Saiph"),
            s(0.48f, 0.35f, 4.0f, "Hat"), s(0.52f, 0.4f, 3.0f, ""),
        ),
        starChartLines = listOf(l(0,1), l(0,2), l(1,4), l(2,3), l(3,4), l(2,5), l(4,6), l(0,7), l(1,8)),
        heroImageUrl = ORION_URL,
    )

    private val ursaMajor = ConstellationEntry(
        name = "Ursa Major", abbr = "UMa", hemisphere = "Northern", brightestStar = "Alioth (ε)",
        description = "The Great Bear — contains the Big Dipper asterism, one of the most recognized star patterns in the northern sky.",
        mythology = "In Greek mythology, Zeus transformed Callisto into a bear to hide her from Hera. Zeus then placed her in the sky as Ursa Major.",
        bestSeason = "Spring (Mar–May)", rightAscension = "11h", declination = "+50°",
        starChartStars = listOf(
            s(0.15f, 0.6f, 1.8f, "Dubhe"), s(0.30f, 0.55f, 2.4f, "Merak"),
            s(0.45f, 0.45f, 2.4f, "Phecda"), s(0.60f, 0.35f, 3.3f, "Megrez"),
            s(0.75f, 0.30f, 1.8f, "Alioth"), s(0.85f, 0.40f, 2.3f, "Mizar"),
            s(0.90f, 0.55f, 1.9f, "Alkaid"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(2,3), l(3,4), l(4,5), l(5,6), l(0,3)),
        heroImageUrl = URSA_MAJOR_URL,
    )

    private val cassiopeia = ConstellationEntry(
        name = "Cassiopeia", abbr = "Cas", hemisphere = "Northern", brightestStar = "Schedar (α)",
        description = "The Queen — distinctive W-shaped constellation visible year-round in the northern sky.",
        mythology = "Cassiopeia was a vain queen who boasted she was more beautiful than the sea nymphs. As punishment, she was placed in the sky to circle the pole, sometimes hanging upside down.",
        bestSeason = "Autumn (Sep–Nov)", rightAscension = "1h", declination = "+60°",
        starChartStars = listOf(
            s(0.15f, 0.35f, 2.2f, "Caph"), s(0.35f, 0.65f, 2.2f, "Schedar"),
            s(0.50f, 0.35f, 2.5f, "Gamma Cas"), s(0.70f, 0.65f, 2.7f, "Ruchbah"),
            s(0.85f, 0.40f, 3.4f, "Segin"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(2,3), l(3,4)),
        heroImageUrl = CASSIOPEIA_URL,
    )

    private val leo = ConstellationEntry(
        name = "Leo", abbr = "Leo", hemisphere = "Equatorial", brightestStar = "Regulus (α)",
        description = "The Lion — a zodiac constellation with a distinctive sickle (backward question mark) pattern forming the lion's mane.",
        mythology = "Leo represents the Nemean Lion slain by Hercules as one of his twelve labors. Zeus placed the lion in the sky to honor the deed.",
        bestSeason = "Spring (Mar–May)", rightAscension = "10h", declination = "+15°",
        starChartStars = listOf(
            s(0.25f, 0.7f, 1.4f, "Regulus"), s(0.30f, 0.55f, 2.6f, "Eta Leo"),
            s(0.35f, 0.40f, 3.5f, "Mu Leo"), s(0.30f, 0.25f, 3.9f, "Epsilon Leo"),
            s(0.45f, 0.30f, 2.0f, "Gamma Leo"), s(0.55f, 0.20f, 3.4f, "Zeta Leo"),
            s(0.75f, 0.35f, 2.6f, "Delta Leo"), s(0.80f, 0.55f, 3.3f, "Beta Leo"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(2,3), l(3,4), l(4,5), l(4,6), l(6,7), l(7,0)),
        heroImageUrl = LEO_URL,
    )

    private val scorpius = ConstellationEntry(
        name = "Scorpius", abbr = "Sco", hemisphere = "Southern", brightestStar = "Antares (α)",
        description = "The Scorpion — a striking S-shaped constellation with the red supergiant Antares at its heart.",
        mythology = "In Greek mythology, Scorpius was the scorpion that killed Orion. They were placed on opposite sides of the sky so they never appear together.",
        bestSeason = "Summer (Jun–Aug)", rightAscension = "17h", declination = "-30°",
        starChartStars = listOf(
            s(0.20f, 0.15f, 2.6f, "Beta Sco"), s(0.30f, 0.20f, 2.9f, "Delta Sco"),
            s(0.40f, 0.25f, 1.1f, "Antares"), s(0.45f, 0.40f, 2.8f, "Sigma Sco"),
            s(0.50f, 0.55f, 3.3f, "Tau Sco"), s(0.55f, 0.70f, 3.6f, "Epsilon Sco"),
            s(0.65f, 0.80f, 3.0f, "Mu Sco"), s(0.80f, 0.85f, 1.9f, "Lambda Sco"),
            s(0.85f, 0.75f, 2.4f, "Upsilon Sco"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(2,3), l(3,4), l(4,5), l(5,6), l(6,7), l(7,8)),
        heroImageUrl = SCORPIUS_URL,
    )

    private val cygnus = ConstellationEntry(
        name = "Cygnus", abbr = "Cyg", hemisphere = "Northern", brightestStar = "Deneb (α)",
        description = "The Swan — a cross-shaped constellation flying along the Milky Way, also known as the Northern Cross.",
        mythology = "Cygnus represents Zeus disguised as a swan, or the musician Orpheus transformed into a swan after death and placed near his lyre (Lyra).",
        bestSeason = "Summer (Jul–Sep)", rightAscension = "20h", declination = "+40°",
        starChartStars = listOf(
            s(0.50f, 0.10f, 1.3f, "Deneb"), s(0.50f, 0.35f, 2.2f, "Sadr"),
            s(0.15f, 0.45f, 2.5f, "Delta Cyg"), s(0.85f, 0.50f, 2.9f, "Gienah"),
            s(0.50f, 0.80f, 3.1f, "Albireo"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(1,3), l(1,4)),
        heroImageUrl = CYGNUS_URL,
    )

    private val lyra = ConstellationEntry(
        name = "Lyra", abbr = "Lyr", hemisphere = "Northern", brightestStar = "Vega (α)",
        description = "The Lyre — small but prominent constellation containing Vega, the fifth brightest star in the night sky.",
        mythology = "Lyra represents the lyre of Orpheus, the legendary musician. After his death, Zeus placed his lyre in the sky.",
        bestSeason = "Summer (Jul–Sep)", rightAscension = "19h", declination = "+35°",
        starChartStars = listOf(
            s(0.50f, 0.10f, 0.0f, "Vega"), s(0.35f, 0.40f, 3.3f, "Epsilon Lyr"),
            s(0.65f, 0.40f, 4.3f, "Zeta Lyr"), s(0.40f, 0.75f, 3.2f, "Sheliak"),
            s(0.60f, 0.75f, 3.3f, "Sulafat"),
        ),
        starChartLines = listOf(l(0,1), l(0,2), l(1,3), l(2,4), l(3,4)),
        heroImageUrl = LYRA_URL,
    )

    private val taurus = ConstellationEntry(
        name = "Taurus", abbr = "Tau", hemisphere = "Equatorial", brightestStar = "Aldebaran (α)",
        description = "The Bull — a zodiac constellation featuring Aldebaran (the bull's eye) and the Pleiades star cluster.",
        mythology = "Taurus represents the bull form Zeus took to abduct Europa. The constellation shows only the front half of the bull, as it emerged from the sea.",
        bestSeason = "Winter (Nov–Jan)", rightAscension = "4h", declination = "+15°",
        starChartStars = listOf(
            s(0.10f, 0.20f, 2.9f, "Epsilon Tau"), s(0.20f, 0.15f, 3.5f, "Theta Tau"),
            s(0.45f, 0.35f, 0.9f, "Aldebaran"), s(0.65f, 0.25f, 3.4f, "Beta Tau"),
            s(0.50f, 0.55f, 3.0f, "Zeta Tau"), s(0.70f, 0.60f, 3.4f, "Elnath"),
            s(0.05f, 0.10f, 4.0f, "Pleiades"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(2,3), l(2,4), l(4,5)),
        heroImageUrl = TAURUS_URL,
    )

    private val gemini = ConstellationEntry(
        name = "Gemini", abbr = "Gem", hemisphere = "Equatorial", brightestStar = "Pollux (β)",
        description = "The Twins — a zodiac constellation with two bright stars (Castor and Pollux) representing the heads of the twins.",
        mythology = "Castor and Pollux were twin brothers in Greek mythology, known as the Dioscuri. They were the patrons of sailors, who prayed to them for protection.",
        bestSeason = "Winter (Jan–Mar)", rightAscension = "7h", declination = "+20°",
        starChartStars = listOf(
            s(0.25f, 0.15f, 1.6f, "Castor"), s(0.55f, 0.20f, 1.1f, "Pollux"),
            s(0.30f, 0.40f, 3.1f, "Wasat"), s(0.50f, 0.45f, 3.5f, "Mebsuta"),
            s(0.20f, 0.70f, 3.6f, "Tejat"), s(0.35f, 0.85f, 2.9f, "Alhena"),
            s(0.60f, 0.75f, 3.3f, "Mu Gem"), s(0.75f, 0.85f, 3.3f, "Alzirr"),
        ),
        starChartLines = listOf(l(0,2), l(2,4), l(4,5), l(1,3), l(3,6), l(6,7), l(2,3)),
        heroImageUrl = GEMINI_URL,
    )

    private val canisMajor = ConstellationEntry(
        name = "Canis Major", abbr = "CMa", hemisphere = "Southern", brightestStar = "Sirius (α)",
        description = "The Greater Dog — contains Sirius, the brightest star in the night sky, and represents one of Orion's hunting dogs.",
        mythology = "Canis Major is one of Orion's hunting dogs, placed in the sky to help the hunter pursue Taurus the bull.",
        bestSeason = "Winter (Jan–Mar)", rightAscension = "7h", declination = "-25°",
        starChartStars = listOf(
            s(0.40f, 0.35f, -1.5f, "Sirius"), s(0.25f, 0.25f, 2.0f, "Mirzam"),
            s(0.50f, 0.55f, 2.4f, "Wezen"), s(0.35f, 0.70f, 2.9f, "Adhara"),
            s(0.60f, 0.75f, 3.0f, "Aludra"), s(0.65f, 0.50f, 3.0f, "Furud"),
        ),
        starChartLines = listOf(l(0,1), l(0,2), l(2,3), l(2,5), l(2,4)),
        heroImageUrl = CANIS_MAJOR_URL,
    )

    private val andromeda = ConstellationEntry(
        name = "Andromeda", abbr = "And", hemisphere = "Northern", brightestStar = "Alpheratz (α)",
        description = "The Princess — home to the Andromeda Galaxy (M31), the nearest major galaxy to the Milky Way.",
        mythology = "Andromeda was a princess chained to a rock as a sacrifice to a sea monster. She was rescued by Perseus, whom she later married.",
        bestSeason = "Autumn (Oct–Nov)", rightAscension = "1h", declination = "+40°",
        starChartStars = listOf(
            s(0.15f, 0.30f, 2.1f, "Alpheratz"), s(0.40f, 0.35f, 2.1f, "Mirach"),
            s(0.65f, 0.40f, 2.3f, "Almach"), s(0.30f, 0.55f, 3.3f, "Delta And"),
            s(0.55f, 0.65f, 4.0f, "Mu And"), s(0.35f, 0.20f, 4.0f, "M31"),
        ),
        starChartLines = listOf(l(0,3), l(0,1), l(1,4), l(1,2), l(1,5)),
        heroImageUrl = ANDROMEDA_URL,
    )

    private val sagittarius = ConstellationEntry(
        name = "Sagittarius", abbr = "Sgr", hemisphere = "Southern", brightestStar = "Kaus Australis (ε)",
        description = "The Archer — a zodiac constellation that points toward the center of the Milky Way galaxy, rich in deep-sky objects.",
        mythology = "Sagittarius represents a centaur archer, sometimes identified as Chiron or Crotus. It aims its arrow at the scorpion (Scorpius).",
        bestSeason = "Summer (Jul–Aug)", rightAscension = "19h", declination = "-25°",
        starChartStars = listOf(
            s(0.30f, 0.70f, 1.9f, "Kaus Aust."), s(0.40f, 0.55f, 2.7f, "Kaus Med."),
            s(0.35f, 0.35f, 2.8f, "Kaus Bor."), s(0.55f, 0.40f, 3.5f, "Alnasl"),
            s(0.60f, 0.25f, 3.5f, "Polis"), s(0.70f, 0.30f, 3.0f, "Nunki"),
            s(0.80f, 0.45f, 3.5f, "Tau Sgr"), s(0.50f, 0.15f, 3.3f, "Ascella"),
        ),
        starChartLines = listOf(l(0,1), l(1,2), l(2,4), l(4,3), l(1,3), l(2,5), l(5,6), l(6,7), l(7,1)),
        heroImageUrl = SAGITTARIUS_URL,
    )

    // ── All 88 constellations ──
    val allConstellations: List<ConstellationEntry> = listOf(
        orion, ursaMajor, cassiopeia, leo, scorpius, cygnus, lyra, taurus, gemini, canisMajor, andromeda, sagittarius,
        // Northern hemisphere
        ConstellationEntry("Andromeda", "And", "Northern", "Alpheratz", "The Princess, chained maiden rescued by Perseus.", "Rescued by Perseus from a sea monster.", "Autumn", "1h", "+40°", heroImageUrl = ANDROMEDA_URL),
        ConstellationEntry("Aquila", "Aql", "Equatorial", "Altair", "The Eagle — carries Zeus's thunderbolts.", "Zeus's eagle.", "Summer", "19h", "+0°"),
        ConstellationEntry("Auriga", "Aur", "Northern", "Capella", "The Charioteer — hexagonal constellation with bright Capella.", "Athena's charioteer.", "Winter", "6h", "+40°"),
        ConstellationEntry("Boötes", "Boo", "Northern", "Arcturus", "The Herdsman — kite-shaped constellation with bright Arcturus.", "The ox driver.", "Spring", "15h", "+30°"),
        ConstellationEntry("Camelopardalis", "Cam", "Northern", "β Cam", "The Giraffe — large but faint circumpolar constellation.", "The giraffe.", "Year-round", "6h", "+70°"),
        ConstellationEntry("Canes Venatici", "CVn", "Northern", "Cor Caroli", "The Hunting Dogs — Boötes's two dogs.", "Boötes's hunting dogs.", "Spring", "13h", "+40°"),
        ConstellationEntry("Canis Minor", "CMi", "Equatorial", "Procyon", "The Lesser Dog — Orion's smaller hunting dog.", "Orion's smaller dog.", "Winter", "7h", "+10°"),
        ConstellationEntry("Capricornus", "Cap", "Equatorial", "Deneb Algedi", "The Sea Goat — zodiac constellation.", "Pan transformed into a fish-goat.", "Autumn", "21h", "-20°"),
        ConstellationEntry("Carina", "Car", "Southern", "Canopus", "The Keel — part of the former ship Argo Navis.", "The keel of Jason's ship Argo.", "Winter", "8h", "-60°"),
        ConstellationEntry("Cepheus", "Cep", "Northern", "Alderamin", "The King — husband of Cassiopeia.", "King of Ethiopia.", "Autumn", "22h", "+70°"),
        ConstellationEntry("Cetus", "Cet", "Equatorial", "Diphda", "The Sea Monster — the beast that threatened Andromeda.", "The sea monster.", "Autumn", "2h", "-10°"),
        ConstellationEntry("Coma Berenices", "Com", "Northern", "Beta Com", "Berenice's Hair — faint constellation near Virgo.", "Queen Berenice's hair.", "Spring", "13h", "+25°"),
        ConstellationEntry("Corona Borealis", "CrB", "Northern", "Alphecca", "The Northern Crown — semicircle of stars.", "Ariadne's crown.", "Summer", "16h", "+30°"),
        ConstellationEntry("Corvus", "Crv", "Equatorial", "Gienah", "The Crow — small quadrilateral near Virgo.", "Apollo's crow.", "Spring", "12h", "-20°"),
        ConstellationEntry("Crater", "Crt", "Equatorial", "Delta Crt", "The Cup — the goblet of Apollo.", "Apollo's cup.", "Spring", "11h", "-15°"),
        ConstellationEntry("Draco", "Dra", "Northern", "Eltanin", "The Dragon — winds between Ursa Major and Minor.", "The dragon guarding the golden apples.", "Year-round", "17h", "+60°"),
        ConstellationEntry("Equuleus", "Equ", "Northern", "Kitalpha", "The Foal — the second smallest constellation.", "Celeris, Mercury's foal.", "Autumn", "21h", "+5°"),
        ConstellationEntry("Hercules", "Her", "Northern", "Kornephoros", "The Strongman — large constellation with the Keystone asterism.", "Hercules kneeling.", "Summer", "17h", "+25°"),
        ConstellationEntry("Lacerta", "Lac", "Northern", "Alpha Lac", "The Lizard — small faint constellation.", "The lizard.", "Autumn", "22h", "+45°"),
        ConstellationEntry("Libra", "Lib", "Equatorial", "Zubeneschamali", "The Scales — zodiac constellation of balance.", "The scales of justice.", "Spring", "15h", "-15°"),
        ConstellationEntry("Lupus", "Lup", "Southern", "Alpha Lup", "The Wolf — near Centaurus.", "A wild animal slain by Centaurus.", "Summer", "15h", "-45°"),
        ConstellationEntry("Lynx", "Lyn", "Northern", "Alpha Lyn", "The Lynx — faint constellation between Ursa Major and Auriga.", "Named for the sharp-eyed animal.", "Winter", "8h", "+45°"),
        ConstellationEntry("Lyra", "Lyr", "Northern", "Vega", "The Lyre — small constellation with brilliant Vega.", "Orpheus's lyre.", "Summer", "19h", "+35°", heroImageUrl = LYRA_URL),
        ConstellationEntry("Ophiuchus", "Oph", "Equatorial", "Rasalhague", "The Serpent Bearer — holds Serpens.", "Asclepius, the healer.", "Summer", "17h", "+0°"),
        ConstellationEntry("Pegasus", "Peg", "Northern", "Enif", "The Winged Horse — features the Great Square asterism.", "Pegasus, sprung from Medusa's blood.", "Autumn", "22h", "+20°"),
        ConstellationEntry("Perseus", "Per", "Northern", "Mirfak", "The Hero — rescuer of Andromeda.", "Slayer of Medusa.", "Winter", "3h", "+45°"),
        ConstellationEntry("Serpens", "Sgr", "Equatorial", "Unukalhai", "The Serpent — split into Caput and Cauda by Ophiuchus.", "The serpent held by the healer.", "Summer", "17h", "+0°"),
        ConstellationEntry("Triangulum", "Tri", "Northern", "Beta Tri", "The Triangle — small but ancient constellation.", "The triangle.", "Autumn", "2h", "+30°"),
        ConstellationEntry("Ursa Minor", "UMi", "Northern", "Polaris", "The Little Bear — contains the North Star (Polaris).", "Callisto's son Arcas.", "Year-round", "15h", "+75°"),
        ConstellationEntry("Vulpecula", "Vul", "Northern", "Alpha Vul", "The Fox — small constellation in the Summer Triangle.", "The fox.", "Summer", "20h", "+25°"),
        // Equatorial / Zodiacal
        ConstellationEntry("Aquarius", "Aqr", "Equatorial", "Sadalsuud", "The Water Bearer — zodiac constellation pouring water.", "Ganymede, cupbearer to the gods.", "Autumn", "22h", "-10°"),
        ConstellationEntry("Aries", "Ari", "Equatorial", "Hamal", "The Ram — zodiac constellation of the golden fleece.", "The ram whose fleece became the Golden Fleece.", "Winter", "3h", "+20°"),
        ConstellationEntry("Cancer", "Cnc", "Equatorial", "Tarf", "The Crab — zodiac constellation containing the Beehive Cluster.", "The crab that fought Hercules.", "Spring", "8h", "+20°"),
        ConstellationEntry("Centaurus", "Cen", "Southern", "Rigil Kentaurus", "The Centaur — contains Alpha Centauri, closest star system.", "The centaur Chiron.", "Spring", "13h", "-50°"),
        ConstellationEntry("Delphinus", "Del", "Equatorial", "Rotanev", "The Dolphin — small but distinctive constellation.", "Poseidon's dolphin.", "Summer", "21h", "+10°"),
        ConstellationEntry("Eridanus", "Eri", "Equatorial", "Achernar", "The River — long winding constellation from Orion to the south.", "A celestial river.", "Winter", "3h", "-30°"),
        ConstellationEntry("Fornax", "For", "Southern", "Alpha For", "The Furnace — faint constellation.", "The chemical furnace.", "Winter", "3h", "-30°"),
        ConstellationEntry("Hydra", "Hya", "Equatorial", "Alphard", "The Water Snake — the largest constellation.", "The multi-headed monster.", "Spring", "11h", "-15°"),
        ConstellationEntry("Monoceros", "Mon", "Equatorial", "Beta Mon", "The Unicorn — faint constellation near Orion.", "The mythical unicorn.", "Winter", "7h", "+0°"),
        ConstellationEntry("Orion", "Ori", "Equatorial", "Rigel", "The Hunter — most recognizable winter constellation.", "The great hunter.", "Winter", "5h", "+5°", heroImageUrl = ORION_URL),
        ConstellationEntry("Pisces", "Psc", "Equatorial", "Alpherg", "The Fishes — zodiac constellation of two fish.", "Aphrodite and Eros as fish.", "Autumn", "1h", "+15°"),
        ConstellationEntry("Piscis Austrinus", "PsA", "Southern", "Fomalhaut", "The Southern Fish — drinks water from Aquarius.", "The fish.", "Autumn", "22h", "-30°"),
        ConstellationEntry("Sextans", "Sex", "Equatorial", "Alpha Sex", "The Sextant — faint constellation near Leo.", "Named after the navigational instrument.", "Spring", "10h", "-5°"),
        ConstellationEntry("Taurus", "Tau", "Equatorial", "Aldebaran", "The Bull — zodiac constellation with the Pleiades and Hyades.", "Zeus as a bull.", "Winter", "4h", "+15°", heroImageUrl = TAURUS_URL),
        ConstellationEntry("Virgo", "Vir", "Equatorial", "Spica", "The Maiden — second largest constellation, zodiac sign.", "Astraea, goddess of justice.", "Spring", "13h", "+0°"),
        // Southern hemisphere
        ConstellationEntry("Ara", "Ara", "Southern", "Alpha Ara", "The Altar — small southern constellation.", "The altar of the gods.", "Summer", "17h", "-55°"),
        ConstellationEntry("Caelum", "Cae", "Southern", "Alpha Cae", "The Chisel — faint southern constellation.", "The sculptor's chisel.", "Winter", "5h", "-40°"),
        ConstellationEntry("Canis Major", "CMa", "Southern", "Sirius", "The Greater Dog — contains the brightest star.", "Orion's hunting dog.", "Winter", "7h", "-25°", heroImageUrl = CANIS_MAJOR_URL),
        ConstellationEntry("Chamaeleon", "Cha", "Southern", "Alpha Cha", "The Chameleon — small circumpolar constellation.", "The chameleon.", "Year-round", "11h", "-80°"),
        ConstellationEntry("Circinus", "Cir", "Southern", "Alpha Cir", "The Compass — small constellation near Centaurus.", "The drafting compass.", "Spring", "15h", "-60°"),
        ConstellationEntry("Columba", "Col", "Southern", "Phact", "The Dove — small constellation near Puppis.", "Noah's dove.", "Winter", "6h", "-35°"),
        ConstellationEntry("Crux", "Cru", "Southern", "Acrux", "The Southern Cross — smallest but most famous southern constellation.", "The cross.", "Year-round", "12h", "-60°"),
        ConstellationEntry("Dorado", "Dor", "Southern", "Alpha Dor", "The Goldfish — contains the Large Magellanic Cloud.", "The dolphinfish.", "Winter", "5h", "-60°"),
        ConstellationEntry("Grus", "Gru", "Southern", "Alnair", "The Crane — elongated southern constellation.", "The crane.", "Autumn", "22h", "-45°"),
        ConstellationEntry("Horologium", "Hor", "Southern", "Alpha Hor", "The Clock — faint southern constellation.", "The pendulum clock.", "Winter", "3h", "-55°"),
        ConstellationEntry("Hydrus", "Hyi", "Southern", "Alpha Hyi", "The Male Water Snake — near the south celestial pole.", "The male water snake.", "Year-round", "2h", "-70°"),
        ConstellationEntry("Indus", "Ind", "Southern", "Alpha Ind", "The Indian — southern constellation near Pavo.", "A Native American figure.", "Autumn", "21h", "-55°"),
        ConstellationEntry("Microscopium", "Mic", "Southern", "Gamma Mic", "The Microscope — faint southern constellation.", "The microscope.", "Autumn", "21h", "-35°"),
        ConstellationEntry("Mensa", "Men", "Southern", "Alpha Men", "The Table — faintest constellation, near the LMC.", "Table Mountain in South Africa.", "Year-round", "5h", "-80°"),
        ConstellationEntry("Musca", "Mus", "Southern", "Alpha Mus", "The Fly — small southern constellation.", "The fly.", "Year-round", "12h", "-70°"),
        ConstellationEntry("Norma", "Nor", "Southern", "Gamma Nor", "The Level — small constellation in the Milky Way.", "The carpenter's level.", "Summer", "16h", "-50°"),
        ConstellationEntry("Octans", "Oct", "Southern", "Sigma Oct", "The Octant — contains the south celestial pole.", "The navigational octant.", "Year-round", "21h", "-85°"),
        ConstellationEntry("Pavo", "Pav", "Southern", "Peacock", "The Peacock — southern constellation.", "The peacock of Hera.", "Summer", "19h", "-65°"),
        ConstellationEntry("Phoenix", "Phe", "Southern", "Ankaa", "The Phoenix — southern constellation.", "The mythical firebird.", "Autumn", "1h", "-50°"),
        ConstellationEntry("Pictor", "Pic", "Southern", "Alpha Pic", "The Painter's Easel — faint southern constellation.", "The painter's easel.", "Winter", "6h", "-55°"),
        ConstellationEntry("Puppis", "Pup", "Southern", "Naos", "The Stern — part of the former Argo Navis.", "The stern of the Argo.", "Winter", "8h", "-30°"),
        ConstellationEntry("Pyxis", "Pyx", "Southern", "Alpha Pyx", "The Compass — small constellation near Puppis.", "The ship's compass.", "Spring", "9h", "-25°"),
        ConstellationEntry("Reticulum", "Ret", "Southern", "Alpha Ret", "The Net — small southern constellation.", "The reticle net.", "Winter", "4h", "-60°"),
        ConstellationEntry("Sagittarius", "Sgr", "Southern", "Kaus Aust.", "The Archer — points toward galactic center.", "The centaur archer.", "Summer", "19h", "-25°", heroImageUrl = SAGITTARIUS_URL),
        ConstellationEntry("Scorpius", "Sco", "Southern", "Antares", "The Scorpion — S-shaped constellation with red Antares.", "The killer of Orion.", "Summer", "17h", "-30°", heroImageUrl = SCORPIUS_URL),
        ConstellationEntry("Sculptor", "Scl", "Southern", "Alpha Scl", "The Sculptor — faint constellation near Cetus.", "The sculptor's studio.", "Autumn", "0h", "-30°"),
        ConstellationEntry("Scutum", "Sct", "Equatorial", "Alpha Sct", "The Shield — small constellation in the Milky Way.", "Sobieski's shield.", "Summer", "19h", "-10°"),
        ConstellationEntry("Tucana", "Tuc", "Southern", "Alpha Tuc", "The Toucan — contains the Small Magellanic Cloud.", "The toucan.", "Autumn", "0h", "-65°"),
        ConstellationEntry("Vela", "Vel", "Southern", "Gamma Vel", "The Sails — part of the former Argo Navis.", "The sails of the Argo.", "Spring", "9h", "-45°"),
        ConstellationEntry("Volans", "Vol", "Southern", "Beta Vol", "The Flying Fish — small southern constellation.", "The flying fish.", "Winter", "8h", "-70°"),
    )

    /** The 12 famous constellations that get hero images. */
    val famousConstellations: Set<String> = setOf(
        "Orion", "Ursa Major", "Cassiopeia", "Leo", "Scorpius", "Cygnus",
        "Lyra", "Taurus", "Gemini", "Canis Major", "Andromeda", "Sagittarius",
    )

    /** Get a constellation by name. */
    fun getByName(name: String): ConstellationEntry? =
        allConstellations.firstOrNull { it.name.equals(name, ignoreCase = true) }
}
