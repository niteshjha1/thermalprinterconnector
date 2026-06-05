package com.niteshkumarjha.apps.thermalprinterconnector.printer

import android.graphics.Bitmap

data class ReceiptLinePreview(
    val text: String,
    val isBold: Boolean = false,
    val isUnderline: Boolean = false,
    val isDoubleSize: Boolean = false,
    val alignment: Int = 0, // 0 = Left, 1 = Center, 2 = Right
    val height: Int = 0,
    val width: Int = 0,
    val isBarcode: Boolean = false,
//    val isQrCode: Boolean = false,
    val bitmap: Bitmap? = null
)

