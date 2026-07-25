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

    // Galaxies
    const val M33_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/9039659c-8558-4fa1-b8b0-3fc50e0b6481.png"
    const val M81_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/33c7db27-2f59-4a9f-83d3-b04cc835f5c3.png"
    const val M82_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/4454fa98-9b15-4f42-bc9a-80434f3858b8.png"
    const val M104_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/c21b633b-ab59-48bc-942e-87524c04728f.png"
    const val M64_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/a8b02235-dc8d-428f-b0fc-0cc60e66ff52.png"
    const val M83_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/3ecc188e-0f20-4f78-a956-d295b754d10f.png"
    const val M87_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e8ad3a6e-f3ff-40f1-8817-0817ce3547b2.png"
    const val NGC4565_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/9616b734-1080-417c-983a-f3b1060f6652.png"

    // Nebulae
    const val M8_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/686b18d6-f264-4a6f-8b27-d0d216668b41.png"
    const val M20_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e7311901-7942-4024-bea3-c2767f620578.png"
    const val NGC7000_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/5bb778d8-304d-454f-be8e-50663e669d40.png"
    const val NGC3372_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/66c2973f-621c-4780-8b7b-0437f9dc33b3.png"
    const val NGC6960_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/15ed2357-299a-44de-a4d1-ed2ded111d65.png"
    const val NGC6543_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/7b2b39f8-4783-422c-a07b-fed2d13217bb.png"

    // Globular clusters
    const val M5_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/260490e5-3443-4a6e-b63c-025be2eeee0a.png"
    const val M3_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/a9dfbff2-6277-4d73-b805-d18ac4cc189e.png"
    const val M15_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/3f614a13-45dd-4c22-ac4e-e0088e8393ad.png"
    const val M92_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/682de800-edae-428b-854e-abb82b7aa8bd.png"
    const val NGC5139_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d243fc3f-c9c6-49ab-ae12-9067ad7286d0.png"
    const val M22_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/6126eb82-4126-4e31-81cf-ef0476516a77.png"

    // Open clusters
    const val M44_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e114994e-252f-482d-be8c-b3cc3628e0c4.png"
    const val M41_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/29879496-4474-4a79-9a70-c3a83432ef01.png"
    const val M47_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/dd983d58-0e3d-4366-961f-dcbaadc77002.png"
    const val M6_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/fe9c6932-e841-4d15-b13c-f3516caddd5d.png"
    const val M7_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/19cfe384-fb5e-4bdd-9b76-1eae75b1a583.png"
    const val M37_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/0d4306cf-e93c-4d1b-957c-352ae17b1662.png"
    const val DOUBLE_CLUSTER_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/89da2153-352f-4aba-8e8b-c38c79098c31.png"
    const val M52_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/6a8d8daa-4991-491c-8d21-71f82eace2a4.png"
    const val M11_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/916e6806-d255-4835-8a71-50f9d9aae392.png"

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
            "73' × 45'", "1654", "Hodierna", "Autumn", "Both", "Binoculars", M33_URL),
        DeepSkyObject("M81", "Bode's Galaxy", "Galaxy", "Ursa Major", "12 million ly", "+6.9",
            "A bright spiral galaxy forming a popular pair with M82. Visible in small telescopes.",
            "26' × 14'", "1774", "Bode", "Spring", "Northern", "Binoculars", M81_URL),
        DeepSkyObject("M82", "Cigar Galaxy", "Galaxy", "Ursa Major", "12 million ly", "+8.4",
            "A starburst galaxy — intense star formation caused by gravitational interaction with M81.",
            "11' × 4'", "1774", "Bode", "Spring", "Northern", "Telescope", M82_URL),
        DeepSkyObject("M104", "Sombrero Galaxy", "Galaxy", "Virgo", "31 million ly", "+8.0",
            "An edge-on galaxy with a dark dust lane resembling a sombrero hat.",
            "9' × 4'", "1781", "Méchain", "Spring", "Both", "Telescope", M104_URL),
        DeepSkyObject("M64", "Black Eye Galaxy", "Galaxy", "Coma Berenices", "24 million ly", "+8.5",
            "A spiral galaxy with a dark dust band in front of the bright nucleus — the 'black eye'.",
            "10' × 5'", "1779", "Bode", "Spring", "Both", "Telescope", M64_URL),
        DeepSkyObject("M83", "Southern Pinwheel", "Galaxy", "Hydra", "15 million ly", "+7.5",
            "A beautiful face-on spiral galaxy with bright arms. A frequent supernova host.",
            "12' × 11'", "1752", "Lacaille", "Spring", "Southern", "Binoculars", M83_URL),
        DeepSkyObject("M87", "Virgo A", "Galaxy", "Virgo", "53 million ly", "+8.6",
            "A giant elliptical galaxy at the heart of the Virgo Cluster. Home to the first imaged black hole (M87*).",
            "7'", "1781", "Messier", "Spring", "Both", "Telescope", M87_URL),
        DeepSkyObject("NGC 4565", "Needle Galaxy", "Galaxy", "Coma Berenices", "40 million ly", "+9.6",
            "A perfect edge-on spiral galaxy — appears as a thin needle of light.",
            "16' × 2'", "1785", "Herschel", "Spring", "Both", "Telescope", NGC4565_URL),

        // ── Nebulae ──
        DeepSkyObject("M42", "Orion Nebula", "Nebula", "Orion", "1,344 ly", "+4.0",
            "The brightest nebula in the sky — a stellar nursery visible to the naked eye in Orion's sword.",
            "1° × 1°", "1610", "Peiresc", "Winter", "Both", "Naked eye", ORION_NEBULA_URL),
        DeepSkyObject("M16", "Eagle Nebula", "Nebula", "Serpens", "7,000 ly", "+6.0",
            "Home to the 'Pillars of Creation' — towering columns of gas and dust photographed by Hubble.",
            "35' × 28'", "1745", "de Cheseaux", "Summer", "Both", "Telescope", EAGLE_NEBULA_URL),
        DeepSkyObject("M8", "Lagoon Nebula", "Nebula", "Sagittarius", "4,100 ly", "+6.0",
            "A bright emission nebula with a dark dust lane (the 'lagoon'). Contains the open cluster NGC 6530.",
            "90' × 40'", "1654", "Hodierna", "Summer", "Both", "Binoculars", M8_URL),
        DeepSkyObject("M20", "Trifid Nebula", "Nebula", "Sagittarius", "5,200 ly", "+6.3",
            "A combined emission, reflection, and dark nebula divided into three parts by dark dust lanes.",
            "28'", "1764", "Messier", "Summer", "Both", "Telescope", M20_URL),
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
            "2° × 1.5'", "1786", "Herschel", "Summer", "Northern", "Binoculars", NGC7000_URL),
        DeepSkyObject("NGC 3372", "Carina Nebula", "Nebula", "Carina", "7,500 ly", "+1.0",
            "One of the largest nebulae — larger and brighter than the Orion Nebula but visible only from the south.",
            "2°", "1751", "Lacaille", "Winter", "Southern", "Naked eye", NGC3372_URL),
        DeepSkyObject("NGC 6960", "Veil Nebula", "Nebula", "Cygnus", "1,470 ly", "+7.0",
            "A faint supernova remnant — the 'Witch's Broom'. Part of the Cygnus Loop.",
            "3°", "1784", "Herschel", "Summer", "Both", "Telescope", NGC6960_URL),
        DeepSkyObject("NGC 6543", "Cat's Eye Nebula", "Planetary Nebula", "Draco", "3,300 ly", "+8.1",
            "One of the most complex planetary nebulae — concentric shells around a central star.",
            "20\"", "1786", "Herschel", "Summer", "Northern", "Telescope", NGC6543_URL),

        // ── Globular Clusters ──
        DeepSkyObject("M13", "Hercules Cluster", "Globular Cluster", "Hercules", "22,200 ly", "+5.8",
            "The finest globular cluster in the northern sky — hundreds of thousands of stars in a spherical ball.",
            "20'", "1714", "Halley", "Summer", "Northern", "Binoculars", HERCULES_CLUSTER_URL),
        DeepSkyObject("M5", "Rose Cluster", "Globular Cluster", "Serpens", "24,500 ly", "+5.7",
            "One of the oldest known globular clusters — estimated at 13 billion years old.",
            "23'", "1702", "Kirch", "Summer", "Both", "Binoculars", M5_URL),
        DeepSkyObject("M3", "NGC 5272", "Globular Cluster", "Canes Venatici", "33,900 ly", "+6.2",
            "One of the largest globular clusters — contains about 500,000 stars.",
            "18'", "1764", "Messier", "Spring", "Both", "Binoculars", M3_URL),
        DeepSkyObject("M15", "NGC 7078", "Globular Cluster", "Pegasus", "33,600 ly", "+6.2",
            "A dense globular cluster containing a possible intermediate-mass black hole at its core.",
            "18'", "1746", "Maraldi", "Autumn", "Both", "Binoculars", M15_URL),
        DeepSkyObject("M92", "NGC 6341", "Globular Cluster", "Hercules", "26,700 ly", "+6.3",
            "A compact globular cluster often overshadowed by nearby M13 but very rewarding.",
            "14'", "1777", "Bode", "Summer", "Northern", "Binoculars", M92_URL),
        DeepSkyObject("NGC 5139", "Omega Centauri", "Globular Cluster", "Centaurus", "15,800 ly", "+3.7",
            "The largest and brightest globular cluster in the Milky Way — visible to the naked eye.",
            "36'", "1652", "Bayer", "Spring", "Southern", "Naked eye", NGC5139_URL),
        DeepSkyObject("M22", "NGC 6656", "Globular Cluster", "Sagittarius", "10,600 ly", "+5.1",
            "One of the brightest globular clusters — one of the few visible to the naked eye from mid-latitudes.",
            "32'", "1665", "Kirch", "Summer", "Both", "Naked eye", M22_URL),

        // ── Open Clusters ──
        DeepSkyObject("M45", "Pleiades", "Open Cluster", "Taurus", "444 ly", "+1.6",
            "The Seven Sisters — the most famous open cluster. Bright blue stars surrounded by reflection nebulosity.",
            "110'", "Prehistoric", "Known since antiquity", "Winter", "Both", "Naked eye", PLEIADES_URL),
        DeepSkyObject("M44", "Beehive Cluster", "Open Cluster", "Cancer", "577 ly", "+3.7",
            "Praesepe — one of the nearest open clusters. Visible to the naked eye as a fuzzy patch.",
            "95'", "Prehistoric", "Known since antiquity", "Winter", "Both", "Naked eye", M44_URL),
        DeepSkyObject("M41", "NGC 2287", "Open Cluster", "Canis Major", "2,300 ly", "+4.5",
            "A bright open cluster near Sirius. Contains about 70 stars.",
            "38'", "1654", "Hodierna", "Winter", "Both", "Naked eye", M41_URL),
        DeepSkyObject("M47", "NGC 2422", "Open Cluster", "Puppis", "1,600 ly", "+4.4",
            "A bright scattered open cluster visible to the naked eye.",
            "30'", "1654", "Hodierna", "Winter", "Both", "Binoculars", M47_URL),
        DeepSkyObject("M6", "Butterfly Cluster", "Open Cluster", "Scorpius", "1,600 ly", "+4.2",
            "An open cluster whose shape resembles a butterfly. Best viewed in summer.",
            "25'", "1654", "Hodierna", "Summer", "Both", "Binoculars", M6_URL),
        DeepSkyObject("M7", "Ptolemy Cluster", "Open Cluster", "Scorpius", "980 ly", "+3.3",
            "A large bright open cluster visible to the naked eye. Known since antiquity.",
            "80'", "130", "Ptolemy", "Summer", "Both", "Naked eye", M7_URL),
        DeepSkyObject("M37", "NGC 2099", "Open Cluster", "Auriga", "4,500 ly", "+6.2",
            "The richest of the three Auriga clusters — about 500 stars.",
            "24'", "1654", "Hodierna", "Winter", "Northern", "Binoculars", M37_URL),
        DeepSkyObject("Double Cluster", "Caldwell 14", "Open Cluster", "Perseus", "7,500 ly", "+4.3",
            "Two adjacent open clusters (NGC 869 & NGC 884) visible as a fuzzy patch to the naked eye.",
            "60'", "130", "Hipparchus", "Autumn", "Northern", "Naked eye", DOUBLE_CLUSTER_URL),
        DeepSkyObject("M52", "NGC 7654", "Open Cluster", "Cassiopeia", "5,000 ly", "+5.0",
            "A rich compressed open cluster near the Cassiopeia W.",
            "13'", "1771", "Messier", "Autumn", "Northern", "Binoculars", M52_URL),
        DeepSkyObject("M11", "Wild Duck Cluster", "Open Cluster", "Scutum", "6,120 ly", "+6.3",
            "One of the richest and most compact open clusters — resembles a flock of flying ducks.",
            "14'", "1681", "Kirch", "Summer", "Both", "Binoculars", M11_URL),
    )
}
