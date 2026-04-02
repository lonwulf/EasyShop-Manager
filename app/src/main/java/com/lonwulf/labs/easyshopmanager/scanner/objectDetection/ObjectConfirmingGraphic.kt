package com.lonwulf.labs.easyshopmanager.scanner.objectDetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.prefs.PreferenceUtils
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay

/**
 * Draws a "center confirm" progress path inside the center reticle box.
 *
 * This mirrors the barcode confirming graphic but uses a percentage driven by
 * object size/progress.
 */
internal class ObjectConfirmingGraphic(
    overlay: GraphicOverlay,
    private val progress: Float,
) : GraphicOverlay.Graphic(overlay) {

    private val reticleBoxRect: RectF = PreferenceUtils.getBarcodeReticleBox(overlay)
    private val cornerRadiusPx: Float =
        overlay.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius).toFloat()

    private val pathPaint: Paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth =
            overlay.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_stroke_width).toFloat()
        pathEffect = CornerPathEffect(cornerRadiusPx)
    }

    override fun draw(canvas: Canvas) {
        val sizeProgress = progress.coerceIn(0f, 1f)
        val path = Path()
        if (sizeProgress > 0.95f) {
            // Full path with rounded corners.
            path.moveTo(reticleBoxRect.left, reticleBoxRect.top)
            path.lineTo(reticleBoxRect.right, reticleBoxRect.top)
            path.lineTo(reticleBoxRect.right, reticleBoxRect.bottom)
            path.lineTo(reticleBoxRect.left, reticleBoxRect.bottom)
            path.close()
        } else {
            path.moveTo(reticleBoxRect.left, reticleBoxRect.top + reticleBoxRect.height() * sizeProgress)
            path.lineTo(reticleBoxRect.left, reticleBoxRect.top)
            path.lineTo(reticleBoxRect.left + reticleBoxRect.width() * sizeProgress, reticleBoxRect.top)

            path.moveTo(reticleBoxRect.right, reticleBoxRect.bottom - reticleBoxRect.height() * sizeProgress)
            path.lineTo(reticleBoxRect.right, reticleBoxRect.bottom)
            path.lineTo(reticleBoxRect.right - reticleBoxRect.width() * sizeProgress, reticleBoxRect.bottom)
        }
        canvas.drawPath(path, pathPaint)
    }
}

