package com.lonwulf.labs.easyshopmanager.auth.domain.state

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}