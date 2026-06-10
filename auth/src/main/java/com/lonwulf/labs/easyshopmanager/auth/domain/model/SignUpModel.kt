package com.lonwulf.labs.easyshopmanager.auth.domain.model

data class SignUpModel(
    val id: String,
    val email: String,
    val fName: String,
    val lName: String,
    val createdAt: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String,
)
