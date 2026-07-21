#!/usr/bin/env python3
"""Replace flat Material 3 buttons with sculpted equivalents."""

BASE = "android-rockhound/app/src/main/java/com/rork/rockscout/ui/screens/"

def edit_file(path, replacements):
    with open(path, 'r') as f:
        content = f.read()
    for i, (old, new) in enumerate(replacements):
        if old not in content:
            print(f"  WARNING #{i}: NOT FOUND in {path}: {repr(old[:80])}")
            continue
        content = content.replace(old, new, 1)
    with open(path, 'w') as f:
        f.write(content)
    print(f"  Done: {path}")

def add_import(anchor, new_import):
    return (anchor, new_import + "\n" + anchor)

# ── File 2: LocationDetailScreen.kt ──
print("LocationDetailScreen.kt")
edit_file(BASE + "LocationDetailScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.sculpted",
        "import com.rork.rockscout.ui.components.SculptedButton\nimport com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.Button\n", ""),
    ("import androidx.compose.material3.ButtonDefaults\n", ""),
    # Button 1: Directions
    ("""                    Button(
                        onClick = {
                            val googleMapsUri = "google.navigation:q=${loc.latitude},${loc.longitude}"
                            val fallbackGeoUri = "geo:${loc.latitude},${loc.longitude}?q=${loc.latitude},${loc.longitude}(${Uri.encode(loc.name)})"
                            SafeLinkOpener.openMaps(context, googleMapsUri, fallbackGeoUri)
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Citrine, contentColor = Ink),
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Directions", fontWeight = FontWeight.Bold)
                    }""",
     """                    SculptedButton(
                        text = "Directions",
                        onClick = {
                            val googleMapsUri = "google.navigation:q=${loc.latitude},${loc.longitude}"
                            val fallbackGeoUri = "geo:${loc.latitude},${loc.longitude}?q=${loc.latitude},${loc.longitude}(${Uri.encode(loc.name)})"
                            SafeLinkOpener.openMaps(context, googleMapsUri, fallbackGeoUri)
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        accent = Citrine,
                        containerColor = Citrine,
                        textColor = Ink,
                        icon = Icons.Filled.Directions,
                    )"""),
    # Button 2: Favorite
    ("""                    Button(
                        onClick = { repo.toggleFavoriteSpot(loc.id) },
                        modifier = Modifier.size(50.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    ) {
                        Icon(
                            if (isFav) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) Citrine else TextMid,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = if (isFav) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Favorite",
                        onClick = { repo.toggleFavoriteSpot(loc.id) },
                        accent = Citrine,
                        iconTint = if (isFav) Citrine else TextMid,
                        size = 50.dp,
                    )"""),
    # Button 3: Share
    ("""                    Button(
                        onClick = {
                            scope.launch {
                                ShareCardImage.share(
                                    context = context,
                                    title = loc.name,
                                    subtitle = loc.region + "  •  " + loc.type.label,
                                    body = loc.summary,
                                    accentHex = 0xFF2C6F9B,
                                    fileName = "rockscout_site_${loc.id}",
                                )
                            }
                        },
                        modifier = Modifier.size(50.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    ) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Share spot",
                            tint = TextMid,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = Icons.Filled.Share,
                        contentDescription = "Share spot",
                        onClick = {
                            scope.launch {
                                ShareCardImage.share(
                                    context = context,
                                    title = loc.name,
                                    subtitle = loc.region + "  •  " + loc.type.label,
                                    body = loc.summary,
                                    accentHex = 0xFF2C6F9B,
                                    fileName = "rockscout_site_${loc.id}",
                                )
                            }
                        },
                        accent = Citrine,
                        iconTint = TextMid,
                        size = 50.dp,
                    )"""),
    # Button 4: PersonAdd
    ("""                    Button(
                        onClick = { shareToProfileLocation = loc },
                        modifier = Modifier.size(50.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    ) {
                        Icon(
                            Icons.Filled.PersonAdd,
                            contentDescription = "Share to profile",
                            tint = Citrine,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = Icons.Filled.PersonAdd,
                        contentDescription = "Share to profile",
                        onClick = { shareToProfileLocation = loc },
                        accent = Citrine,
                        iconTint = Citrine,
                        size = 50.dp,
                    )"""),
])

