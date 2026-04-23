package com.lonwulf.labs.easyshopmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import com.lonwulf.labs.easyshopmanager.util.SyncEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SyncWorker(
    private val context: Context,
    private val params: WorkerParameters,
    private val productsUseCase: ProductsUseCase,
    private val syncEvent: SyncEvent
) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            syncEvent.startSync()
            val fullSyncJobs = listOf(
                async {  }
            )

            fullSyncJobs.awaitAll()
            syncEvent.endSync()
            Result.success()
        } catch (ex: Exception) {
            ex.printStackTrace()
            syncEvent.endSync()
            Result.failure()
        }
    }
}