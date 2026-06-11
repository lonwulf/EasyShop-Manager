package com.lonwulf.labs.easyshopmanager.core.domain.model

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val message: String, val cause: Throwable? = null) : Resource<Nothing>()
}

inline fun <T, R> Resource<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (message: String, cause: Throwable?) -> R,
    onLoading: () -> R
): R {
    return when (this) {
        is Resource.Loading -> onLoading()
        is Resource.Success -> onSuccess(data)
        is Resource.Failure -> onFailure(message, cause)
    }
}