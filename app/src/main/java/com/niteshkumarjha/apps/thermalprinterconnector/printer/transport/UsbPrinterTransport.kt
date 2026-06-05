package com.niteshkumarjha.apps.thermalprinterconnector.printer.transport

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log

class UsbPrinterTransport(
    context: Context
) : PrinterTransport {

    private val usbManager =
        context.getSystemService(Context.USB_SERVICE) as UsbManager

    private var connection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var endpointOut: UsbEndpoint? = null

    override fun connect(): Boolean {

        try {

            val deviceList = usbManager.deviceList

            if (deviceList.isEmpty()) {

                Log.e("USB_PRINT", "No USB device found")

                return false
            }

            val device = deviceList.values.first()

            if (!usbManager.hasPermission(device)) {

                Log.e("USB_PRINT", "USB permission missing")

                return false
            }

            return connectToDevice(device)

        } catch (e: Exception) {

            Log.e("USB_PRINT", "USB connect failed", e)

            return false
        }
    }

    private fun connectToDevice(
        device: UsbDevice
    ): Boolean {

        connection = usbManager.openDevice(device)

        if (connection == null) {

            Log.e("USB_PRINT", "openDevice returned null")

            return false
        }

        for (i in 0 until device.interfaceCount) {

            val currentInterface = device.getInterface(i)

            for (j in 0 until currentInterface.endpointCount) {

                val endpoint =
                    currentInterface.getEndpoint(j)

                if (
                    endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                    endpoint.direction == UsbConstants.USB_DIR_OUT
                ) {

                    usbInterface = currentInterface
                    endpointOut = endpoint

                    connection?.claimInterface(
                        usbInterface,
                        true
                    )

                    Log.d("USB_PRINT", "USB connected")

                    return true
                }
            }
        }

        Log.e("USB_PRINT", "No BULK OUT endpoint found")

        return false
    }

    override fun write(
        data: ByteArray
    ) {

        try {

            val result = connection?.bulkTransfer(
                endpointOut,
                data,
                data.size,
                5000
            ) ?: -1

            if (result < 0) {

                Log.e("USB_PRINT", "bulkTransfer failed")

            } else {

                Log.d("USB_PRINT", "Sent ${data.size} bytes")
            }

        } catch (e: Exception) {

            Log.e("USB_PRINT", "USB write failed", e)
        }
    }

    override fun disconnect() {

        try {

            usbInterface?.let {
                connection?.releaseInterface(it)
            }

            connection?.close()

            connection = null
            usbInterface = null
            endpointOut = null

            Log.d("USB_PRINT", "USB disconnected")

        } catch (e: Exception) {

            Log.e("USB_PRINT", "USB disconnect failed", e)
        }
    }
}