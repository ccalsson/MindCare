package com.mindcare.mindcare.data.repository

import com.mindcare.mindcare.data.dao.NotificationDao
import com.mindcare.mindcare.data.dao.ReminderDao
import com.mindcare.mindcare.notifications.*
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val reminderDao: ReminderDao,
    private val notificationService: NotificationService
) {
    val notifications: Flow<List<Notification>> = notificationDao.getAllNotifications()
    val unreadNotifications: Flow<List<Notification>> = notificationDao.getUnreadNotifications()
    val activeReminders: Flow<List<Reminder>> = reminderDao.getActiveReminders()
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun createNotification(
        type: NotificationType,
        title: String,
        message: String,
        actionData: Map<String, String>? = null
    ) {
        val notification = Notification(
            type = type,
            title = title,
            message = message,
            timestamp = Date(),
            actionData = actionData
        )
        notificationDao.insertNotification(notification)
        notificationService.showNotification(notification)
    }

    suspend fun markNotificationAsRead(notificationId: Long) {
        notificationDao.markAsRead(notificationId)
    }

    suspend fun createReminder(reminder: Reminder): Long {
        val id = reminderDao.insertReminder(reminder)
        if (reminder.isEnabled) {
            notificationService.scheduleReminder(reminder.copy(id = id))
        }
        return id
    }

    suspend fun toggleReminder(reminderId: Long, enabled: Boolean) {
        reminderDao.setReminderEnabled(reminderId, enabled)
        if (!enabled) {
            notificationService.cancelReminder(reminderId)
        } else {
            // Re-programar el recordatorio
            reminderDao.getAllReminders().collect { reminders ->
                reminders.find { it.id == reminderId }?.let {
                    notificationService.scheduleReminder(it)
                }
            }
        }
    }

    suspend fun deleteReminder(reminderId: Long) {
        notificationService.cancelReminder(reminderId)
        reminderDao.getAllReminders().collect { reminders ->
            reminders.find { it.id == reminderId }?.let {
                reminderDao.deleteReminder(it)
            }
        }
    }

    suspend fun cleanOldNotifications(daysToKeep: Int = 30) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysToKeep)
        }
        notificationDao.deleteOldNotifications(calendar.timeInMillis)
    }
} 