package com.lonwulf.labs.easyshopmanager.scanner

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeScannerProcessor(context: Context, zoomCallback: ZoomSuggestionOptions.ZoomCallback?) :
    VisionProcessorBase<List<Barcode>>(context) {

    private var barcodeScanner: BarcodeScanner

    init {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        // BarcodeScannerOptions.Builder()
        //     .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        //     .build();
        barcodeScanner =
            if (zoomCallback != null) {
                val options =
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                            Barcode.FORMAT_EAN_8,
                            Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_UPC_E,
                            Barcode.FORMAT_UPC_A,
                        )
                        .setZoomSuggestionOptions(ZoomSuggestionOptions.Builder(zoomCallback).build())
                        .build()
                BarcodeScanning.getClient(options)
            } else {
                BarcodeScanning.getClient()
            }
    }

    override fun stop() {
        super.stop()
        barcodeScanner.close()
    }

    override fun detectInImage(image: InputImage): Task<List<Barcode>> =
        barcodeScanner.process(image)

    override fun onSuccess(
        results: List<Barcode>,
        graphicOverlay: GraphicOverlay
    ) {
        if (results.isEmpty()) {
            Log.v("MANUAL_TESTING_LOG", "No barcode has been detected")
        }
        for (i in results.indices) {
            val barcode = results[i]
            graphicOverlay.add(BarcodeGraphic(graphicOverlay, barcode))
//            logExtrasForTesting(barcode)
        }
    }

    override fun onFailure(e: Exception) {
        Log.e("Oeee", "Barcode detection failed $e")
    }

}