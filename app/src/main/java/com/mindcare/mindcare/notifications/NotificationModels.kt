package com.mindcare.mindcare.notifications

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false,
    val actionData: Map<String, String>? = null
)

enum class NotificationType {
    BREATHING_REMINDER,
    JOURNAL_REMINDER,
    GROUP_ACTIVITY,
    SUPPORT_MESSAGE,
    ACHIEVEMENT,
    SYSTEM
}

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: ReminderType,
    val title: String,
    val message: String,
    val time: Date,
    val frequency: ReminderFrequency,
    val isEnabled: Boolean = true,
    val lastTriggered: Date? = null
)

enum class ReminderType {
    BREATHING_EXERCISE,
    JOURNAL_ENTRY,
    MEDITATION,
    WATER,
    MEDICATION,
    CUSTOM
}

enum class ReminderFrequency {
    ONCE,
    DAILY,
    WEEKLY,
    CUSTOM
}

data class NotificationPreferences(
    val breathingReminders: Boolean = true,
    val journalReminders: Boolean = true,
    val groupNotifications: Boolean = true,
    val achievementNotifications: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22, // Hora en formato 24h
    val quietHoursEnd: Int = 8
) 