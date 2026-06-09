package com.lonwulf.labs.easyshopmanager.core.domain.repository

import com.lonwulf.labs.easyshopmanager.core.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface IDatastoreRepository {
    val appSettings: Flow<AppSettings>
    suspend fun saveAppSettings(appSettings: AppSettings)
    suspend fun clearAppSettings()
}