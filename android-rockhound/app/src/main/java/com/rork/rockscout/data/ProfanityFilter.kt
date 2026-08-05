package com.rork.rockscout.data

/**
 * Three-tier text filter for user-generated content in RockScout.
 *
 * User-selectable levels (stored on profile, synced via Supabase):
 *  - [ProfanityLevel.OFF]: Only sexually explicit words, racial slurs, and
 *    extra-offensive terms (retard, rape) are asterisked + trigger a warning.
 *    All "fuck" variants and mild profanity are shown uncensored.
 *  - [ProfanityLevel.LOW] (default): Same as OFF, plus "fuck" variants are
 *    silently asterisked (no warning popup). Mild profanity is shown uncensored.
 *  - [ProfanityLevel.STRICT]: Everything except "hell" and "damn" is asterisked.
 *    Explicit words still trigger warnings; "fuck" and mild words are silent.
 *
 * The warning system is unaffected by the level: 3 warnings = auto-report,
 * 5 = 2nd report, 6 = account ban. Only the [alwaysCensored] set triggers
 * warnings. Self-harm detection runs independently and is always active.
 *
 * Group chats can override the user's personal level to STRICT via the
 * [groupStrict] parameter on [filterWithWarning].
 *
 * The filter is case-insensitive, replaces matched words with asterisks,
 * and normalizes common leetspeak substitutions before matching.
 * Word-boundary matching prevents false positives on innocent words like
 * "cocktail", "Dickerson", or "Dickinson".
 */
object ProfanityFilter {

    /** User-selectable profanity filter level. */
    enum class ProfanityLevel(val value: String) {
        OFF("off"),
        LOW("low"),
        STRICT("strict");

        companion object {
            fun fromValue(v: String?): ProfanityLevel =
                entries.firstOrNull { it.value == v } ?: LOW
        }
    }

    /** Current user's filter level. Set by [AppRepository] when the profile loads. */
    @Volatile
    var userLevel: ProfanityLevel = ProfanityLevel.LOW

    /**
     * Sexually explicit words, racial slurs, and severe harassment terms.
     * Asterisked AND trigger a warning in ALL filter levels (off/low/strict).
     */
    private val alwaysCensored = setOf(
        "pussy", "pussies", "pussys",
        "cunt", "cunts",
        "twat", "twats",
        "vagina", "vaginas",
        "cock", "cocks",
        "dick", "dicks", "dickhead",
        "penis", "penises",
        "clit", "clitoris",
        "nigger", "niggers",
        "faggot", "faggots",
        "retard", "retards", "retarded",
        "rape", "rapes", "raping", "rapist", "rapists",
    )

    /** "fuck" variants — silently asterisked in LOW and STRICT. Shown in OFF. */
    private val fuckVariants = setOf(
        "fuck", "fucking", "fucked", "fucker", "fuckers", "fuckin", "fuckn",
        "motherfucker", "motherfuckers", "motherfucking", "motherfuckin",
        "fuckface", "fuckhead", "fuckboy", "fuckoff", "fck", "fcking",
        "fuk", "fuuk", "phuck", "phucking",
    )

    /**
     * Mild profanity — shown uncensored in OFF and LOW, silently asterisked
     * in STRICT. Never triggers a warning.
     */
    private val mildProfanity = setOf(
        "piss",
        "shit", "shitty", "shits", "shitting", "bullshit",
        "dumbass", "asshole", "assholes",
        "bitch", "bitches", "bitching",
        "bastard", "bastards",
        "slut", "sluts",
        "whore", "whores",
        "nigga", "niggas",
        "ass", "asses",
        "damned",
    )

