package com.lonwulf.labs.easyshopmanager.di

import com.lonwulf.labs.easyshopmanager.data.repository.DatastoreRepositoryImpl
import com.lonwulf.labs.easyshopmanager.domain.repository.IDatastoreRepository
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel() }
    single<IDatastoreRepository> { DatastoreRepositoryImpl(androidContext()) }
}