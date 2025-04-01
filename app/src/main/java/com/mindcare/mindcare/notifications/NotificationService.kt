package com.mindcare.mindcare.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mindcare.mindcare.R
import com.mindcare.mindcare.MainActivity
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mindcare.mindcare.workers.ReminderWorker
import com.mindcare.mindcare.data.preferences.NotificationPreferencesManager
import com.mindcare.mindcare.notifications.NotificationPreferences

class NotificationService @Inject constructor(
    private val context: Context,
    private val workManager: WorkManager,
    private val preferencesManager: NotificationPreferencesManager
) {
    
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_REMINDERS,
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Recordatorios de ejercicios y actividades"
                },
                NotificationChannel(
                    CHANNEL_COMMUNITY,
                    "Comunidad",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Actividad de grupos y mensajes de apoyo"
                },
                NotificationChannel(
                    CHANNEL_ACHIEVEMENTS,
                    "Logros",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notificaciones de logros y progreso"
                }
            )

            notificationManager.createNotificationChannels(channels)
        }
    }

    fun showNotification(notification: Notification) {
        // Verificar las preferencias antes de mostrar la notificación
        preferencesManager.preferences.value?.let { preferences ->
            // Verificar si el tipo de notificación está habilitado
            if (!shouldShowNotification(notification.type, preferences)) {
                return
            }

            // Verificar horas silenciosas
            if (preferences.quietHoursEnabled && isInQuietHours(preferences)) {
                return
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                notification.actionData?.forEach { (key, value) ->
                    putExtra(key, value)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                notification.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val channelId = getChannelId(notification.type)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.title)
                .setContentText(notification.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            // Aplicar preferencias de sonido y vibración
            if (!preferences.soundEnabled) {
                builder.setSound(null)
            }
            if (!preferences.vibrationEnabled) {
                builder.setVibrate(null)
            }

            notificationManager.notify(notification.id.toInt(), builder.build())
        }
    }

    private fun shouldShowNotification(type: NotificationType, preferences: NotificationPreferences): Boolean {
        return when (type) {
            NotificationType.BREATHING_REMINDER -> preferences.breathingReminders
            NotificationType.JOURNAL_REMINDER -> preferences.journalReminders
            NotificationType.GROUP_ACTIVITY,
            NotificationType.SUPPORT_MESSAGE -> preferences.groupNotifications
            NotificationType.ACHIEVEMENT -> preferences.achievementNotifications
            NotificationType.SYSTEM -> true
        }
    }

    private fun isInQuietHours(preferences: NotificationPreferences): Boolean {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (preferences.quietHoursStart > preferences.quietHoursEnd) {
            // Período que cruza la medianoche
            currentHour >= preferences.quietHoursStart || currentHour < preferences.quietHoursEnd
        } else {
            // Período normal
            currentHour in preferences.quietHoursStart until preferences.quietHoursEnd
        }
    }

    private fun getChannelId(type: NotificationType): String {
        return when (type) {
            NotificationType.BREATHING_REMINDER,
            NotificationType.JOURNAL_REMINDER -> CHANNEL_REMINDERS
            NotificationType.GROUP_ACTIVITY,
            NotificationType.SUPPORT_MESSAGE -> CHANNEL_COMMUNITY
            NotificationType.ACHIEVEMENT -> CHANNEL_ACHIEVEMENTS
            NotificationType.SYSTEM -> CHANNEL_REMINDERS
        }
    }

    fun scheduleReminder(reminder: Reminder) {
        val now = Calendar.getInstance()
        val reminderTime = Calendar.getInstance().apply { time = reminder.time }
        
        // Calcular el delay inicial
        var delayMinutes = if (reminderTime.before(now)) {
            // Si la hora ya pasó, programar para mañana
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, reminderTime.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, reminderTime.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
            }
            TimeUnit.MILLISECONDS.toMinutes(tomorrow.timeInMillis - now.timeInMillis)
        } else {
            TimeUnit.MILLISECONDS.toMinutes(reminderTime.timeInMillis - now.timeInMillis)
        }

        val workRequest = when (reminder.frequency) {
            ReminderFrequency.ONCE -> ReminderWorker.createWorkRequest(
                reminderId = reminder.id,
                title = reminder.title,
                message = reminder.message,
                type = NotificationType.BREATHING_REMINDER,
                delayInMinutes = delayMinutes,
                isPeriodic = false
            )
            ReminderFrequency.DAILY -> ReminderWorker.createWorkRequest(
                reminderId = reminder.id,
                title = reminder.title,
                message = reminder.message,
                type = NotificationType.BREATHING_REMINDER,
                delayInMinutes = delayMinutes,
                isPeriodic = true,
                intervalInHours = 24
            )
            ReminderFrequency.WEEKLY -> ReminderWorker.createWorkRequest(
                reminderId = reminder.id,
                title = reminder.title,
                message = reminder.message,
                type = NotificationType.BREATHING_REMINDER,
                delayInMinutes = delayMinutes,
                isPeriodic = true,
                intervalInHours = 24 * 7
            )
            ReminderFrequency.CUSTOM -> ReminderWorker.createWorkRequest(
                reminderId = reminder.id,
                title = reminder.title,
                message = reminder.message,
                type = NotificationType.BREATHING_REMINDER,
                delayInMinutes = delayMinutes,
                isPeriodic = false
            )
        }

        workManager.enqueueUniqueWork(
            "reminder_${reminder.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(reminderId: Long) {
        workManager.cancelUniqueWork("reminder_${reminderId}")
    }

    companion object {
        private const val CHANNEL_REMINDERS = "reminders"
        private const val CHANNEL_COMMUNITY = "community"
        private const val CHANNEL_ACHIEVEMENTS = "achievements"
    }
} 