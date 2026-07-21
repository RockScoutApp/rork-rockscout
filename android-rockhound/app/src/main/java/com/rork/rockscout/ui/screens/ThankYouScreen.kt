package com.rork.rockscout.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.ui.components.RockBackground
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Success

/**
 * Thank-you screen shown after a successful donation.
 * Displays tokens granted and full-feature unlock duration,
 * with a Continue button that returns home.
 */
@Composable
fun ThankYouScreen(
    navController: NavController,
    tokensGranted: Int = 0,
    unlockDays: Int = 0,
) {
    BackHandler {
        navController.popBackStack(Routes.HOME, inclusive = false)
    }
    RockBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Heart icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFE2574C).copy(alpha = 0.25f), Color(0xFFE2574C).copy(alpha = 0.08f))
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE2574C),
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Thank you for your support!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            if (tokensGranted > 0) {
                Text(
                    text = "$tokensGranted identification tokens granted",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Citrine,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(6.dp))

            if (unlockDays > 0) {
                val unlockLabel = when (unlockDays) {
                    2 -> "2 days"
                    7 -> "7 days"
                    14 -> "2 weeks"
                    21 -> "3 weeks"
                    30 -> "1 month"
                    else -> "$unlockDays days"
                }
                Text(
                    text = "$unlockLabel of full-feature access unlocked",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Aqua,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your support keeps RockScout growing. Made by a rockhounder, for rockhounders.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            SculptedButton(
                text = "Continue",
                onClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                accent = Success,
                containerColor = Success,
                textColor = Ink,
                icon = Icons.Filled.Check,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}
