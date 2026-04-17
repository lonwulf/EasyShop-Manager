package com.lonwulf.labs.easyshopmanager

import android.app.Application
import com.lonwulf.labs.camera.di.cameraDependencies
import com.lonwulf.labs.easyshopmanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class EasyShopApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyShopApplication)
            androidLogger(Level.DEBUG)
            modules(appModule,cameraDependencies)
        }
    }
}