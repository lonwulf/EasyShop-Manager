package com.lonwulf.labs.easyshopmanager.domain.useCase

import com.lonwulf.labs.easyshopmanager.core.domain.model.UserPrefs
import com.lonwulf.labs.easyshopmanager.core.domain.repository.IDatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPrefsUseCase(private val dataStoreRepository: IDatastoreRepository) {

    operator fun invoke(): Flow<UserPrefs> = dataStoreRepository.appSettings
        .map { it.userPrefs }

    suspend fun saveAppSettings(userPrefs: UserPrefs) {
        dataStoreRepository.saveUserPrefsSettings(userPrefs)
    }

    suspend fun clearAppHistory() = dataStoreRepository.clearAppSettings()

}