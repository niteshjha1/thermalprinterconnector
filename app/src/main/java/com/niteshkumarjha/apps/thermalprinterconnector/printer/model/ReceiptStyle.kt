package com.niteshkumarjha.apps.thermalprinterconnector.printer.model


data class ReceiptStyle(
    val bold: Boolean = false,
    val underline: Boolean = false,
    val doubleSize: Boolean = false,
    val alignment: ReceiptAlignment = ReceiptAlignment.LEFT
)
