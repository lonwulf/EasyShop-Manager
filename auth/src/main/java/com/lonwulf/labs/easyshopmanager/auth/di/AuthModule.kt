package com.lonwulf.labs.easyshopmanager.auth.di

import com.lonwulf.labs.easyshopmanager.auth.data.repository.AuthRepositoryImpl
import com.lonwulf.labs.easyshopmanager.auth.data.source.AuthAPIServiceImpl
import com.lonwulf.labs.easyshopmanager.auth.data.source.AuthRemoteDataSource
import com.lonwulf.labs.easyshopmanager.auth.data.source.IAuthAPIService
import com.lonwulf.labs.easyshopmanager.auth.domain.repository.IAuthRepository
import com.lonwulf.labs.easyshopmanager.auth.domain.usecase.AuthUseCase
import org.koin.dsl.module

val authModule = module {
    single<IAuthAPIService> { AuthAPIServiceImpl(get()) }
    single { AuthRemoteDataSource(get()) }
    single<IAuthRepository> { AuthRepositoryImpl(get()) }
    single { AuthUseCase(get(), get()) }
}