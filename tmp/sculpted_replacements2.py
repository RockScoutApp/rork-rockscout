#!/usr/bin/env python3
"""Replace flat Material 3 buttons with sculpted equivalents - Part 2."""

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

# ── File 11: DeveloperConsoleScreen.kt ──
print("DeveloperConsoleScreen.kt")
edit_file(BASE + "DeveloperConsoleScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.sculpted",
        "import com.rork.rockscout.ui.components.SculptedButton\nimport com.rork.rockscout.ui.components.SculptedDialogButton\nimport com.rork.rockscout.ui.components.SculptedOutlinedButton\nimport com.rork.rockscout.ui.components.SculptedTextButton"),
    # Remove flat Button import
    ("import androidx.compose.material3.Button\n", ""),
    # Remove OutlinedButton import
    ("import androidx.compose.material3.OutlinedButton\n", ""),
    # Remove TextButton import
    ("import androidx.compose.material3.TextButton\n", ""),
    # Keep ButtonDefaults import (still used by OutlinedButton .sculpted pattern... actually check)
    # Actually ButtonDefaults is used by the .sculpted OutlinedButtons that already have .sculpted().
    # Those use ButtonDefaults.outlinedButtonBorder and ButtonDefaults.outlinedButtonColors.
    # But we're replacing those OutlinedButtons too. Let me check...
    # The OutlinedButtons at lines 1175 and 1350 already have .sculpted() on them.
    # Rule 8 says: Do NOT touch buttons that already use `.sculpted()`.
    # So those OutlinedButtons should be left alone. ButtonDefaults is still needed.
    
    # --- AnalyticsTab: Reset Analytics Button ---
    ("""            Button(
                onClick = { confirmReset = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Danger.copy(alpha = 0.15f),
                    contentColor = Danger,
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Reset Analytics Stats", fontWeight = FontWeight.Bold)
            }""",
     """            SculptedButton(
                text = "Reset Analytics Stats",
                onClick = { confirmReset = true },
                accent = Danger,
                containerColor = Danger.copy(alpha = 0.15f),
                textColor = Danger,
                icon = Icons.Filled.Delete,
                modifier = Modifier.fillMaxWidth(),
            )"""),
    
    # --- AnalyticsTab: AlertDialog confirm "Reset" ---
    ("""                        Button(
                            onClick = {
                                AdAnalyticsTracker.reset(context)
                                confirmReset = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Danger, contentColor = Ink),
                        ) { Text("Reset", fontWeight = FontWeight.Bold) }""",
     """                        SculptedDialogButton(
                            text = "Reset",
                            onClick = {
                                AdAnalyticsTracker.reset(context)
                                confirmReset = false
                            },
                            accent = Danger,
                        )"""),
    
    # --- AnalyticsTab: AlertDialog dismiss "Cancel" ---
    ("""                    dismissButton = {
                        TextButton(onClick = { confirmReset = false }) { Text("Cancel") }
                    },
                    containerColor = Color(0xFF1E1C16),
                )
            }
        }
    }
}""",
     """                    dismissButton = {
                        SculptedTextButton(text = "Cancel", onClick = { confirmReset = false }, accent = Danger, textColor = Danger)
                    },
                    containerColor = Color(0xFF1E1C16),
                )
            }
        }
    }
}"""),
    
    # --- SubscriptionsTab: "Clear" TextButton in search trailingIcon ---
    ("""                    if (searchQuery.isNotEmpty()) {
                        TextButton(onClick = {
                            searchQuery = ""
                            searched = false
                        }) { Text("Clear", color = DarkTextMid) }
                    }""",
     """                    if (searchQuery.isNotEmpty()) {
                        SculptedTextButton(
                            text = "Clear",
                            onClick = {
                                searchQuery = ""
                                searched = false
                            },
                            accent = Aqua,
                            textColor = DarkTextMid,
                        )
                    }"""),
    
    # --- SubscriptionsTab: "Search Users" Button ---
    ("""            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        results = SubscriptionAdminManager.searchUsers(searchQuery)
                        analytics = SubscriptionAdminManager.computeAnalytics()
                        loading = false
                        searched = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Citrine.copy(alpha = 0.18f),
                    contentColor = Citrine,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Filled.PersonSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Search Users", fontWeight = FontWeight.Bold)
            }""",
     """            SculptedButton(
                text = "Search Users",
                onClick = {
                    scope.launch {
                        loading = true
                        results = SubscriptionAdminManager.searchUsers(searchQuery)
                        analytics = SubscriptionAdminManager.computeAnalytics()
                        loading = false
                        searched = true
                    }
                },
                accent = Citrine,
                containerColor = Citrine.copy(alpha = 0.18f),
                textColor = Citrine,
                icon = Icons.Filled.PersonSearch,
                modifier = Modifier.fillMaxWidth(),
            )"""),
    
    # --- ModerationTab: AlertDialog confirm "Reinstate" ---
    ("""                Button(
                    onClick = {
                        scope.launch {
                            ReportRepository.instance.reinstateUser(group.reportedUserId)
                            groups = ReportRepository.instance.getAllModerationGroups()
                            reinstating = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Success, contentColor = Ink),
                ) { Text("Reinstate", fontWeight = FontWeight.Bold) }""",
     """                SculptedDialogButton(
                    text = "Reinstate",
                    onClick = {
                        scope.launch {
                            ReportRepository.instance.reinstateUser(group.reportedUserId)
                            groups = ReportRepository.instance.getAllModerationGroups()
                            reinstating = null
                        }
                    },
                    accent = Success,
                )"""),
    
    # --- ModerationTab: AlertDialog dismiss "Cancel" ---
    ("""            dismissButton = {
                TextButton(onClick = { reinstating = null }) { Text("Cancel") }
            },
            containerColor = Color(0xFF1E1C16),
        )
    }
}""",
     """            dismissButton = {
                SculptedTextButton(text = "Cancel", onClick = { reinstating = null }, accent = Citrine, textColor = DarkTextMid)
            },
            containerColor = Color(0xFF1E1C16),
        )
    }
}"""),
    
    # --- BugLogTab: AlertDialog confirm "Clear" (bug log) ---
    ("""                Button(
                    onClick = {
                        BugLogger.clear(context)
                        confirmClear = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger, contentColor = Ink),
                ) { Text("Clear", fontWeight = FontWeight.Bold) }""",
     """                SculptedDialogButton(
                    text = "Clear",
                    onClick = {
                        BugLogger.clear(context)
                        confirmClear = false
                    },
                    accent = Danger,
                )"""),
    
    # --- BugLogTab: AlertDialog dismiss "Cancel" (bug log) ---
    ("""            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } },
            containerColor = Color(0xFF1E1C16),
        )
    }

    if (confirmClearDiscovered) {""",
     """            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { confirmClear = false }, accent = Danger, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }

    if (confirmClearDiscovered) {"""),
    
    # --- BugLogTab: AlertDialog confirm "Clear" (discovered) ---
    ("""                Button(
                    onClick = {
                        DigSiteDiscoveryStore.clear()
                        selectedIds = emptySet()
                        confirmClearDiscovered = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Aqua, contentColor = Ink),
                ) { Text("Clear", fontWeight = FontWeight.Bold) }""",
     """                SculptedDialogButton(
                    text = "Clear",
                    onClick = {
                        DigSiteDiscoveryStore.clear()
                        selectedIds = emptySet()
                        confirmClearDiscovered = false
                    },
                    accent = Aqua,
                )"""),
    
    # --- BugLogTab: AlertDialog dismiss "Cancel" (discovered) ---
    ("""            dismissButton = { TextButton(onClick = { confirmClearDiscovered = false }) { Text("Cancel") } },
            containerColor = Color(0xFF1E1C16),
        )
    }
}

@Composable
private fun DevCard(""",
     """            dismissButton = { SculptedTextButton(text = "Cancel", onClick = { confirmClearDiscovered = false }, accent = Aqua, textColor = DarkTextMid) },
            containerColor = Color(0xFF1E1C16),
        )
    }
}

@Composable
private fun DevCard("""),
])

