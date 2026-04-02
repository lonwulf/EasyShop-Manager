package com.lonwulf.labs.easyshopmanager.presentation.viewmodel

import android.content.res.Resources
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.scanner.objectDetection.DetectedObjectInfo

/**
 * UI-friendly wrapper around a confirmed object-detection result and its search products.
 *
 * This is referenced by `WorkflowViewModel.searchedObject`.
 */
data class SearchedObject(
    val resources: Resources,
    val detectedObject: DetectedObjectInfo?,
    val products: List<Product>,
)

