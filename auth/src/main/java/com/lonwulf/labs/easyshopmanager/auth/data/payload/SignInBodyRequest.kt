package com.lonwulf.labs.easyshopmanager.auth.data.payload

import kotlinx.serialization.Serializable

@Serializable
data class SignInBodyRequest(
    private val email:String,
    private val password:String
)
