package com.mindcare.mindcare.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mindcare.mindcare.data.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NotificationRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Eliminar notificaciones más antiguas que 30 días
            repository.cleanOldNotifications(30)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun createPeriodicWork(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<NotificationCleanupWorker>(
                7, TimeUnit.DAYS // Ejecutar semanalmente
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
        }
    }
} 