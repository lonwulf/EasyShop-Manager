package com.lonwulf.labs.easyshopmanager.core.network

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json

open class RemoteDataSource {
    open suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): APIResult<T> =
        withContext(dispatcher) {
            try {
                APIResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                when (throwable) {
                    is ResponseException -> {
                        when (throwable.response.status.value) {
                            in 400..451 -> parseHttpError(throwable)
                            in 500..599 -> error(
                                HttpResult.SERVER_ERROR,
                                throwable.response.status.value,
                                throwable.message
                            )

                            else -> error(
                                HttpResult.UN_DEFINED,
                                throwable.response.status.value,
                                throwable.message
                            )
                        }
                    }

                    is HttpRequestTimeoutException,
                    is SocketTimeoutException -> error(cause = HttpResult.TIMEOUT, msg = "timeout", code = null)

                    else -> error(cause = HttpResult.UN_DEFINED, msg = throwable.message, code = null)
                }
            }
        }

    private suspend fun parseHttpError(exception: ResponseException): APIResult.Failure {
        return try {
            val errorBody = exception.response.bodyAsText()
            val errorMessage = Json.decodeFromString<ErrorParser>(errorBody)
            error(HttpResult.CLIENT_ERROR, exception.response.status.value, errorMessage.message)
        } catch (ex: Exception) {
            error(HttpResult.CLIENT_ERROR, exception.response.status.value, exception.message)
        }
    }

    private fun error(cause: HttpResult, code: Int?, msg: String?): APIResult.Failure =
        APIResult.Failure(errorCode = code, errorMessage = msg, cause = cause)

    data class ErrorParser(@SerialName("message") val message: String? = null)
}