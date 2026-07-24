package com.rork.rockscout.data

/**
 * Hardcoded list of storm chaser YouTube channels for the Severe Weather tab.
 * Organized as national channels (always visible) plus state-specific entries.
 *
 * To add a channel: append a [StormChaserChannel] entry to [national] or [byState].
 */
data class StormChaserChannel(
    val name: String,
    val handle: String,
    val channelUrl: String,
    val stateOrRegion: String,
    val description: String,
)

object StormChaserData {

    /** National channels — always shown, cover all states. */
    val national: List<StormChaserChannel> = listOf(
        StormChaserChannel(
            name = "Ryan Hall Y'all",
            handle = "@RyanHallYall",
            channelUrl = "https://www.youtube.com/@RyanHallYall",
            stateOrRegion = "National",
            description = "Severe weather coverage and live streams across the US.",
        ),
        StormChaserChannel(
            name = "Max Velocity",
            handle = "@MaxVelocityWX",
            channelUrl = "https://www.youtube.com/@MaxVelocityWX",
            stateOrRegion = "National",
            description = "Extreme weather chasing — tornadoes, hurricanes, and supercells.",
        ),
        StormChaserChannel(
            name = "Reed Timmer",
            handle = "@ReedTimmerWx",
            channelUrl = "https://www.youtube.com/@ReedTimmerWx",
            stateOrRegion = "National",
            description = "Veteran tornado chaser and meteorologist. Live intercepts and storm analysis.",
        ),
        StormChaserChannel(
            name = "NOAA National Weather Service",
            handle = "@noaanationalweatherservice",
            channelUrl = "https://www.youtube.com/@noaanationalweatherservice",
            stateOrRegion = "National",
            description = "Official NWS updates, briefings, and safety information.",
        ),
    )

