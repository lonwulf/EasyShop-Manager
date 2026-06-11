package com.lonwulf.labs.easyshopmanager.auth.data.source

import com.lonwulf.labs.easyshopmanager.auth.data.dto.SignInDTO
import com.lonwulf.labs.easyshopmanager.auth.data.dto.SignUpDTO
import com.lonwulf.labs.easyshopmanager.auth.data.payload.SignInBodyRequest
import com.lonwulf.labs.easyshopmanager.auth.data.payload.SignUpBodyRequest
import com.lonwulf.labs.easyshopmanager.core.data.dto.APIResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo

interface IAuthAPIService {
    suspend fun signInUser(signInBodyRequest: SignInBodyRequest): APIResponse<SignInDTO>
    suspend fun signUpUser(signUpBodyRequest: SignUpBodyRequest): APIResponse<SignUpDTO>
}

class AuthAPIServiceImpl(private val client: HttpClient) : IAuthAPIService {
    override suspend fun signInUser(signInBodyRequest: SignInBodyRequest): APIResponse<SignInDTO> =
        client.post("auth/signin") {
            setBody(signInBodyRequest)
        }
            .body(typeInfo<APIResponse<SignInDTO>>())

    override suspend fun signUpUser(signUpBodyRequest: SignUpBodyRequest): APIResponse<SignUpDTO> =
        client.post("auth/signUp") {
            setBody(signUpBodyRequest)
        }.body(typeInfo<APIResponse<SignUpDTO>>())

}