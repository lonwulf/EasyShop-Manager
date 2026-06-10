package com.lonwulf.labs.easyshopmanager

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
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
import coil3.util.DebugLogger
import com.lonwulf.labs.camera.di.cameraDependencies
import com.lonwulf.labs.camera.domain.usecase.CameraPrefsUseCase
import com.lonwulf.labs.camera.util.PreferenceUtils
import com.lonwulf.labs.easyshopmanager.core.di.coreModule
import com.lonwulf.labs.easyshopmanager.core.di.networkModule
import com.lonwulf.labs.easyshopmanager.di.appModule
import io.kotzilla.generated.monitoring
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent
import java.io.File
import java.util.concurrent.Executors


class EasyShopApplication : MultiDexApplication(), Configuration.Provider, SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EasyShopApplication)
            monitoring()
            androidLogger(Level.DEBUG)
            workManagerFactory()
            modules(coreModule, networkModule, appModule, cameraDependencies)
        }

        PreferenceUtils.init(
            cameraPrefsUseCase = KoinJavaComponent.get(CameraPrefsUseCase::class.java),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
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

        if (BuildConfig.DEBUG) {
            builder.logger(DebugLogger())
        }
        return builder.build()

    }
}