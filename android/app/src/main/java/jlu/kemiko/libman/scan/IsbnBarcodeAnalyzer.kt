package jlu.kemiko.libman.scan

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import jlu.kemiko.libman.common.validation.Validators

class IsbnBarcodeAnalyzer(
    private val shouldProcess: () -> Boolean,
    private val onIsbnDetected: (String) -> Unit,
    private val onError: (Throwable) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13)
            .build()
    )

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (!shouldProcess()) return@addOnSuccessListener
                val isbn = barcodes.firstNotNullOfOrNull { barcode ->
                    val rawValue = barcode.rawValue?.filter(Char::isDigit) ?: return@firstNotNullOfOrNull null
                    if (rawValue.length == 13 && Validators.isValidIsbn13(rawValue)) rawValue else null
                }
                if (isbn != null) {
                    onIsbnDetected(isbn)
                }
            }
            .addOnFailureListener { error ->
                if (!shouldProcess()) return@addOnFailureListener
                onError(error)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun close() {
        scanner.close()
    }
}
