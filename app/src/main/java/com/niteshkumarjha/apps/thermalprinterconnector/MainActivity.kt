package com.niteshkumarjha.apps.thermalprinterconnector

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.niteshkumarjha.apps.thermalprinterconnector.printer.PrinterControlScreen
import com.niteshkumarjha.apps.thermalprinterconnector.printer.PrinterManager
import com.niteshkumarjha.apps.thermalprinterconnector.printer.builder.ReceiptBuilder
import com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos.EscPosBarcodeGenerator
import com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos.EscPosCommandBuilder
import com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos.EscPosImageHelper
import com.niteshkumarjha.apps.thermalprinterconnector.printer.escpos.EscPosQrGenerator
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptDocument
import com.niteshkumarjha.apps.thermalprinterconnector.printer.transport.SocketPrinterTransport
import com.niteshkumarjha.apps.thermalprinterconnector.printer.transport.UsbPrinterTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var receiptBuilder: ReceiptBuilder

    private val escPosCommandBuilder by lazy {
        EscPosCommandBuilder(
            imageHelper = EscPosImageHelper(),
            barcodeGenerator = EscPosBarcodeGenerator(),
            qrGenerator = EscPosQrGenerator(),
        )
    }

    private var receiptDocument by mutableStateOf<ReceiptDocument?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("USB_PRINT", "MainActivity onCreate")
        receiptBuilder = ReceiptBuilder(this)

        setContent {
            var connectionMode by remember {
                mutableStateOf("USB")
            }

            var ipTargetAddress by remember {
                mutableStateOf("192.168.1.89")
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PrinterControlScreen(
                        receiptDocument = receiptDocument,
                        onGeneratePreviewClick = {
                            receiptDocument = receiptBuilder.buildTestReceipt()
                        },
                        onConnectAndPrintClick = {
                            val document = receiptDocument ?: return@PrinterControlScreen
                            lifecycleScope.launch(Dispatchers.IO) {
                                if (connectionMode == "USB") {
                                    printViaUsb(document)
                                } else {
                                    printViaIp(
                                        document = document,
                                        ipAddress = ipTargetAddress
                                    )
                                }
                            }
                        },
                        connectionMode = connectionMode,
                        onConnectionModeChange = {
                            connectionMode = it
                        },
                        ipAddress = ipTargetAddress,
                        onIpAddressChange = {
                            ipTargetAddress = it
                        }
                    )
                }
            }
        }
    }

    private fun printViaUsb(
        document: ReceiptDocument
    ) {
        try {
            val printerManager = PrinterManager(
                transport = UsbPrinterTransport(this),
                escPosCommandBuilder = escPosCommandBuilder
            )
            printerManager.print(document)
            Log.d("USB_PRINT", "USB print completed")
        } catch (e: Exception) {
            Log.e("USB_PRINT", "USB print failed", e)
        }
    }

    private fun printViaIp(
        document: ReceiptDocument,
        ipAddress: String
    ) {

        try {
            val printerManager = PrinterManager(
                transport = SocketPrinterTransport(
                    ipAddress = ipAddress,
                    port = 9100
                ),
                escPosCommandBuilder = escPosCommandBuilder
            )
            printerManager.print(document)
            Log.d("IP_PRINT", "IP print completed")
        } catch (e: Exception) {
            Log.e("IP_PRINT", "IP print failed", e)
        }
    }
}