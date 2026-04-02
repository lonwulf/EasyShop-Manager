package com.lonwulf.labs.easyshopmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.lonwulf.labs.easyshopmanager.scanner.dto.BarcodeScannerCallback
import com.lonwulf.labs.easyshopmanager.scanner.dto.DetectedObjectUi
import com.lonwulf.labs.easyshopmanager.scanner.dto.LiveScanWorkflowState
import com.lonwulf.labs.easyshopmanager.scanner.dto.ObjectDetectionCallback
import com.lonwulf.labs.easyshopmanager.scanner.dto.ScannedBarcodeUi
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Compose-friendly state holder for live barcode + object detection.
 *
 * Camera controllers push results through the callback interfaces; Compose reads the flows.
 */
class LiveBarcodeViewModel : ViewModel(), BarcodeScannerCallback, ObjectDetectionCallback {

    private val cameraLive = AtomicBoolean(false)

    private val _workflowState = MutableStateFlow(LiveScanWorkflowState.NOT_STARTED)
    val workflowState: StateFlow<LiveScanWorkflowState> = _workflowState.asStateFlow()

    private val _detectedBarcode = MutableStateFlow<ScannedBarcodeUi?>(null)
    val detectedBarcode: StateFlow<ScannedBarcodeUi?> = _detectedBarcode.asStateFlow()

    private val _objectCandidate = MutableStateFlow<DetectedObjectUi?>(null)
    val objectCandidate: StateFlow<DetectedObjectUi?> = _objectCandidate.asStateFlow()

    private val _objectConfirmationProgress = MutableStateFlow(0f)
    val objectConfirmationProgress: StateFlow<Float> = _objectConfirmationProgress.asStateFlow()

    fun setCameraLive(live: Boolean) {
        cameraLive.set(live)
        if (!live) {
            // Keeps stale state from showing when the screen returns.
            _detectedBarcode.value = null
            _objectCandidate.value = null
            _objectConfirmationProgress.value = 0f
            _workflowState.value = LiveScanWorkflowState.NOT_STARTED
        }
    }

    override fun isCameraLive(): Boolean = cameraLive.get()

    override fun onWorkflowStateChanged(state: LiveScanWorkflowState) {
        _workflowState.value = state
    }

    override fun onBarcodeDetected(barcode: ScannedBarcodeUi) {
        _detectedBarcode.value = barcode
    }

    override fun onObjectCandidateChanged(candidate: DetectedObjectUi) {
        _objectCandidate.value = candidate
    }

    override fun onObjectConfirmationProgress(candidate: DetectedObjectUi, progress: Float) {
        _objectConfirmationProgress.value = progress
        // Candidate should remain aligned with the progress update.
        _objectCandidate.value = candidate
    }
}

