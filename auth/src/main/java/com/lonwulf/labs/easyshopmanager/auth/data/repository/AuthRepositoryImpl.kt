package com.lonwulf.labs.easyshopmanager.auth.data.repository

import com.lonwulf.labs.easyshopmanager.auth.data.dto.toDomain
import com.lonwulf.labs.easyshopmanager.auth.data.payload.SignInBodyRequest
import com.lonwulf.labs.easyshopmanager.auth.data.payload.SignUpBodyRequest
import com.lonwulf.labs.easyshopmanager.auth.data.source.AuthRemoteDataSource
import com.lonwulf.labs.easyshopmanager.auth.domain.model.SignInModel
import com.lonwulf.labs.easyshopmanager.auth.domain.model.SignUpModel
import com.lonwulf.labs.easyshopmanager.auth.domain.repository.IAuthRepository
import com.lonwulf.labs.easyshopmanager.core.network.APIResult
import kotlinx.coroutines.Dispatchers

class AuthRepositoryImpl(private val authRemoteDataSource: AuthRemoteDataSource) : IAuthRepository {
    override suspend fun signInUser(
        email: String,
        password: String
    ): APIResult<SignInModel?> {
        return when (val response =
            authRemoteDataSource.signInUser(Dispatchers.IO, SignInBodyRequest(email, password))) {
            is APIResult.Loading -> APIResult.Loading
            is APIResult.Success -> {
                val result = response.value.data?.toDomain()
                APIResult.Success(result)
            }

            is APIResult.Failure -> APIResult.Failure(response.errorCode, response.errorMessage, response.cause)
        }
    }

    override suspend fun signUpUser(
        email: String,
        password: String,
        fName: String,
        lName: String
    ): APIResult<SignUpModel?> =
        when (val response =
            authRemoteDataSource.signUpUser(Dispatchers.IO, SignUpBodyRequest(email, password, fName, lName))) {
            is APIResult.Loading -> APIResult.Loading
            is APIResult.Success -> {
                val result = response.value.data?.toDomain()
                APIResult.Success(result)
            }

            is APIResult.Failure -> APIResult.Failure(response.errorCode, response.errorMessage, response.cause)
        }
}