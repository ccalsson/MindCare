package com.mindcare.mindcare.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mindcare.mindcare.data.repository.NotificationRepository
import com.mindcare.mindcare.notifications.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1)
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()
        val type = inputData.getString(KEY_TYPE)?.let {
            NotificationType.valueOf(it)
        } ?: return Result.failure()

        try {
            notificationRepository.createNotification(
                type = type,
                title = title,
                message = message,
                actionData = mapOf(KEY_REMINDER_ID to reminderId.toString())
            )
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_TYPE = "type"

        fun createWorkRequest(
            reminderId: Long,
            title: String,
            message: String,
            type: NotificationType,
            delayInMinutes: Long = 0,
            isPeriodic: Boolean = false,
            intervalInHours: Long = 24
        ): OneTimeWorkRequest {
            val data = workDataOf(
                KEY_REMINDER_ID to reminderId,
                KEY_TITLE to title,
                KEY_MESSAGE to message,
                KEY_TYPE to type.name
            )

            return if (isPeriodic) {
                PeriodicWorkRequestBuilder<ReminderWorker>(
                    intervalInHours, TimeUnit.HOURS
                )
                .setInputData(data)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .setConstraints(createConstraints())
                .build()
            } else {
                OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .setConstraints(createConstraints())
                .build()
            }
        }

        private fun createConstraints() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
    }
} 