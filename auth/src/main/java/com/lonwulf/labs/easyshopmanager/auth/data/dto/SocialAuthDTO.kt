package com.lonwulf.labs.easyshopmanager.auth.data.dto

data class SocialAuthDTO(
    val user: UserDto,
    val accessToken: String,    // your app's own JWT
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String,      // "Bearer"
    val isNewUser: Boolean,     // true = sign up, false = sign in
)
