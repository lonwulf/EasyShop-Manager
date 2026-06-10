package com.lonwulf.labs.camera.util

import android.graphics.RectF
import com.google.mlkit.vision.barcode.common.Barcode
import com.lonwulf.labs.camera.domain.usecase.CameraPrefsUseCase
import com.lonwulf.labs.camera.ui.camera.GraphicOverlay
import com.lonwulf.labs.easyshopmanager.core.domain.model.CameraSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

object PreferenceUtils {
    private lateinit var cameraPrefsUseCase: CameraPrefsUseCase
    private lateinit var scope: CoroutineScope
    private lateinit var cameraSettings: StateFlow<CameraSettings?>

    fun init(cameraPrefsUseCase: CameraPrefsUseCase, scope: CoroutineScope) {
        this.cameraPrefsUseCase = cameraPrefsUseCase
        this.scope = scope
        cameraSettings = cameraPrefsUseCase()
            .map { it.cameraSettings }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )
    }

    private val settings get() = cameraSettings.value

    fun isAutoSearchEnabled(): Boolean = settings?.isAutoSearchEnabled == true

    fun isMultipleObjectsMode(): Boolean = settings?.isMultipleObjectsMode == true

    fun isClassificationEnabled(): Boolean = settings?.isClassificationEnabled == true

    fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
        val overlayWidth = overlay.width.toFloat()
        val overlayHeight = overlay.height.toFloat()
        val boxWidth = overlayWidth * ((settings?.barcodeReticleWidth ?: 80) / 100)
        val boxHeight = overlayHeight * ((settings?.barcodeReticleHeight ?: 35) / 100)
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2
        return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
    }

    fun getProgressToMeetBarcodeSizeRequirement(overlay: GraphicOverlay, barcode: Barcode): Float {
        return if (settings?.isEnabledBarcodeSizeCheck == true) {
            val reticleBoxWidth = getBarcodeReticleBox(overlay).width()
            val barcodeBoundingBox = barcode.boundingBox ?: return 0f
            val barcodeWidth = overlay.translateX(barcodeBoundingBox.width().toFloat())
            val requiredWidth = reticleBoxWidth * ((settings?.minimumBarcodeWidth ?: 50) / 100)
            (barcodeWidth / requiredWidth).coerceAtMost(1f)
        } else {
            1f
        }
    }

    fun shouldDelayLoadingBarcodeResult(): Boolean = settings?.shouldDelayLoadingBarcodeResult == true

    fun getConfirmationTimeMs(): Int =
        when {
            isMultipleObjectsMode() -> 300
            isAutoSearchEnabled() -> settings?.confirmationTimeInAutoSearch ?: 1500

            else -> settings?.confirmationTimeInManualSearch ?: 500
        }
}