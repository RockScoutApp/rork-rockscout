package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.Warning
import com.rork.rockscout.ui.components.glowingBorder

/**
 * Escalating warning pop-ups for the family-friendly moderation system.
 *
 * 1 report  → warning pop-up (must acknowledge to continue)
 * 2 reports → 2-week social feature block + warning pop-up
 * 3 reports → permanent social feature termination + warning pop-up
 *
 * All warnings make the escalation path and appeal process clear.
 * Appeals go through the Contact Us screen (email).
 */
@Composable
fun ReportWarningDialog(
    reportCount: Int,
    onAcknowledge: () -> Unit,
    onAppeal: () -> Unit,
) {
    if (reportCount <= 0) return
    val level = when {
        reportCount >= 3 -> WarningLevel.PERMANENT
        reportCount >= 2 -> WarningLevel.BLOCKED
        else -> WarningLevel.FIRST_WARNING
    }
    var showAppealComposer by remember { mutableStateOf(false) }

    if (showAppealComposer) {
        AppealComposer(
            appealType = "report_ban",
            refId = null,
            title = "Appeal a Report or Ban",
            onDismiss = { showAppealComposer = false },
        )
        return
    }

    AlertDialog(
        onDismissRequest = { /* must acknowledge — no dismiss on outside tap */ },
        title = null,
        text = { WarningContent(level) },
        confirmButton = {
            val confirmAccent = if (level == WarningLevel.PERMANENT) Danger else Warning
            SculptedButton(
                text = if (level == WarningLevel.PERMANENT) "I understand" else "Acknowledge",
                onClick = onAcknowledge,
                accent = confirmAccent,
                containerColor = confirmAccent,
                textColor = Ink,
            )
        },
        dismissButton = {
            SculptedOutlinedButton(
                text = "Appeal",
                onClick = {
                    onAppeal()
                    showAppealComposer = true
                },
                accent = Citrine,
                textColor = Citrine,
            )
        },
        containerColor = Color(0xFF1E1C16),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextHigh,
    )
}

private enum class WarningLevel { FIRST_WARNING, BLOCKED, PERMANENT }

@Composable
private fun WarningContent(level: WarningLevel) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (level == WarningLevel.PERMANENT) Danger.copy(alpha = 0.25f)
                        else Warning.copy(alpha = 0.25f)
                    )
                    .glowingBorder(1.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (level == WarningLevel.PERMANENT) "\u26D4" else "\u26A0\uFE0F",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                when (level) {
                    WarningLevel.FIRST_WARNING -> "Community Warning"
                    WarningLevel.BLOCKED -> "Social Features Blocked"
                    WarningLevel.PERMANENT -> "Social Privileges Terminated"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(16.dp))

        when (level) {
            WarningLevel.FIRST_WARNING -> FirstWarningText()
            WarningLevel.BLOCKED -> BlockedWarningText()
            WarningLevel.PERMANENT -> PermanentWarningText()
        }
    }
}

@Composable
private fun FirstWarningText() {
    Text(
        "A fellow RockScout reported your activity for violating our family-friendly community guidelines. " +
            "RockScout is a safe, family-friendly space for rockhounders of all ages — no profanity " +
            "(with the exception of \"hell\" and \"damn\"), no sexual content, and no harassment.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "This is your first warning. Please review the community guidelines and keep all " +
            "interactions respectful and appropriate.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
    )
    Spacer(Modifier.height(12.dp))
    EscalationNotice()
}

@Composable
private fun BlockedWarningText() {
    Text(
        "You have received a second community report. As a result, all of your in-app social " +
            "features — RockScout Friends, scanning, messaging, pings, and the swap & sell board — have " +
            "been blocked for 2 weeks.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "During this block you can still use the specimen database, identify rocks, browse dig " +
            "sites, and access all non-social features. Your social access will automatically " +
            "restore after the 2-week period if no further reports are filed.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
    )
    Spacer(Modifier.height(12.dp))
    EscalationNotice()
}

@Composable
private fun PermanentWarningText() {
    Text(
        "You have received a third community report. Per RockScout's family-friendly community " +
            "guidelines, your social privileges have been permanently terminated.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "This means you can no longer use RockScout Friends, scanning, messaging, pings, the swap & " +
            "sell board, or any other social feature — permanently. All non-social features (specimen " +
            "database, identify, dig sites, field captures, etc.) remain available to you.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "You can appeal this decision by emailing us through the Contact Us screen. If the appeal " +
            "is granted, your social privileges may be restored.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkTextMid,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun EscalationNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sculpted(shape = RoundedCornerShape(10.dp), accent = Warning, shadowElevation = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2A2820))
            .glowingBorder(2.dp, Warning.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(12.dp),
    ) {
        Column {
            Text(
                "How the warning system works:",
                style = MaterialTheme.typography.labelLarge,
                color = Warning,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "1st report → this warning (you are here)\n" +
                    "2nd report → 2-week social feature block\n" +
                    "3rd report → permanent social privileges termination",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMid,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "You can appeal any warning by emailing us through the Contact Us tab.",
                style = MaterialTheme.typography.bodySmall,
                color = Aqua,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/**
 * Report-confirmation dialog — shown to the reporter after they tap the report
 * button on a user's profile. Confirms the report was submitted and reminds
 * them of the family-friendly policy.
 */
@Composable
fun ReportSubmittedDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report submitted", color = DarkTextHigh, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Thank you for helping keep RockScout safe and family-friendly. " +
                    "Our moderation team will review the report. If the reported user " +
                    "violates our community guidelines, they will receive an escalating " +
                    "warning as outlined in our policy.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
            )
        },
        confirmButton = {
            SculptedTextButton(
                text = "OK",
                onClick = onDismiss,
                accent = Citrine,
                textColor = Citrine,
                fontWeight = FontWeight.Bold,
            )
        },
        containerColor = Color(0xFF1E1C16),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextMid,
    )
}
