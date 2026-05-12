package com.lonwulf.labs.camera.di

import com.lonwulf.labs.camera.ui.viewModel.CameraXViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val cameraDependencies = module {
    viewModel { CameraXViewModel(application = androidApplication()) }
}