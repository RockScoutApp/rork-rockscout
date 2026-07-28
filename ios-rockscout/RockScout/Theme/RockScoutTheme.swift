import SwiftUI

/// Earth-tone rock-surface palette matching the Android app's dark agate aesthetic.
/// All colors are hex-exact ports from `android-rockhound/.../ui/theme/Color.kt`.
enum RockScoutColors {
    // Core surfaces — dark warm stone
    static let obsidian = Color(red: 0x2A/255, green: 0x26/255, blue: 0x20/255)
    static let slate900 = Color(red: 0x24/255, green: 0x20/255, blue: 0x19/255)
    static let slate800 = Color(red: 0x33/255, green: 0x2E/255, blue: 0x25/255)
    static let slate700 = Color(red: 0x3F/255, green: 0x39/255, blue: 0x30/255)
    static let slate600 = Color(red: 0x4A/255, green: 0x43/255, blue: 0x38/255)
    static let stoneLine = Color(red: 0x6B/255, green: 0x60/255, blue: 0x52/255)

    // Ink — dark text on accent fills
    static let ink = Color(red: 0x1C/255, green: 0x1A/255, blue: 0x14/255)

    // Accent — warm citrine amber
    static let citrine = Color(red: 0xE8/255, green: 0xA3/255, blue: 0x3D/255)
    static let citrineSoft = Color(red: 0xF2/255, green: 0xC1/255, blue: 0x71/255)
    static let citrineDeep = Color(red: 0xB9/255, green: 0x7B/255, blue: 0x1E/255)

    // Secondary — warm copper mineral accent
    static let copper = Color(red: 0xDC/255, green: 0x9A/255, blue: 0x6E/255)
    static let copperDeep = Color(red: 0x9C/255, green: 0x5C/255, blue: 0x3A/255)

    // Tertiary — amethyst
    static let amethyst = Color(red: 0x9B/255, green: 0x7B/255, blue: 0xD8/255)

    // Text — light on dark
    static let textHigh = Color(red: 0xF5/255, green: 0xF2/255, blue: 0xEA/255)
    static let textMid = Color(red: 0xD8/255, green: 0xD2/255, blue: 0xC3/255)
    static let textLow = Color(red: 0xB5/255, green: 0xAE/255, blue: 0x9A/255)

    // Rock-type category colors
    static let igneous = Color(red: 0xE5/255, green: 0x68/255, blue: 0x3C/255)
    static let sedimentary = Color(red: 0xD9/255, green: 0xB2/255, blue: 0x6A/255)
    static let metamorphic = Color(red: 0x6F/255, green: 0xA8/255, blue: 0xC7/255)
    static let fossil = Color(red: 0xC9/255, green: 0xA8/255, blue: 0x7C/255)

    // Status
    static let success = Color(red: 0x5C/255, green: 0xC9/255, blue: 0x8C/255)
    static let warning = Color(red: 0xE8/255, green: 0xA3/255, blue: 0x3D/255)
    static let danger = Color(red: 0xE2/255, green: 0x57/255, blue: 0x4C/255)
}

extension Color {
    /// App background — dark warm stone.
    static let rsBackground = RockScoutColors.obsidian
    /// Card / elevated surface.
    static let rsCard = RockScoutColors.slate800
    /// Surface variant.
    static let rsSurface = RockScoutColors.slate900
    /// Surface variant high.
    static let rsSurfaceHigh = RockScoutColors.slate700
    /// Primary accent — citrine amber.
    static let rsAccent = RockScoutColors.citrine
    /// Secondary accent — copper.
    static let rsSecondary = RockScoutColors.copper
    /// Primary text on dark.
    static let rsText = RockScoutColors.textHigh
    /// Secondary text on dark.
    static let rsTextSecondary = RockScoutColors.textMid
    /// Muted text.
    static let rsTextMuted = RockScoutColors.textLow
    /// Border on dark.
    static let rsBorder = RockScoutColors.stoneLine
}
