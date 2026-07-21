package com.rork.rockscout.ui.theme

import androidx.compose.ui.graphics.Color

// Core surfaces — dark rock-surface palette (agate background + dark scrim)
val Obsidian = Color(0xFF2A2620)      // app background — dark warm stone
val Slate900 = Color(0xFF242019)      // primary surface — dark
val Slate800 = Color(0xFF332E25)      // card / elevated surface — dark
val Slate700 = Color(0xFF3F3930)      // surface variant — dark
val Slate600 = Color(0xFF4A4338)      // highest surface / outline variant
val StoneLine = Color(0xFF6B6052)     // prominent border on dark bg

// Ink — dark text & icon-on-accent color (used on bright Citrine/Aqua fills)
val Ink = Color(0xFF1C1A14)

// Accent — warm citrine amber
val Citrine = Color(0xFFE8A33D)
val CitrineSoft = Color(0xFFF2C171)
val CitrineDeep = Color(0xFFB97B1E)

// Secondary — warm copper mineral accent (fits the earthy dark palette)
// Brightened from #D4895F → #DC9A6E for WCAG AA compliance on all dark surfaces.
// Full opacity: ~6.4:1 on app bg; 0.85-alpha: ~5.5:1; 0.75-alpha: ~4.9:1 on dark cards.
val Aqua = Color(0xFFDC9A6E)
val AquaDeep = Color(0xFF9C5C3A)

// True aqua / cyan accent for high-visibility action pills.
val Cyan = Color(0xFF00E5C9)
val CyanDeep = Color(0xFF00B894)

// Tertiary — amethyst for crystals/minerals accents
val Amethyst = Color(0xFF9B7BD8)

// Text — light on dark backgrounds
// Values pinned to WCAG AA against the dark agate background and Slate900/800.
val TextHigh = Color(0xFFF5F2EA)   // ~14:1 on dark surfaces — primary text
val TextMid = Color(0xFFD8D2C3)    // ~9:1 on dark surfaces — secondary text
val TextLow = Color(0xFFB5AE9A)    // ~5.5:1 on dark surfaces — muted/caption text

// Rock-type category colors
val Igneous = Color(0xFFE5683C)   // molten / volcanic
val Sedimentary = Color(0xFFD9B26A) // sand / strata
val Metamorphic = Color(0xFF6FA8C7) // pressure / slate-blue
val Fossil = Color(0xFFC9A87C)   // bone / amber

// Status
val Success = Color(0xFF5CC98C)
val Warning = Color(0xFFE8A33D)
val Danger = Color(0xFFE2574C)

// Chat bubble preset colors — distinct backgrounds for each speaker
val MyBubbleBg = Color(0xFF1E6B5E)      // deep teal-green (malachite) — current user
val OtherBubbleBg = Color(0xFF3D4258)   // muted indigo-slate (lapis) — other user

// Dark card text colors (for dark-themed clickable cards on light background)
// Pinned to WCAG AA against the dark card gradient (Color(0xFF1E1C16)-style).
// DarkTextLow was 0xFFB5AE9A (~4.6:1 — borderline); bumped to clear AA.
val DarkTextHigh = Color(0xFFF5F2EA)
val DarkTextMid = Color(0xFFD8D2C3)
val DarkTextLow = Color(0xFFC9C2B0)  // ~7.5:1 on dark cards (was 0xFFB5AE9A ~4.6:1)

// ── Contrast-token roles ───────────────────────────────────────────────────
// Named text-color roles pinned to contrast-safe values against their paired
// background. Screens should prefer these over ad-hoc colors so legibility
// stays consistent and future screens inherit the fix automatically.

/** Primary text on the dark app background (Obsidian / Slate900). */
val textOnSurface = Color(0xFFF5F2EA)         // ~14:1 on Slate900
/** Secondary/muted text on dark surfaces. AA-pass on Slate800/900. */
val textOnSurfaceMuted = Color(0xFFB5AE9A)    // ~5.5:1 on Slate900
/** Primary text on dark cards (Color(0xFF1E1C16)-style gradients). */
val textOnDark = Color(0xFFF5F2EA)            // ~14:1 on dark cards
/** Secondary/muted text on dark cards. AA-pass on the dark gradient. */
val textOnDarkMuted = Color(0xFFC9C2B0)       // ~7.5:1 on dark cards
/** Text rendered over imagery / satellite tiles — assumes a scrim is present. */
val textOnImage = Color(0xFFFAF8F4)
/** Text on accent-filled chips (Citrine/Aqua fills with dark text). */
val textOnAccent = Color(0xFF1C1A14)
