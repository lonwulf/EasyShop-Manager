package com.lonwulf.labs.easyshopmanager.di

import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.MainViewModel
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.LiveBarcodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel() }
    viewModel { LiveBarcodeViewModel() }
}