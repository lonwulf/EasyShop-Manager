package com.lonwulf.labs.easyshopmanager.core.network

sealed class APIResult<out T> {
    object Loading : APIResult<Nothing>()
    data class Success<out T>(val value: T) : APIResult<T>()
    data class Failure(
        val errorCode: Int?,
        val errorMessage: String?,
        val cause: HttpResult
    ) : APIResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Loading -> "Loading"
            is Success<*> -> "Success[data=$value]"
            is Failure -> "Failure[errorCode=$errorCode, errorMessage=$errorMessage, cause=$cause]"
        }
    }
}