# ── File 3: WishlistScreen.kt ──
print("WishlistScreen.kt")
edit_file(BASE + "WishlistScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.sculpted",
        "import com.rork.rockscout.ui.components.SculptedIconButton\nimport com.rork.rockscout.ui.components.SculptedTextButton"),
    ("import androidx.compose.material3.ButtonDefaults\n", ""),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("import androidx.compose.material3.TextButton\n", ""),
    # TextButton → SculptedTextButton
    ("""                                    TextButton(
                                        onClick = {
                                            // Moving a wishlist specimen to My Rocks is a
                                            // wishlist-hit-found — the +25 XP bonus action.
                                            val wasInWishlist = repo.isWishlisted(spec.id)
                                            repo.toggleCollection(spec.id)
                                            repo.toggleWishlist(spec.id)
                                            if (wasInWishlist) {
                                                AchievementsRepository.award(XpSource.WISHLIST_HIT, familyTag = spec.id)
                                            }
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = if (isInCollection) Success else Aqua,
                                        ),
                                        modifier = Modifier.padding(vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = if (isInCollection) "In collection" else "Add to collection",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }""",
     """                                    SculptedTextButton(
                                        text = if (isInCollection) "In collection" else "Add to collection",
                                        onClick = {
                                            // Moving a wishlist specimen to My Rocks is a
                                            // wishlist-hit-found — the +25 XP bonus action.
                                            val wasInWishlist = repo.isWishlisted(spec.id)
                                            repo.toggleCollection(spec.id)
                                            repo.toggleWishlist(spec.id)
                                            if (wasInWishlist) {
                                                AchievementsRepository.award(XpSource.WISHLIST_HIT, familyTag = spec.id)
                                            }
                                        },
                                        accent = if (isInCollection) Success else Aqua,
                                        textColor = if (isInCollection) Success else Aqua,
                                        modifier = Modifier.padding(vertical = 2.dp),
                                    )"""),
    # IconButton → SculptedIconButton
    ("""                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                val photo: android.graphics.Bitmap? =
                                                    SpecimenImages.urls[spec.id]?.firstOrNull()?.let { url ->
                                                        ShareCardImage.loadDownsampled(context, Uri.parse(url))
                                                    }
                                                ShareCardImage.share(
                                                    context = context,
                                                    title = spec.name,
                                                    subtitle = spec.category + "  •  " + spec.rarity + "  •  On my wishlist",
                                                    body = spec.tagline,
                                                    accentHex = spec.colorHex,
                                                    photoBitmap = photo,
                                                    fileName = "rockscout_wishlist_${spec.id}",
                                                )
                                            }
                                        },
                                        modifier = Modifier.size(36.dp),
                                    ) {
                                        Icon(
                                            Icons.Filled.Share,
                                            contentDescription = "Share wishlist specimen",
                                            tint = TextLow,
                                            modifier = Modifier.size(22.dp),
                                        )
                                    }""",
     """                                    SculptedIconButton(
                                        icon = Icons.Filled.Share,
                                        contentDescription = "Share wishlist specimen",
                                        onClick = {
                                            scope.launch {
                                                val photo: android.graphics.Bitmap? =
                                                    SpecimenImages.urls[spec.id]?.firstOrNull()?.let { url ->
                                                        ShareCardImage.loadDownsampled(context, Uri.parse(url))
                                                    }
                                                ShareCardImage.share(
                                                    context = context,
                                                    title = spec.name,
                                                    subtitle = spec.category + "  •  " + spec.rarity + "  •  On my wishlist",
                                                    body = spec.tagline,
                                                    accentHex = spec.colorHex,
                                                    photoBitmap = photo,
                                                    fileName = "rockscout_wishlist_${spec.id}",
                                                )
                                            }
                                        },
                                        accent = Citrine,
                                        iconTint = TextLow,
                                        size = 36.dp,
                                    )"""),
])

