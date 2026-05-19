package com.lonwulf.labs.easyshopmanager.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import com.lonwulf.labs.easyshopmanager.BuildConfig
import com.lonwulf.labs.easyshopmanager.R
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
import com.lonwulf.labs.easyshopmanager.domain.useCase.BrandsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.CategoriesSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertBrandsUseCase
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
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
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
    single { FetchAndInsertBrandsUseCase(get(), get()) }
    single { FetchAndInsertCategoriesUseCase(get(), get()) }
    single { BrandsUseCase(get()) }
    single { CategoriesSubCategoriesUseCase(get()) }
    worker {
        SyncWorker(
            androidContext(),
            get(),
            productsUseCase = get(),
            syncEvent = get(),
            fetchAndInsertCategoriesUseCase = get(),
            fetchAndInsertSubCategoriesUseCase = get(),
            fetchAndInsertBrandsUseCase = get()
        )
    }
    worker { CatalogConsolidateWorker(androidContext(), get()) }
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
}


val networkModule = module {
    single {
        if (BuildConfig.DEBUG) {
            provideMockHttpClient(androidContext())
        } else {
            HttpClient(Android) {
                engine {
                    connectTimeout = 60_000
                    socketTimeout = 60_000
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = false
                    })
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.NONE
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                defaultRequest {
                    url()
                    contentType(ContentType.Application.Json)
                }

                expectSuccess = false
            }
        }

    }
}

fun provideMockHttpClient(context: Context): HttpClient {
    val categoriesJson = resourceString(context, R.raw.categories)
    val subCategoriesJson = resourceString(context, R.raw.sub_categories)
    val brandsJson = resourceString(context, R.raw.brands_sanitized_updated)

    return HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val path = request.url.encodedPath
                val responseJson = when {
                    path.endsWith("/categories") -> categoriesJson
                    path.endsWith("/subcategories") -> subCategoriesJson
                    path.endsWith("/brands") -> brandsJson
                    else -> return@addHandler respond(
                        content = """{"error": "Not Found"}""",
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }

        expectSuccess = false
    }
}

private fun resourceString(context: Context, resId: Int): String =
    context.resources.openRawResource(resId)
        .bufferedReader().use { it.readText() }