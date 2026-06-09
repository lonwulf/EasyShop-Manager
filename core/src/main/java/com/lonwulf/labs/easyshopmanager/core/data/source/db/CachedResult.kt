package com.lonwulf.labs.easyshopmanager.core.data.source.db

import com.lonwulf.labs.easyshopmanager.core.data.source.SqliteOperationResult


sealed class CacheResult<out T> {
    data class Success<T>(val data: T) : CacheResult<T>()
    data class Error(
        val operation: SqliteOperationResult,
        val msg: String,
        val cause: Throwable? = null
    ) : CacheResult<Nothing>()
}