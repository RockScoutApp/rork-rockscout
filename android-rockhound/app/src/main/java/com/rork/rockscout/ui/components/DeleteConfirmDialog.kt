package com.rork.rockscout.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid

/**
 * Reusable confirmation dialog for destructive delete actions.
 * Shows a dark-themed alert with red "Delete" and neutral "Cancel" buttons.
 */
@Composable
fun DeleteConfirmDialog(
    title: String = "Delete?",
    message: String = "Are you sure you want to delete this? This action cannot be undone.",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = DarkTextHigh, fontWeight = FontWeight.Bold) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid) },
        confirmButton = {
            SculptedButton(
                text = "Delete",
                onClick = onConfirm,
                accent = Danger,
                containerColor = Danger,
                textColor = Color.White,
            )
        },
        dismissButton = {
            SculptedTextButton(
                text = "Cancel",
                onClick = onDismiss,
                accent = DarkTextMid,
                textColor = DarkTextMid,
            )
        },
        containerColor = Color(0xFF1E1C16),
        titleContentColor = DarkTextHigh,
        textContentColor = DarkTextMid,
    )
}
