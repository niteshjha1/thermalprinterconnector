package com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class EscPosImageHelper {

    // Convert Bitmap into ESC/POS GS v 0 raster format
    fun bitmapToEscPos(bitmap: Bitmap): ByteArray {

        val width = bitmap.width
        val height = bitmap.height

        val bytesPerRow = (width + 7) / 8

        val stream = ByteArrayOutputStream()

        val header = byteArrayOf(
            0x1D.toByte(), // GS
            0x76.toByte(), // v
            0x30.toByte(), // 0
            0x00.toByte(), // Normal mode
            (bytesPerRow and 0xFF).toByte(),
            ((bytesPerRow shr 8) and 0xFF).toByte(),
            (height and 0xFF).toByte(),
            ((height shr 8) and 0xFF).toByte()
        )

        stream.write(header)

        for (y in 0 until height) {

            for (xByte in 0 until bytesPerRow) {

                var currentByte = 0

                for (bit in 0 until 8) {

                    val x = (xByte * 8) + bit

                    currentByte = currentByte shl 1

                    if (x >= width) {
                        continue
                    }

                    val pixel = bitmap.getPixel(x, y)

                    val red = (pixel shr 16) and 0xFF
                    val green = (pixel shr 8) and 0xFF
                    val blue = pixel and 0xFF

                    val gray = (red + green + blue) / 3

                    if (gray < 128) {
                        currentByte = currentByte or 0x01
                    }
                }

                stream.write(currentByte)
            }
        }

        return stream.toByteArray()
    }
}