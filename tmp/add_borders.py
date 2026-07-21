#!/usr/bin/env python3
"""Add glowingBorder to borderless .clip().background() patterns."""
import re
import os
import glob

BASE = "android-rockhound/app/src/main/java/com/rork/rockscout"
files = glob.glob(f"{BASE}/ui/screens/*.kt") + glob.glob(f"{BASE}/ui/components/*.kt")

IMPORT_LINE = "import com.rork.rockscout.ui.components.glowingBorder"


def determine_border_color(bg_text: str) -> str:
    if "accent" in bg_text:
        return "accent.copy(alpha = 0.35f)"
    if "Citrine" in bg_text:
        return "Citrine.copy(alpha = 0.35f)"
    if "Aqua" in bg_text:
        return "Aqua.copy(alpha = 0.35f)"
    if "Danger" in bg_text:
        return "Danger.copy(alpha = 0.35f)"
    if "Success" in bg_text:
        return "Success.copy(alpha = 0.35f)"
    if "Color.White" in bg_text:
        return "Color.White.copy(alpha = 0.20f)"
    if "Color.Black" in bg_text:
        return "Color.White.copy(alpha = 0.15f)"
    m = re.search(r"Color\(0xFF([0-9A-Fa-f]+)\)", bg_text)
    if m:
        return f"Color(0xFF{m.group(1)}).copy(alpha = 0.35f)"
    return "Color.White.copy(alpha = 0.20f)"


def should_skip(shape: str, bg_text: str) -> bool:
    # Skip tiny shapes (dots, indicators)
    for sz in ("2.dp", "3.dp", "4.dp", "5.dp"):
        if f"RoundedCornerShape({sz})" == shape:
            return True
    # Skip scrims/overlays
    if "alpha = 0.9" in bg_text or "alpha = 0.95" in bg_text:
        return True
    if "Color.Black.copy" in bg_text and ("0.7" in bg_text or "0.8" in bg_text):
        return True
    if bg_text.strip() == ".background(Color.Black)":
        return True
    if "Transparent" in bg_text:
        return True
    return False


# Pattern: .clip(<shape>)\n<indent>.background(<balanced parens>)
# We use a simpler heuristic: match .clip + newline + indent + .background( ... )
# where background args don't contain nested parens beyond one level.
# For multi-line background calls, we match up to the closing paren.
pattern = re.compile(
    r"\.clip\((RoundedCornerShape\(\d+\.dp\)|CircleShape)\)\n"
    r"(\s+)\.background\(([^()]*(?:\([^()]*\)[^()]*)*)\)",
    re.MULTILINE,
)

total = 0
changed_files = []

for fpath in sorted(files):
    with open(fpath) as f:
        content = f.read()

    original = content
    matches = list(pattern.finditer(content))

    for m in reversed(matches):
        shape = m.group(1)
        indent = m.group(2)
        bg_args = m.group(3)
        bg_text = f".background({bg_args})"

        # Check what follows - skip if glowingBorder/border already present within 120 chars
        after = content[m.end():m.end() + 120]
        if ".glowingBorder(" in after or ".border(" in after:
            continue

        if should_skip(shape, bg_text):
            continue

        border_color = determine_border_color(bg_text)
        if shape == "CircleShape":
            border_call = f"\n{indent}.glowingBorder(1.dp, {border_color}, CircleShape)"
        else:
            border_call = f"\n{indent}.glowingBorder(1.dp, {border_color}, {shape})"

        content = content[:m.end()] + border_call + content[m.end():]

    if content != original:
        # Add import if not present
        if IMPORT_LINE not in content:
            # Find last import line
            last_import_match = None
            for lm in re.finditer(r"^import .+$", content, re.MULTILINE):
                last_import_match = lm
            if last_import_match:
                pos = last_import_match.end()
                content = content[:pos] + "\n" + IMPORT_LINE + content[pos:]

        with open(fpath, "w") as f:
            f.write(content)

        n = len(matches)
        total += n
        changed_files.append((fpath, n))
        print(f"  {fpath}: +{n}")

print(f"\nTotal: {total} glowing borders added across {len(changed_files)} files")
