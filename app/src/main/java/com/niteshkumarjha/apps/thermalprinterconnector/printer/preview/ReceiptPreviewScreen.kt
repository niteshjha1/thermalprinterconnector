package com.niteshkumarjha.apps.thermalprinterconnector.printer.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptAlignment
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptDocument
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptElement

@Composable
fun ReceiptPreviewScreen(
    receiptDocument: ReceiptDocument
) {

    Column(
        modifier = Modifier
            .width(300.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(4.dp)
            )
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 20.dp
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        receiptDocument.elements.forEach { element ->

            when (element) {

                is ReceiptElement.Text -> {

                    val textAlign = when (element.style.alignment) {
                        ReceiptAlignment.LEFT -> TextAlign.Left
                        ReceiptAlignment.CENTER -> TextAlign.Center
                        ReceiptAlignment.RIGHT -> TextAlign.Right
                    }

                    Text(
                        text = element.value,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = textAlign,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (element.style.bold) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        textDecoration = if (element.style.underline) {
                            TextDecoration.Underline
                        } else {
                            TextDecoration.None
                        },
                        fontSize = if (element.style.doubleSize) {
                            20.sp
                        } else {
                            13.sp
                        }
                    )
                }

                is ReceiptElement.Image -> {

                    Image(
                        bitmap = element.bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }

                is ReceiptElement.Barcode -> {

                    BarcodeVisualizer.generate(element.value)?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Barcode",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    Text(
                        text = element.value,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                is ReceiptElement.QrCode -> {

                    QrCodeVisualizer.generate(element.value)?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 4.dp)
                        )
                    }

                    Text(
                        text = element.value,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }

                is ReceiptElement.Feed -> {
                    repeat(element.lines) {
                        Text("")
                    }
                }
            }
        }
    }
}