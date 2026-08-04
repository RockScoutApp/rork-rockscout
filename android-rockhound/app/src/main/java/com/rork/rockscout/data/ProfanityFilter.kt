package com.rork.rockscout.data

/**
 * Two-tier text filter for user-generated content in RockScout (18+ app).
 *
 * Tier 1 — "fuck" variants: silently asterisked, NO warning.
 * Tier 2 — sexually explicit words: asterisked AND trigger a warning popup.
 *   3 warnings = auto-report, 5 = 2nd report, 6 = account ban.
 *
 * Light profanity (shit, bitch, damn, hell, ass) is allowed by default.
 * The "Extra Strict" mode (group chats) extends tier 2 to also catch
 * light profanity with the same warning system.
 *
 * The filter is case-insensitive, replaces matched words with asterisks,
 * and normalizes common leetspeak substitutions before matching.
 * Word-boundary matching prevents false positives on innocent words like
 * "cocktail", "Dickerson", or "Dickinson".
 */
object ProfanityFilter {

    /** "fuck" variants — silently asterisked, no warning. */
    private val fuckVariants = setOf(
        "fuck", "fucking", "fucked", "fucker", "fuckers", "fuckin", "fuckn",
        "motherfucker", "motherfuckers", "motherfucking", "motherfuckin",
        "fuckface", "fuckhead", "fuckboy", "fuckoff", "fck", "fcking",
        "fuk", "fuuk", "phuck", "phucking",
    )

    /** Sexually explicit words — asterisked AND trigger a warning. */
    private val explicitWords = setOf(
        "pussy", "pussies", "pussys",
        "cunt", "cunts",
        "twat", "twats",
        "vagina", "vaginas",
        "cock", "cocks",
        "dick", "dicks", "dickhead",
        "penis", "penises",
        "clit", "clitoris",
        "asshole", "assholes",
    )

    /** Light profanity — only filtered in "Extra Strict" mode. */
    private val lightProfanity = setOf(
        "shit", "shitty", "shits", "shitting",
        "bitch", "bitches", "bitching",
        "damn", "damned",
        "hell",
        "ass", "asses",
    )

    /** Words that must never be censored even if they contain a censored substring. */
    private val allowedWords = setOf(
        "hell", "damn", "ass", "asses", "badass", "hardass", "smartass",
        "jackass", "dumbass", "kickass", "lass", "class", "grass", "mass",
        "pass", "bass", "glass",
        // False-positive names and words
        "cocktail", "cocktails",
        "dickerson", "dickinson",
    )

    /** Common leetspeak substitutions to normalize before matching. */
    private val leetMap = mapOf(
        '1' to 'i', '3' to 'e', '4' to 'a', '5' to 's', '6' to 'g',
        '7' to 't', '8' to 'b', '0' to 'o', '$' to 's', '@' to 'a',
        '!' to 'i', '+' to 't', '#' to 'h', '9' to 'g',
    )

    /**
     * Result of a filter pass: the censored text and whether any
     * sexually explicit (warning-tier) words were found.
     */
    data class FilterResult(
        val filteredText: String,
        val hasExplicitContent: Boolean,
    )

    /**
     * Filter [text] with the normal (non-strict) profanity policy.
     * Returns the censored text. Use [filterWithWarning] if you need
     * to know whether explicit words were found (for the warning popup).
     */
    fun filter(text: String): String {
        return filterWithWarning(text, strict = false).filteredText
    }

    /**
     * Filter [text] and return both the censored text and whether
     * explicit (warning-tier) words were found.
     *
     * @param strict When true, light profanity is also treated as
     *   warning-tier (for "Extra Strict" group chats).
     */
    fun filterWithWarning(text: String, strict: Boolean = false): FilterResult {
        if (text.isBlank()) return FilterResult(text, false)

        val warningWords = if (strict) explicitWords + lightProfanity else explicitWords
        val allCensored = fuckVariants + warningWords

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

    /**
     * Walks [input] and, for each run of letters/digits/leet chars separated by
     * other characters, collapses the run, normalizes leet, and checks whether
     * the collapsed form equals a censored word. Word-boundary matching prevents
     * false positives on innocent words.
     */
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
}
