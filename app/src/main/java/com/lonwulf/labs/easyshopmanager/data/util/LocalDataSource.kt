package com.lonwulf.labs.easyshopmanager.data.util

import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import android.database.sqlite.SQLiteOutOfMemoryException
import android.database.sqlite.SQLiteReadOnlyDatabaseException
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

open class LocalDataSource {

    open suspend fun <T> safeCacheCall(dispatcher: CoroutineDispatcher, dbCall: suspend () -> T): CacheResult<T> {
        return withContext(dispatcher) {
            try {
                val result = retryDbCall {
                    dbCall()
                }
                CacheResult.Success(result)
            } catch (throwable: Throwable) {
                val operationResult = mapToSqliteError(throwable)
                val message = throwable.message ?: throwable.localizedMessage ?: "Unknown DB error"
                CacheResult.Error(operationResult, message, throwable)
            }
        }
    }

    open fun <T> safeCacheFlow(
        flow: Flow<T>
    ): Flow<CacheResult<T>> {
        return flow
            .retrySqlite()
            .map<T, CacheResult<T>> { CacheResult.Success(it) }
            .catch { throwable ->
                val op = mapToSqliteError(throwable)
                emit(
                    CacheResult.Error(
                        operation = op,
                        msg = throwable.message ?: "Flow DB error",
                        cause = throwable
                    )
                )
            }
    }

    private fun mapToSqliteError(throwable: Throwable): SqliteOperationResult {
        return when (throwable) {
            is SQLiteConstraintException -> SqliteOperationResult.CONSTRAINT
            is SQLiteFullException -> SqliteOperationResult.FULL
            is SQLiteDiskIOException -> SqliteOperationResult.DISK
            is SQLiteReadOnlyDatabaseException -> SqliteOperationResult.READONLY
            is SQLiteOutOfMemoryException -> SqliteOperationResult.NOMEM
            is SQLiteDatabaseCorruptException -> SqliteOperationResult.CORRUPT
            is SQLiteAbortException -> SqliteOperationResult.ABORT
            is SQLiteException -> mapByMessage(throwable.message)
            else -> SqliteOperationResult.UNKNOWN
        }
    }

    private fun mapByMessage(message: String?): SqliteOperationResult {
        val msg = message?.lowercase().orEmpty()
        return when {
            msg.contains("locked") -> SqliteOperationResult.LOCKED
            msg.contains("busy") -> SqliteOperationResult.BUSY
            msg.contains("no such table") -> SqliteOperationResult.MISMATCH
            msg.contains("datatype mismatch") -> SqliteOperationResult.MISMATCH
            msg.contains("too big") -> SqliteOperationResult.TOO_BIG
            msg.contains("unable to open") -> SqliteOperationResult.CANT_OPEN
            msg.contains("permission denied") -> SqliteOperationResult.PERM
            msg.contains("out of range") -> SqliteOperationResult.RANGE
            else -> SqliteOperationResult.UNKNOWN
        }
    }

    private suspend fun <T> retryDbCall(
        maxRetries: Int = 3,
        initialDelayMs: Long = 50,
        maxDelayMs: Long = 2_000,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMs

        repeat(maxRetries - 1) { attempt ->
            try {
                return block()
            } catch (t: Throwable) {
                val op = mapToSqliteError(t)
                if (!op.isRetryable()) throw t
                delay(currentDelay.milliseconds)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMs)
            }
        }

        // Final attempt — let the exception propagate naturally to safeCacheCall
        return block()
    }

    private fun <T> Flow<T>.retrySqlite(
        maxRetries: Int = 3,
        initialDelayMs: Long = 50,
        maxDelayMs: Long = 2_000
    ): Flow<T> {
        return retryWhen { cause, attempt ->
            val op = mapToSqliteError(cause)
            val shouldRetry = op.isRetryable() && attempt < maxRetries

            if (shouldRetry) {
                val backoff = (initialDelayMs * 2.0.pow(attempt.toDouble()))
                    .toLong()
                    .coerceAtMost(maxDelayMs)
                delay(backoff.milliseconds)
            }

            shouldRetry
        }
    }

    private fun SqliteOperationResult.isRetryable(): Boolean {
        return this == SqliteOperationResult.LOCKED ||
                this == SqliteOperationResult.BUSY
    }
}

enum class SqliteOperationResult {
    UNKNOWN,
    PERM,
    ABORT,
    BUSY,
    MISUSE,
    TOO_BIG,
    CANT_OPEN,
    CONSTRAINT,
    CORRUPT,
    RANGE,
    MISMATCH,
    DISK,
    DONE,
    FULL,
    NOMEM,
    READONLY,
    LOCKED,
}