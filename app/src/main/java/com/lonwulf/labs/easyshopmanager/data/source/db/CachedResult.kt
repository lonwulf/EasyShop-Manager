package com.lonwulf.labs.easyshopmanager.data.source.db

import com.lonwulf.labs.easyshopmanager.data.util.SqliteOperationResult

sealed class CacheResult<out T> {
    data class Success<T>(val data: T) : CacheResult<T>()
    data class Error(
        val operation: SqliteOperationResult,
        val msg: String,
        val cause: Throwable? = null
    ) : CacheResult<Nothing>()
}