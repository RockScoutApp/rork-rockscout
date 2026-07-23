package com.rork.rockscout.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink

/**
 * Full-screen blocking popup shown when the signed-in user's account has been
 * admin-deleted. The user cannot dismiss or get past this screen — there is
 * no back button and no close button. The only actionable element is an
 * "Appeal" button that opens the [AppealComposer].
 *
 * @param reason   The reason for the account deletion (shown to the user).
 * @param onAppeal Called when the user taps the Appeal button. The caller
 *                 should show the [AppealComposer] with type "account_deletion".
 */
@Composable
fun AccountDeletedPopup(
    reason: String,
    onAppeal: () -> Unit,
) {
    var showAppealComposer by remember { mutableStateOf(false) }

    if (showAppealComposer) {
        AppealComposer(
            appealType = "account_deletion",
            refId = null,
            title = "Appeal Account Deletion",
            onDismiss = { showAppealComposer = false },
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A0E0E), Color(0xFF0D0707), Color(0xFF050303)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Large warning icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Danger.copy(alpha = 0.18f))
                    .glowingBorder(3.dp, Danger.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.NoAccounts,
                    contentDescription = null,
                    tint = Danger,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Title
            Text(
                text = "Account Deleted",
                style = MaterialTheme.typography.headlineMedium,
                color = Danger,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            // Explanation
            Text(
                text = "Your RockScout account has been permanently deleted by an administrator for violating our community guidelines.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkTextMid,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            // Reason card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E1C16))
                    .glowingBorder(2.dp, Danger.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "Reason:",
                        style = MaterialTheme.typography.labelLarge,
                        color = Danger,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextHigh,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Appeal button — the only action the user can take
            SculptedButton(
                text = "Appeal This Decision",
                onClick = {
                    showAppealComposer = true
                    onAppeal()
                },
                accent = Citrine,
                containerColor = Citrine,
                textColor = Ink,
                icon = Icons.Filled.Gavel,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            )

            Spacer(Modifier.height(16.dp))

            // Helper text
            Text(
                text = "Appeals are reviewed personally. You will receive a response via email.",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
