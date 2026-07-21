BASE = "android-rockhound/app/src/main/java/com/rork/rockscout/ui/screens/"
path = BASE + "ElementDetailScreen.kt"
with open(path, 'r') as f:
    content = f.read()

# Add Citrine import after TextHigh import (which exists)
old = "import com.rork.rockscout.ui.theme.TextHigh"
new = "import com.rork.rockscout.ui.theme.Citrine\nimport com.rork.rockscout.ui.theme.TextHigh"
if "import com.rork.rockscout.ui.theme.Citrine" not in content:
    content = content.replace(old, new, 1)
    print("Added Citrine import to ElementDetailScreen.kt")
else:
    print("Citrine import already present")

with open(path, 'w') as f:
    f.write(content)
