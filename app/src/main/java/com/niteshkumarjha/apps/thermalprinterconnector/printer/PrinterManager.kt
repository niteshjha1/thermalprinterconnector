package com.niteshkumarjha.apps.thermalprinterconnector.printer

import android.util.Log
import com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos.EscPosCommandBuilder
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptDocument
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptElement
import com.niteshkumarjha.apps.thermalprinterconnector.printer.transport.PrinterTransport

private const val TAG = "PrinterManager"
private const val MAX_RECEIPT_PAYLOAD_SIZE = 64 * 1024
private const val MAX_LOGO_WIDTH = 576
private const val MAX_FEED_LINES = 5

class PrinterManager(
    private val transport: PrinterTransport,
    private val escPosCommandBuilder: EscPosCommandBuilder
) {

    fun print(receiptDocument: ReceiptDocument) {

        validateDocument(receiptDocument)

        val payload = escPosCommandBuilder.build(receiptDocument)

        Log.d(TAG, "Payload size=${payload.size} bytes")

        if (payload.size > MAX_RECEIPT_PAYLOAD_SIZE) {
            val errorMsg = "Receipt too large. size=${payload.size} limit=$MAX_RECEIPT_PAYLOAD_SIZE"
            Log.e(TAG, errorMsg)
            throw IllegalArgumentException(errorMsg)
        }

        if (!transport.connect()) {
            Log.e(TAG, "Transport connection failed")
            return
        }

        try {
            transport.write(payload)
            Log.d(TAG, "Print completed size=${payload.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Write operation failed", e)
        } finally {
            transport.disconnect()
        }
    }

    // Validate receipt before building ESC/POS payload
    private fun validateDocument(document: ReceiptDocument) {

        document.elements.forEachIndexed { index, element ->

            when (element) {

                is ReceiptElement.Image -> {

                    Log.d(
                        TAG,
                        "Logo size=${element.bitmap.width}x${element.bitmap.height}"
                    )

                    if (element.bitmap.width > MAX_LOGO_WIDTH) {
                        throw IllegalArgumentException(
                            "Logo too wide at index=$index width=${element.bitmap.width}"
                        )
                    }
                }

                is ReceiptElement.Barcode -> {

                    if (element.value.length > 50) {
                        throw IllegalArgumentException(
                            "Barcode content too large at index=$index"
                        )
                    }
                }

                is ReceiptElement.QrCode -> {

                    if (element.value.length > 300) {
                        throw IllegalArgumentException(
                            "QR content too large at index=$index"
                        )
                    }
                }

                is ReceiptElement.Feed -> {

                    if (element.lines > MAX_FEED_LINES) {
                        throw IllegalArgumentException(
                            "Feed too large at index=$index lines=${element.lines}"
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}