package com.niteshkumarjha.apps.thermalprinterconnector.printer.transport

/**
 * Interface defining the contract for any hardware communication channel.
 */
interface PrinterTransport {

    /** Establishes a connection to the printer device. */
    fun connect(): Boolean

    /** Sends raw data bytes to the printer. */
    fun write(data: ByteArray)

    /** Closes the connection and releases resources. */
    fun disconnect()
}