package com.lonwulf.labs.easyshopmanager.scanner.camera

import androidx.camera.core.ImageProxy

/**
 * Converts a CameraX [ImageProxy] (typically `YUV_420_888`) to NV21 bytes.
 *
 * The conversion logic is based on plane row/pixel strides so it works on more devices.
 */
fun ImageProxy.toNv21Bytes(): ByteArray {
    val imageWidth = width
    val imageHeight = height

    val yPlane = planes[0]
    val uPlane = planes.getOrNull(1)
    val vPlane = planes.getOrNull(2)
        ?: throw IllegalStateException("Expected 3 planes for YUV_420_888 format.")

    val yBuffer = yPlane.buffer
    val uBuffer = uPlane!!.buffer
    val vBuffer = vPlane.buffer

    val yRowStride = yPlane.rowStride
    val yPixelStride = yPlane.pixelStride

    val uRowStride = uPlane.rowStride
    val uPixelStride = uPlane.pixelStride

    val vRowStride = vPlane.rowStride
    val vPixelStride = vPlane.pixelStride

    val nv21 = ByteArray(imageWidth * imageHeight * 3 / 2)
    var outPos = 0

    // Copy Y plane.
    for (row in 0 until imageHeight) {
        val yRowStart = row * yRowStride
        for (col in 0 until imageWidth) {
            val yIndex = yRowStart + col * yPixelStride
            nv21[outPos++] = yBuffer.get(yIndex)
        }
    }

    // Copy interleaved VU (NV21).
    val chromaHeight = imageHeight / 2
    val chromaWidth = imageWidth / 2
    for (row in 0 until chromaHeight) {
        val uRowStart = row * uRowStride
        val vRowStart = row * vRowStride
        for (col in 0 until chromaWidth) {
            val uIndex = uRowStart + col * uPixelStride
            val vIndex = vRowStart + col * vPixelStride

            // NV21: V then U.
            nv21[outPos++] = vBuffer.get(vIndex)
            nv21[outPos++] = uBuffer.get(uIndex)
        }
    }

    return nv21
}

