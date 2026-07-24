package com.rork.rockscout.data

/**
 * Deep Sky Objects — galaxies, nebulae, and star clusters for the DSO screen.
 * ~40 notable objects with observing data.
 */

data class DeepSkyObject(
    val catalog: String,        // "M31", "NGC 224"
    val commonName: String,
    val type: String,           // "Galaxy", "Nebula", "Globular Cluster", "Open Cluster", "Planetary Nebula"
    val constellation: String,
    val distance: String,       // "2.5 million ly"
    val magnitude: String,      // apparent magnitude
    val description: String,
    val angularSize: String,    // apparent size in sky
    val discoveryYear: String,
    val discoverer: String,
    val bestSeason: String,
    val hemisphere: String,     // "Northern", "Southern", "Both"
    val equipment: String,      // "Naked eye", "Binoculars", "Telescope"
    val heroImageUrl: String? = null,
)

object DeepSkyObjectData {

    const val ANDROMEDA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/923312d6-4c0c-4855-8e00-827426991a2f.png"
    const val ORION_NEBULA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/cb5fe164-426f-44e0-aeeb-8e7bf0e6576c.png"
    const val PLEIADES_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/0f8973f5-db6b-4ff7-903c-e332f0d9b06b.png"
    const val CRAB_NEBULA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/4d0936fe-2b38-4fd6-9be1-f7eacfc625ba.png"
    const val RING_NEBULA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/78bd6615-efc3-450a-84a7-f924f953f372.png"
    const val WHIRLPOOL_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/22b67767-dc92-4f38-b493-55815afa14cd.png"
    const val EAGLE_NEBULA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/63d41d11-bbe8-4179-bd05-b063311840d3.png"
    const val HERCULES_CLUSTER_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/fef1fa96-034c-4558-bffb-d5593d7e35ee.png"

