#!/usr/bin/env python3
"""
For each Specimen with a rarity value containing extra context (parentheses,
semicolons, etc.), check if that context is already mentioned in the description.
If not, append "Rarity: <full rarity>." to the description.
"""
import re
import os
import glob

DATA_DIR = "android-rockhound/app/src/main/java/com/rork/rockscout/data"

def has_extra_context(rarity: str) -> bool:
    """Check if rarity has extra context beyond a basic rarity word."""
    return '(' in rarity or ';' in rarity or ',' in rarity

def get_context_keywords(rarity: str) -> list:
    """Extract key context phrases from rarity value."""
    parts = re.findall(r'\(([^)]+)\)', rarity)
    if ';' in rarity:
        # Handle semicolon-separated like "Common (pegmatites), Rare (gem pockets)"
        pass  # Already captured by parentheses regex
    return [p.strip() for p in parts if p.strip()]

def description_mentions_context(description: str, rarity: str) -> bool:
    """Check if description already mentions the rarity context."""
    contexts = get_context_keywords(rarity)
    desc_lower = description.lower()
    for ctx in contexts:
        # Extract meaningful words (length > 2) from context
        words = [w.lower().strip('.,;:~%') for w in ctx.split() if len(w.strip('.,;:~%')) > 2]
        if not words:
            continue
        matches = sum(1 for w in words if w in desc_lower)
        # If >= 60% of context words are already in description, skip
        if matches >= max(1, len(words) * 0.6):
            return True
    return False

def process_file(filepath: str) -> int:
    """Process a .kt file, adding rarity context to descriptions. Returns count of changes."""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find all Specimen( positions
    spec_positions = [(m.start(), m.end()) for m in re.finditer(r'Specimen\s*\(', content)]
    if not spec_positions:
        return 0

    changes = []  # (start, end, new_text) for description value replacements

    for i, (spec_start, spec_paren_start) in enumerate(spec_positions):
        # Find end of this Specimen block
        if i + 1 < len(spec_positions):
            spec_end = spec_positions[i + 1][0]
        else:
            # Last specimen — track parenthesis depth to find closing )
            remaining = content[spec_paren_start:]
            depth = 1
            j = 0
            while j < len(remaining) and depth > 0:
                ch = remaining[j]
                if ch == '(':
                    depth += 1
                elif ch == ')':
                    depth -= 1
                elif ch == '"':
                    j += 1
                    while j < len(remaining) and remaining[j] != '"':
                        if remaining[j] == '\\':
                            j += 1
                        j += 1
                j += 1
            spec_end = spec_paren_start + j

        spec_content = content[spec_start:spec_end]

        # Find description in this block
        desc_match = re.search(r'description\s*=\s*"([^"]*)"', spec_content)
        if not desc_match:
            continue
        description = desc_match.group(1)

        # Find rarity in this block
        rarity_match = re.search(r'rarity\s*=\s*"([^"]*)"', spec_content)
        if not rarity_match:
            continue
        rarity = rarity_match.group(1)

        if not has_extra_context(rarity):
            continue

        if description_mentions_context(description, rarity):
            continue

        # Append rarity info to description
        rarity_sentence = f" Rarity: {rarity}."
        new_description = description + rarity_sentence

        # Absolute positions of the description VALUE in content
        desc_val_start = spec_start + desc_match.start(1)
        desc_val_end = spec_start + desc_match.end(1)

        changes.append((desc_val_start, desc_val_end, new_description))

    if changes:
        # Apply in reverse order to preserve positions
        changes.sort(key=lambda x: x[0], reverse=True)
        for start, end, new_text in changes:
            content = content[:start] + new_text + content[end:]
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"  Modified: {os.path.basename(filepath)} ({len(changes)} changes)")
        return len(changes)
    return 0

total = 0
for filepath in sorted(glob.glob(os.path.join(DATA_DIR, "*.kt"))):
    total += process_file(filepath)

print(f"\nTotal changes: {total}")
