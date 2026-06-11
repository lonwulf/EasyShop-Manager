package com.lonwulf.labs.camera.domain.usecase

import com.lonwulf.labs.easyshopmanager.core.domain.model.AppSettings
import com.lonwulf.labs.easyshopmanager.core.domain.model.CameraSettings
import com.lonwulf.labs.easyshopmanager.core.domain.repository.IDatastoreRepository
import kotlinx.coroutines.flow.Flow

class CameraPrefsUseCase(private val dataStoreRepository: IDatastoreRepository) {
    operator fun invoke(): Flow<AppSettings> = dataStoreRepository.appSettings

    suspend fun saveCameraSettings(cameraSettings: CameraSettings) {
        dataStoreRepository.saveCameraSettings(cameraSettings)
    }

    suspend fun clearAppHistory() = dataStoreRepository.clearAppSettings()

}