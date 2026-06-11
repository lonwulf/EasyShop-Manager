package com.lonwulf.labs.easyshopmanager.auth.data.source

import com.lonwulf.labs.easyshopmanager.auth.data.dto.SignInDTO
import com.lonwulf.labs.easyshopmanager.auth.data.dto.SignUpDTO
import com.lonwulf.labs.easyshopmanager.auth.data.payload.SignInBodyRequest
import com.lonwulf.labs.easyshopmanager.auth.data.payload.SignUpBodyRequest
import com.lonwulf.labs.easyshopmanager.core.data.dto.APIResponse
import com.lonwulf.labs.easyshopmanager.core.network.APIResult
import com.lonwulf.labs.easyshopmanager.core.network.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher

class AuthRemoteDataSource(private val authAPIService: IAuthAPIService) : RemoteDataSource() {

    suspend fun signInUser(
        dispatcher: CoroutineDispatcher,
        signInBodyRequest: SignInBodyRequest
    ): APIResult<APIResponse<SignInDTO>> =
        safeApiCall(dispatcher) {
            authAPIService.signInUser(signInBodyRequest)
        }

    suspend fun signUpUser(
        dispatcher: CoroutineDispatcher,
        signUpBodyRequest: SignUpBodyRequest
    ): APIResult<APIResponse<SignUpDTO>> =
        safeApiCall(dispatcher) {
            authAPIService.signUpUser(signUpBodyRequest)
        }
}