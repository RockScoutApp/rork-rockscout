package com.rork.rockscout.data

/**
 * Common wildlife data organized by U.S. state/region and coastal type.
 * Used by the "Common Wildlife" tile on BLM, beach, and state park detail screens.
 */

data class WildlifeEntry(
    val mammals: List<String>,
    val birds: List<String>,
    val reptiles: List<String> = emptyList(),
    val marine: List<String> = emptyList(),
)

object WildlifeData {

    private val desertSouthwest = WildlifeEntry(
        mammals = listOf(" Coyote", "🦊 Kit Fox", "🦌 Mule Deer", "🐗 Javelina", "🐰 Jackrabbit"),
        birds = listOf("🦅 Golden Eagle", "🐦 Roadrunner", "🦉 Great Horned Owl", "🦃 Gambel's Quail"),
        reptiles = listOf("🦎 Desert Iguana", "🐍 Gopher Snake", "🦂 Desert Scorpion", "🦗 Tarantula Hawk"),
    )

    private val pacificNorthwest = WildlifeEntry(
        mammals = listOf("🐻 Black Bear", "🦌 Roosevelt Elk", "🦫 North American Beaver", "🐺 Gray Wolf", "🦦 River Otter"),
        birds = listOf("🦅 Bald Eagle", "🦉 Spotted Owl", "🐦 Varied Thrush", "🦆 Harlequin Duck"),
        reptiles = listOf("🐸 Pacific Tree Frog", "🦎 Western Fence Lizard"),
        marine = listOf("🐋 Orca", "🦭 Harbor Seal", "🐟 Chinook Salmon", "🦀 Dungeness Crab"),
    )

    private val rockyMountain = WildlifeEntry(
        mammals = listOf("🦬 Bison", "🦌 Elk", "🐻 Grizzly Bear", "🐐 Mountain Goat", "🐑 Bighorn Sheep"),
        birds = listOf("🦅 Bald Eagle", "🦉 Great Gray Owl", "🐦 Clark's Nutcracker", "🦃 Wild Turkey"),
        reptiles = listOf("🐍 Western Rattlesnake", "🦎 Sagebrush Lizard", "🐸 Boreal Toad"),
    )

    private val greatPlains = WildlifeEntry(
        mammals = listOf("🦬 Bison", "🦌 Pronghorn", "🦊 Swift Fox", "🐰 Black-tailed Prairie Dog", "💨 Badger"),
        birds = listOf("🦅 Ferruginous Hawk", "🐦 Greater Prairie Chicken", "🦉 Burrowing Owl", "🦆 Northern Pintail"),
        reptiles = listOf("🐍 Bullsnake", "🦎 Prairie Lizard", "🐸 Plains Spadefoot"),
    )

    private val appalachian = WildlifeEntry(
        mammals = listOf("🐻 Black Bear", "🦌 White-tailed Deer", "🦝 Raccoon", "🐿️ Eastern Chipmunk", "🦫 Groundhog"),
        birds = listOf("🦅 Red-tailed Hawk", "🐦 Scarlet Tanager", "🦉 Barred Owl", "🐦 Wood Thrush"),
        reptiles = listOf("🐍 Timber Rattlesnake", "🦎 Five-lined Skink", "🐸 American Bullfrog"),
    )

    private val southeastern = WildlifeEntry(
        mammals = listOf("🐻 Black Bear", "🦌 White-tailed Deer", "🦝 Raccoon", "🐊 Alligator (marsh areas)", "🐰 Marsh Rabbit"),
        birds = listOf("🦅 Osprey", "🐦 Northern Cardinal", "🦉 Barred Owl", "🐦 Brown Pelican"),
        reptiles = listOf("🐍 Cottonmouth", "🐢 Gopher Tortoise", "🦎 Green Anole"),
    )

    private val southwestern = WildlifeEntry(
        mammals = listOf("🦌 Mule Deer", "🐗 Javelina", "🦝 Ringtail", "🐰 Antelope Jackrabbit", "🦫 Coati"),
        birds = listOf("🦅 Bald Eagle", "🐦 Vermilion Flycatcher", "🦉 Elf Owl", "🐦 Cactus Wren"),
        reptiles = listOf("🐍 Western Diamondback", "🦎 Gila Monster", "🐢 Desert Tortoise"),
    )

    private val alaska = WildlifeEntry(
        mammals = listOf("🐻 Grizzly Bear", "🦬 Caribou", "🐺 Gray Wolf", "🦌 Moose", "🐑 Dall Sheep"),
        birds = listOf("🦅 Bald Eagle", "🦉 Snowy Owl", "🐦 Willow Ptarmigan", "🦆 Harlequin Duck"),
        marine = listOf("🐋 Humpback Whale", "🦭 Steller Sea Lion", "🐬 Orca", "🐟 Pacific Salmon"),
    )

    private val defaultWildlife = WildlifeEntry(
        mammals = listOf("🦌 White-tailed Deer", "🦝 Raccoon", "🐰 Cottontail Rabbit", "🦊 Red Fox"),
        birds = listOf("🦅 Red-tailed Hawk", "🐦 Northern Cardinal", "🦉 Great Horned Owl", "🐦 Blue Jay"),
        reptiles = listOf("🐍 Garter Snake", "🦎 Fence Lizard", "🐸 American Toad"),
    )

