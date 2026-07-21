package com.rork.rockscout.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.TagChip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.glowingBorder

private const val CONTACT_EMAIL = "RockScoutApp2026@yahoo.com"

private val DISCLAIMER_TEXT = """
These images are AI generated, but I did my best to make them as realistic and accurate as possible. Although I would've preferred real images, it would've been near impossible to add an actual picture for everything in the app.

Please feel free to contact me with any edits that should be made. This was a huge app to create, and I did my best to make sure it was as complete as possible.

This app was developed by a rockhounder, made for rockhounders of all ages. I really do appreciate your support.
""".trimIndent()

@Composable
fun ContactUsScreen(navController: NavController) {
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    RockBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(800)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Contact Us",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Subtitle
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                                )
                            )
                            .padding(20.dp),
                    ) {
                        Text(
                            text = "Questions, corrections, or just want to say hello?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextMid,
                        )
                    }
                }

            // Email card
            item {
                DarkCard(
                    accent = Citrine,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$CONTACT_EMAIL")
                                putExtra(Intent.EXTRA_SUBJECT, "RockScout App - Contact")
                            }
                            SafeLinkOpener.openShareChooser(context, intent, "Send email")
                        },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Citrine.copy(alpha = 0.30f), Citrine.copy(alpha = 0.06f))
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = "Email",
                                tint = Citrine,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Email Us",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = CONTACT_EMAIL,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Citrine,
                                fontWeight = FontWeight.Medium,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Tap to send us a message",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }

            // Disclaimer card
            item {
                DarkCard(modifier = Modifier.fillMaxWidth(), accent = Aqua) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Aqua.copy(alpha = 0.18f))
                                    .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = Aqua,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "About the Images",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = DISCLAIMER_TEXT,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f,
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagChip("AI Generated", color = Aqua)
                            TagChip("Rockhounder Made", color = Citrine)
                        }
                    }
                }
            }

            // Appreciation footer
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Citrine.copy(alpha = 0.15f), Slate800.copy(alpha = 0.85f))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Thank you for your support!",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextHigh,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "RockScout — The most comprehensive rock app ever created. Probably.",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMid,
                        )
                    }
                }
            }
        }
        }
        }
    }
}