    /**
     * Words that must never be censored even if they contain a censored substring.
     * "hell" and "damn" are exempt from all filtering (including strict).
     * Compound "-ass" words are protected so they don't get caught by "ass".
     */
    private val allowedWords = setOf(
        "hell", "damn",
        "badass", "hardass", "smartass", "jackass", "kickass",
        "lass", "class", "grass", "mass", "pass", "bass", "glass",
        "cocktail", "cocktails",
        "dickerson", "dickinson", "dickinsonia", "dickinsoniids",
        "snigger", "scunthorpe", "penistone", "lightfoot",
        "assassin", "assay", "casserole", "grasshopper",
        "passerby", "massive", "classroom", "classic", "classify",
        "crass", "brass", "morass", "amass",
        "stash", "flash", "splash", "smash",
        "password", "bypass", "underpass", "overpass",
    )

    /** Common leetspeak substitutions to normalize before matching. */
    private val leetMap = mapOf(
        '1' to 'i', '3' to 'e', '4' to 'a', '5' to 's', '6' to 'g',
        '7' to 't', '8' to 'b', '0' to 'o', '$' to 's', '@' to 'a',
        '!' to 'i', '+' to 't', '#' to 'h', '9' to 'g',
    )

    data class FilterResult(
        val filteredText: String,
        val hasExplicitContent: Boolean,
    )

    /** Filter [text] with the user's current level. Returns censored text only. */
    fun filter(text: String): String {
        return filterWithWarning(text).filteredText
    }

    /**
     * Filter [text] and return both the censored text and whether
     * explicit (warning-tier) words were found.
     *
     * @param groupStrict When true, the effective level is forced to STRICT
     *   (for group chats with strict profanity_filter_level).
     */
    fun filterWithWarning(text: String, groupStrict: Boolean = false): FilterResult {
        if (text.isBlank()) return FilterResult(text, false)

        val level = if (groupStrict) ProfanityLevel.STRICT else userLevel

        val warningWords = alwaysCensored
        val silentCensored: Set<String> = when (level) {
            ProfanityLevel.OFF -> emptySet()
            ProfanityLevel.LOW -> fuckVariants
            ProfanityLevel.STRICT -> fuckVariants + mildProfanity
        }
        val allCensored = alwaysCensored + silentCensored

        // First pass: tokenize and censor whole tokens
        val pattern = Regex("([a-zA-Z0-9\\$@!+?#]+)|([^a-zA-Z0-9\\$@!+?#]+)")
        val matches = pattern.findAll(text)

        val result = StringBuilder()
        var foundExplicit = false
        for (match in matches) {
            val token = match.value
            if (!token[0].isLetterOrDigit() && token[0] !in leetMap) {
                result.append(token)
                continue
            }

            val lower = token.lowercase()
            val normalized = normalize(lower)

            if (allowedWords.contains(lower) || allowedWords.contains(normalized)) {
                result.append(token)
            } else if (allCensored.contains(lower) || allCensored.contains(normalized)) {
                result.append("*".repeat(token.length))
                if (warningWords.contains(lower) || warningWords.contains(normalized)) {
                    foundExplicit = true
                }
            } else {
                result.append(token)
            }
        }
        val firstPass = result.toString()

        // Second pass: catch separator-split obscenities
        val (secondPass, foundExplicit2) = secondPassCollapsible(firstPass, allCensored, warningWords)
        return FilterResult(secondPass, foundExplicit || foundExplicit2)
    }

    private fun secondPassCollapsible(
        input: String,
        censored: Set<String>,
        warningWords: Set<String>,
    ): Pair<String, Boolean> {
        if (input.isEmpty()) return input to false
        val out = StringBuilder()
        var i = 0
        var foundExplicit = false
        while (i < input.length) {
            val ch = input[i]
            if (ch.isLetterOrDigit() || ch in leetMap) {
                val runStart = i
                var j = i
                while (j < input.length) {
                    val c = input[j]
                    if (c.isLetterOrDigit() || c in leetMap) {
                        j++
                    } else if (c == '.' || c == '-' || c == '_' || c == '*' ||
                               c == ' ' || c == ',' || c == '/' || c == '\\') {
                        j++
                    } else {
                        break
                    }
                }
                val run = input.substring(runStart, j)
                val lower = run.lowercase()
                val normalized = normalize(lower.filter { it.isLetterOrDigit() || it in leetMap })
                val allowed = allowedWords.contains(lower) || allowedWords.contains(normalized)
                val blocked = !allowed &&
                    (censored.contains(normalized) || censored.contains(lower))
                if (blocked) {
                    out.append("*".repeat(run.length))
                    if (warningWords.contains(normalized) || warningWords.contains(lower)) {
                        foundExplicit = true
                    }
                } else {
                    out.append(run)
                }
                i = j
            } else {
                out.append(ch)
                i++
            }
        }
        return out.toString() to foundExplicit
    }

