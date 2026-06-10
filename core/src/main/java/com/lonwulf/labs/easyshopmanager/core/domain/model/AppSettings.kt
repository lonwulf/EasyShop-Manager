package com.lonwulf.labs.easyshopmanager.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val userPrefs: UserPrefs? = null,
    /**
     * camera prefs
     */
   val cameraSettings: CameraSettings? = null,
)