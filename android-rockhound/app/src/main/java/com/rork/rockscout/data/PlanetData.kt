package com.rork.rockscout.data

/**
 * Solar system bodies — 8 planets + 2 dwarf planets with full physical data.
 */

data class PlanetEntry(
    val name: String,
    val type: String,               // "Terrestrial", "Gas Giant", "Ice Giant", "Dwarf Planet"
    val diameterKm: Int,
    val distanceFromSunAu: Double,  // astronomical units
    val orbitalPeriodDays: Double,
    val moons: Int,
    val description: String,
    val massEarth: Double,          // mass relative to Earth
    val gravityMs2: Double,         // surface gravity m/s²
    val tempRangeC: String,         // temperature range in Celsius
    val atmosphere: String,
    val bestViewing: String,
    val apparentMagnitude: String,
    val constellationSeen: String,  // which constellation it appears in (varies)
    val notableFeatures: String,
    val heroImageUrl: String,
)

object PlanetData {

    const val MERCURY_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/71c657e0-e977-49cb-b2e3-070ed6240a08.png"
    const val VENUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/414052dc-ce00-4d98-a7ef-fc65ca035878.png"
    const val EARTH_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/b6d4a564-6452-4e76-949f-ea74a56b1586.png"
    const val MARS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/603efb41-cf11-4f3c-99df-904ebdba0f67.png"
    const val JUPITER_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/af53fc78-a4a6-44e4-a030-ecf90c8ccd2d.png"
    const val SATURN_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/ba946272-55cb-45e6-8ec0-e0da20f0daa9.png"
    const val URANUS_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/686cf9d9-ae12-4ca7-bebf-a5cc7c314f5d.png"
    const val NEPTUNE_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/daf1b5b1-9cb5-437b-b865-5f6a9d002d6e.png"
    const val PLUTO_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/b3fd6d82-8f79-47a4-acb2-8cfa144da517.png"
    const val CERES_URL = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/508c57cb-1f8a-4165-b76c-bcb4c6f3a6da.png"

    val allPlanets: List<PlanetEntry> = listOf(
        PlanetEntry("Mercury", "Terrestrial", 4879, 0.39, 88.0, 0,
            "The smallest planet and closest to the Sun. A cratered, airless world with extreme temperature swings.",
            0.055, 3.7, "-173 to 427", "None (exosphere)", "Dawn or dusk (low on horizon)",
            "-0.4 to +5.5", "Varies (zodiacal)", "Largest crater: Caloris Basin (1,550 km). No atmosphere means no weather.",
            MERCURY_URL),
        PlanetEntry("Venus", "Terrestrial", 12104, 0.72, 224.7, 0,
            "Earth's 'sister planet' — similar in size but with a toxic atmosphere and surface hot enough to melt lead.",
            0.815, 8.87, "462 (avg surface)", "CO₂ (96.5%), N₂ (3.5%)", "Evening or morning sky",
            "-4.9 to -3.8", "Varies (zodiacal)", "Hottest planet. Thick clouds of sulfuric acid. Rotates backwards (retrograde).",
            VENUS_URL),
        PlanetEntry("Earth", "Terrestrial", 12742, 1.0, 365.25, 1,
            "Our home planet — the only known world with life. 71% covered by liquid water oceans.",
            1.0, 9.81, "-89 to 58", "N₂ (78%), O₂ (21%), Ar (0.9%)", "N/A (home planet)",
            "N/A", "N/A", "Only planet with liquid water on surface. Magnetic field protects from solar wind.",
            EARTH_URL),
        PlanetEntry("Mars", "Terrestrial", 6779, 1.52, 687.0, 2,
            "The Red Planet — home to the tallest volcano (Olympus Mons) and deepest canyon (Valles Marineris) in the solar system.",
            0.107, 3.71, "-153 to 20", "CO₂ (95%), N₂ (3%), Ar (2%)", "Opposition (every ~2 years)",
            "-2.9 to +1.8", "Varies (zodiacal)", "Olympus Mons: 22 km tall. Polar ice caps. Dust storms can cover the planet.",
            MARS_URL),
        PlanetEntry("Jupiter", "Gas Giant", 139820, 5.20, 4333.0, 95,
            "The largest planet — a gas giant with the Great Red Spot, a storm larger than Earth that has raged for centuries.",
            317.8, 24.79, "-145 (cloud tops)", "H₂ (90%), He (10%)", "Opposition (yearly)",
            "-2.8 to -1.2", "Varies (zodiacal)", "Great Red Spot: storm 1.3× Earth's diameter. 95 moons including the Galilean 4.",
            JUPITER_URL),
        PlanetEntry("Saturn", "Gas Giant", 116460, 9.58, 10759.0, 146,
            "The ringed jewel of the solar system. Its rings span 280,000 km but are only ~10 meters thick.",
            95.2, 10.44, "-178 (cloud tops)", "H₂ (96%), He (3%)", "Opposition (yearly)",
            "-0.5 to +0.5", "Varies (zodiacal)", "Spectacular ring system. 146 moons including Titan (larger than Mercury).",
            SATURN_URL),
        PlanetEntry("Uranus", "Ice Giant", 50724, 19.22, 30687.0, 28,
            "An ice giant tilted 98° on its side — it rolls around the Sun like a ball. Methane gives it a cyan color.",
            14.5, 8.69, "-224 (cloud tops)", "H₂ (83%), He (15%), CH₄ (2%)", "Opposition (yearly, needs binoculars)",
            "+5.5 to +6.0", "Varies (zodiacal)", "Tilted 98° — poles take turns facing the Sun. Faint rings. Coldest planetary atmosphere.",
            URANUS_URL),
        PlanetEntry("Neptune", "Ice Giant", 49244, 30.05, 60190.0, 16,
            "The windiest planet — supersonic winds reach 2,100 km/h. Deep blue from methane in its atmosphere.",
            17.1, 11.15, "-218 (cloud tops)", "H₂ (80%), He (19%), CH₄ (1%)", "Opposition (needs telescope)",
            "+7.7 to +8.0", "Varies (zodiacal)", "Supersonic winds (2,100 km/h). Great Dark Spot storm. 16 moons including Triton.",
            NEPTUNE_URL),
        PlanetEntry("Pluto", "Dwarf Planet", 2376, 39.48, 90560.0, 5,
            "The most famous dwarf planet — reclassified in 2006. New Horizons revealed a heart-shaped nitrogen ice glacier.",
            0.0022, 0.62, "-229 (surface)", "N₂, CH₄, CO (thin atmosphere)", "Telescope required",
            "+13.5 to +16.0", "Varies", "Heart-shaped Sputnik Planitia glacier. Mountains of water ice. 5 moons including Charon.",
            PLUTO_URL),
        PlanetEntry("Ceres", "Dwarf Planet", 940, 2.77, 1680.0, 0,
            "The largest object in the asteroid belt — the only dwarf planet in the inner solar system.",
            0.00016, 0.27, "-105 (surface)", "Thin water vapor exosphere", "Telescope required",
            "+6.8 to +9.0", "Varies (zodiacal)", "Bright spots of hydrated salts (Occator Crater). May have a subsurface ocean.",
            CERES_URL),
    )
}
