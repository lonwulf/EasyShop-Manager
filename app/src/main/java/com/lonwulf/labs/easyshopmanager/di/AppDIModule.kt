package com.lonwulf.labs.easyshopmanager.di

import com.lonwulf.labs.easyshopmanager.data.repository.APIRepositoryImpl
import com.lonwulf.labs.easyshopmanager.data.source.remote.ApiServiceImpl
import com.lonwulf.labs.easyshopmanager.data.source.remote.AppRemoteDataSource
import com.lonwulf.labs.easyshopmanager.data.source.remote.IApiService
import com.lonwulf.labs.easyshopmanager.domain.repository.IAPIRepository
import com.lonwulf.labs.easyshopmanager.domain.useCase.BrandsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.CategoriesSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertBrandsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchProductsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.UserPrefsUseCase
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.ProductViewModel
import com.lonwulf.labs.easyshopmanager.worker.CatalogConsolidateWorker
import com.lonwulf.labs.easyshopmanager.worker.SyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module


val appModule = module {
    single<IApiService> { ApiServiceImpl(get()) }
    single { AppRemoteDataSource(get()) }
    single<IAPIRepository> { APIRepositoryImpl(get()) }
    single { ProductsUseCase(get()) }
    single { FetchProductsUseCase(get()) }
    single { FetchAndInsertSubCategoriesUseCase(get(), get()) }
    single { FetchAndInsertBrandsUseCase(get(), get()) }
    single { FetchAndInsertCategoriesUseCase(get(), get()) }
    single { BrandsUseCase(get()) }
    single { CategoriesSubCategoriesUseCase(get()) }
    single { UserPrefsUseCase(get()) }
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
    viewModel { MainViewModel(get(), get(), get()) }
}