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
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationRetryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NotificationRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Obtener notificaciones fallidas
            val failedNotifications = repository.getFailedNotifications().first()

            failedNotifications.forEach { notification ->
                if (notification.retryCount < MAX_RETRY_ATTEMPTS) {
                    // Intentar mostrar la notificación nuevamente
                    try {
                        notificationService.showNotification(notification)
                        repository.updateNotificationStatus(
                            notification.id,
                            NotificationStatus.PROCESSED
                        )
                    } catch (e: Exception) {
                        // Incrementar contador de reintentos
                        repository.updateNotificationRetryCount(
                            notification.id,
                            notification.retryCount + 1
                        )
                        
                        // Programar próximo reintento con backoff exponencial
                        scheduleRetry(notification.id, notification.retryCount + 1)
                    }
                } else {
                    // Marcar como fallida permanentemente después de máximos intentos
                    repository.updateNotificationStatus(
                        notification.id,
                        NotificationStatus.FAILED_PERMANENT
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun scheduleRetry(notificationId: Long, retryCount: Int) {
        val delay = calculateBackoffDelay(retryCount)
        
        val retryWork = OneTimeWorkRequestBuilder<NotificationRetryWorker>()
            .setInputData(workDataOf(KEY_NOTIFICATION_ID to notificationId))
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "retry_notification_$notificationId",
                ExistingWorkPolicy.REPLACE,
                retryWork
            )
    }

    private fun calculateBackoffDelay(retryCount: Int): Long {
        // Implementar backoff exponencial: 5min, 15min, 45min, etc.
        return (5 * Math.pow(3.0, (retryCount - 1).toDouble())).toLong()
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 5
        private const val KEY_NOTIFICATION_ID = "notification_id"

        fun createWork(notificationId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NotificationRetryWorker>()
                .setInputData(workDataOf(KEY_NOTIFICATION_ID to notificationId))
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }
} 