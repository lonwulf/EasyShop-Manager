package com.lonwulf.labs.easyshopmanager.data.repository

import android.content.Context
import androidx.datastore.dataStore
import com.lonwulf.labs.easyshopmanager.data.util.AppSettingsSerializer
import com.lonwulf.labs.easyshopmanager.domain.model.AppSettings
import com.lonwulf.labs.easyshopmanager.domain.repository.IDatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatastoreRepositoryImpl(private val context: Context) : IDatastoreRepository {
    private val Context.appSettingsStore by dataStore(
        "app-settings.json",
        AppSettingsSerializer
    )

    override val appSettings: Flow<AppSettings>
        get() = context.appSettingsStore.data.map { it }

    override suspend fun saveAppSettings(appSettings: AppSettings) {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                fName = appSettings.fName,
                lName = appSettings.lName,
                email = appSettings.email,
                userId = appSettings.userId
            )
        }
    }

    override suspend fun clearAppSettings() {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                fName = "",
                lName = "",
                email = "",
                userId = ""
            )
        }
    }
}