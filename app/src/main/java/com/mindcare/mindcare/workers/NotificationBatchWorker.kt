package com.mindcare.mindcare.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mindcare.mindcare.data.repository.NotificationRepository
import com.mindcare.mindcare.notifications.NotificationService
import com.mindcare.mindcare.notifications.NotificationStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationBatchWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NotificationRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Obtener notificaciones pendientes
            val pendingNotifications = repository.getPendingNotifications().first()

            // Procesar en lotes de 5
            pendingNotifications.chunked(5).forEach { batch ->
                batch.forEach { notification ->
                    notificationService.showNotification(notification)
                    repository.updateNotificationStatus(
                        notification.id,
                        NotificationStatus.PROCESSED
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun createWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NotificationBatchWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
        }
    }
} 