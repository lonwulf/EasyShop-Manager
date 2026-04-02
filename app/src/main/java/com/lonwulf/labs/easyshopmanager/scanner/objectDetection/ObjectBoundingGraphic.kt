package com.lonwulf.labs.easyshopmanager.scanner.objectDetection

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.ContextCompat
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay

/**
 * Draws the current center object candidate bounding box with a subtle gradient.
 *
 * `progress` is expected in [0f, 1f] where 1 means "confirmed enough".
 */
internal class ObjectBoundingGraphic(
    overlay: GraphicOverlay,
    private val objectRect: RectF,
    private val progress: Float,
) : GraphicOverlay.Graphic(overlay) {

    private val cornerRadiusPx: Float =
        context.resources.getDimensionPixelOffset(R.dimen.bounding_box_corner_radius).toFloat()

    private val detectedBgPaint: Paint = Paint().apply { style = Paint.Style.FILL }
    private val confirmedBgPaint: Paint = Paint().apply { style = Paint.Style.FILL }

    private val borderGradientPaint: Paint = Paint().apply { style = Paint.Style.STROKE }

    private val boundingBoxConfirmedStrokeWidthPx: Float =
        context.resources.getDimensionPixelOffset(R.dimen.bounding_box_confirmed_stroke_width).toFloat()

    private val boundingBoxStrokeWidthPxForProgress: Float
        get() {
            val clamped = progress.coerceIn(0f, 1f)
            return if (clamped >= 0.999f) {
                boundingBoxConfirmedStrokeWidthPx
            } else {
                (boundingBoxConfirmedStrokeWidthPx / 2f).coerceAtLeast(1f)
            }
        }

    override fun draw(canvas: Canvas) {
        val (bgStartColor, bgEndColor) = if (progress.coerceIn(0f, 1f) >= 0.999f) {
            ContextCompat.getColor(context, R.color.object_confirmed_bg_gradient_start) to
                ContextCompat.getColor(context, R.color.object_confirmed_bg_gradient_end)
        } else {
            ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start) to
                ContextCompat.getColor(context, R.color.object_detected_bg_gradient_end)
        }

        val bgPaint = if (progress.coerceIn(0f, 1f) >= 0.999f) confirmedBgPaint else detectedBgPaint
        val bgAlphaScale = progress.coerceIn(0f, 1f).coerceAtLeast(0.2f)

        bgPaint.shader = LinearGradient(
            objectRect.left,
            objectRect.top,
            objectRect.right,
            objectRect.bottom,
            bgStartColor,
            bgEndColor,
            Shader.TileMode.CLAMP
        )
        bgPaint.alpha = (bgPaint.alpha * bgAlphaScale).toInt().coerceIn(0, 255)

        // Fill.
        canvas.drawRoundRect(objectRect, cornerRadiusPx, cornerRadiusPx, bgPaint)

        // Border.
        val borderStart = ContextCompat.getColor(context, R.color.bounding_box_gradient_start)
        val borderEnd = ContextCompat.getColor(context, R.color.bounding_box_gradient_end)
        borderGradientPaint.shader = LinearGradient(
            objectRect.left,
            objectRect.top,
            objectRect.right,
            objectRect.bottom,
            borderStart,
            borderEnd,
            Shader.TileMode.CLAMP
        )
        borderGradientPaint.strokeWidth = boundingBoxStrokeWidthPxForProgress
        canvas.drawRoundRect(objectRect, cornerRadiusPx, cornerRadiusPx, borderGradientPaint)
    }
}

