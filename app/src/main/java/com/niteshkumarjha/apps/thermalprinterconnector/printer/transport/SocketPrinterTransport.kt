package com.niteshkumarjha.apps.thermalprinterconnector.printer.transport

import android.util.Log
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class SocketPrinterTransport(
    private val ipAddress: String,
    private val port: Int = 9100
) : PrinterTransport {

    private var socket: Socket? = null
    private var outputStream: OutputStream? = null

    override fun connect(): Boolean {
        return try {
            Log.d("IP_PRINT", "ip=$ipAddress port=$port")
            socket = Socket()
            socket?.connect(
                InetSocketAddress(ipAddress, port),
                4000
            )
            outputStream = socket?.getOutputStream()
            Log.d("IP_PRINT", "Socket connected")
            true
        } catch (e: Exception) {
            Log.e("IP_PRINT", "Connection failed", e)
            false
        }
    }

    override fun write(data: ByteArray) {
        try {
            outputStream?.write(data)
            outputStream?.flush()
            Log.d("IP_PRINT", "Sent ${data.size} bytes")
        } catch (e: Exception) {
            Log.e("IP_PRINT", "Write failed", e)
        }
    }

    override fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()

            outputStream = null
            socket = null

            Log.d("IP_PRINT", "Socket disconnected")
        } catch (e: Exception) {
            Log.e("IP_PRINT", "Disconnect failed", e)
        }
    }
}