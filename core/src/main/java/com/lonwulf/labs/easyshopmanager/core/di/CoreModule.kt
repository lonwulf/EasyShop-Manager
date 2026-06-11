package com.lonwulf.labs.easyshopmanager.core.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import com.lonwulf.labs.easyshopmanager.core.BuildConfig
import com.lonwulf.labs.easyshopmanager.core.R
import com.lonwulf.labs.easyshopmanager.core.data.repository.DatastoreRepositoryImpl
import com.lonwulf.labs.easyshopmanager.core.data.repository.SQLRepositoryImpl
import com.lonwulf.labs.easyshopmanager.core.data.source.db.DatabaseDriverFactory
import com.lonwulf.labs.easyshopmanager.core.domain.repository.IDatastoreRepository
import com.lonwulf.labs.easyshopmanager.core.domain.repository.ISQLRepository
import com.lonwulf.labs.easyshopmanager.core.util.SyncEvent
import com.lonwulf.labs.easyshopmanager.db.Catalogue
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
import org.koin.dsl.module

val coreModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
    single<Catalogue> { Catalogue(get()) }
    single { SyncEvent() }
    single<ISQLRepository> { SQLRepositoryImpl(get()) }
    single<IDatastoreRepository> { DatastoreRepositoryImpl(androidContext()) }
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