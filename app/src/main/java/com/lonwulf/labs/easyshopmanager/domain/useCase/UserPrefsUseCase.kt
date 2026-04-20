package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.domain.model.AppSettings
import com.lonwulf.labs.easyshopmanager.domain.repository.IDatastoreRepository
import kotlinx.coroutines.flow.Flow

class UserPrefsUseCase(private val dataStoreRepository: IDatastoreRepository) {

    operator fun invoke(): Flow<AppSettings> = dataStoreRepository.appSettings
    suspend fun saveAppSettings(appSettings: AppSettings) {
        dataStoreRepository.saveAppSettings(appSettings)
    }

    suspend fun clearAppHistory() = dataStoreRepository.clearAppSettings()

}