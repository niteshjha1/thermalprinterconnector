package com.niteshkumarjha.apps.thermalprinterconnector.printer.model

import android.graphics.Bitmap
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptStyle

sealed class ReceiptElement {

    data class Text(
        val value: String,
        val style: ReceiptStyle = ReceiptStyle()
    ) : ReceiptElement()

    data class Image(
        val bitmap: Bitmap
    ) : ReceiptElement()

    data class Barcode(
        val value: String,
        val height: Int = 80,
        val width: Int = 2
    ) : ReceiptElement()

    data class QrCode(
        val value: String,
        val size: Int = 6
    ) : ReceiptElement()

    data class Feed(
        val lines: Int = 1
    ) : ReceiptElement()
}