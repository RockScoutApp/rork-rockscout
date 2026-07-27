package com.rork.rockscout.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rork.rockscout.data.Artifact
import com.rork.rockscout.ui.components.AutoSizeTaglineText
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Ink

/**
 * Artifact list card — visually matches [SpecimenListItem] by construction
 * (same layout, spacing, theming) but reads from the [Artifact] model.
 * [SpecimenListItem] itself is NOT modified.
 *
 * @param artifact the artifact to display
 * @param accent the accent color (from artifact.accentHex)
 * @param onClick called when the card is tapped
 * @param imageSize the image thumbnail size (matches SpecimenListItem default)
 */
@Composable
fun ArtifactListItem(
    artifact: Artifact,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    imageSize: androidx.compose.ui.unit.Dp = 113.dp,
) {
    val imageHeight = (imageSize.value * 1.31f).dp
    val shape = RoundedCornerShape(20.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF473320),
                            Color(0xFF2A1E10),
                            Color(0xFF1E1408),
                        )
                    )
                )
                .glowingBorder(3.dp, accent.copy(alpha = 0.50f), shape)
                .clickable { onClick() },
        ) {
            // Accent glow overlay at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(accent.copy(alpha = 0.15f), Color.Transparent)
                        )
                    ),
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                // Image (left) + text rows (right) — matches SpecimenListItem layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight + 12.dp)
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Tall image
                    Box(
                        modifier = Modifier
                            .width(imageSize)
                            .height(imageHeight)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(accent.copy(alpha = 0.28f), Color(0xFF1A1812))
                                )
                            )
                            .glowingBorder(2.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = artifact.imageUrl,
                            contentDescription = artifact.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    // Right column — name + family + locations
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(imageHeight),
                    ) {
                        // Row 1 — name
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = artifact.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                lineHeight = 22.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        // Row 2 — blank (keeps spacing consistent with SpecimenListItem)
                        Box(modifier = Modifier.weight(1f).fillMaxWidth())

                        // Row 3 — family pill
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accent.copy(alpha = 0.16f))
                                    .glowingBorder(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = artifact.emoji,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 12.sp,
                                )
                                Text(
                                    text = artifact.family,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = brightenForText(accent, amount = 0.30f),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        // Row 4 — two location pills
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            val locations = artifact.whereFound.filter { it.isNotBlank() }.take(2)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                locations.forEach { loc ->
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Aqua.copy(alpha = 0.16f))
                                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 9.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = Aqua,
                                            modifier = Modifier.size(13.dp),
                                        )
                                        Text(
                                            text = loc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = brightenForText(Aqua),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }

                        // Row 5 — time period pill
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF5CC98C).copy(alpha = 0.18f))
                                    .glowingBorder(1.dp, Color(0xFF5CC98C).copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = artifact.timePeriod,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Ink,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

                // Tagline
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 16.dp, top = 10.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1f).height(64.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        com.rork.rockscout.ui.components.AutoSizeTaglineText(
                            text = artifact.tagline,
                            accent = accent,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
