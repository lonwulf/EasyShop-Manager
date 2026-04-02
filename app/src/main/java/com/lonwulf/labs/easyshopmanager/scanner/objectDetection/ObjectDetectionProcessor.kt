package com.lonwulf.labs.easyshopmanager.scanner.objectDetection

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.lonwulf.labs.easyshopmanager.prefs.PreferenceUtils
import com.lonwulf.labs.easyshopmanager.scanner.InputInfo
import com.lonwulf.labs.easyshopmanager.scanner.camera.CameraReticleAnimator
import com.lonwulf.labs.easyshopmanager.scanner.camera.FrameProcessorBase
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay
import com.lonwulf.labs.easyshopmanager.scanner.dto.DetectedObjectUi
import com.lonwulf.labs.easyshopmanager.scanner.dto.LiveScanWorkflowState
import com.lonwulf.labs.easyshopmanager.scanner.dto.ObjectDetectionCallback

/**
 * Live processor that detects objects and drives a center-confirm interaction.
 *
 * - Picks the first detected object whose bounding box covers the overlay center.
 * - Computes "size/progress" relative to the center reticle box.
 * - Draws reticle/bounding box/progress into the provided [GraphicOverlay].
 * - Emits selected candidate UI state via [callback].
 */
class ObjectDetectionProcessor(
    private val graphicOverlay: GraphicOverlay,
    private val callback: ObjectDetectionCallback,
) : FrameProcessorBase<List<DetectedObject>>() {

    private val reticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)

    private val objectDetector: ObjectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .build()
    )

    override fun stop() {
        super.stop()
        try {
            objectDetector.close()
        } catch (_: Exception) {
            // No-op
        }
    }

    override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
        return objectDetector.process(image)
    }

    override fun onSuccess(
        inputInfo: InputInfo,
        results: List<DetectedObject>,
        graphicOverlay: GraphicOverlay
    ) {
        if (!callback.isCameraLive()) return

        val centerX = graphicOverlay.width / 2f
        val centerY = graphicOverlay.height / 2f

        val candidate = results.firstOrNull { detected ->
            val rect = graphicOverlay.translateRect(detected.boundingBox)
            rect.contains(centerX, centerY)
        }

        graphicOverlay.clear()

        if (candidate == null) {
            reticleAnimator.start()
            graphicOverlay.add(ObjectReticleGraphic(graphicOverlay, reticleAnimator))
            callback.onWorkflowStateChanged(LiveScanWorkflowState.DETECTING)
            graphicOverlay.invalidate()
            return
        }

        reticleAnimator.cancel()
        val candidateRect = graphicOverlay.translateRect(candidate.boundingBox)
        val candidateUi = candidate.toDetectedObjectUi(candidateRect)
        callback.onObjectCandidateChanged(candidateUi)

        // Draw center reticle (no ripple) + candidate visuals.
        graphicOverlay.add(ObjectReticleGraphic(graphicOverlay, reticleAnimator))

        val progress = PreferenceUtils.getProgressToMeetObjectSizeRequirement(
            overlay = graphicOverlay,
            objectBoundingBox = candidate.boundingBox
        )

        graphicOverlay.add(ObjectBoundingGraphic(graphicOverlay, candidateRect, progress))
        graphicOverlay.add(ObjectConfirmingGraphic(graphicOverlay, progress))

        callback.onObjectConfirmationProgress(candidateUi, progress)
        callback.onWorkflowStateChanged(
            if (progress >= 0.999f) LiveScanWorkflowState.CONFIRMED
            else LiveScanWorkflowState.CONFIRMING
        )

        graphicOverlay.invalidate()
    }

    private fun DetectedObject.toDetectedObjectUi(objectRect: android.graphics.RectF): DetectedObjectUi {
        val labelText = labels
            .firstOrNull { it.text != INVALID_LABEL }
            ?.text

        return DetectedObjectUi(
            objectId = trackingId,
            label = labelText,
            boundingBox = objectRect,
        )
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Object detection failed: ${e.message}", e)
    }

    companion object {
        private const val TAG = "ObjectDetectionProcessor"
        private const val INVALID_LABEL = "N/A"
    }
}

