package com.lonwulf.labs.easyshopmanager.scanner

import android.graphics.Bitmap
import android.graphics.Canvas
import com.lonwulf.labs.easyshopmanager.scanner.GraphicOverlay.Graphic


/** Draw camera image to background.  */
class CameraImageGraphic(overlay: GraphicOverlay, private val bitmap: Bitmap?) : Graphic(overlay) {

    override fun draw(canvas: Canvas?) {
        bitmap?.let {
            canvas?.drawBitmap(it, getTransformationMatrix(), null)
        }
    }
}