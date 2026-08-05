package com.rork.rockscout.ui.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.DeviceManager
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.SculptedOutlinedButton
import com.rork.rockscout.ui.components.glowingBorder
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Danger
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Warning
import kotlinx.coroutines.launch

@Composable
fun ManageDevicesScreen(navController: NavController) {
    val auth = AuthRepository.instance
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val devices by DeviceManager.devices.collectAsState()
    val isLoading by DeviceManager.isLoading.collectAsState()
    val deviceOverLimit by DeviceManager.deviceOverLimit.collectAsState()
    val userId = auth.currentUserId
    val myFingerprint = remember { DeviceManager.getDeviceFingerprint(context) }
    var showRemoveConfirm by remember { mutableStateOf<DeviceManager.DeviceInfo?>(null) }
    var removing by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            DeviceManager.refreshDevices(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .navigationBarsPadding(),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1A14).copy(alpha = 0.92f))
                .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkTextHigh)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "Manage Devices",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Limit info banner
            val deviceCount = devices.size
            val limitColor = if (deviceOverLimit) Danger else if (deviceCount >= 3) Warning else Aqua
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(limitColor.copy(alpha = 0.10f))
                    .glowingBorder(2.dp, limitColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Devices, contentDescription = null, tint = limitColor, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "$deviceCount of 3 devices used",
                            style = MaterialTheme.typography.titleSmall,
                            color = limitColor,
                            fontWeight = FontWeight.Bold,
                        )
                        if (deviceOverLimit) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "This device is over the limit. Premium features are paused. Remove an old device below to restore access.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        } else if (deviceCount >= 3) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "You've reached the limit. Remove a device before signing in on a new one.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        } else {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Premium works on up to 3 devices. Remove old devices you no longer use.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMid,
                            )
                        }
                    }
                }
            }

            if (isLoading && devices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp,
                        color = Citrine,
                    )
                }
            }

            // Device list
            devices.forEach { device ->
                val isCurrentDevice = device.device_fingerprint == myFingerprint
                DeviceRow(
                    device = device,
                    isCurrentDevice = isCurrentDevice,
                    onRemove = { showRemoveConfirm = device },
                )
            }

            if (devices.isEmpty() && !isLoading) {
                Text(
                    "No devices registered yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                )
            }

            Spacer(Modifier.height(8.dp))

            SculptedOutlinedButton(
                text = "Done",
                onClick = { navController.popBackStack() },
                accent = Citrine,
                textColor = DarkTextHigh,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            )
        }
    }

    // Remove confirmation dialog
    if (showRemoveConfirm != null) {
        val device = showRemoveConfirm!!
        AlertDialog(
            onDismissRequest = { showRemoveConfirm = null },
            containerColor = Color(0xFF1C1A14),
            titleContentColor = DarkTextHigh,
            textContentColor = DarkTextHigh,
            title = { Text("Remove device?") },
            text = {
                Text(
                    "Remove ${device.device_label ?: "this device"}? If this is an active device, premium will be paused on it until you sign in again on a device within the 3-device limit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextMid,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = if (removing) "Removing..." else "Remove",
                    onClick = {
                        removing = true
                        scope.launch {
                            val success = DeviceManager.removeDevice(device.id)
                            removing = false
                            if (success) {
                                showRemoveConfirm = null
                                userId?.let {
                                    DeviceManager.refreshDevices(it)
                                    DeviceManager.checkDeviceAccess(it)
                                }
                            }
                        }
                    },
                    accent = Danger,
                    containerColor = Danger,
                    textColor = Color.White,
                    icon = Icons.Filled.Delete,
                )
            },
            dismissButton = {
                SculptedOutlinedButton(
                    text = "Cancel",
                    onClick = { showRemoveConfirm = null },
                    accent = Aqua,
                    textColor = DarkTextHigh,
                )
            },
        )
    }
}

@Composable
private fun DeviceRow(
    device: DeviceManager.DeviceInfo,
    isCurrentDevice: Boolean,
    onRemove: () -> Unit,
) {
    val platformIcon = when (device.device_platform?.lowercase()) {
        "android" -> Icons.Filled.PhoneAndroid
        "ios" -> Icons.Filled.PhoneAndroid
        "web" -> Icons.Filled.DesktopWindows
        else -> Icons.Filled.Devices
    }
    val platformLabel = when (device.device_platform?.lowercase()) {
        "android" -> "Android"
        "ios" -> "iOS"
        "web" -> "Web"
        else -> "Unknown"
    }
    val platformColor = when (device.device_platform?.lowercase()) {
        "android" -> Color(0xFF3DDC84)
        "ios" -> Color(0xFF007AFF)
        "web" -> Citrine
        else -> Aqua
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1A14))
            .glowingBorder(
                1.dp,
                if (isCurrentDevice) Citrine.copy(alpha = 0.4f) else Color(0xFF3A3830).copy(alpha = 0.3f),
                RoundedCornerShape(14.dp),
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Platform icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(platformColor.copy(alpha = 0.15f))
                    .glowingBorder(1.dp, platformColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(platformIcon, contentDescription = null, tint = platformColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            // Device info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        device.device_label ?: "Unknown device",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextHigh,
                        fontWeight = FontWeight.Bold,
                    )
                    if (isCurrentDevice) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Citrine.copy(alpha = 0.2f))
                                .glowingBorder(1.dp, Citrine.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                "This device",
                                style = MaterialTheme.typography.labelSmall,
                                color = Citrine,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Platform badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(platformColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(
                            platformLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = platformColor,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Added ${device.installed_at?.substringBefore("T") ?: "—"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMid,
                    )
                }
            }
            // Remove button (not shown for current device)
            if (!isCurrentDevice) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onRemove() }
                        .background(Danger.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove device", tint = Danger, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
