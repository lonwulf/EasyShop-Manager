package com.lonwulf.labs.easyshopmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertBrandsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchAndInsertSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import com.lonwulf.labs.easyshopmanager.util.SyncEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val productsUseCase: ProductsUseCase,
    private val fetchAndInsertCategoriesUseCase: FetchAndInsertCategoriesUseCase,
    private val fetchAndInsertSubCategoriesUseCase: FetchAndInsertSubCategoriesUseCase,
    private val fetchAndInsertBrandsUseCase: FetchAndInsertBrandsUseCase,
    private val syncEvent: SyncEvent
) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            syncEvent.startSync()
            val fullSyncJobs = listOf(
                async { fetchAndInsertCategoriesUseCase().collect {  } },
                async { fetchAndInsertSubCategoriesUseCase().collect {  } },
                async { fetchAndInsertBrandsUseCase().collect {  } },
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