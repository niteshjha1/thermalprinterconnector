package com.niteshkumarjha.apps.thermalprinterconnector.printer

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class EscPosImageHelper {

    // Converts an Android Bitmap into raw ESC/POS GS v 0 (raster) print bytes
    fun bitmapToEscPos(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height

        // Horizontal byte counts must be calculated as (width + 7) / 8
        val xBytes = (width + 7) / 8
        val totalImageBytes = xBytes * height

        val stream = ByteArrayOutputStream()

        // GS v 0 m xL xH yL yH Command Header definition
        val header = byteArrayOf(
            0x1D.toByte(), // GS
            0x76.toByte(), // v
            0x30.toByte(), // 0
            0x00.toByte(), // m = 0 (Normal mode sizing)
            (xBytes % 256).toByte(), // xL
            (xBytes / 256).toByte(), // xH
            (height % 256).toByte(), // yL
            (height / 256).toByte()  // yH
        )

        stream.write(header)

        var currentByte = 0
        var bytePixelCount = 0

        // Loop down rows then across column segments
        for (y in 0 until height) {
            for (x in 0 until xBytes * 8) {
                if (x < width) {
                    val pixel = bitmap.getPixel(x, y)
                    val alpha = (pixel shr 24) and 0xFF
                    val red = (pixel shr 16) and 0xFF
                    val green = (pixel shr 8) and 0xFF
                    val blue = pixel and 0xFF

                    // Handle transparency fallback to white, otherwise calculate luminance threshold
                    if (alpha < 128) {
                        currentByte = currentByte shl 1
                    } else {
                        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
                        if (luminance < 128) {
                            currentByte = (currentByte shl 1) or 1 // Black ink mark pixel
                        } else {
                            currentByte = currentByte shl 1 // White empty space pixel
                        }
                    }
                } else {
                    currentByte = currentByte shl 1
                }

                bytePixelCount++

                if (bytePixelCount == 8) {
                    stream.write(currentByte)
                    currentByte = 0
                    bytePixelCount = 0
                }
            }
        }

        return stream.toByteArray()
    }
}