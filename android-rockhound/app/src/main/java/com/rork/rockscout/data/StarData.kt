package com.rork.rockscout.data

/**
 * Notable stars with properties for the Important Stars screen.
 * Includes the 21 first-magnitude stars plus other famous stars.
 */

data class StarEntry(
    val name: String,
    val constellation: String,
    val apparentMagnitude: Float,
    val absoluteMagnitude: Float,
    val distanceLy: Double,         // light-years
    val spectralClass: String,
    val spectralColor: Long,        // representative color
    val temperatureK: Int,          // surface temperature in Kelvin
    val luminositySolar: Double,    // luminosity compared to Sun
    val description: String,
    val bestViewingMonth: String,
    val hemisphere: String,         // "Northern", "Southern", "Both"
    val heroImageUrl: String? = null,
)

object StarData {

    const val SIRIUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/db28c13c-a1f3-4a81-9cad-5b9f51b66460.png"
    const val BETELGEUSE_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/99444525-77ee-476f-a8eb-8d160862a705.png"
    const val VEGA_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/659b5fcd-533b-4b47-a126-40f1c7aa5a3f.png"
    const val POLARIS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/c800e841-9ef7-49ab-a3c8-951fce55700d.png"
    const val RIGEL_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/42a0ae7b-9d9d-4ab4-8abd-af8ad89b6719.png"
    const val ANTARES_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/925c68e9-3d37-4634-a446-d8b4e99de9d9.png"

    private val WHITE: Long = 0xFFFFFFFF
    private val BLUE_WHITE: Long = 0xFFA0C4FF
    private val YELLOW: Long = 0xFFFFF4C2
    private val ORANGE: Long = 0xFFFFB874
    private val RED: Long = 0xFFFF6B4A

