package com.lonwulf.labs.easyshopmanager.scanner

import android.animation.ValueAnimator
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.lonwulf.labs.easyshopmanager.prefs.PreferenceUtils
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.WorkflowViewModel
import com.lonwulf.labs.easyshopmanager.scanner.barcode.BarcodeConfirmingGraphic
import com.lonwulf.labs.easyshopmanager.scanner.barcode.BarcodeLoadingGraphic
import com.lonwulf.labs.easyshopmanager.scanner.barcode.BarcodeReticleGraphic
import com.lonwulf.labs.easyshopmanager.scanner.camera.CameraReticleAnimator
import com.lonwulf.labs.easyshopmanager.scanner.camera.FrameMetadata
import com.lonwulf.labs.easyshopmanager.scanner.camera.FrameProcessorBase
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay
import java.io.IOException

class BarcodeScannerProcessor(
    graphicOverlay: GraphicOverlay,
    zoomCallback: ZoomSuggestionOptions.ZoomCallback?,
    private val workflowViewModel: WorkflowViewModel
) :
    FrameProcessorBase<List<Barcode>>() {
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)
//    private val barcodeScanner = BarcodeScanning.getClient()

    // Note that if you know which format of barcode your app is dealing with, detection will be
    // faster to specify the supported barcode formats one by one, e.g.
    // BarcodeScannerOptions.Builder()
    //     .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
    //     .build();
    private var barcodeScanner: BarcodeScanner = if (zoomCallback != null) {
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

    override fun stop() {
        super.stop()
        try {
            barcodeScanner.close()
        } catch (e: IOException) {
            Log.e("Barcode scanner: ", "Failed to close barcode detector!", e)
        }
    }

    override fun detectInImage(image: InputImage): Task<List<Barcode>> =
        barcodeScanner.process(image)

    override fun onSuccess(
        inputInfo: InputInfo,
        results: List<Barcode>,
        graphicOverlay: GraphicOverlay
    ) {

        if (!workflowViewModel.isCameraLive) return

        Log.d("Barcode scanner: ", "Barcode result size: ${results.size}")

        // Picks the barcode, if exists, that covers the center of graphic overlay.

        val barcodeInCenter = results.firstOrNull { barcode ->
            val boundingBox = barcode.boundingBox ?: return@firstOrNull false
            val box = graphicOverlay.translateRect(boundingBox)
            box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
        }

        graphicOverlay.clear()
        if (barcodeInCenter == null) {
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))
            workflowViewModel.setWorkflowState(WorkflowViewModel.WorkflowState.DETECTING)
        } else {
            cameraReticleAnimator.cancel()
            val sizeProgress = PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(graphicOverlay, barcodeInCenter)
            if (sizeProgress < 1) {
                // Barcode in the camera view is too small, so prompt user to move camera closer.
                graphicOverlay.add(BarcodeConfirmingGraphic(graphicOverlay, barcodeInCenter))
                workflowViewModel.setWorkflowState(WorkflowViewModel.WorkflowState.CONFIRMING)
            } else {
                // Barcode size in the camera view is sufficient.
                if (PreferenceUtils.shouldDelayLoadingBarcodeResult(graphicOverlay.context)) {
                    val loadingAnimator = createLoadingAnimator(graphicOverlay, barcodeInCenter)
                    loadingAnimator.start()
                    graphicOverlay.add(BarcodeLoadingGraphic(graphicOverlay, loadingAnimator))
                    workflowViewModel.setWorkflowState(WorkflowViewModel.WorkflowState.SEARCHING)
                } else {
                    workflowViewModel.setWorkflowState(WorkflowViewModel.WorkflowState.DETECTED)
                    workflowViewModel.detectedBarcode.setValue(barcodeInCenter)
                }
            }
        }
        graphicOverlay.invalidate()
    }

    private fun createLoadingAnimator(graphicOverlay: GraphicOverlay, barcode: Barcode): ValueAnimator {
        val endProgress = 1.1f
        return ValueAnimator.ofFloat(0f, endProgress).apply {
            duration = 2000
            addUpdateListener {
                if ((animatedValue as Float).compareTo(endProgress) >= 0) {
                    graphicOverlay.clear()
                    workflowViewModel.setWorkflowState(WorkflowViewModel.WorkflowState.SEARCHED)
                    workflowViewModel.detectedBarcode.setValue(barcode)
                } else {
                    graphicOverlay.invalidate()
                }
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e("Oeee", "Barcode detection failed $e")
    }

}