    private fun normalize(input: String): String {
        return input.lowercase().map { leetMap[it] ?: it }.joinToString("")
    }

    /** Quick check to tell callers whether a string contains any filtered word. */
    fun hasFilteredWord(text: String): Boolean = filter(text) != text

    // ─── Self-Harm Phrase Detection ──────────────────────────────────────
    private val selfHarmPhrases = listOf(
        "kill yourself", "kill urself", "kys", "go kill yourself", "go k!ll yourself",
        "kill yourself.", "kill yourself!", "kill urself.", "kill urself!",
        "go kill urself", "kill myself", "kill urself", "kys.", "kys!",
        "end your life", "end ur life", "end my life", "end it all",
        "hurt myself", "hurt urself", "cut myself", "cut urself",
        "suicide", "suicidal", "kill me", "kill urself now",
        "you should kill yourself", "u should kill yourself",
        "you should kill urself", "u should kill urself",
        "go die", "go d!e", "die already", "d!e already",
        "nobody would miss you", "nobody would care if you died",
    )

    private val selfHarmLeetMap = mapOf(
        '1' to 'i', '3' to 'e', '4' to 'a', '5' to 's', '6' to 'g',
        '7' to 't', '8' to 'b', '0' to 'o', '$' to 's', '@' to 'a',
        '!' to 'i', '+' to 't', '#' to 'h', '9' to 'g',
    )

    data class SelfHarmResult(
        val filteredText: String,
        val hasSelfHarm: Boolean,
        val matchedPhrases: List<String>,
    )

    fun filterSelfHarm(text: String): SelfHarmResult {
        if (text.isBlank()) return SelfHarmResult(text, false, emptyList())

        var result = text
        val matched = mutableListOf<String>()
        val lower = text.lowercase()

        for (phrase in selfHarmPhrases) {
            val normalizedPhrase = normalizeSelfHarm(phrase)
            val normalizedText = normalizeSelfHarm(lower)

            if (lower.contains(phrase)) {
                val pattern = Regex(Regex.escape(phrase), RegexOption.IGNORE_CASE)
                result = pattern.replace(result) { match ->
                    "*".repeat(match.value.length)
                }
                if (phrase !in matched) matched.add(phrase)
            } else if (normalizedText.contains(normalizedPhrase)) {
                val leetPattern = buildLeetInsensitivePattern(phrase)
                val regex = Regex(leetPattern, RegexOption.IGNORE_CASE)
                result = regex.replace(result) { match ->
                    "*".repeat(match.value.length)
                }
                if (phrase !in matched) matched.add(phrase)
            }
        }

        return SelfHarmResult(result, matched.isNotEmpty(), matched)
    }

    private fun normalizeSelfHarm(input: String): String {
        return input.lowercase().map { selfHarmLeetMap[it] ?: it }.joinToString("")
    }

    private fun buildLeetInsensitivePattern(phrase: String): String {
        val leetReplacements = mapOf(
            'i' to "[i1!]", 'e' to "[e3]", 'a' to "[a4@]", 's' to "[s5$]",
            'g' to "[g69]", 't' to "[t7+]", 'b' to "[b8]", 'o' to "[o0]",
            'h' to "[h#]",
        )
        val sb = StringBuilder()
        for (ch in phrase) {
            val lower = ch.lowercaseChar()
            sb.append(leetReplacements[lower] ?: Regex.escape(ch.toString()))
        }
        return sb.toString()
    }
}
