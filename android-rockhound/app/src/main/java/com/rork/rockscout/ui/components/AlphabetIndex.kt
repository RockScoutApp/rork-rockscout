package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.components.glowingBorder

/** Vertical A–Z scroller that highlights letters present in the supplied list and
 *  jumps to the first item matching the tapped letter. */
@Composable
fun AlphabetIndex(
    names: List<String>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    val letters = ('A'..'Z').toList()
    val available = remember(names) {
        names.mapNotNull { it.firstOrNull()?.uppercaseChar() }.filter { it in 'A'..'Z' }.toSet()
    }
    Column(
        modifier = modifier
            .padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
            .width(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        letters.forEach { letter ->
            val hasItem = available.contains(letter)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = Citrine,
                        shadowElevation = 4.dp,
                        circular = true,
                        enabled = hasItem,
                        onClick = if (hasItem) { { onLetterClick(letter) } } else null,
                    )
                    .clip(CircleShape)
                    .background(Citrine)
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    letter.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasItem) Color.Black else Color.Black.copy(alpha = 0.4f),
                    fontWeight = if (hasItem) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}
