package com.lonwulf.labs.easyshopmanager.scanner.dto

import android.graphics.RectF
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Compose-friendly, platform-agnostic models for live camera scanning.
 *
 * These DTOs intentionally avoid exposing ML Kit types to the UI layer.
 */

enum class LiveScanWorkflowState {
    NOT_STARTED,
    DETECTING,
    DETECTED,
    CONFIRMING,
    CONFIRMED,
    SEARCHING,
    SEARCHED,
}

data class ScannedBarcodeUi(
    val rawValue: String,
    val format: String,
    val valueType: String?,
    /**
     * Bounding box in preview coordinate space (scaled later by `GraphicOverlay`).
     * Null when ML Kit doesn't provide it.
     */
    val boundingBox: RectF? = null,
)

data class DetectedObjectUi(
    val objectId: Int?,
    val label: String?,
    val boundingBox: RectF? = null,
)

interface BarcodeScannerCallback {
    /** True when the camera is actively bound to the UI (screen is visible). */
    fun isCameraLive(): Boolean

    fun onWorkflowStateChanged(state: LiveScanWorkflowState)

    /** Called when a barcode is ready for the UI (i.e., SEARCHED or DETECTED). */
    fun onBarcodeDetected(barcode: ScannedBarcodeUi)
}

interface ObjectDetectionCallback {
    fun isCameraLive(): Boolean

    fun onWorkflowStateChanged(state: LiveScanWorkflowState)

    /**
     * Called when the current center candidate changes.
     * The UI can use this to populate the bottom sheet.
     */
    fun onObjectCandidateChanged(candidate: DetectedObjectUi)

    /** Called as long as the center candidate is being confirmed. */
    fun onObjectConfirmationProgress(candidate: DetectedObjectUi, progress: Float)
}

internal fun Barcode.toScannedBarcodeUi(): ScannedBarcodeUi {
    val raw = rawValue.orEmpty()
    return ScannedBarcodeUi(
        rawValue = raw,
        format = format.toString(),
        valueType = valueType?.toString(),
        // boundingBox translation depends on overlay scaling; UI doesn't need it yet.
        boundingBox = null,
    )
}

