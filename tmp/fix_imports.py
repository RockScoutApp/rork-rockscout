BASE = "android-rockhound/app/src/main/java/com/rork/rockscout/ui/screens/"

# DeveloperConsoleScreen: restore OutlinedButton import (still used by .sculpted() OutlinedButtons)
path = BASE + "DeveloperConsoleScreen.kt"
with open(path, 'r') as f:
    content = f.read()

# Add OutlinedButton import back after MaterialTheme import
old = "import androidx.compose.material3.MaterialTheme\n"
new = "import androidx.compose.material3.MaterialTheme\nimport androidx.compose.material3.OutlinedButton\n"
if "import androidx.compose.material3.OutlinedButton" not in content:
    content = content.replace(old, new, 1)
    print("Restored OutlinedButton import in DeveloperConsoleScreen.kt")
else:
    print("OutlinedButton import already present")

with open(path, 'w') as f:
    f.write(content)

# Also check: is 'Button' import still needed in DeveloperConsole? We removed all flat Button() calls.
# The OutlinedButtons use .sculpted() already. Let's verify no remaining flat Button( usage.
import re
# Check for standalone Button( calls (not ButtonDefaults, not SculptedButton, not OutlinedButton, not TextButton)
flat_buttons = []
for i, line in enumerate(content.split('\n'), 1):
    stripped = line.strip()
    # Look for Button( but not ButtonDefaults, SculptedButton, OutlinedButton, TextButton, SculptedDialogButton
    if re.search(r'\bButton\(', stripped) and 'SculptedButton' not in stripped and 'ButtonDefaults' not in stripped and 'OutlinedButton' not in stripped:
        flat_buttons.append((i, stripped))

if flat_buttons:
    print(f"  WARNING: Still has flat Button( calls: {flat_buttons}")
else:
    print("  No flat Button() calls remaining - good")

# Check LocationDetailScreen for unused imports
path2 = BASE + "LocationDetailScreen.kt"
with open(path2, 'r') as f:
    content2 = f.read()
# Check if Button or ButtonDefaults still used
if "Button(" in content2 and "SculptedButton" not in content2.split("Button(")[0][-10:]:
    # more careful check
    lines2 = content2.split('\n')
    flat2 = []
    for i, line in enumerate(lines2, 1):
        stripped = line.strip()
        if re.search(r'\bButton\(', stripped) and 'SculptedButton' not in stripped and 'ButtonDefaults' not in stripped:
            flat2.append((i, stripped))
    if flat2:
        print(f"LocationDetailScreen: Still has flat Button() calls: {flat2}")
    else:
        print("LocationDetailScreen: No flat Button() calls - good")

# Check WishlistScreen for unused IconButton import
path3 = BASE + "WishlistScreen.kt"
with open(path3, 'r') as f:
    content3 = f.read()
if "IconButton(" in content3:
    print(f"WishlistScreen: Still has IconButton( usage - may need import")
else:
    print("WishlistScreen: No IconButton() calls - import correctly removed")

# Check FavoriteSpotsScreen for unused IconButton import  
path4 = BASE + "FavoriteSpotsScreen.kt"
with open(path4, 'r') as f:
    content4 = f.read()
if "IconButton(" in content4:
    print(f"FavoriteSpotsScreen: Still has IconButton( usage - may need import")
else:
    print("FavoriteSpotsScreen: No IconButton() calls - import correctly removed")

# Check CapturesScreen for unused IconButton/TextButton imports
path5 = BASE + "CapturesScreen.kt"
with open(path5, 'r') as f:
    content5 = f.read()
if "IconButton(" in content5:
    print(f"CapturesScreen: Still has IconButton( usage - may need import")
else:
    print("CapturesScreen: No IconButton() calls - import correctly removed")
if "TextButton(" in content5:
    print(f"CapturesScreen: Still has TextButton( usage - may need import")
else:
    print("CapturesScreen: No TextButton() calls - import correctly removed")

# Check HomeScreen for unused TextButton import
path6 = BASE + "HomeScreen.kt"
with open(path6, 'r') as f:
    content6 = f.read()
if "TextButton(" in content6:
    print(f"HomeScreen: Still has TextButton( usage - may need import")
else:
    print("HomeScreen: No TextButton() calls - import correctly removed")

print("\nDone checking imports")
