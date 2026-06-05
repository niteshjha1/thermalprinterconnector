package com.niteshkumarjha.apps.thermalprinterconnector.printer.transport

interface PrinterTransport {

    fun connect(): Boolean

    fun write(data: ByteArray)

    fun disconnect()
}