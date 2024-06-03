package com.alad1nks.productsandroid.sync.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alad1nks.productsandroid.core.data.repository.ProductsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ProductsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.v("SyncWorker", "Entering function doWork()")
        return try {
            repository.refreshProducts()
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error refreshing products: $e")
            Result.retry()
        }
    }
}
