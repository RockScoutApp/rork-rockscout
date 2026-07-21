import re
import sys
from pathlib import Path

UI_DIR = Path("android-rockhound/app/src/main/java/com/rork/rockscout/ui")

def double_border_width(match: re.Match) -> str:
    prefix = match.group(1)  # ".border(" or "BorderStroke("
    value = float(match.group(2))
    doubled = value * 2
    # Format as integer if whole number, else keep one decimal
    if doubled == int(doubled):
        value_str = str(int(doubled))
    else:
        value_str = str(doubled)
    return f"{prefix}{value_str}.dp"

# Match .border(N.dp, or BorderStroke(N.dp,
border_pattern = re.compile(r"(\.border\(|BorderStroke\()([0-9]+(?:\.[0-9]+)?)\.dp")

changed = 0
for path in UI_DIR.rglob("*.kt"):
    text = path.read_text()
    new_text, count = border_pattern.subn(double_border_width, text)
    if count:
        path.write_text(new_text)
        changed += count
        print(f"{path}: {count} replacements")

print(f"Total border widths doubled: {changed}")
