package com.lonwulf.labs.easyshopmanager.scanner

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.scanner.objectDetection.DetectedObjectInfo
import com.lonwulf.labs.easyshopmanager.scanner.util.Utils

/** Hosts the detected object info and its search result.  */
class SearchedObject(
    resources: Resources,
    private val detectedObject: DetectedObjectInfo,
    val productList: List<Product>
) {

    private val objectThumbnailCornerRadius: Int = resources.getDimensionPixelOffset(R.dimen.bounding_box_corner_radius)
    private var objectThumbnail: Bitmap? = null

    val objectIndex: Int
        get() = detectedObject.objectIndex

    val boundingBox: Rect
        get() = detectedObject.boundingBox

    @Synchronized
    fun getObjectThumbnail(): Bitmap = objectThumbnail ?: let {
        Utils.getCornerRoundedBitmap(detectedObject.getBitmap(), objectThumbnailCornerRadius)
            .also { objectThumbnail = it }
    }
}