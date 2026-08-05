/**
 * Web profanity filter — mirrors Android's ProfanityFilter.kt.
 * Three-tier system with user-selectable levels (off/low/strict):
 *
 * - OFF: Only sexually explicit words, racial slurs, and severe terms
 *   (retard, rape) are asterisked + trigger a warning.
 *   All "fuck" variants and mild profanity are shown uncensored.
 * - LOW (default): Same as OFF, plus "fuck" variants are silently
 *   asterisked (no warning). Mild profanity is shown uncensored.
 * - STRICT: Everything except "hell" and "damn" is asterisked.
 *   Explicit words still trigger warnings; "fuck" and mild are silent.
 *
 * Self-harm detection runs independently and is always active.
 * Includes an allowlist of false-positive words that must never be censored
 * even if they contain a censored substring (e.g. "cocktail", "Dickerson").
 */

export type ProfanityLevel = "off" | "low" | "strict";

interface FilterResult {
  filteredText: string;
  hasExplicitContent: boolean;
}

/**
 * Sexually explicit words, racial slurs, and severe harassment terms.
 * Asterisked AND trigger a warning in ALL filter levels.
 */
const ALWAYS_CENSORED: string[] = [
  "cunt", "cunts",
  "cock", "cocks",
  "dick", "dicks", "dickhead",
  "pussy", "pussies", "pussys",
  "twat", "twats",
  "vagina", "vaginas",
  "penis", "penises",
  "clit", "clitoris",
  "nigger", "niggers",
  "faggot", "faggots",
  "retard", "retards", "retarded",
  "rape", "rapes", "raping", "rapist", "rapists",
];

/** "fuck" variants — silently asterisked in LOW and STRICT. Shown in OFF. */
const FUCK_VARIANTS: string[] = [
  "fuck", "fucker", "fucking", "fucked", "fuckers", "fuckin", "fuckn",
  "motherfucker", "motherfuckers", "motherfucking", "motherfuckin",
  "fuckface", "fuckhead", "fuckboy", "fuckoff",
  "fck", "fcking", "fuk", "fuuk", "phuck", "phucking",
];

/**
 * Mild profanity — shown uncensored in OFF and LOW, silently asterisked
 * in STRICT. Never triggers a warning.
 */
const MILD_PROFANITY: string[] = [
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
];

/**
 * Words that must never be censored even if they contain a censored substring.
 * "hell" and "damn" are exempt from all filtering (including strict).
 * Compound "-ass" words are protected so they don't get caught by "ass".
 */
const ALLOWED_WORDS: Set<string> = new Set([
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
]);

function asteriskWord(word: string): string {
  if (word.length <= 2) return "*".repeat(word.length);
  return word[0] + "*".repeat(word.length - 2) + word[word.length - 1];
}

const escapeRegex = (s: string) => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

/**
 * Filter text using the three-tier profanity system.
 * Returns the filtered text and whether explicit content was detected.
 *
 * @param userLevel The user's profanity filter level (off/low/strict).
 * @param groupStrict When true, forces STRICT regardless of userLevel
 *   (for group chats with strict profanity_filter_level).
 */
export function filterProfanity(
  text: string,
  userLevel: ProfanityLevel = "low",
  groupStrict = false,
): FilterResult {
  let filteredText = text;
  let hasExplicitContent = false;

  const level: ProfanityLevel = groupStrict ? "strict" : userLevel;

  // Determine which words to silently asterisk (no warning)
  const silentCensored: string[] =
    level === "off"
      ? []
      : level === "low"
        ? FUCK_VARIANTS
        : [...FUCK_VARIANTS, ...MILD_PROFANITY];

  const allCensored = [...ALWAYS_CENSORED, ...silentCensored];

  // Protect allowed words by temporarily replacing them with placeholders.
  const placeholders: string[] = [];
  for (const allowed of ALLOWED_WORDS) {
    const regex = new RegExp(`\\b${escapeRegex(allowed)}\\b`, "gi");
    filteredText = filteredText.replace(regex, (match) => {
      const idx = placeholders.length;
      placeholders.push(match);
      return `\u0000ALLOWED_${idx}\u0000`;
    });
  }

  // Censor all matched words
  for (const word of allCensored) {
    const regex = new RegExp(`\\b${escapeRegex(word)}\\b`, "gi");
    if (regex.test(filteredText)) {
      const isExplicit = ALWAYS_CENSORED.includes(word);
      if (isExplicit) hasExplicitContent = true;
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

// ─── Self-Harm Phrase Detection ──────────────────────────────────────
const SELF_HARM_PHRASES: string[] = [
  "kill yourself", "kill urself", "kys", "go kill yourself",
  "kill myself", "end your life", "end ur life", "end my life",
  "end it all", "hurt myself", "hurt urself", "cut myself", "cut urself",
  "suicide", "suicidal", "kill me", "you should kill yourself",
  "u should kill yourself", "you should kill urself", "u should kill urself",
  "go die", "die already", "nobody would miss you",
  "nobody would care if you died",
];

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
