package com.rork.rockscout.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Generates printable specimen label images with a QR code linking back to the
 * specimen's app page. Labels include the specimen name, date found, location,
 * and a QR code encoding the rockscout:// deep link.
 */
object SpecimenLabelGenerator {

    /**
     * Generate a label bitmap for a single specimen.
     *
     * @param specimenId The specimen's unique ID (used for the QR deep link)
     * @param name Display name of the specimen
     * @param dateFound Date string (e.g. "Jul 22, 2026") or empty
     * @param location Location string (e.g. "Crater of Diamonds, AR") or empty
     * @param accentHex Accent color for the label border (e.g. 0xFFE8A33D)
     * @return A Bitmap suitable for sharing or printing
     */
    fun generateLabel(
        specimenId: String,
        name: String,
        dateFound: String,
        location: String,
        accentHex: Long = 0xFFE8A33D,
    ): Bitmap {
        val labelWidth = 600
        val labelHeight = 400
        val bitmap = Bitmap.createBitmap(labelWidth, labelHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val accentColor = Color.rgb(
            ((accentHex shr 16) and 0xFF).toInt(),
            ((accentHex shr 8) and 0xFF).toInt(),
            (accentHex and 0xFF).toInt(),
        )

        // Border
        val borderPaint = Paint().apply {
            color = accentColor
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }
        canvas.drawRect(4f, 4f, labelWidth - 4f, labelHeight - 4f, borderPaint)

        // Top accent bar
        val barPaint = Paint().apply { color = accentColor }
        canvas.drawRect(4f, 4f, labelWidth - 4f, 40f, barPaint)

        // Header text
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            isAntiAlias = true
            isFakeBoldText = true
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("ROCKSCOUT SPECIMEN LABEL", 20f, 28f, headerPaint)

        // QR code — encodes the deep link
        val qrContent = "rockscout://specimen/$specimenId"
        val qrSize = 180
        val qrBitmap = generateQrCode(qrContent, qrSize)
        val qrLeft = labelWidth - qrSize - 30
        val qrTop = 60
        if (qrBitmap != null) {
            canvas.drawBitmap(qrBitmap, qrLeft.toFloat(), qrTop.toFloat(), null)
        }

        // Specimen name
        val namePaint = Paint().apply {
            color = Color.rgb(0x1C, 0x1A, 0x14)
            textSize = 36f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val truncatedName = if (name.length > 28) name.take(25) + "…" else name
        canvas.drawText(truncatedName, 30f, 100f, namePaint)

        // Subtitle line
        val subPaint = Paint().apply {
            color = accentColor
            textSize = 20f
            isAntiAlias = true
            isFakeBoldText = true
        }
        canvas.drawText("ID: $specimenId", 30f, 135f, subPaint)

        // Info lines
        val infoPaint = Paint().apply {
            color = Color.rgb(0x55, 0x50, 0x45)
            textSize = 22f
            isAntiAlias = true
        }
        var y = 180f
        if (dateFound.isNotBlank()) {
            canvas.drawText("Date found: $dateFound", 30f, y, infoPaint)
            y += 36f
        }
        if (location.isNotBlank()) {
            val truncatedLoc = if (location.length > 40) location.take(37) + "…" else location
            canvas.drawText("Location: $truncatedLoc", 30f, y, infoPaint)
            y += 36f
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.rgb(0x99, 0x90, 0x80)
            textSize = 16f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Scan QR code to view specimen in RockScout", labelWidth / 2f, labelHeight - 25f, footerPaint)

        return bitmap
    }

    /**
     * Generate a QR code bitmap for the given content.
     * Returns null if encoding fails.
     */
    private fun generateQrCode(content: String, size: Int): Bitmap? {
        return runCatching {
            val hints = mapOf(
                EncodeHintType.MARGIN to 0,
                EncodeHintType.PDF417_COMPACT to false,
            )
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        }.getOrNull()
    }
}