    val allObjects: List<DeepSkyObject> = listOf(
        // ── Galaxies ──
        DeepSkyObject("M31", "Andromeda Galaxy", "Galaxy", "Andromeda", "2.5 million ly", "+3.4",
            "The nearest major galaxy to the Milky Way. Visible to the naked eye as a fuzzy patch. Will collide with the Milky Way in ~4.5 billion years.",
            "3° × 1°", "964", "Al-Sufi", "Autumn", "Both", "Naked eye", ANDROMEDA_URL),
        DeepSkyObject("M51", "Whirlpool Galaxy", "Galaxy", "Canes Venatici", "23 million ly", "+8.4",
            "A grand-design spiral galaxy interacting with its companion NGC 5195. First galaxy recognized as spiral.",
            "11' × 7'", "1773", "Messier", "Spring", "Northern", "Telescope", WHIRLPOOL_URL),
        DeepSkyObject("M33", "Triangulum Galaxy", "Galaxy", "Triangulum", "3 million ly", "+5.7",
            "The third-largest galaxy in our Local Group. Visible to naked eye under dark skies.",
            "73' × 45'", "1654", "Hodierna", "Autumn", "Both", "Binoculars"),
        DeepSkyObject("M81", "Bode's Galaxy", "Galaxy", "Ursa Major", "12 million ly", "+6.9",
            "A bright spiral galaxy forming a popular pair with M82. Visible in small telescopes.",
            "26' × 14'", "1774", "Bode", "Spring", "Northern", "Binoculars"),
        DeepSkyObject("M82", "Cigar Galaxy", "Galaxy", "Ursa Major", "12 million ly", "+8.4",
            "A starburst galaxy — intense star formation caused by gravitational interaction with M81.",
            "11' × 4'", "1774", "Bode", "Spring", "Northern", "Telescope"),
        DeepSkyObject("M104", "Sombrero Galaxy", "Galaxy", "Virgo", "31 million ly", "+8.0",
            "An edge-on galaxy with a dark dust lane resembling a sombrero hat.",
            "9' × 4'", "1781", "Méchain", "Spring", "Both", "Telescope"),
        DeepSkyObject("M64", "Black Eye Galaxy", "Galaxy", "Coma Berenices", "24 million ly", "+8.5",
            "A spiral galaxy with a dark dust band in front of the bright nucleus — the 'black eye'.",
            "10' × 5'", "1779", "Bode", "Spring", "Both", "Telescope"),
        DeepSkyObject("M83", "Southern Pinwheel", "Galaxy", "Hydra", "15 million ly", "+7.5",
            "A beautiful face-on spiral galaxy with bright arms. A frequent supernova host.",
            "12' × 11'", "1752", "Lacaille", "Spring", "Southern", "Binoculars"),
        DeepSkyObject("M87", "Virgo A", "Galaxy", "Virgo", "53 million ly", "+8.6",
            "A giant elliptical galaxy at the heart of the Virgo Cluster. Home to the first imaged black hole (M87*).",
            "7'", "1781", "Messier", "Spring", "Both", "Telescope"),
        DeepSkyObject("NGC 4565", "Needle Galaxy", "Galaxy", "Coma Berenices", "40 million ly", "+9.6",
            "A perfect edge-on spiral galaxy — appears as a thin needle of light.",
            "16' × 2'", "1785", "Herschel", "Spring", "Both", "Telescope"),

        // ── Nebulae ──
        DeepSkyObject("M42", "Orion Nebula", "Nebula", "Orion", "1,344 ly", "+4.0",
            "The brightest nebula in the sky — a stellar nursery visible to the naked eye in Orion's sword.",
            "1° × 1°", "1610", "Peiresc", "Winter", "Both", "Naked eye", ORION_NEBULA_URL),
        DeepSkyObject("M16", "Eagle Nebula", "Nebula", "Serpens", "7,000 ly", "+6.0",
            "Home to the 'Pillars of Creation' — towering columns of gas and dust photographed by Hubble.",
            "35' × 28'", "1745", "de Cheseaux", "Summer", "Both", "Telescope", EAGLE_NEBULA_URL),
        DeepSkyObject("M8", "Lagoon Nebula", "Nebula", "Sagittarius", "4,100 ly", "+6.0",
            "A bright emission nebula with a dark dust lane (the 'lagoon'). Contains the open cluster NGC 6530.",
            "90' × 40'", "1654", "Hodierna", "Summer", "Both", "Binoculars"),
        DeepSkyObject("M20", "Trifid Nebula", "Nebula", "Sagittarius", "5,200 ly", "+6.3",
            "A combined emission, reflection, and dark nebula divided into three parts by dark dust lanes.",
            "28'", "1764", "Messier", "Summer", "Both", "Telescope"),
        DeepSkyObject("M1", "Crab Nebula", "Nebula", "Taurus", "6,500 ly", "+8.4",
            "Supernova remnant from a star explosion witnessed in 1054 AD. Contains a pulsar at its center.",
            "6' × 4'", "1731", "Bevis", "Winter", "Both", "Telescope", CRAB_NEBULA_URL),
        DeepSkyObject("M57", "Ring Nebula", "Planetary Nebula", "Lyra", "2,300 ly", "+8.8",
            "A planetary nebula — the glowing remains of a Sun-like star. Appears as a ghostly smoke ring.",
            "1.4' × 1.0'", "1779", "Darquier", "Summer", "Both", "Telescope", RING_NEBULA_URL),
        DeepSkyObject("M27", "Dumbbell Nebula", "Planetary Nebula", "Vulpecula", "1,360 ly", "+7.5",
            "The brightest planetary nebula — resembles a dumbbell or apple core shape.",
            "8' × 6'", "1764", "Messier", "Summer", "Both", "Binoculars"),
        DeepSkyObject("NGC 7000", "North America Nebula", "Nebula", "Cygnus", "2,590 ly", "+4.0",
            "A large nebula shaped like the continent of North America. Near Deneb.",
            "2° × 1.5'", "1786", "Herschel", "Summer", "Northern", "Binoculars"),
        DeepSkyObject("NGC 3372", "Carina Nebula", "Nebula", "Carina", "7,500 ly", "+1.0",
            "One of the largest nebulae — larger and brighter than the Orion Nebula but visible only from the south.",
            "2°", "1751", "Lacaille", "Winter", "Southern", "Naked eye"),
        DeepSkyObject("NGC 6960", "Veil Nebula", "Nebula", "Cygnus", "1,470 ly", "+7.0",
            "A faint supernova remnant — the 'Witch's Broom'. Part of the Cygnus Loop.",
            "3°", "1784", "Herschel", "Summer", "Both", "Telescope"),
        DeepSkyObject("NGC 6543", "Cat's Eye Nebula", "Planetary Nebula", "Draco", "3,300 ly", "+8.1",
            "One of the most complex planetary nebulae — concentric shells around a central star.",
            "20\"", "1786", "Herschel", "Summer", "Northern", "Telescope"),

        // ── Globular Clusters ──
        DeepSkyObject("M13", "Hercules Cluster", "Globular Cluster", "Hercules", "22,200 ly", "+5.8",
            "The finest globular cluster in the northern sky — hundreds of thousands of stars in a spherical ball.",
            "20'", "1714", "Halley", "Summer", "Northern", "Binoculars", HERCULES_CLUSTER_URL),
        DeepSkyObject("M5", "Rose Cluster", "Globular Cluster", "Serpens", "24,500 ly", "+5.7",
            "One of the oldest known globular clusters — estimated at 13 billion years old.",
            "23'", "1702", "Kirch", "Summer", "Both", "Binoculars"),
        DeepSkyObject("M3", "NGC 5272", "Globular Cluster", "Canes Venatici", "33,900 ly", "+6.2",
            "One of the largest globular clusters — contains about 500,000 stars.",
            "18'", "1764", "Messier", "Spring", "Both", "Binoculars"),
        DeepSkyObject("M15", "NGC 7078", "Globular Cluster", "Pegasus", "33,600 ly", "+6.2",
            "A dense globular cluster containing a possible intermediate-mass black hole at its core.",
            "18'", "1746", "Maraldi", "Autumn", "Both", "Binoculars"),
        DeepSkyObject("M92", "NGC 6341", "Globular Cluster", "Hercules", "26,700 ly", "+6.3",
            "A compact globular cluster often overshadowed by nearby M13 but very rewarding.",
            "14'", "1777", "Bode", "Summer", "Northern", "Binoculars"),
        DeepSkyObject("NGC 5139", "Omega Centauri", "Globular Cluster", "Centaurus", "15,800 ly", "+3.7",
            "The largest and brightest globular cluster in the Milky Way — visible to the naked eye.",
            "36'", "1652", "Bayer", "Spring", "Southern", "Naked eye"),
        DeepSkyObject("M22", "NGC 6656", "Globular Cluster", "Sagittarius", "10,600 ly", "+5.1",
            "One of the brightest globular clusters — one of the few visible to the naked eye from mid-latitudes.",
            "32'", "1665", "Kirch", "Summer", "Both", "Naked eye"),

        // ── Open Clusters ──
        DeepSkyObject("M45", "Pleiades", "Open Cluster", "Taurus", "444 ly", "+1.6",
            "The Seven Sisters — the most famous open cluster. Bright blue stars surrounded by reflection nebulosity.",
            "110'", "Prehistoric", "Known since antiquity", "Winter", "Both", "Naked eye", PLEIADES_URL),
        DeepSkyObject("M44", "Beehive Cluster", "Open Cluster", "Cancer", "577 ly", "+3.7",
            "Praesepe — one of the nearest open clusters. Visible to the naked eye as a fuzzy patch.",
            "95'", "Prehistoric", "Known since antiquity", "Winter", "Both", "Naked eye"),
        DeepSkyObject("M41", "NGC 2287", "Open Cluster", "Canis Major", "2,300 ly", "+4.5",
            "A bright open cluster near Sirius. Contains about 70 stars.",
            "38'", "1654", "Hodierna", "Winter", "Both", "Naked eye"),
        DeepSkyObject("M47", "NGC 2422", "Open Cluster", "Puppis", "1,600 ly", "+4.4",
            "A bright scattered open cluster visible to the naked eye.",
            "30'", "1654", "Hodierna", "Winter", "Both", "Binoculars"),
        DeepSkyObject("M6", "Butterfly Cluster", "Open Cluster", "Scorpius", "1,600 ly", "+4.2",
            "An open cluster whose shape resembles a butterfly. Best viewed in summer.",
            "25'", "1654", "Hodierna", "Summer", "Both", "Binoculars"),
        DeepSkyObject("M7", "Ptolemy Cluster", "Open Cluster", "Scorpius", "980 ly", "+3.3",
            "A large bright open cluster visible to the naked eye. Known since antiquity.",
            "80'", "130", "Ptolemy", "Summer", "Both", "Naked eye"),
        DeepSkyObject("M37", "NGC 2099", "Open Cluster", "Auriga", "4,500 ly", "+6.2",
            "The richest of the three Auriga clusters — about 500 stars.",
            "24'", "1654", "Hodierna", "Winter", "Northern", "Binoculars"),
        DeepSkyObject("Double Cluster", "Caldwell 14", "Open Cluster", "Perseus", "7,500 ly", "+4.3",
            "Two adjacent open clusters (NGC 869 & NGC 884) visible as a fuzzy patch to the naked eye.",
            "60'", "130", "Hipparchus", "Autumn", "Northern", "Naked eye"),
        DeepSkyObject("M52", "NGC 7654", "Open Cluster", "Cassiopeia", "5,000 ly", "+5.0",
            "A rich compressed open cluster near the Cassiopeia W.",
            "13'", "1771", "Messier", "Autumn", "Northern", "Binoculars"),
        DeepSkyObject("M11", "Wild Duck Cluster", "Open Cluster", "Scutum", "6,120 ly", "+6.3",
            "One of the richest and most compact open clusters — resembles a flock of flying ducks.",
            "14'", "1681", "Kirch", "Summer", "Both", "Binoculars"),
    )
}
