/**
 * Web profanity filter — mirrors Android's ProfanityFilter.kt.
 * Two-tier system:
 * - Tier 1 (common profanity): silently asterisked, no warning
 * - Tier 2 (explicit language): asterisked + triggers a warning popup
 *
 * Includes an allowlist of false-positive words that must never be censored
 * even if they contain a censored substring (e.g. "cocktail", "Dickerson").
 */

interface FilterResult {
  filteredText: string;
  hasExplicitContent: boolean;
}

// Common profanity — silently asterisked (tier 1)
const COMMON_PROFANITY: string[] = [
  "fuck",
  "fucker",
  "fucking",
  "fucked",
  "motherfucker",
  "shit",
  "bullshit",
  "dumbass",
  "asshole",
  "damn",
  "hell",
];

// Explicit language — asterisked + warning (tier 2)
const EXPLICIT_WORDS: string[] = [
  "cunt",
  "cock",
  "dick",
  "pussy",
  "bitch",
  "bastard",
  "slut",
  "whore",
  "nigger",
  "nigga",
  "faggot",
  "retard",
  "rape",
];

// Words that must never be censored even if they contain a censored substring.
// Mirrors Android's ProfanityFilter.allowedWords plus additional geological /
// paleontological / proper-noun terms relevant to RockScout.
const ALLOWED_WORDS: Set<string> = new Set([
  "cocktail",
  "cocktails",
  "dickerson",
  "dickinson",
  "dickinsonia",
  "dickinsoniids",
  // Geological / mineral terms
  "ass",
  "asses",
  "badass",
  "hardass",
  "smartass",
  "jackass",
  "dumbass",
  "kickass",
  "lass",
  "class",
  "grass",
  "mass",
  "pass",
  "bass",
  "glass",
  // Additional false positives
  "snigger",
  "scunthorpe",
  "penistone",
  "lightfoot",
  "assassin",
  "assay",
  "casserole",
  "grasshopper",
  "passerby",
  "massive",
  "classroom",
  "classic",
  "classify",
]);

function asteriskWord(word: string): string {
  if (word.length <= 2) return "*".repeat(word.length);
  return word[0] + "*".repeat(word.length - 2) + word[word.length - 1];
}

/**
 * Filter text using the two-tier profanity system.
 * Returns the filtered text and whether explicit content was detected.
 */
export function filterProfanity(text: string, strict = false): FilterResult {
  let filteredText = text;
  let hasExplicitContent = false;

  const escapeRegex = (s: string) => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

  // Protect allowed words by temporarily replacing them with placeholders.
  // This prevents both tier-1 and tier-2 filters from touching them, then we
  // restore the originals after all filtering is done.
  const placeholders: string[] = [];
  for (const allowed of ALLOWED_WORDS) {
    const regex = new RegExp(`\\b${escapeRegex(allowed)}\\b`, "gi");
    filteredText = filteredText.replace(regex, (match) => {
      const idx = placeholders.length;
      placeholders.push(match); // preserve original casing
      return `\u0000ALLOWED_${idx}\u0000`;
    });
  }

  // Tier 1: common profanity — silent asterisk
  for (const word of COMMON_PROFANITY) {
    const regex = new RegExp(`\\b${escapeRegex(word)}\\b`, "gi");
    filteredText = filteredText.replace(regex, (match) => asteriskWord(match));
  }

  // Tier 2: explicit language — asterisk + warning
  const explicitList = strict
    ? [...EXPLICIT_WORDS, "crap", "piss", "jackass", " prick"]
    : EXPLICIT_WORDS;
  for (const word of explicitList) {
    const trimmed = word.trim();
    const regex = new RegExp(`\\b${escapeRegex(trimmed)}\\b`, "gi");
    if (regex.test(filteredText)) {
      hasExplicitContent = true;
      filteredText = filteredText.replace(regex, (match) =>
        asteriskWord(match),
      );
    }
  }

  // Restore protected allowed words
  for (let i = placeholders.length - 1; i >= 0; i--) {
    filteredText = filteredText.replace(
      `\u0000ALLOWED_${i}\u0000`,
      placeholders[i],
    );
  }

  return { filteredText, hasExplicitContent };
}

/**
 * Parse @username patterns from text and match against known member names.
 * Returns the list of user IDs that were tagged.
 */
// ─── Self-Harm Phrase Detection ──────────────────────────────────────
// Phrases that indicate self-harm or encouraging self-harm.
// 1st offense = warning popup, 2nd offense = auto-report + bell + email.
const SELF_HARM_PHRASES: string[] = [
  "kill yourself", "kill urself", "kys", "go kill yourself",
  "kill myself", "end your life", "end ur life", "end my life",
  "end it all", "hurt myself", "hurt urself", "cut myself", "cut urself",
  "suicide", "suicidal", "kill me", "you should kill yourself",
  "u should kill yourself", "you should kill urself", "u should kill urself",
  "go die", "die already", "nobody would miss you",
  "nobody would care if you died",
];

// Leetspeak normalization map
const LEET_MAP: Record<string, string> = {
  "1": "i", "3": "e", "4": "a", "5": "s", "6": "g",
  "7": "t", "8": "b", "0": "o", "$": "s", "@": "a",
  "!": "i", "+": "t", "#": "h", "9": "g",
};

function normalizeLeet(input: string): string {
  return input
    .toLowerCase()
    .split("")
    .map((ch) => LEET_MAP[ch] ?? ch)
    .join("");
}

function buildLeetInsensitivePattern(phrase: string): string {
  const replacements: Record<string, string> = {
    i: "[i1!]", e: "[e3]", a: "[a4@]", s: "[s5$]",
    g: "[g69]", t: "[t7+]", b: "[b8]", o: "[o0]", h: "[h#]",
  };
  return phrase
    .split("")
    .map((ch) => {
      const lower = ch.toLowerCase();
      return replacements[lower] ?? ch.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    })
    .join("");
}

export interface SelfHarmFilterResult {
  filteredText: string;
  hasSelfHarm: boolean;
  matchedPhrases: string[];
}

/** Check for self-harm phrases in text. Returns asterisked text and matched phrases. */
export function filterSelfHarm(text: string): SelfHarmFilterResult {
  if (!text || !text.trim()) {
    return { filteredText: text, hasSelfHarm: false, matchedPhrases: [] };
  }

  let result = text;
  const matched: string[] = [];
  const lower = text.toLowerCase();
  const normalizedText = normalizeLeet(lower);

  for (const phrase of SELF_HARM_PHRASES) {
    const normalizedPhrase = normalizeLeet(phrase);

    if (lower.includes(phrase)) {
      const regex = new RegExp(
        phrase.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"),
        "gi",
      );
      result = result.replace(regex, (match) => "*".repeat(match.length));
      if (!matched.includes(phrase)) matched.push(phrase);
    } else if (normalizedText.includes(normalizedPhrase)) {
      const pattern = buildLeetInsensitivePattern(phrase);
      const regex = new RegExp(pattern, "gi");
      result = result.replace(regex, (match) => "*".repeat(match.length));
      if (!matched.includes(phrase)) matched.push(phrase);
    }
  }

  return { filteredText: result, hasSelfHarm: matched.length > 0, matchedPhrases: matched };
}

export function parseTaggedUserIds(
  text: string,
  members: { id: string; display_name: string }[],
): string[] {
  if (!text || !members.length) return [];
  const tagged: string[] = [];
  for (const member of members) {
    const pattern = `@${member.display_name}`;
    if (text.toLowerCase().includes(pattern.toLowerCase())) {
      tagged.push(member.id);
    }
  }
  return [...new Set(tagged)];
}
