package com.lonwulf.labs.easyshopmanager.core.data.repository

import android.content.Context
import androidx.datastore.dataStore
import com.lonwulf.labs.easyshopmanager.core.data.dataStore.AppSettingsSerializer
import com.lonwulf.labs.easyshopmanager.core.domain.model.AppSettings
import com.lonwulf.labs.easyshopmanager.core.domain.model.CameraSettings
import com.lonwulf.labs.easyshopmanager.core.domain.model.UserPrefs
import com.lonwulf.labs.easyshopmanager.core.domain.repository.IDatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatastoreRepositoryImpl(private val context: Context) : IDatastoreRepository {
    private val Context.appSettingsStore by dataStore(
        "app-settings.json",
        AppSettingsSerializer
    )

    override val appSettings: Flow<AppSettings>
        get() = context.appSettingsStore.data.map { it }

    override suspend fun saveUserPrefsSettings(userPrefs: UserPrefs) {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                userPrefs = userPrefs
            )
        }
    }

    override suspend fun saveCameraSettings(cameraSettings: CameraSettings) {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                cameraSettings = cameraSettings
            )
        }
    }

//    override suspend fun saveAppSettings(appSettings: AppSettings) {
//        context.appSettingsStore.updateData { prefs ->
//            prefs.copy(
//                userPrefs = appSettings.userPrefs,
//                fName = appSettings.fName,
//                lName = appSettings.lName,
//                email = appSettings.email,
//                userId = appSettings.userId,
//                sessionToken = appSettings.sessionToken
//            )
//        }
//    }

    override suspend fun clearAppSettings() {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                userPrefs = UserPrefs(),
                cameraSettings = CameraSettings()
            )
        }
    }

    override suspend fun clearUserPrefsSettings() {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                userPrefs = UserPrefs()
            )
        }
    }

    override suspend fun clearCameraPrefsSettings() {
        context.appSettingsStore.updateData { prefs ->
            prefs.copy(
                cameraSettings = CameraSettings()
            )
        }
    }
}