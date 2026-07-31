package com.rork.rockscout.data

/**
 * 16 narration chapters for the RockScout tutorial narrator player.
 * Audio files are bundled in res/raw/ as narrator_*.mp3.
 * Titles and file names match the narration script in web/rockscout_tutorial_script.md.
 */
data class NarratorChapter(
    val index: Int,
    val title: String,
    val rawResName: String,
    val scriptPreview: String,
)

object NarratorChapters {
    val chapters: List<NarratorChapter> = listOf(
        NarratorChapter(1, "Welcome", "narrator_rockscout_welcome_intro", "Hey there. Welcome to RockScout."),
        NarratorChapter(2, "5-Source AI Rock ID", "narrator_rock_identification_voice", "This is the big one. Tap Identify a Rock right on the home screen."),
        NarratorChapter(3, "Your Collection", "narrator_rocks_collection_guide", "This is My Rocks — your personal collection."),
        NarratorChapter(4, "Field Tools", "narrator_field_capture_voice", "Field Captures is where you log photos of rocks you find out in the field."),
        NarratorChapter(5, "Dig Sites & Gem Shows", "narrator_treasure_map_voice", "This is your treasure map."),
        NarratorChapter(6, "Trip Planning", "narrator_trip_planner_voice", "The Trip Planner is where you build your hunt route."),
        NarratorChapter(7, "Trading & Community", "narrator_trade_board_intro_voice", "The Trade Board is where you post specimens to swap, sell, or trade."),
        NarratorChapter(8, "Social", "narrator_social_network_voice", "RockScout's got a whole social network built in."),
        NarratorChapter(9, "Aurora & Night Sky", "narrator_aurora_forecaster_voice", "This is your personal space weather station."),
        NarratorChapter(10, "Your Profile", "narrator_profile_level_up_voice", "Tap your avatar to open your Profile — your Player Card."),
        NarratorChapter(11, "Reference Library", "narrator_periodic_table_voice_guide", "The Periodic Table — all 118 elements."),
        NarratorChapter(12, "Artifacts & Wonders", "narrator_artifact_catalog_voice", "The Artifacts tile takes you to a growing catalog of prehistoric artifacts."),
        NarratorChapter(13, "Field Kit", "narrator_rockhounding_guide_voice", "The BLM Public Lands Guide breaks down rockhounding rules."),
        NarratorChapter(14, "Learn & Explore", "narrator_educational_guides_intro", "The Educational Guides hub is where you go to learn the science."),
        NarratorChapter(15, "Premium & Free Tier", "narrator_pricing_explanation_voice", "Let's talk about the money side of things."),
        NarratorChapter(16, "Outro", "narrator_rockscout_voice_intro", "That's RockScout. I built it for rockhounders, because I am one."),
    )
}
