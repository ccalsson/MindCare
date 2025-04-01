package com.mindcare.mindcare.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mindcare.mindcare.data.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NotificationRepository,
    private val workManager: WorkManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Obtener todos los recordatorios activos
            val activeReminders = repository.activeReminders.first()

            // Reprogramar cada recordatorio
            activeReminders.forEach { reminder ->
                val request = ReminderWorker.createWorkRequest(
                    reminderId = reminder.id,
                    title = reminder.title,
                    message = reminder.message,
                    type = reminder.type.toNotificationType(),
                    delayInMinutes = calculateDelay(reminder),
                    isPeriodic = reminder.frequency.isPeriodic(),
                    intervalInHours = reminder.frequency.getIntervalHours()
                )

                workManager.enqueueUniqueWork(
                    "reminder_${reminder.id}",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun createWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ReminderSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
        }
    }
} 