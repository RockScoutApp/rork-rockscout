/** Solar system bodies — 8 planets + 2 dwarf planets with full physical data. */
export interface PlanetEntry {
  name: string;
  type: string;
  diameterKm: number;
  distanceFromSunAu: number;
  orbitalPeriodDays: number;
  moons: number;
  description: string;
  massEarth: number;
  gravityMs2: number;
  tempRangeC: string;
  atmosphere: string;
  bestViewing: string;
  apparentMagnitude: string;
  constellationSeen: string;
  notableFeatures: string;
  imageUrl: string;
}

const BASE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets";

export const PLANETS: PlanetEntry[] = [
  { name: "Mercury", type: "Terrestrial", diameterKm: 4879, distanceFromSunAu: 0.39, orbitalPeriodDays: 88.0, moons: 0,
    description: "The smallest planet and closest to the Sun. A cratered, airless world with extreme temperature swings.",
    massEarth: 0.055, gravityMs2: 3.7, tempRangeC: "-173 to 427", atmosphere: "None (exosphere)", bestViewing: "Dawn or dusk (low on horizon)",
    apparentMagnitude: "-0.4 to +5.5", constellationSeen: "Varies (zodiacal)", notableFeatures: "Largest crater: Caloris Basin (960 mi). No atmosphere means no weather.",
    imageUrl: `${BASE}/71c657e0-e977-49cb-b2e3-070ed6240a08.png` },
  { name: "Venus", type: "Terrestrial", diameterKm: 12104, distanceFromSunAu: 0.72, orbitalPeriodDays: 224.7, moons: 0,
    description: "Earth's 'sister planet' — similar in size but with a toxic atmosphere and surface hot enough to melt lead.",
    massEarth: 0.815, gravityMs2: 8.87, tempRangeC: "462 (avg surface)", atmosphere: "CO₂ (96.5%), N₂ (3.5%)", bestViewing: "Evening or morning sky",
    apparentMagnitude: "-4.9 to -3.8", constellationSeen: "Varies (zodiacal)", notableFeatures: "Hottest planet. Thick clouds of sulfuric acid. Rotates backwards (retrograde).",
    imageUrl: `${BASE}/414052dc-ce00-4d98-a7ef-fc65ca035878.png` },
  { name: "Earth", type: "Terrestrial", diameterKm: 12742, distanceFromSunAu: 1.0, orbitalPeriodDays: 365.25, moons: 1,
    description: "Our home planet — the only known world with life. 71% covered by liquid water oceans.",
    massEarth: 1.0, gravityMs2: 9.81, tempRangeC: "-89 to 58", atmosphere: "N₂ (78%), O₂ (21%), Ar (0.9%)", bestViewing: "N/A (home planet)",
    apparentMagnitude: "N/A", constellationSeen: "N/A", notableFeatures: "Only planet with liquid water on surface. Magnetic field protects from solar wind.",
    imageUrl: `${BASE}/b6d4a564-6452-4e76-949f-ea74a56b1586.png` },
  { name: "Mars", type: "Terrestrial", diameterKm: 6779, distanceFromSunAu: 1.52, orbitalPeriodDays: 687.0, moons: 2,
    description: "The Red Planet — home to the tallest volcano (Olympus Mons) and deepest canyon (Valles Marineris) in the solar system.",
    massEarth: 0.107, gravityMs2: 3.71, tempRangeC: "-153 to 20", atmosphere: "CO₂ (95%), N₂ (3%), Ar (2%)", bestViewing: "Opposition (every ~2 years)",
    apparentMagnitude: "-2.9 to +1.8", constellationSeen: "Varies (zodiacal)", notableFeatures: "Olympus Mons: 13.7 miles tall. Polar ice caps. Dust storms can cover the planet.",
    imageUrl: `${BASE}/603efb41-cf11-4f3c-99df-904ebdba0f67.png` },
  { name: "Jupiter", type: "Gas Giant", diameterKm: 139820, distanceFromSunAu: 5.20, orbitalPeriodDays: 4333.0, moons: 95,
    description: "The largest planet — a gas giant with the Great Red Spot, a storm larger than Earth that has raged for centuries.",
    massEarth: 317.8, gravityMs2: 24.79, tempRangeC: "-145 (cloud tops)", atmosphere: "H₂ (90%), He (10%)", bestViewing: "Opposition (yearly)",
    apparentMagnitude: "-2.8 to -1.2", constellationSeen: "Varies (zodiacal)", notableFeatures: "Great Red Spot: storm 1.3× Earth's diameter. 95 moons including the Galilean 4.",
    imageUrl: `${BASE}/af53fc78-a4a6-44e4-a030-ecf90c8ccd2d.png` },
  { name: "Saturn", type: "Gas Giant", diameterKm: 116460, distanceFromSunAu: 9.58, orbitalPeriodDays: 10759.0, moons: 146,
    description: "The ringed jewel of the solar system. Its rings span 173,000 miles but are only ~33 feet thick.",
    massEarth: 95.2, gravityMs2: 10.44, tempRangeC: "-178 (cloud tops)", atmosphere: "H₂ (96%), He (3%)", bestViewing: "Opposition (yearly)",
    apparentMagnitude: "-0.5 to +0.5", constellationSeen: "Varies (zodiacal)", notableFeatures: "Spectacular ring system. 146 moons including Titan (larger than Mercury).",
    imageUrl: `${BASE}/ba946272-55cb-45e6-8ec0-e0da20f0daa9.png` },
  { name: "Uranus", type: "Ice Giant", diameterKm: 50724, distanceFromSunAu: 19.22, orbitalPeriodDays: 30687.0, moons: 28,
    description: "An ice giant tilted 98° on its side — it rolls around the Sun like a ball. Methane gives it a cyan color.",
    massEarth: 14.5, gravityMs2: 8.69, tempRangeC: "-224 (cloud tops)", atmosphere: "H₂ (83%), He (15%), CH₄ (2%)", bestViewing: "Opposition (yearly, needs binoculars)",
    apparentMagnitude: "+5.5 to +6.0", constellationSeen: "Varies (zodiacal)", notableFeatures: "Tilted 98° — poles take turns facing the Sun. Faint rings. Coldest planetary atmosphere.",
    imageUrl: `${BASE}/686cf9d9-ae12-4ca7-bebf-a5cc7c314f5d.png` },
  { name: "Neptune", type: "Ice Giant", diameterKm: 49244, distanceFromSunAu: 30.05, orbitalPeriodDays: 60190.0, moons: 16,
    description: "The windiest planet — supersonic winds reach 1,300 mph. Deep blue from methane in its atmosphere.",
    massEarth: 17.1, gravityMs2: 11.15, tempRangeC: "-218 (cloud tops)", atmosphere: "H₂ (80%), He (19%), CH₄ (1%)", bestViewing: "Opposition (needs telescope)",
    apparentMagnitude: "+7.7 to +8.0", constellationSeen: "Varies (zodiacal)", notableFeatures: "Supersonic winds (1,300 mph). Great Dark Spot storm. 16 moons including Triton.",
    imageUrl: `${BASE}/daf1b5b1-9cb5-437b-b865-5f6a9d002d6e.png` },
  { name: "Pluto", type: "Dwarf Planet", diameterKm: 2376, distanceFromSunAu: 39.48, orbitalPeriodDays: 90560.0, moons: 5,
    description: "The most famous dwarf planet — reclassified in 2006. New Horizons revealed a heart-shaped nitrogen ice glacier.",
    massEarth: 0.0022, gravityMs2: 0.62, tempRangeC: "-229 (surface)", atmosphere: "N₂, CH₄, CO (thin atmosphere)", bestViewing: "Telescope required",
    apparentMagnitude: "+13.5 to +16.0", constellationSeen: "Varies", notableFeatures: "Heart-shaped Sputnik Planitia glacier. Mountains of water ice. 5 moons including Charon.",
    imageUrl: `${BASE}/b3fd6d82-8f79-47a4-acb2-8cfa144da517.png` },
  { name: "Ceres", type: "Dwarf Planet", diameterKm: 940, distanceFromSunAu: 2.77, orbitalPeriodDays: 1680.0, moons: 0,
    description: "The largest object in the asteroid belt — the only dwarf planet in the inner solar system.",
    massEarth: 0.00016, gravityMs2: 0.27, tempRangeC: "-105 (surface)", atmosphere: "Thin water vapor exosphere", bestViewing: "Telescope required",
    apparentMagnitude: "+6.8 to +9.0", constellationSeen: "Varies (zodiacal)", notableFeatures: "Bright spots of hydrated salts (Occator Crater). May have a subsurface ocean.",
    imageUrl: `${BASE}/508c57cb-1f8a-4165-b76c-bcb4c6f3a6da.png` },
];
