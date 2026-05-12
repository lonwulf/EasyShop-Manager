package com.lonwulf.labs.camera.domain.model

import android.util.Size

/**
 * Stores a preview size and a corresponding same-aspect-ratio picture size. To avoid distorted
 * preview images on some devices, the picture size must be set to a size that is the same aspect
 * ratio as the preview size or the preview may end up being distorted. If the picture size is null,
 * then there is no picture size with the same aspect ratio as the preview size.
 */
class CameraSizePair {

    val preview: Size
    val picture: Size?

    constructor(previewSize: Size, pictureSize: Size?) {
        preview = previewSize
        picture = pictureSize
    }

    /**
     * Optional helper if you're mapping from raw width/height values.
     */
    constructor(
        previewWidth: Int,
        previewHeight: Int,
        pictureWidth: Int?,
        pictureHeight: Int?
    ) {
        preview = Size(previewWidth, previewHeight)
        picture = if (pictureWidth != null && pictureHeight != null) {
            Size(pictureWidth, pictureHeight)
        } else {
            null
        }
    }
}