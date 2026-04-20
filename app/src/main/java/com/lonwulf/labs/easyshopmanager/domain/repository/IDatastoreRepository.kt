package com.lonwulf.labs.easyshopmanager.domain.repository

import com.lonwulf.labs.easyshopmanager.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface IDatastoreRepository {
    val appSettings: Flow<AppSettings>
    suspend fun saveAppSettings(appSettings: AppSettings)
    suspend fun clearAppSettings()
}