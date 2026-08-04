/**
 * Web profanity filter — mirrors Android's ProfanityFilter.kt.
 * Two-tier system:
 * - Tier 1 (common profanity): silently asterisked, no warning
 * - Tier 2 (explicit language): asterisked + triggers a warning popup
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

  return { filteredText, hasExplicitContent };
}

/**
 * Parse @username patterns from text and match against known member names.
 * Returns the list of user IDs that were tagged.
 */
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
