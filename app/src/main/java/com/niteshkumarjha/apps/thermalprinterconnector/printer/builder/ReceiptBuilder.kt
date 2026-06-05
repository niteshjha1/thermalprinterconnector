package com.niteshkumarjha.apps.thermalprinterconnector.printer.builder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptDocument
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptElement
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptStyle
import com.niteshkumarjha.apps.thermalprinterconnector.printer.model.ReceiptAlignment

/**
 * Builder class to construct a structured [ReceiptDocument].
 * Provides a high-level API to define receipt layout without worrying about raw bytes.
 */
class ReceiptBuilder(
    private val context: Context
) {
    private val TAG = "ReceiptBuilder"

    /**
     * Constructs a sample receipt for testing purposes.
     * Demonstrates use of logos, headers, itemized tables, totals, barcodes, and QR codes.
     *
     * @return A [ReceiptDocument] ready for preview or printing.
     */
    fun buildTestReceipt(): ReceiptDocument {
        val receipt = ReceiptDocument()

        // Logo
        loadLogo()?.let {
            receipt.elements.add(
                ReceiptElement.Image(
                    bitmap = it
                )
            )
        }

        // Header
        receipt.elements.add(
            ReceiptElement.Text(
                value = "MITHILA RESTAURANT",
                style = ReceiptStyle(
                    bold = true,
                    doubleSize = true,
                    alignment = ReceiptAlignment.CENTER
                )
            )
        )

        receipt.elements.add(
            ReceiptElement.Text(
                value = "Plot 18, Commercial Belt, Alpha 1"
            )
        )

        receipt.elements.add(
            ReceiptElement.Text(
                value = "Greater Noida, UP"
            )
        )

        receipt.elements.add(
            ReceiptElement.Text(
                value = "Tel: 0120 456 7890"
            )
        )

        receipt.elements.add(ReceiptElement.Text(value = "--------------------------------"))

        // Ticket Info
        receipt.elements.add(
            ReceiptElement.Text(
                value = "Ticket: 100001"
            )
        )

        receipt.elements.add(
            ReceiptElement.Text(
                value = "Table: 12"
            )
        )

        receipt.elements.add(
            ReceiptElement.Text(
                value = "Server: Nitesh"
            )
        )

        receipt.elements.add(ReceiptElement.Text(value = "--------------------------------"))

        // Items
        // Swapped items for authentic Indian and Mithila dishes with updated pricing
        receipt.elements.add(ReceiptElement.Text("Chicken Biryani x2     Rs 650.00"))
        receipt.elements.add(ReceiptElement.Text("Paneer Tikka x1        Rs 320.00"))
        receipt.elements.add(ReceiptElement.Text("Daal Makhani x2        Rs 400.00"))
        receipt.elements.add(ReceiptElement.Text("Masala Naan x3         Rs 180.00"))
        receipt.elements.add(ReceiptElement.Text("Butter Chicken x1      Rs 380.00"))
        receipt.elements.add(ReceiptElement.Text("Gajar ka Halwa x2      Rs 250.00"))
        receipt.elements.add(ReceiptElement.Text("Indian Salad x1        Rs 150.00"))
        receipt.elements.add(ReceiptElement.Text("Mango Lassi x2         Rs 200.00"))
        receipt.elements.add(ReceiptElement.Text("Chai Tea x3            Rs 120.00"))
        receipt.elements.add(ReceiptElement.Text("Mineral Water x2       Rs  90.00"))

        receipt.elements.add(ReceiptElement.Text(value = "--------------------------------"))

        // Totals
        receipt.elements.add(
            ReceiptElement.Text(
                // Adjusted total to match item sum calculations
                value = "TOTAL : Rs 2740.00",
                style = ReceiptStyle(
                    bold = true,
                    doubleSize = true
                )
            )
        )

        receipt.elements.add(ReceiptElement.Text(value = "--------------------------------"))

        // Barcode
        receipt.elements.add(
            ReceiptElement.Barcode(
                value = "100001"
            )
        )

        // QR
        receipt.elements.add(
            ReceiptElement.QrCode(
                value = "https://mithilarestaurant.com"
            )
        )

        // Footer
        receipt.elements.add(
            ReceiptElement.Text(
                value = "THANK YOU",
                style = ReceiptStyle(
                    bold = true,
                    alignment = ReceiptAlignment.CENTER
                )
            )
        )

        receipt.elements.add(
            ReceiptElement.Text(
                value = "Visit Again",
                style = ReceiptStyle(
                    alignment = ReceiptAlignment.CENTER
                )
            )
        )

        receipt.elements.add(
            ReceiptElement.Feed(3)
        )

        return receipt
    }

    /**
     * Loads and scales the logo from resources.
     * Ensures the bitmap fits within the printer's maximum printable width.
     */
    private fun loadLogo(): Bitmap? {
        val resId = context.resources.getIdentifier("logo", "drawable", context.packageName)
        if (resId == 0) {
            return null
        }

        val originalBitmap = BitmapFactory.decodeResource(context.resources, resId) ?: return null
        Log.d(TAG, "Original logo size=${originalBitmap.width}x${originalBitmap.height}")
        val maxWidth = 576
        if (originalBitmap.width <= maxWidth) {
            return originalBitmap
        }
        val scaleFactor = maxWidth.toFloat() / originalBitmap.width
        val scaledWidth = maxWidth
        val scaledHeight = (originalBitmap.height * scaleFactor).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)

        Log.d(TAG, "Scaled logo size=${scaledBitmap.width}x${scaledBitmap.height}")

        return scaledBitmap
    }
}