    /** Atlantic coast wildlife. */
    private val atlanticCoast = WildlifeEntry(
        mammals = listOf("🦭 Harbor Seal", "🐬 Bottlenose Dolphin", "🐋 Humpback Whale (migratory)", "🦝 Raccoon"),
        birds = listOf("🐦 Brown Pelican", "🦆 Common Eider", "🐦 Sanderling", "🦅 Osprey", "🐦 Piping Plover"),
        reptiles = listOf("🐢 Loggerhead Sea Turtle (nesting)", "🦎 Six-lined Racerunner"),
        marine = listOf("🦀 Atlantic Blue Crab", "🦞 American Lobster", "🐟 Striped Bass", "🦪 Eastern Oyster"),
    )

    /** Gulf coast wildlife. */
    private val gulfCoast = WildlifeEntry(
        mammals = listOf("🦭 Harbor Seal", "🐬 Bottlenose Dolphin", "🦝 Raccoon", "🐗 Feral Hog"),
        birds = listOf("🐦 Brown Pelican", "🦆 Mottled Duck", "🐦 Roseate Spoonbill", "🦅 Osprey", "🐦 Laughing Gull"),
        reptiles = listOf("🐊 American Alligator", "🐢 Kemp's Ridley Sea Turtle", "🐍 Diamondback Watersnake"),
        marine = listOf("🦀 Blue Crab", "🦐 Brown Shrimp", "🐟 Red Drum", "🦪 Eastern Oyster"),
    )

    /** Pacific coast wildlife. */
    private val pacificCoast = WildlifeEntry(
        mammals = listOf("🦭 Harbor Seal", "🐬 Bottlenose Dolphin", "🐋 Gray Whale (migratory)", "🦦 Sea Otter"),
        birds = listOf("🐦 Brown Pelican", "🐦 Western Gull", "🦆 Surf Scoter", "🦅 Bald Eagle", "🐦 Black Oystercatcher"),
        reptiles = listOf("🦎 Western Fence Lizard", "🐸 Pacific Tree Frog"),
        marine = listOf("🦀 Dungeness Crab", "🦀 Rock Crab", "🐟 Chinook Salmon", "🐙 Giant Pacific Octopus"),
    )

    /** Great Lakes coastal wildlife. */
    private val greatLakesCoast = WildlifeEntry(
        mammals = listOf("🦌 White-tailed Deer", "🦝 Raccoon", "🦫 North American Beaver", "🐺 Gray Wolf (remote areas)"),
        birds = listOf("🦅 Bald Eagle", "🐦 Ring-billed Gull", "🦆 Common Goldeneye", "🐦 Piping Plover"),
        reptiles = listOf("🐸 American Bullfrog", "🦎 Five-lined Skink"),
        marine = listOf("🐟 Lake Trout", "🐟 Walleye", "🦪 Native Mussel", "🐟 Yellow Perch"),
    )

    /** Map state codes to wildlife entries. */
    private val stateWildlife: Map<String, WildlifeEntry> = mapOf(
        "AZ" to desertSouthwest, "NM" to desertSouthwest, "NV" to desertSouthwest,
        "UT" to desertSouthwest, "CA" to pacificNorthwest,
        "OR" to pacificNorthwest, "WA" to pacificNorthwest, "ID" to rockyMountain,
        "MT" to rockyMountain, "WY" to rockyMountain, "CO" to rockyMountain,
        "AK" to alaska,
        "ND" to greatPlains, "SD" to greatPlains, "NE" to greatPlains, "KS" to greatPlains,
        "OK" to greatPlains, "TX" to southwestern,
        "MN" to greatPlains, "IA" to greatPlains,
        "WV" to appalachian, "VA" to appalachian, "KY" to appalachian, "TN" to appalachian,
        "NC" to appalachian, "SC" to southeastern, "GA" to southeastern, "FL" to southeastern,
        "AL" to southeastern, "MS" to southeastern, "LA" to southeastern, "AR" to southeastern,
        "MO" to appalachian, "IL" to appalachian, "IN" to appalachian, "OH" to appalachian,
        "PA" to appalachian, "NY" to appalachian, "ME" to appalachian, "VT" to appalachian,
        "NH" to appalachian, "MA" to appalachian, "CT" to appalachian, "RI" to appalachian,
        "NJ" to appalachian, "DE" to appalachian, "MD" to appalachian, "DC" to appalachian,
        "WI" to greatPlains, "MI" to greatPlains,
        "HI" to WildlifeEntry(
            mammals = listOf("🐗 Feral Pig", "🦭 Hawaiian Monk Seal", "🐬 Spinner Dolphin"),
            birds = listOf("🐦 Nene (Hawaiian Goose)", "🦅 Hawaiian Hawk", "🐦 Hawaiian Honeycreeper"),
            marine = listOf("🐢 Green Sea Turtle", "🐟 Humuhumunukunukuapua'a", "🐙 Hawaiian Octopus"),
        ),
    )

    /** Get wildlife for a state code. */
    fun forState(stateCode: String): WildlifeEntry {
        return stateWildlife[stateCode.uppercase()] ?: defaultWildlife
    }

    /** Get coastal wildlife for a beach/shore location based on its region. */
    fun forCoastalLocation(region: String, latitude: Double, longitude: Double): WildlifeEntry {
        val lower = region.lowercase()
        return when {
            lower.contains("pacific") || lower.contains("californ") || lower.contains("oregon") ||
                lower.contains("washington") || longitude < -115.0 -> pacificCoast
            lower.contains("gulf") || lower.contains("florida") || lower.contains("texas") ||
                lower.contains("louisiana") || lower.contains("mississippi") || lower.contains("alabama") -> gulfCoast
            lower.contains("great lake") || lower.contains("michigan") || lower.contains("erie") ||
                lower.contains("superior") || lower.contains("huron") -> greatLakesCoast
            else -> atlanticCoast
        }
    }
}