# ── File 4: CollectionScreen.kt ──
print("CollectionScreen.kt")
edit_file(BASE + "CollectionScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.rockClassColor",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("""                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            val photo: android.graphics.Bitmap? =
                                                (SpecimenImages.urls[spec.id] ?: spec.imageUrls).firstOrNull()?.let { url ->
                                                    ShareCardImage.loadDownsampled(context, Uri.parse(url))
                                                }
                                            ShareCardImage.share(
                                                context = context,
                                                title = spec.name,
                                                subtitle = spec.category + "  •  " + spec.rarity + "  •  In my collection",
                                                body = entry.note.ifBlank { spec.tagline },
                                                accentHex = spec.colorHex,
                                                photoBitmap = photo,
                                                fileName = "rockscout_collection_${spec.id}",
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.Share,
                                        contentDescription = "Share specimen",
                                        tint = TextLow,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }""",
     """                                SculptedIconButton(
                                    icon = Icons.Filled.Share,
                                    contentDescription = "Share specimen",
                                    onClick = {
                                        scope.launch {
                                            val photo: android.graphics.Bitmap? =
                                                (SpecimenImages.urls[spec.id] ?: spec.imageUrls).firstOrNull()?.let { url ->
                                                    ShareCardImage.loadDownsampled(context, Uri.parse(url))
                                                }
                                            ShareCardImage.share(
                                                context = context,
                                                title = spec.name,
                                                subtitle = spec.category + "  •  " + spec.rarity + "  •  In my collection",
                                                body = entry.note.ifBlank { spec.tagline },
                                                accentHex = spec.colorHex,
                                                photoBitmap = photo,
                                                fileName = "rockscout_collection_${spec.id}",
                                            )
                                        }
                                    },
                                    accent = Citrine,
                                    iconTint = TextLow,
                                    size = 36.dp,
                                )"""),
])

# ── File 5: FavoriteSpotsScreen.kt ──
print("FavoriteSpotsScreen.kt")
edit_file(BASE + "FavoriteSpotsScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.TagChip",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    # IconButton 1: Share
    ("""                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            ShareCardImage.share(
                                                context = context,
                                                title = loc.name,
                                                subtitle = loc.region + "  •  " + loc.type.label,
                                                body = loc.summary,
                                                accentHex = 0xFF2C6F9B,
                                                fileName = "rockscout_spot_${loc.id}",
                                            )
                                        }
                                    },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.Share,
                                        contentDescription = "Share spot",
                                        tint = TextLow,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }""",
     """                                SculptedIconButton(
                                    icon = Icons.Filled.Share,
                                    contentDescription = "Share spot",
                                    onClick = {
                                        scope.launch {
                                            ShareCardImage.share(
                                                context = context,
                                                title = loc.name,
                                                subtitle = loc.region + "  •  " + loc.type.label,
                                                body = loc.summary,
                                                accentHex = 0xFF2C6F9B,
                                                fileName = "rockscout_spot_${loc.id}",
                                            )
                                        }
                                    },
                                    accent = Citrine,
                                    iconTint = TextLow,
                                    size = 36.dp,
                                )"""),
    # IconButton 2: PersonAdd
    ("""                                IconButton(
                                    onClick = { shareToProfileLocId = loc.id },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.PersonAdd,
                                        contentDescription = "Share to Profile",
                                        tint = Citrine,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }""",
     """                                SculptedIconButton(
                                    icon = Icons.Filled.PersonAdd,
                                    contentDescription = "Share to Profile",
                                    onClick = { shareToProfileLocId = loc.id },
                                    accent = Citrine,
                                    iconTint = Citrine,
                                    size = 36.dp,
                                )"""),
])

# ── File 6: AchievementsScreen.kt ──
print("AchievementsScreen.kt")
edit_file(BASE + "AchievementsScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.badgePalette",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("""                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Color.White,
                    )"""),
])

# ── File 7: UserAchievementsScreen.kt ──
print("UserAchievementsScreen.kt")
edit_file(BASE + "UserAchievementsScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.GamerBadgeTile",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("""                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Color.White,
                    )"""),
])

# ── File 8: ElementDetailScreen.kt ──
print("ElementDetailScreen.kt")
edit_file(BASE + "ElementDetailScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.RockBackground",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    # IconButton 1: main screen back
    ("""            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextHigh,
                )
            }""",
     """            SculptedIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = { navController.popBackStack() },
                accent = Citrine,
                iconTint = TextHigh,
            )"""),
    # IconButton 2: not-found screen back
    ("""            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextHigh,
                )
            }""",
     """            SculptedIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = { navController.popBackStack() },
                accent = Citrine,
                iconTint = TextHigh,
            )"""),
])

# ── File 9: MeteoriteHuntingScreen.kt ──
print("MeteoriteHuntingScreen.kt")
edit_file(BASE + "MeteoriteHuntingScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.DarkCard",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("""                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextMid,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = TextMid,
                    )"""),
])

# ── File 10: RocksAreAmazingScreen.kt ──
print("RocksAreAmazingScreen.kt")
edit_file(BASE + "RocksAreAmazingScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.DarkCard",
        "import com.rork.rockscout.ui.components.SculptedIconButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("""                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Citrine,
                        )
                    }""",
     """                    SculptedIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        accent = Citrine,
                        iconTint = Citrine,
                    )"""),
])

print("\n=== Part 1 complete ===")
