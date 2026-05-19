package com.lonwulf.labs.easyshopmanager.util

import com.lonwulf.labs.easyshopmanager.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.domain.model.fold
import kotlinx.coroutines.flow.Flow

suspend fun <T> Flow<Resource<T>>.lastResourceOrThrow(): T {
    var lastSuccess: T? = null
    collect { resource ->
        resource.fold(
            onLoading = { /*skip, not a terminal state*/ },
            onSuccess = { lastSuccess = it },
            onFailure = { message, cause -> throw SyncException(message, cause) }
        )
    }

    return lastSuccess ?: throw SyncException("Flow completed without emitting a Success value")
}