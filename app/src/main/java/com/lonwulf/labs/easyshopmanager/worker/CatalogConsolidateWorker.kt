package com.lonwulf.labs.easyshopmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope

class CatalogConsolidateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            Result.success()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.failure()
        }
    }
}