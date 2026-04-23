package com.lonwulf.labs.easyshopmanager

import android.app.Application
import androidx.work.Configuration
import com.lonwulf.labs.camera.di.cameraDependencies
import com.lonwulf.labs.easyshopmanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.Executors

class EasyShopApplication: Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyShopApplication)
            androidLogger(Level.DEBUG)
            modules(appModule,cameraDependencies)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setExecutor(Executors.newFixedThreadPool(8))
//            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()
}