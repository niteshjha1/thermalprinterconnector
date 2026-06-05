package com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos

class EscPosQrGenerator {

    private val GS = 0x1D.toByte()

    fun generate(
        data: String,
        size: Int = 6
    ): ByteArray {

        val bytes = mutableListOf<Byte>()

        bytes += GS
        bytes += 0x28
        bytes += 0x6B
        bytes += 0x04
        bytes += 0x00
        bytes += 0x31
        bytes += 0x41
        bytes += 0x32
        bytes += 0x00

        bytes += GS
        bytes += 0x28
        bytes += 0x6B
        bytes += 0x03
        bytes += 0x00
        bytes += 0x31
        bytes += 0x43
        bytes += size.toByte()

        val dataBytes = data.toByteArray(Charsets.UTF_8)

        val pL = (dataBytes.size + 3) % 256
        val pH = (dataBytes.size + 3) / 256

        bytes += GS
        bytes += 0x28
        bytes += 0x6B
        bytes += pL.toByte()
        bytes += pH.toByte()
        bytes += 0x31
        bytes += 0x50
        bytes += 0x30

        bytes.addAll(dataBytes.toList())

        bytes += GS
        bytes += 0x28
        bytes += 0x6B
        bytes += 0x03
        bytes += 0x00
        bytes += 0x31
        bytes += 0x51
        bytes += 0x30

        return bytes.toByteArray()
    }
}