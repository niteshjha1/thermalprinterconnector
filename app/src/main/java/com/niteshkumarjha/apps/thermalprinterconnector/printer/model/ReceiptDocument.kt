package com.niteshkumarjha.apps.thermalprinterconnector.printer.model

import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptElement

data class ReceiptDocument(
    val elements: MutableList<ReceiptElement> = mutableListOf()
)