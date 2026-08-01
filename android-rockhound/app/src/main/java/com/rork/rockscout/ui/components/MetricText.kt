package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt

/**
 * Converts an imperial measurement string to its metric equivalent.
 * Handles formats like:
 *   "40 ft", "10–20 ft", "23 ft wingspan", "3.3 ft", "6.5 ft"
 *   "880 lb", "2–5 tons", "300–770 lb", "8.4–14 tons", "12,800 psi"
 *   "15 inches", "3 ft wingspan", "50 miles", "10–20 mi"
 *
 * @return The metric string (e.g. "12.2 m length", "2–4.5 tonnes"), or null if no conversion is possible.
 */
fun convertToMetric(imperial: String): String? {
    // Length in feet — handle ranges and single values
    val feetRangeRegex = Regex("""([\d.]+)\s*[–-]\s*([\d.]+)\s*ft""")
    val feetSingleRegex = Regex("""([\d.]+)\s*ft""")

    // Check for range first
    feetRangeRegex.find(imperial)?.let { match ->
        val low = match.groupValues[1].toFloatOrNull() ?: return null
        val high = match.groupValues[2].toFloatOrNull() ?: return null
        val suffix = if (imperial.contains("wingspan", ignoreCase = true)) " wingspan" else " length"
        return "${feetToMeters(low)}–${feetToMeters(high)} m$suffix"
    }

    // Single feet value
    feetSingleRegex.find(imperial)?.let { match ->
        val feet = match.groupValues[1].toFloatOrNull() ?: return null
        val suffix = if (imperial.contains("wingspan", ignoreCase = true)) " wingspan" else " length"
        return "${feetToMeters(feet)} m$suffix"
    }

    // Inches
    val inchRegex = Regex("""([\d.]+)\s*in(?:ch(?:es)?)?""")
    inchRegex.find(imperial)?.let { match ->
        val inches = match.groupValues[1].toFloatOrNull() ?: return null
        val cm = inches * 2.54f
        return "${formatMetric(cm)} cm"
    }

    // Weight — tons (imperial/US)
    val tonsRangeRegex = Regex("""([\d.]+)\s*[–-]\s*([\d.]+)\s*tons?""")
    tonsRangeRegex.find(imperial)?.let { match ->
        val low = match.groupValues[1].toFloatOrNull() ?: return null
        val high = match.groupValues[2].toFloatOrNull() ?: return null
        return "${tonsToTonnes(low)}–${tonsToTonnes(high)} tonnes"
    }

    val tonsSingleRegex = Regex("""([\d.]+)\s*tons?""")
    tonsSingleRegex.find(imperial)?.let { match ->
        val tons = match.groupValues[1].toFloatOrNull() ?: return null
        return "${tonsToTonnes(tons)} tonnes"
    }

    // Weight — pounds (handle commas)
    val lbRangeRegex = Regex("""([\d,]+)\s*[–-]\s*([\d,]+)\s*lb""")
    lbRangeRegex.find(imperial)?.let { match ->
        val low = match.groupValues[1].replace(",", "").toFloatOrNull() ?: return null
        val high = match.groupValues[2].replace(",", "").toFloatOrNull() ?: return null
        return "${lbToKg(low)}–${lbToKg(high)} kg"
    }

    val lbSingleRegex = Regex("""([\d,]+)\s*lb""")
    lbSingleRegex.find(imperial)?.let { match ->
        val lb = match.groupValues[1].replace(",", "").toFloatOrNull() ?: return null
        return "${lbToKg(lb)} kg"
    }

    // PSI
    val psiRegex = Regex("""([\d,]+)\s*psi""")
    psiRegex.find(imperial)?.let { match ->
        val psi = match.groupValues[1].replace(",", "").toFloatOrNull() ?: return null
        val pascals = psi * 6894.76f
        if (pascals >= 1_000_000f) {
            return "${formatMetric(pascals / 1_000_000f)} MPa"
        }
        return "${formatMetric(pascals / 1000f)} kPa"
    }

    // Miles
    val mileRangeRegex = Regex("""([\d.]+)\s*[–-]\s*([\d.]+)\s*(?:miles|mi)\b""")
    mileRangeRegex.find(imperial)?.let { match ->
        val low = match.groupValues[1].toFloatOrNull() ?: return null
        val high = match.groupValues[2].toFloatOrNull() ?: return null
        return "${milesToKm(low)}–${milesToKm(high)} km"
    }

    val mileSingleRegex = Regex("""([\d.]+)\s*(?:miles|mi)\b""")
    mileSingleRegex.find(imperial)?.let { match ->
        val miles = match.groupValues[1].toFloatOrNull() ?: return null
        return "${milesToKm(miles)} km"
    }

    return null
}

private fun feetToMeters(feet: Float): String = formatMetric(feet * 0.3048f)

private fun tonsToTonnes(tons: Float): String = formatMetric(tons * 0.9072f)

private fun lbToKg(lb: Float): String = formatMetric(lb * 0.4536f)

private fun milesToKm(miles: Float): String = formatMetric(miles * 1.6093f)

private fun formatMetric(value: Float): String {
    return if (value >= 100) {
        (value.roundToInt()).toString()
    } else if (value >= 10) {
        String.format("%.1f", value).trimEnd('0').trimEnd('.')
    } else {
        String.format("%.2f", value).trimEnd('0').trimEnd('.')
    }
}

/**
 * A text label that shows an imperial measurement and reveals the metric equivalent
 * in a small popup when long-pressed. If no metric conversion is available, the
 * long-press does nothing.
 *
 * The popup appears as a small bubble below the text with a dark background,
 * showing the metric value prefixed with "≈". Tapping anywhere outside dismisses it.
 *
 * @param text The imperial measurement text to display
 * @param color Text color
 * @param fontSize Font size in sp
 * @param fontWeight Font weight
 * @param modifier Layout modifier
 * @param textAlign Optional text alignment
 * @param lineHeight Optional line height in sp
 */
@Composable
fun MetricText(
    text: String,
    color: Color,
    fontSize: Int = 14,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    lineHeight: Int? = null,
) {
    val metricConversion = remember(text) { convertToMetric(text) }
    var showPopup by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.Center)) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            textAlign = textAlign,
            lineHeight = if (lineHeight != null) lineHeight.sp else fontSize.sp,
            modifier = Modifier
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        if (metricConversion != null) {
                            showPopup = true
                        }
                    },
                ),
        )
    }

    if (showPopup && metricConversion != null) {
        Popup(
            alignment = Alignment.BottomCenter,
            offset = IntOffset(0, 40),
            onDismissRequest = { showPopup = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f),
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2A1E))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "≈",
                            color = Color(0xFF8BBF6A),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = metricConversion,
                            color = Color(0xFFE8F4D8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "metric",
                        color = Color(0xFF6A8A5A),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}
