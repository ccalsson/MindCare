package com.mindcare.mindcare.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mindcare.mindcare.data.repository.NotificationRepository
import com.mindcare.mindcare.notifications.NotificationStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NotificationRepository,
    private val workManager: WorkManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Limpiar notificaciones antiguas
            scheduleCleanup()

            // 2. Sincronizar recordatorios
            syncReminders()

            // 3. Procesar notificaciones pendientes
            processPendingNotifications()

            // 4. Reintentar notificaciones fallidas
            retryFailedNotifications()

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun scheduleCleanup() {
        workManager.enqueueUniquePeriodicWork(
            CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            NotificationCleanupWorker.createPeriodicWork()
        )
    }

    private suspend fun syncReminders() {
        // Verificar y reprogramar recordatorios
        workManager.enqueueUniqueWork(
            REMINDER_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            ReminderSyncWorker.createWork()
        )
    }

    private suspend fun processPendingNotifications() {
        val pendingCount = repository.getPendingNotifications().first().size
        if (pendingCount > 0) {
            workManager.enqueueUniqueWork(
                BATCH_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                NotificationBatchWorker.createWork()
            )
        }
    }

    private suspend fun retryFailedNotifications() {
        val failedNotifications = repository.getFailedNotifications().first()
        failedNotifications.forEach { notification ->
            if (notification.retryCount < MAX_RETRY_ATTEMPTS) {
                workManager.enqueueUniqueWork(
                    "retry_notification_${notification.id}",
                    ExistingWorkPolicy.REPLACE,
                    NotificationRetryWorker.createWork(notification.id)
                )
            } else {
                repository.updateNotificationStatus(
                    notification.id,
                    NotificationStatus.FAILED_PERMANENT
                )
            }
        }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val CLEANUP_WORK_NAME = "notification_cleanup"
        private const val REMINDER_SYNC_WORK_NAME = "reminder_sync"
        private const val BATCH_WORK_NAME = "notification_batch"

        fun createPeriodicWork(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<NotificationSyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES // Flexibilidad
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
        }

        fun createOneTimeWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NotificationSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }
} 