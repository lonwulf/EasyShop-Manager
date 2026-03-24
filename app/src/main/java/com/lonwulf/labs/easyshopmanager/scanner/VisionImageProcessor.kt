package com.lonwulf.labs.easyshopmanager.scanner

import androidx.camera.core.ImageProxy
import com.google.mlkit.common.MlKitException
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay


interface VisionImageProcessor {

    /** Processes ImageProxy image data, e.g. used for CameraX live preview case.  */
    @Throws(MlKitException::class)
    fun processImageProxy(image: ImageProxy?, graphicOverlay: GraphicOverlay?)

    /** Stops the underlying machine learning model and release resources.  */
    fun stop()
}