    /** State-specific and regional channels. Keyed by state name. */
    val byState: List<StormChaserChannel> = listOf(
        StormChaserChannel("Storm Chaser Brad Arnold", "@StormChaserBradArnold", "https://www.youtube.com/@StormChaserBradArnold", "Alabama", "Based in Huntsville — covers AL, TN, KY, MO outbreaks."),
        StormChaserChannel("Arkansas Weather Watchers", "youtube.com/channel/UCIsJiPHPi8t8FgTwIh9Grqg", "https://www.youtube.com/channel/UCIsJiPHPi8t8FgTwIh9Grqg", "Arkansas", "Arkansas-focused weather monitoring and storm chasing."),
        StormChaserChannel("Storm Chaser Brandon Copic", "@BrandonCopicWx", "https://www.youtube.com/@BrandonCopicWx", "Colorado", "High Plains and CO specialist — severe weather and tornadoes."),
        StormChaserChannel("CJ Morgan — Florida Man Weather", "@floridamanwx", "https://www.youtube.com/@floridamanwx", "Florida", "FL-based — hurricanes, tropical systems, and severe weather."),
        StormChaserChannel("Edgar ONeal", "@EdgarTheStormChaser", "https://www.youtube.com/@EdgarTheStormChaser", "Georgia", "Covers FL/GA severe weather and hurricane coverage."),
        StormChaserChannel("Illinois Storm Chasers", "@IllinoisStormChasers", "https://www.youtube.com/@IllinoisStormChasers", "Illinois", "IL-focused storm chasing and severe weather alerts."),
        StormChaserChannel("Connor Croff", "@ConnorCroff", "https://www.youtube.com/@ConnorCroff", "Indiana", "Based in IN — covers Midwest and Montana tornado threats."),
        StormChaserChannel("Storm Chaser Corey Gerken", "@coreygerkenwx", "https://www.youtube.com/@coreygerkenwx", "Iowa", "IA/IL/CO storm chases — tornadoes and supercells."),
        StormChaserChannel("Kansas Storm Chaser", "@KansasStormChaser", "https://www.youtube.com/@KansasStormChaser", "Kansas", "KS-based since 2010 — tornado alley coverage."),
        StormChaserChannel("Storm Chaser Scott Peake", "@StormChaserScottPeake", "https://www.youtube.com/@StormChaserScottPeake", "Louisiana", "LA/MS/AL coverage — Gulf Coast severe weather."),
        StormChaserChannel("MI Storm Chasers", "@MIStormChasers", "https://www.youtube.com/@MIStormChasers", "Michigan", "MI-based storm chasing — Great Lakes severe weather."),
        StormChaserChannel("Storm Chaser Vince Waelti", "@StormChaserVince", "https://www.youtube.com/@StormChaserVince", "Minnesota", "MN/WI/NE specialist — Plains and Upper Midwest storms."),
        StormChaserChannel("Storm Chaser Scott Peake", "@StormChaserScottPeake", "https://www.youtube.com/@StormChaserScottPeake", "Mississippi", "LA/MS/AL coverage — Gulf Coast severe weather."),
        StormChaserChannel("Storm Chaser Brad Arnold", "@StormChaserBradArnold", "https://www.youtube.com/@StormChaserBradArnold", "Missouri", "Covers MO severe weather outbreaks."),
        StormChaserChannel("Connor Croff", "@ConnorCroff", "https://www.youtube.com/@ConnorCroff", "Montana", "Covers MT tornado threats and High Plains storms."),
        StormChaserChannel("Max Olson Chasing", "@maxolsonchasing", "https://www.youtube.com/@maxolsonchasing", "Nebraska", "Plains/NE coverage — tornadoes and severe thunderstorms."),
        StormChaserChannel("New York State Storm Chasers", "@newyorkstatestormchasers", "https://www.youtube.com/@newyorkstatestormchasers", "New York", "NY-focused — Northeast severe weather and winter storms."),
        StormChaserChannel("HurricaneTrack", "@HurricaneTrack", "https://www.youtube.com/@HurricaneTrack", "North Carolina", "Carolinas and hurricane coverage — landfall intercepts."),
        StormChaserChannel("Storm Chaser Brandon Copic", "@BrandonCopicWx", "https://www.youtube.com/@BrandonCopicWx", "North Dakota", "Covers SD/ND High Plains — tornadoes and severe storms."),
        StormChaserChannel("Storm Chaser Aaron Rigsby", "youtube.com/c/ohiostormchasers", "https://www.youtube.com/c/ohiostormchasers", "Ohio", "OH-based storm chaser — tornadoes and severe weather."),
        StormChaserChannel("Oklahoma Storm Chaser", "@Oklahomachaser", "https://www.youtube.com/@Oklahomachaser", "Oklahoma", "OK-based — tornado alley intercepts."),
        StormChaserChannel("Pacific Northwest Weather Watch", "@PacificNWWxWatch", "https://www.youtube.com/@PacificNWWxWatch", "Oregon", "WA/OR/ID/MT — Pacific Northwest weather coverage."),
        StormChaserChannel("HurricaneTrack", "@HurricaneTrack", "https://www.youtube.com/@HurricaneTrack", "South Carolina", "Carolinas coverage — hurricanes and severe weather."),
        StormChaserChannel("John McKinney", "@JohnMcKinney", "https://www.youtube.com/@JohnMcKinney", "South Dakota", "SD-based chaser — Plains tornadoes and storms."),
        StormChaserChannel("Storm Chaser Brad Arnold", "@StormChaserBradArnold", "https://www.youtube.com/@StormChaserBradArnold", "Tennessee", "Covers TN severe weather outbreaks."),
        StormChaserChannel("Texas Storm Chasers", "@texasstormchasers", "https://www.youtube.com/@texasstormchasers", "Texas", "Texas-only since 2009 — tornadoes, hail, and supercells."),
        StormChaserChannel("Pacific Northwest Weather Watch", "@PacificNWWxWatch", "https://www.youtube.com/@PacificNWWxWatch", "Washington", "WA/OR/ID/MT — Pacific Northwest weather coverage."),
        StormChaserChannel("Storm Chaser Vince Waelti", "@StormChaserVince", "https://www.youtube.com/@StormChaserVince", "Wisconsin", "Covers WI — Upper Midwest severe weather."),
        StormChaserChannel("Storm Chaser Brandon Copic", "@BrandonCopicWx", "https://www.youtube.com/@BrandonCopicWx", "Wyoming", "Covers WY High Plains — severe storms and tornadoes."),
        // New England regional coverage
        StormChaserChannel("New England Storm Chasers", "youtube.com/c/nescweather", "https://www.youtube.com/c/nescweather", "Connecticut", "Regional — covers all 6 New England states."),
        StormChaserChannel("New England Storm Chasers", "youtube.com/c/nescweather", "https://www.youtube.com/c/nescweather", "Maine", "Regional — covers all 6 New England states."),
        StormChaserChannel("Henry's Weather Channel", "@henrysweatherchannel", "https://www.youtube.com/@henrysweatherchannel", "Massachusetts", "Based in Topsfield, MA — New England weather coverage."),
        StormChaserChannel("New England Storm Chasers", "youtube.com/c/nescweather", "https://www.youtube.com/c/nescweather", "New Hampshire", "Regional — covers all 6 New England states."),
        StormChaserChannel("New England Storm Chasers", "youtube.com/c/nescweather", "https://www.youtube.com/c/nescweather", "Rhode Island", "Regional — covers all 6 New England states."),
        StormChaserChannel("New England Storm Chasers", "youtube.com/c/nescweather", "https://www.youtube.com/c/nescweather", "Vermont", "Regional — covers all 6 New England states."),
    )

    /** States with no dedicated chaser — fall back to national channels. */
    val statesWithoutDedicatedChaser: List<String> = listOf(
        "Alaska", "Arizona", "California", "Delaware", "Hawaii", "Idaho",
        "Kentucky", "Maryland", "Nevada", "New Jersey", "New Mexico",
        "Pennsylvania", "Utah", "Virginia", "West Virginia",
    )

    /** All channels combined, national first then state-sorted. */
    val all: List<StormChaserChannel> = national + byState.sortedBy { it.stateOrRegion }
}