# Note: The two OutlinedButtons in DeveloperConsoleScreen already use .sculpted() — skip per rule 8.
# The IconButton (Box with .clickable for back arrow) at top — it's actually a Box, not an IconButton composable. Skip.

# ── File 12: CapturesScreen.kt ──
print("CapturesScreen.kt")
edit_file(BASE + "CapturesScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.rockClassColor",
        "import com.rork.rockscout.ui.components.SculptedIconButton\nimport com.rork.rockscout.ui.components.SculptedTextButton"),
    ("import androidx.compose.material3.IconButton\n", ""),
    ("import androidx.compose.material3.TextButton\n", ""),
    # AlertDialog confirm: Delete All
    ("""            confirmButton = {
                TextButton(onClick = {
                    repo.removeCaptures(selectedIds.toSet())
                    selectedIds.clear()
                    selectionMode = false
                    showBatchDeleteDialog = false
                }) {
                    Text("Delete All", color = Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDeleteDialog = false }) {
                    Text("Cancel", color = TextLow)
                }
            }""",
     """            confirmButton = {
                SculptedTextButton(
                    text = "Delete All",
                    onClick = {
                        repo.removeCaptures(selectedIds.toSet())
                        selectedIds.clear()
                        selectionMode = false
                        showBatchDeleteDialog = false
                    },
                    accent = Danger,
                    textColor = Danger,
                )
            },
            dismissButton = {
                SculptedTextButton(
                    text = "Cancel",
                    onClick = { showBatchDeleteDialog = false },
                    accent = Citrine,
                    textColor = TextLow,
                )
            }"""),
    # ScreenScaffold action: select toggle IconButton
    ("""                IconButton(onClick = {
                    if (selectionMode) {
                        selectedIds.clear()
                    }
                    selectionMode = !selectionMode
                }) {
                    Icon(
                        if (selectionMode) Icons.Filled.Close else Icons.Filled.CheckCircle,
                        contentDescription = if (selectionMode) "Exit selection" else "Select captures",
                        tint = if (selectionMode) Citrine else TextLow,
                        modifier = Modifier.size(22.dp),
                    )
                }""",
     """                SculptedIconButton(
                    icon = if (selectionMode) Icons.Filled.Close else Icons.Filled.CheckCircle,
                    contentDescription = if (selectionMode) "Exit selection" else "Select captures",
                    onClick = {
                        if (selectionMode) {
                            selectedIds.clear()
                        }
                        selectionMode = !selectionMode
                    },
                    accent = Citrine,
                    iconTint = if (selectionMode) Citrine else TextLow,
                )"""),
])

# ── File 13: HomeScreen.kt ──
print("HomeScreen.kt")
edit_file(BASE + "HomeScreen.kt", [
    add_import("import com.rork.rockscout.ui.components.sculpted",
        "import com.rork.rockscout.ui.components.SculptedTextButton"),
    ("import androidx.compose.material3.TextButton\n", ""),
    # FellowRockScoutsNoteDialog: TextButton → SculptedTextButton
    ("""        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Aqua)
            }
        }""",
     """        confirmButton = {
            SculptedTextButton(
                text = "Close",
                onClick = onDismiss,
                accent = Aqua,
                textColor = Aqua,
            )
        }"""),
])

print("\n=== Part 2 complete ===")
