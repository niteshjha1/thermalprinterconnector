package com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos

import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptDocument
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptElement
import java.io.ByteArrayOutputStream
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptAlignment

/**
 * Translates [ReceiptDocument] elements into raw ESC/POS byte commands.
 * This class implements the core protocol logic used by most thermal printers.
 */
class EscPosCommandBuilder(
    private val imageHelper: EscPosImageHelper,
    private val barcodeGenerator: EscPosBarcodeGenerator,
    private val qrGenerator: EscPosQrGenerator
) {

    private val ESC = 0x1B.toByte()
    private val GS = 0x1D.toByte()

    /** Initializes printer to default state. */
    private fun initPrinter() = byteArrayOf(ESC, 0x40)

    /** Sets text alignment to Left. */
    private fun alignLeft() = byteArrayOf(ESC, 0x61, 0x00)

    /** Sets text alignment to Center. */
    private fun alignCenter() = byteArrayOf(ESC, 0x61, 0x01)

    /** Sets text alignment to Right. */
    private fun alignRight() = byteArrayOf(ESC, 0x61, 0x02)

    /** Enables bold mode. */
    private fun boldOn() = byteArrayOf(ESC, 0x45, 0x01)

    /** Disables bold mode. */
    private fun boldOff() = byteArrayOf(ESC, 0x45, 0x00)

    /** Enables underline. */
    private fun underlineOn() = byteArrayOf(ESC, 0x2D, 0x01)

    /** Disables underline. */
    private fun underlineOff() = byteArrayOf(ESC, 0x2D, 0x00)

    /** Enables double-width and double-height text. */
    private fun doubleSizeOn() = byteArrayOf(GS, 0x21, 0x11)

    /** Disables double-size text. */
    private fun doubleSizeOff() = byteArrayOf(GS, 0x21, 0x00)

    /** Triggers the automatic paper cutter. */
    private fun cutPaper() = byteArrayOf(GS, 0x56, 0x00)

    /** Feeds paper by [lines] amount. */
    private fun feedLines(lines: Int) = byteArrayOf(ESC, 0x64, lines.toByte())

    /**
     * Converts a [ReceiptDocument] into a single [ByteArray] of ESC/POS commands.
     */
    fun build(document: ReceiptDocument): ByteArray {

        val stream = ByteArrayOutputStream()

        stream.write(initPrinter())

        document.elements.forEach { element ->

            when (element) {

                is ReceiptElement.Text -> {
                    appendText(stream, element)
                }

                is ReceiptElement.Image -> {
                    appendImage(stream, element)
                }

                is ReceiptElement.Barcode -> {
                    appendBarcode(stream, element)
                }

                is ReceiptElement.QrCode -> {
                    appendQrCode(stream, element)
                }

                is ReceiptElement.Feed -> {
                    stream.write(feedLines(element.lines))
                }
            }
        }

        stream.write(feedLines(3))
        stream.write(cutPaper())

        return stream.toByteArray()
    }

    private fun appendText(
        stream: ByteArrayOutputStream,
        element: ReceiptElement.Text
    ) {

        when (element.style.alignment) {

            ReceiptAlignment.LEFT -> {
                stream.write(alignLeft())
            }

            ReceiptAlignment.CENTER -> {
                stream.write(alignCenter())
            }

            ReceiptAlignment.RIGHT -> {
                stream.write(alignRight())
            }
        }

        if (element.style.bold) {
            stream.write(boldOn())
        }

        if (element.style.underline) {
            stream.write(underlineOn())
        }

        if (element.style.doubleSize) {
            stream.write(doubleSizeOn())
        }

        stream.write("${element.value}\n".toByteArray())

        if (element.style.bold) {
            stream.write(boldOff())
        }

        if (element.style.underline) {
            stream.write(underlineOff())
        }

        if (element.style.doubleSize) {
            stream.write(doubleSizeOff())
        }
    }

    private fun appendImage(
        stream: ByteArrayOutputStream,
        element: ReceiptElement.Image
    ) {

        stream.write(alignCenter())

        stream.write(
            imageHelper.bitmapToEscPos(
                element.bitmap
            )
        )

        stream.write("\n".toByteArray())
    }

    private fun appendBarcode(
        stream: ByteArrayOutputStream,
        element: ReceiptElement.Barcode
    ) {

        stream.write(alignCenter())

        stream.write(
            barcodeGenerator.setHeight(
                element.height
            )
        )

        stream.write(
            barcodeGenerator.setWidth(
                element.width
            )
        )

        stream.write(
            barcodeGenerator.code128(
                element.value
            )
        )

        stream.write("\n".toByteArray())
    }

    private fun appendQrCode(
        stream: ByteArrayOutputStream,
        element: ReceiptElement.QrCode
    ) {

        stream.write(alignCenter())

        stream.write(
            qrGenerator.generate(
                data = element.value,
                size = element.size
            )
        )

        stream.write("\n".toByteArray())
    }
}