package com.lonwulf.labs.easyshopmanager.scanner

import android.graphics.Bitmap
import com.lonwulf.labs.easyshopmanager.scanner.camera.FrameMetadata
import com.lonwulf.labs.easyshopmanager.scanner.util.Utils
import java.nio.ByteBuffer

interface InputInfo {
    fun getBitmap(): Bitmap
}

class CameraInputInfo(
    private val frameByteBuffer: ByteBuffer,
    private val frameMetadata: FrameMetadata
) : InputInfo {

    private var bitmap: Bitmap? = null

    @Synchronized
    override fun getBitmap(): Bitmap {
        return bitmap ?: let {
            bitmap = Utils.convertToBitmap(
                frameByteBuffer, frameMetadata.width, frameMetadata.height, frameMetadata.rotation
            )
            bitmap!!
        }
    }
}

class BitmapInputInfo(private val bitmap: Bitmap) : InputInfo {
    override fun getBitmap(): Bitmap {
        return bitmap
    }
}