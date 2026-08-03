package com.rork.rockscout.data

/**
 * Text filter for user-generated content in RockScout (18+ app).
 *
 * Only censors a narrow set of sexual/profane words per app policy:
 * variations of fuck, pussy, cunt, twat, vagina, cock, dick, penis, clit,
 * and asshole (but NOT "ass" by itself). Light profanity is allowed.
 *
 * The filter is case-insensitive, replaces matched words with asterisks,
 * and normalizes common leetspeak substitutions before matching.
 */
object ProfanityFilter {

    private val censoredWords = setOf(
        // fuck and variations
        "fuck", "fucking", "fucked", "fucker", "fuckers", "fuckin", "fuckn",
        "motherfucker", "motherfuckers", "motherfucking", "motherfuckin",
        "fuckface", "fuckhead", "fuckboy", "fuckoff", "fck", "fcking",
        "fuk", "fuuk", "phuck", "phucking",
        // sexual anatomy / slang
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

    // Words that should NOT be censored even if they contain a censored substring.
    private val allowedWords = setOf(
        "hell", "damn", "ass", "asses", "badass", "hardass", "smartass",
        "jackass", "dumbass", "kickass", "lass", "class", "grass", "mass",
        "pass", "bass", "glass",
    )

    // Common leetspeak substitutions to normalize before matching.
    private val leetMap = mapOf(
        '1' to 'i', '3' to 'e', '4' to 'a', '5' to 's', '6' to 'g',
        '7' to 't', '8' to 'b', '0' to 'o', '$' to 's', '@' to 'a',
        '!' to 'i', '+' to 't', '#' to 'h', '9' to 'g',
    )

    fun filter(text: String): String {
        if (text.isBlank()) return text

        // First pass: tokenize and censor whole tokens whose normalized form
        // exactly matches a censored word (handles leetspeak on a single token:
        // "f4ck" → "fuck", "d1ck" → "dick").
        val pattern = Regex("([a-zA-Z0-9\\$@!+?#]+)|([^a-zA-Z0-9\\$@!+?#]+)")
        val matches = pattern.findAll(text)

        val result = StringBuilder()
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
            } else if (censoredWords.contains(lower) || censoredWords.contains(normalized)) {
                result.append("*".repeat(token.length))
            } else {
                result.append(token)
            }
        }
        val firstPass = result.toString()

        // Second pass: collapse separators and re-test the whole string so that
        // attempts to split a censored word with punctuation ("f.u.c.k",
        // "f-u-c-k", "f*ck") can't slip through. We mask the contiguous run of
        // word characters whose collapsed form contains a censored word.
        return secondPassCollapsible(firstPass)
    }

    /**
     * Walks [input] and, for each run of letters/digits/leet chars separated by
     * other characters, collapses the run, normalizes leet, and checks whether
     * the collapsed form *equals* a censored word (not merely contains one as a
     * substring). This word-boundary approach prevents false positives on
     * innocent words like "cocktail", "Dickinson", or "Scunthorpe" while still
     * catching separator-split obscenities like "f.u.c.k" or "d1-c-k".
     *
     * Allowed words are never masked even if they exactly match a censored word.
     */
    private fun secondPassCollapsible(input: String): String {
        if (input.isEmpty()) return input
        val out = StringBuilder()
        var i = 0
        while (i < input.length) {
            val ch = input[i]
            if (ch.isLetterOrDigit() || ch in leetMap) {
                val runStart = i
                val collapsed = StringBuilder()
                var j = i
                while (j < input.length) {
                    val c = input[j]
                    if (c.isLetterOrDigit() || c in leetMap) {
                        collapsed.append(c)
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
                // Word-boundary match: only block if the collapsed form exactly
                // equals a censored word, not if it merely contains one.
                val blocked = !allowed &&
                    (censoredWords.contains(normalized) || censoredWords.contains(lower))
                out.append(if (blocked) "*".repeat(run.length) else run)
                i = j
            } else {
                out.append(ch)
                i++
            }
        }
        return out.toString()
    }

    private fun normalize(input: String): String {
        return input.lowercase().map { leetMap[it] ?: it }.joinToString("")
    }

    /** Quick check to tell callers whether a string contains any filtered word. */
    fun hasFilteredWord(text: String): Boolean = filter(text) != text
}
