package com.lonwulf.labs.easyshopmanager.scanner.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay

/** Draw camera image to background.  */
class CameraImageGraphic(overlay: GraphicOverlay, private val bitmap: Bitmap?) : GraphicOverlay.Graphic(overlay) {

    override fun draw(canvas: Canvas) {
        bitmap?.let { bmp ->
            // This is a simple placeholder; the app currently doesn't rely on background frame drawing.
            canvas.drawBitmap(bmp, 0f, 0f, null)
        }
    }
}