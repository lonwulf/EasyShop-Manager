package com.lonwulf.labs.easyshopmanager.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CameraSettings(
    val isAutoSearchEnabled: Boolean = false,
    val isMultipleObjectsMode: Boolean = false,
    val isClassificationEnabled: Boolean = false,
    val shouldDelayLoadingBarcodeResult: Boolean = false,
    val barcodeReticleWidth: Int = 80,
    val barcodeReticleHeight: Int = 35,
    val isEnabledBarcodeSizeCheck: Boolean = false,
    val minimumBarcodeWidth: Int = 50,
    val confirmationTimeInAutoSearch: Int = 1500,
    val confirmationTimeInManualSearch: Int = 500,
)
