package com.lonwulf.labs.easyshopmanager.auth.domain.model

data class SignInModel(
    val fName: String?,
    val lName: String?,
    val email: String?,
    val token: String?,
    val expiresIn: Int?,
    val id: String?,
)