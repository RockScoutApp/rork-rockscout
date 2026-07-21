BASE = "android-rockhound/app/src/main/java/com/rork/rockscout/ui/screens/"
path = BASE + "LocationsScreen.kt"
with open(path, 'r') as f:
    content = f.read()
content = content.replace("import androidx.compose.material3.IconButton\n", "", 1)
with open(path, 'w') as f:
    f.write(content)
print("Removed unused IconButton import from LocationsScreen.kt")
