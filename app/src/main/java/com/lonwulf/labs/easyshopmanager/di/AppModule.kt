package com.lonwulf.labs.easyshopmanager.di

import app.cash.sqldelight.db.SqlDriver
import com.lonwulf.labs.easyshopmanager.data.repository.DatastoreRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.repository.SQLRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.source.db.DatabaseDriverFactory
import com.lonwulf.labs.easyshopmanager.db.Catalogue
import com.lonwulf.labs.easyshopmanager.domain.repository.IDatastoreRepository
import com.lonwulf.labs.easyshopmanager.domain.repository.ISQLRepository
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
    single<Catalogue> { Catalogue(get()) }
    single<IDatastoreRepository> { DatastoreRepositoryImpl(androidContext()) }
    single<ISQLRepository> { SQLRepositoryImpl(get()) }
    viewModel { MainViewModel() }

}