package com.lonwulf.labs.easyshopmanager.scanner.camera

/** An interface to process the input camera frame and perform detection on it.  */
interface FrameProcessor {

    /** Processes the input frame with the underlying detector.  */
    fun process(data: ByteArray, metadata: FrameMetadata,  graphicOverlay: GraphicOverlay)

    /** Stops the underlying detector and release resources.  */
    fun stop()
}