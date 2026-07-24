package com.rork.rockscout.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.GemShow
import com.rork.rockscout.data.GemShowData
import com.rork.rockscout.data.SafeLinkOpener
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.TextMid

/**
 * Full-screen detail page for a gem show. Shows the full description,
 * venue, entry fee, dates, website link, phone/email (when available),
 * and a Get Directions button that opens the map app.
 *
 * Layout follows the LocationDetailScreen pattern — show name and city
 * at the top, a row of action buttons (Directions + Website), then info
 * sections (About, Visitor Info, Contact).
 */
@Composable
fun GemShowDetailScreen(navController: NavController, showId: String) {
    val show = GemShowData.showById(showId)
    val context = LocalContext.current

    if (show == null) {
        RockBackground {
            Box(Modifier.fillMaxSize().padding(40.dp)) {
                Text("Show not found.", color = TextMid)
            }
        }
        return
    }

    RockBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Back button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .sculpted(
                                shape = CircleShape,
                                accent = Citrine,
                                shadowElevation = 5.dp,
                                circular = true,
                                onClick = { navController.popBackStack() },
                            )
                            .clip(CircleShape)
                            .background(Color(0xCC0C0F14))
                            .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            }

            // Title + city/state
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TagChip("GEM SHOW", color = Amethyst, filled = true)
                        Spacer(Modifier.width(8.dp))
                        TagChip(if (show.isAnnual) "ANNUAL" else "EVENT", color = Citrine)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(show.name, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Aqua, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${show.city}, ${show.state}", style = MaterialTheme.typography.bodyLarge, color = TextMid)
                    }
                }
            }

            // Action buttons: Directions + Website
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (show.latitude != 0.0 && show.longitude != 0.0) {
                        SculptedButton(
                            text = "Get Directions",
                            onClick = {
                                val googleMapsUri = "google.navigation:q=${show.latitude},${show.longitude}"
                                val fallbackGeoUri = "geo:${show.latitude},${show.longitude}?q=${show.latitude},${show.longitude}(${Uri.encode(show.name)})"
                                SafeLinkOpener.openMaps(context, googleMapsUri, fallbackGeoUri)
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.Directions,
                        )
                    } else {
                        SculptedButton(
                            text = "Get Directions",
                            onClick = {
                                val query = Uri.encode("${show.name} ${show.venue} ${show.city} ${show.state}")
                                val googleMapsUri = "google.navigation:q=$query"
                                val fallbackGeoUri = "geo:0,0?q=$query"
                                SafeLinkOpener.openMaps(context, googleMapsUri, fallbackGeoUri)
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            accent = Citrine,
                            containerColor = Citrine,
                            textColor = Ink,
                            icon = Icons.Filled.Directions,
                        )
                    }
                    SculptedButton(
                        text = "Website",
                        onClick = { SafeLinkOpener.openUrl(context, show.website) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        accent = Aqua,
                        containerColor = Aqua,
                        textColor = Ink,
                        icon = Icons.Filled.Language,
                    )
                }
            }

            // About this show
            item {
                GemShowSection("About this show") {
                    Text(show.description, style = MaterialTheme.typography.bodyLarge, color = DarkTextMid)
                }
            }

            // Visitor info
            item {
                GemShowSection("Visitor info") {
                    GemShowInfoLine(Icons.Filled.CalendarMonth, "Dates", "${show.monthLabel} — ${show.dateRange}")
                    Spacer(Modifier.height(12.dp))
                    GemShowInfoLine(Icons.Filled.Place, "Venue", show.venue)
                    Spacer(Modifier.height(12.dp))
                    GemShowInfoLine(Icons.Filled.LocalActivity, "Entry fee", show.entryFee)
                }
            }

            // Contact info (only when phone or email is available)
            if (show.phone != null || show.email != null) {
                item {
                    GemShowSection("Contact") {
                        show.phone?.let { phone ->
                            GemShowInfoLine(Icons.Filled.Phone, "Phone", phone)
                            if (show.email != null) Spacer(Modifier.height(12.dp))
                        }
                        show.email?.let { email ->
                            GemShowInfoLine(Icons.Filled.Email, "Email", email)
                        }
                    }
                }
            }

            // Website card
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    DarkCard(
                        accent = Aqua,
                        modifier = Modifier.fillMaxWidth().clickable {
                            SafeLinkOpener.openUrl(context, show.website)
                        },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(42.dp).clip(CircleShape).background(Aqua.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Filled.Language, contentDescription = null, tint = Aqua) }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Visit official website", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text(show.website.removePrefix("https://").removePrefix("http://"), style = MaterialTheme.typography.bodyMedium, color = Aqua, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun GemShowSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextMid,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        )
        DarkCard(modifier = Modifier.fillMaxWidth(), accent = Citrine) { content() }
    }
}

@Composable
private fun GemShowInfoLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Citrine, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = DarkTextLow, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = DarkTextHigh, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}
