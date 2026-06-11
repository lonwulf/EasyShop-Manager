package com.lonwulf.labs.easyshopmanager.auth.data.payload

data class SocialAuthBodyRequest(
    val provider: SocialProvider,
    val idToken: String,        // token from Google/Facebook/Apple SDK
    val accessToken: String?,   // some providers return this too (Facebook)
)
enum class SocialProvider {
    GOOGLE,
    FACEBOOK,
    APPLE,
}