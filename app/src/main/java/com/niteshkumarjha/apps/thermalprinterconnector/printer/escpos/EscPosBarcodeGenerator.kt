package com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos

class EscPosBarcodeGenerator {

    private val GS = 0x1D.toByte()

    fun setHeight(height: Int): ByteArray {
        return byteArrayOf(GS, 0x68, height.toByte())
    }

    fun setWidth(width: Int): ByteArray {
        return byteArrayOf(GS, 0x77, width.toByte())
    }

    fun code128(data: String): ByteArray {
        val header = byteArrayOf(GS, 0x6B, 0x49, data.length.toByte())
        return header + data.toByteArray()
    }
}