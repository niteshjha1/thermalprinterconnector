# ThermalPrinterConnector

An Android library and sample application for seamless integration with ESC/POS thermal printers via USB and Network (TCP/IP) connections. It provides a structured "Receipt Builder" DSL, a real-time Compose-based previewer, and a robust command generator to handle text formatting, images, barcodes, and QR codes.

---

## Overall Objective
The goal of this project is to simplify the complex process of communicating with thermal receipt printers. Instead of manually handling byte arrays and ESC/POS hex codes, developers can define a receipt as a collection of high-level elements (Text, Images, Barcodes). The system then automatically translates these elements into the specific low-level commands required by the printer hardware and handles the underlying transport logic (finding USB devices or opening Network sockets).

---

## Detailed Class Breakdown

### 1. Core Logic & Management
*   **`PrinterManager`**: The "brain" of the operation. It orchestrates the entire printing flow. It validates the `ReceiptDocument`, uses the `EscPosCommandBuilder` to generate the raw bytes, and uses a `PrinterTransport` to send the data to the hardware.
*   **`ReceiptBuilder`**: A helper class used to construct a `ReceiptDocument`. It provides a structured way to add headers, logos, itemized lists, totals, and footers with specific styling.

### 2. Models (The "What" to Print)
*   **`ReceiptDocument`**: A data container holding a list of `ReceiptElement`s. It represents the complete structure of a single receipt.
*   **`ReceiptElement`**: A sealed class representing different types of content:
    *   `Text`: Includes string value and `ReceiptStyle`.
    *   `Image`: Holds a Bitmap to be converted to monochrome bit-image commands.
    *   `Barcode` / `QrCode`: Holds data to be rendered as scannable codes.
    *   `Feed`: Instructs the printer to move the paper forward.
*   **`ReceiptStyle` / `ReceiptAlignment`**: Define formatting options like **bold**, *underline*, double-height/width, and Left/Center/Right alignment.

### 3. ESC/POS Generation (The "Translator")
*   **`EscPosCommandBuilder`**: Iterates through a `ReceiptDocument` and converts each element into a stream of ESC/POS bytes.
*   **`EscPosImageHelper`**: Contains the logic to convert a standard Android `Bitmap` into the specific 1-bit monochrome format required by thermal printers.
*   **`EscPosBarcodeGenerator` / `EscPosQrGenerator`**: Specialized helpers for generating the specific command sequences for scannable codes.

### 4. Transports (The "How" to Send)
*   **`PrinterTransport` (Interface)**: Defines a common contract for connecting, writing data, and disconnecting.
*   **`UsbPrinterTransport`**: Handles Android USB Host API interactions. It discovers connected USB printers and performs data transfers to the printer's endpoint.
*   **`SocketPrinterTransport`**: Handles Network printing via TCP/IP. It opens a socket to the printer's IP (usually on port 9100) and streams the data.

### 5. UI Components
*   **`ReceiptPreviewScreen`**: A Jetpack Compose implementation that renders a visual "mock" of how the receipt will look on paper before it is actually printed.
*   **`PrinterControlScreen`**: The main interface for the user to select connection modes (USB vs. IP), enter IP addresses, and trigger the print action.
