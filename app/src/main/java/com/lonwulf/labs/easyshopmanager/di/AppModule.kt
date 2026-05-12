package com.lonwulf.labs.easyshopmanager.di

import app.cash.sqldelight.db.SqlDriver
import com.lonwulf.labs.easyshopmanager.data.repository.DatastoreRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.repository.SQLRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.source.db.DatabaseDriverFactory
import com.lonwulf.labs.easyshopmanager.db.Catalogue
import com.lonwulf.labs.easyshopmanager.domain.repository.IDatastoreRepository
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchProductsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.ProductViewModel
import com.lonwulf.labs.easyshopmanager.util.SyncEvent
import com.lonwulf.labs.easyshopmanager.worker.CatalogConsolidateWorker
import com.lonwulf.labs.easyshopmanager.worker.SyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
    single<Catalogue> { Catalogue(get()) }
    single { SyncEvent() }
    single<IDatastoreRepository> { DatastoreRepositoryImpl(androidContext()) }
    single<ISQLRepository> { SQLRepositoryImpl(get()) }
    single { ProductsUseCase(get()) }
    single { FetchProductsUseCase(get()) }
    worker { SyncWorker(androidContext(), get(), productsUseCase = get(), syncEvent = get()) }
    worker { CatalogConsolidateWorker(androidContext(), get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { MainViewModel(get()) }
}