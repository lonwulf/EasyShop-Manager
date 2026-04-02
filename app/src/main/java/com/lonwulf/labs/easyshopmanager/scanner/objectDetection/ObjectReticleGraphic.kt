package com.lonwulf.labs.easyshopmanager.scanner.objectDetection

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Paint.Style
import androidx.core.content.ContextCompat
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.prefs.PreferenceUtils
import com.lonwulf.labs.easyshopmanager.scanner.camera.CameraReticleAnimator
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay

/**
 * Draws the center object reticle (outer/inner ring) plus ripple animation.
 *
 * Unlike barcode graphics, this doesn't draw a dark scrim; it is meant to be layered
 * on top of the barcode overlay without clearing its contents.
 */
internal class ObjectReticleGraphic(
    overlay: GraphicOverlay,
    private val animator: CameraReticleAnimator,
) : GraphicOverlay.Graphic(overlay) {

    private val ripplePaint: Paint = Paint().apply {
        style = Style.STROKE
        color = ContextCompat.getColor(context, R.color.reticle_ripple)
    }

    private val outerFillPaint: Paint = Paint().apply {
        style = Style.FILL
        color = ContextCompat.getColor(context, R.color.object_reticle_outer_ring_fill)
    }

    private val outerStrokePaint: Paint = Paint().apply {
        style = Style.STROKE
        color = ContextCompat.getColor(context, R.color.object_reticle_outer_ring_stroke)
        strokeWidth = context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_stroke_width).toFloat()
    }

    private val innerPaint: Paint = Paint().apply {
        style = Style.FILL
        color = ContextCompat.getColor(context, R.color.object_reticle_inner_ring)
    }

    private val rippleSizeOffsetPx: Float =
        context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset).toFloat()

    private val rippleStrokeWidthPx: Float =
        context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width).toFloat()

    private val boxCornerRadiusPx: Float =
        context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius).toFloat()

    private val innerInsetPx: Float =
        context.resources.getDimensionPixelOffset(R.dimen.bounding_box_corner_radius).toFloat() / 8f

    override fun draw(canvas: Canvas) {
        // Reuse the same "center reticle box" sizing as barcode for now.
        val box = PreferenceUtils.getBarcodeReticleBox(overlay)

        // Base rings.
        canvas.drawRoundRect(box, boxCornerRadiusPx, boxCornerRadiusPx, outerFillPaint)
        canvas.drawRoundRect(box, boxCornerRadiusPx, boxCornerRadiusPx, outerStrokePaint)

        val innerRect = RectF(
            box.left + innerInsetPx,
            box.top + innerInsetPx,
            box.right - innerInsetPx,
            box.bottom - innerInsetPx,
        )
        val innerCornerRadius = (boxCornerRadiusPx - innerInsetPx).coerceAtLeast(0f)
        canvas.drawRoundRect(innerRect, innerCornerRadius, innerCornerRadius, innerPaint)

        // Ripple ring (animated).
        val rippleRect = RectF(
            box.left - rippleSizeOffsetPx * animator.rippleSizeScale,
            box.top - rippleSizeOffsetPx * animator.rippleSizeScale,
            box.right + rippleSizeOffsetPx * animator.rippleSizeScale,
            box.bottom + rippleSizeOffsetPx * animator.rippleSizeScale,
        )
        ripplePaint.alpha = (ripplePaint.alpha * animator.rippleAlphaScale).toInt().coerceIn(0, 255)
        ripplePaint.strokeWidth = rippleStrokeWidthPx * animator.rippleStrokeWidthScale
        canvas.drawRoundRect(rippleRect, boxCornerRadiusPx, boxCornerRadiusPx, ripplePaint)
    }
}

