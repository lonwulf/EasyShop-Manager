package com.lonwulf.labs.easyshopmanager

import android.app.Application
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.lonwulf.labs.camera.di.cameraDependencies
import com.lonwulf.labs.easyshopmanager.di.appModule
import com.lonwulf.labs.easyshopmanager.di.networkModule
import io.kotzilla.generated.monitoring
import io.ktor.client.HttpClient
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.io.File
import java.util.concurrent.Executors


class EasyShopApplication : Application(), Configuration.Provider, SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyShopApplication)
            monitoring()
            androidLogger(Level.DEBUG)
            workManagerFactory()
            modules(networkModule, appModule, cameraDependencies)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setExecutor(Executors.newFixedThreadPool(8))
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val client: HttpClient = getKoin().get()
        val builder = ImageLoader.Builder(this)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = client))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(percent = 0.25, context = this)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(cacheDir, "img_cache"))
                    .maxSizeBytes(200L * 1024 * 1024) //200MB
                    .build()
            }.crossfade(true)

        return builder.build()

    }
}