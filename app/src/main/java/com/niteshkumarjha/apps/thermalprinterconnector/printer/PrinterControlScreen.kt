package com.niteshkumarjha.apps.thermalprinterconnector.printer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptDocument
import com.niteshkumarjha.apps.thermalprinterconnector.printer.preview.ReceiptPreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterControlScreen(
    receiptDocument: ReceiptDocument?,
    onGeneratePreviewClick: () -> Unit,
    onConnectAndPrintClick: () -> Unit,
    connectionMode: String,
    onConnectionModeChange: (String) -> Unit,
    ipAddress: String,
    onIpAddressChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Thermal Printer Connector")
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onConnectionModeChange("USB")
                    }
                ) {
                    Text("USB Mode")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onConnectionModeChange("IP")
                    }
                ) {
                    Text("IP Mode")
                }
            }

            if (connectionMode == "IP") {

                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = onIpAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Printer IP")
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onGeneratePreviewClick
            ) {
                Text("Generate Receipt Preview")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (receiptDocument != null) {

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConnectAndPrintClick
                ) {
                    Text(
                        if (connectionMode == "USB") {
                            "Print via USB"
                        } else {
                            "Print via IP"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Receipt Preview"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ReceiptPreviewScreen(
                    receiptDocument = receiptDocument
                )
            }
        }
    }
}