    val allStars: List<StarEntry> = listOf(
        StarEntry("Sirius", "Canis Major", -1.46f, 1.42f, 8.6, "A1V", BLUE_WHITE, 9940, 25.0,
            "The brightest star in the night sky. A binary star system with a white dwarf companion (Sirius B).",
            "February", "Both", SIRIUS_URL),
        StarEntry("Canopus", "Carina", -0.74f, -5.71f, 310.0, "A9II", YELLOW, 7400, 15000.0,
            "Second brightest star in the night sky. A giant star used for spacecraft navigation.",
            "March", "Southern"),
        StarEntry("Arcturus", "Boötes", -0.05f, -0.30f, 36.7, "K1.5III", ORANGE, 4286, 170.0,
            "The brightest star in the northern celestial hemisphere. An orange giant in the late stages of stellar evolution.",
            "May", "Both"),
        StarEntry("Rigel Kentaurus", "Centaurus", -0.27f, 4.38f, 4.37, "G2V", YELLOW, 5790, 1.5,
            "Alpha Centauri — the closest star system to the Sun. Actually a triple star system.",
            "May", "Southern"),
        StarEntry("Vega", "Lyra", 0.03f, 0.58f, 25.0, "A0V", BLUE_WHITE, 9602, 40.0,
            "The fifth brightest star. Once the northern pole star (around 12,000 BC) and will be again around 13,700 AD.",
            "August", "Both", VEGA_URL),
        StarEntry("Capella", "Auriga", 0.08f, -0.48f, 42.9, "G8III", YELLOW, 4970, 78.0,
            "A quadruple star system appearing as a single bright yellow star. Sixth brightest in the night sky.",
            "January", "Northern"),
        StarEntry("Rigel", "Orion", 0.13f, -7.84f, 860.0, "B8Ia", BLUE_WHITE, 12100, 120000.0,
            "A blue supergiant — the brightest star in Orion despite being named 'Beta Orionis'.",
            "January", "Both", RIGEL_URL),
        StarEntry("Procyon", "Canis Minor", 0.34f, 2.66f, 11.5, "F5IV", YELLOW, 6530, 7.0,
            "The eighth brightest star. Forms the Winter Triangle with Sirius and Betelgeuse.",
            "February", "Both"),
        StarEntry("Achernar", "Eridanus", 0.46f, -1.46f, 139.0, "B6V", BLUE_WHITE, 15000, 1000.0,
            "The brightest star in Eridanus. The flattest star known, with an equatorial bulge.",
            "November", "Southern"),
        StarEntry("Betelgeuse", "Orion", 0.50f, -5.85f, 642.0, "M1Ia", RED, 3600, 126000.0,
            "A red supergiant marking Orion's shoulder. One of the largest known stars — it would fill Jupiter's orbit.",
            "January", "Both", BETELGEUSE_URL),
        StarEntry("Hadar", "Centaurus", 0.61f, -5.42f, 390.0, "B1III", BLUE_WHITE, 25000, 39000.0,
            "Also known as Agena. A triple star system in the Southern Cross region.",
            "May", "Southern"),
        StarEntry("Altair", "Aquila", 0.77f, 2.22f, 16.7, "A7V", WHITE, 7700, 10.6,
            "The brightest star in Aquila. Part of the Summer Triangle with Vega and Deneb.",
            "August", "Both"),
        StarEntry("Acrux", "Crux", 0.77f, -4.14f, 321.0, "B0.5IV", BLUE_WHITE, 28000, 25000.0,
            "The brightest star in the Southern Cross. A multiple star system.",
            "April", "Southern"),
        StarEntry("Aldebaran", "Taurus", 0.86f, -0.63f, 65.3, "K5III", ORANGE, 3910, 439.0,
            "The 'eye of the bull' in Taurus. An orange giant star. Pioneer 10 is heading in its direction.",
            "January", "Both"),
        StarEntry("Antares", "Scorpius", 1.09f, -5.28f, 550.0, "M1.5Iab", RED, 3660, 75900.0,
            "The 'rival of Mars' — a red supergiant at the heart of Scorpius. One of the largest known stars.",
            "July", "Both", ANTARES_URL),
        StarEntry("Spica", "Virgo", 1.04f, -3.55f, 250.0, "B1V", BLUE_WHITE, 22400, 20500.0,
            "The brightest star in Virgo. Actually a very close binary star system.",
            "April", "Both"),
        StarEntry("Pollux", "Gemini", 1.14f, 1.08f, 33.8, "K0III", ORANGE, 4666, 43.0,
            "The brightest star in Gemini. An orange giant with a confirmed exoplanet.",
            "February", "Both"),
        StarEntry("Fomalhaut", "Piscis Austrinus", 1.16f, 1.72f, 25.1, "A3V", WHITE, 8590, 16.6,
            "The 'lonely star' — bright but with no other bright stars nearby. Has a debris disk.",
            "October", "Both"),
        StarEntry("Deneb", "Cygnus", 1.25f, -8.38f, 2600.0, "A2Ia", WHITE, 8525, 196000.0,
            "One of the most luminous stars known. Part of the Summer Triangle despite being the farthest.",
            "September", "Northern"),
        StarEntry("Mimosa", "Crux", 1.25f, -3.92f, 280.0, "B0.5IV", BLUE_WHITE, 27000, 15900.0,
            "The second brightest star in the Southern Cross.",
            "April", "Southern"),
        StarEntry("Regulus", "Leo", 1.35f, -0.52f, 79.3, "B8IV", BLUE_WHITE, 12460, 316.0,
            "The 'heart of the lion' in Leo. A multiple star system. Lies almost on the ecliptic.",
            "April", "Both"),
        StarEntry("Polaris", "Ursa Minor", 1.98f, -3.64f, 433.0, "F7Ib", YELLOW, 6015, 2500.0,
            "The North Star — currently the closest bright star to the north celestial pole. A Cepheid variable.",
            "Year-round", "Northern", POLARIS_URL),
        StarEntry("Castor", "Gemini", 1.57f, 0.59f, 51.0, "A1V", WHITE, 10286, 49.0,
            "The second brightest star in Gemini. Actually a sextuple star system.",
            "February", "Both"),
        StarEntry("Bellatrix", "Orion", 1.64f, -2.75f, 250.0, "B2III", BLUE_WHITE, 22000, 9211.0,
            "The 'Amazon star' marking Orion's left shoulder. A blue giant.",
            "January", "Both"),
        StarEntry("Alnilam", "Orion", 1.69f, -7.40f, 1340.0, "B0Ia", BLUE_WHITE, 27000, 537000.0,
            "The central star of Orion's Belt. A blue supergiant.",
            "January", "Both"),
        StarEntry("Mintaka", "Orion", 2.23f, -4.20f, 1200.0, "O9.5II", BLUE_WHITE, 29500, 90000.0,
            "The westernmost star of Orion's Belt.",
            "January", "Both"),
        StarEntry("Saiph", "Orion", 2.09f, -6.78f, 650.0, "B0.5Ia", BLUE_WHITE, 26500, 56000.0,
            "Marks Orion's right foot. A blue supergiant.",
            "January", "Both"),
        StarEntry("Algol", "Perseus", 2.12f, -0.07f, 90.0, "B8V", BLUE_WHITE, 13000, 182.0,
            "The 'Demon Star' — a famous eclipsing binary that changes brightness every 2.87 days.",
            "November", "Both"),
        StarEntry("Mizar", "Ursa Major", 2.27f, 0.33f, 83.0, "A2V", WHITE, 9000, 25.0,
            "The middle star of the Big Dipper's handle. Forms a visual double with Alcor.",
            "April", "Northern"),
        StarEntry("Dubhe", "Ursa Major", 1.79f, -0.17f, 124.0, "K0III", ORANGE, 4660, 416.0,
            "The pointer star at the tip of the Big Dipper's bowl. Points toward Polaris.",
            "March", "Northern"),
    )
}
