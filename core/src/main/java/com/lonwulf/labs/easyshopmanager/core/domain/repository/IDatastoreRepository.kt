package com.lonwulf.labs.easyshopmanager.core.domain.repository

import com.lonwulf.labs.easyshopmanager.core.domain.model.AppSettings
import com.lonwulf.labs.easyshopmanager.core.domain.model.CameraSettings
import com.lonwulf.labs.easyshopmanager.core.domain.model.UserPrefs
import kotlinx.coroutines.flow.Flow

interface IDatastoreRepository {
    val appSettings: Flow<AppSettings>
    suspend fun saveUserPrefsSettings(userPrefs: UserPrefs)
    suspend fun saveCameraSettings(cameraSettings: CameraSettings)
    suspend fun clearAppSettings()
    suspend fun clearUserPrefsSettings()
    suspend fun clearCameraPrefsSettings()
}