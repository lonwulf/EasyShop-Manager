package com.lonwulf.labs.easyshopmanager.auth.data.payload

import kotlinx.serialization.Serializable

@Serializable
data class SignUpBodyRequest(
    val email: String,
    val password: String,
    val fName: String,
    val lName: String,
)
