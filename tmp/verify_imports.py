import re

BASE = "android-rockhound/app/src/main/java/com/rork/rockscout/ui/screens/"

files_to_check = [
    ("WishlistScreen.kt", ["IconButton", "TextButton", "ButtonDefaults"]),
    ("FavoriteSpotsScreen.kt", ["IconButton"]),
    ("CapturesScreen.kt", ["IconButton", "TextButton"]),
    ("HomeScreen.kt", ["TextButton"]),
    ("LocationDetailScreen.kt", ["Button", "ButtonDefaults"]),
    ("CollectionScreen.kt", ["IconButton"]),
    ("AchievementsScreen.kt", ["IconButton"]),
    ("UserAchievementsScreen.kt", ["IconButton"]),
    ("ElementDetailScreen.kt", ["IconButton"]),
    ("MeteoriteHuntingScreen.kt", ["IconButton"]),
    ("RocksAreAmazingScreen.kt", ["IconButton"]),
    ("DeveloperConsoleScreen.kt", ["Button", "TextButton"]),
    ("LocationsScreen.kt", ["IconButton"]),
]

for fname, types in files_to_check:
    path = BASE + fname
    with open(path, 'r') as f:
        content = f.read()
    lines = content.split('\n')
    
    for btype in types:
        # Check for flat usage: the type name followed by ( but NOT preceded by "Sculpted" or part of "ButtonDefaults"
        flat_usages = []
        for i, line in enumerate(lines, 1):
            # Find pattern like "Button(" or "IconButton(" but not "SculptedButton(" or "SculptedIconButton(" or "ButtonDefaults"
            pattern = r'(?<!Sculpted)(?<!Outlined)(?<!Text)\b' + re.escape(btype) + r'\('
            if btype == "Button":
                # Also exclude ButtonDefaults
                if re.search(r'\bButton\(', line) and 'SculptedButton' not in line and 'ButtonDefaults' not in line and 'OutlinedButton' not in line and 'TextButton' not in line:
                    flat_usages.append((i, line.strip()))
            elif btype == "ButtonDefaults":
                if 'ButtonDefaults' in line and 'ButtonDefaults.' in line:
                    flat_usages.append((i, line.strip()))
            else:
                if re.search(pattern, line) and 'Sculpted' + btype not in line:
                    flat_usages.append((i, line.strip()))
        
        # Check import
        import_line = f"import androidx.compose.material3.{btype}"
        has_import = import_line + "\n" in content or content.endswith(import_line)
        
        if flat_usages and not has_import:
            print(f"  ❌ {fname}: {btype} used but import missing! Lines: {flat_usages[:3]}")
        elif flat_usages and has_import:
            print(f"  ⚠️  {fname}: {btype} import present and still used (flat): {flat_usages[:3]}")
        elif not flat_usages and has_import:
            print(f"  ⚠️  {fname}: {btype} import present but no flat usage (unused import)")
        elif not flat_usages and not has_import:
            print(f"  ✅ {fname}: {btype} - no flat usage, no import (correct)")

print("\nDone")
