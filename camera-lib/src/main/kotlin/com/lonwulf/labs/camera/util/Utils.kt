package com.lonwulf.labs.camera.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import java.io.ByteArrayOutputStream

object Utils {
    private const val TAG = "Utils"


    /** Convert NV21 or YUV_420_888 format Image to bitmap. */
    fun convertToBitmap(image: Image, rotationDegrees: Int): Bitmap? {
        val nv21Buffer = yuv420ThreePlanesToNV21(image.planes, image.width, image.height)
        try {
            val yuvImage = YuvImage(nv21Buffer, ImageFormat.NV21, image.width, image.height, null)
            val stream = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 80, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            stream.close()

            // Rotate the image back to straight.
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
        } catch (e: Exception) {
            Log.e(TAG, "Error: " + e.message)
        }
        return null
    }

    private fun yuv420ThreePlanesToNV21(
        planes: Array<Image.Plane>, width: Int, height: Int
    ): ByteArray {
        val imageSize = width * height
        val out = ByteArray(imageSize + 2 * (imageSize / 4))

        if (planes[1].buffer.remaining() == imageSize / 2 - 1) {
            // Both u and v planes are tightly packed and interleaved, we can copy them at once.
            planes[0].buffer.get(out, 0, imageSize)
            planes[2].buffer.get(out, imageSize, 1)
            planes[1].buffer.get(out, imageSize + 1, imageSize / 2 - 1)
        } else {
            // Standard case where planes are separate.
            planes[0].buffer.get(out, 0, imageSize)
            val uBuffer = planes[1].buffer
            val vBuffer = planes[2].buffer
            val uPixelStride = planes[1].pixelStride
            val vPixelStride = planes[2].pixelStride
            val uRowStride = planes[1].rowStride
            val vRowStride = planes[2].rowStride

            var pos = imageSize
            for (row in 0 until height / 2) {
                for (col in 0 until width / 2) {
                    out[pos++] = vBuffer.get(row * vRowStride + col * vPixelStride)
                    out[pos++] = uBuffer.get(row * uRowStride + col * uPixelStride)
                }
            }
        }
        return out
    }

    fun getCornerRoundedBitmap(srcBitmap: Bitmap, cornerRadius: Int): Bitmap {
        val dstBitmap = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dstBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        val rectF = RectF(0f, 0f, srcBitmap.width.toFloat(), srcBitmap.height.toFloat())
        canvas.drawRoundRect(rectF, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(srcBitmap, 0f, 0f, paint)
        return dstBitmap
    }
}