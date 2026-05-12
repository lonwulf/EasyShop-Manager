package com.lonwulf.labs.easyshopmanager

import android.app.Application
import androidx.work.Configuration
import com.lonwulf.labs.camera.di.cameraDependencies
import com.lonwulf.labs.easyshopmanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.Executors
import io.kotzilla.generated.monitoring


class EasyShopApplication: Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyShopApplication)
            monitoring()
            androidLogger(Level.DEBUG)
            workManagerFactory()
            modules(appModule,cameraDependencies)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setExecutor(Executors.newFixedThreadPool(8))
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()
}