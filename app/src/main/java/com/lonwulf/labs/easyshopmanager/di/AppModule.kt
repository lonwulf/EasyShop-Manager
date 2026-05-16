package com.lonwulf.labs.easyshopmanager.di

import app.cash.sqldelight.db.SqlDriver
import com.lonwulf.labs.easyshopmanager.data.repository.APIRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.repository.DatastoreRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.repository.SQLRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.source.db.DatabaseDriverFactory
import com.lonwulf.labs.easyshopmanager.data.source.remote.ApiServiceImpl
import com.lonwulf.labs.easyshopmanager.data.source.remote.AppRemoteDataSource
import com.lonwulf.labs.easyshopmanager.data.source.remote.IApiService
import com.lonwulf.labs.easyshopmanager.db.Catalogue
import com.lonwulf.labs.easyshopmanager.domain.repository.IAPIRepository
import com.lonwulf.labs.easyshopmanager.domain.repository.IDatastoreRepository
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchProductsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.ProductViewModel
import com.lonwulf.labs.easyshopmanager.util.SyncEvent
import com.lonwulf.labs.easyshopmanager.worker.CatalogConsolidateWorker
import com.lonwulf.labs.easyshopmanager.worker.SyncWorker
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val appModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
    single<Catalogue> { Catalogue(get()) }
    single { SyncEvent() }
    single<IDatastoreRepository> { DatastoreRepositoryImpl(androidContext()) }
    single<IApiService> { ApiServiceImpl(get()) }
    single { AppRemoteDataSource(get()) }
    single<ISQLRepository> { SQLRepositoryImpl(get()) }
    single<IAPIRepository> { APIRepositoryImpl(get()) }
    single { ProductsUseCase(get()) }
    single { FetchProductsUseCase(get()) }
    single { FetchAndInsertSubCategoriesUseCase(get(), get()) }
    single { FetchAndInsertCategoriesUseCase(get(), get()) }
    worker { SyncWorker(androidContext(), get(), productsUseCase = get(), syncEvent = get()) }
    worker { CatalogConsolidateWorker(androidContext(), get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { MainViewModel(get()) }
}


val networkModule = module {
    single {
        HttpClient(Android) {
            engine {
                connectTimeout = 60_000
                socketTimeout = 60_000
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            expectSuccess = false
        }
    }
}