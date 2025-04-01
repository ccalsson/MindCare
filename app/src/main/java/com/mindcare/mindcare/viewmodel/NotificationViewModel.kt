package com.mindcare.mindcare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.notifications.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val notificationService = NotificationService(application)
    
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications
    
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders
    
    private val _preferences = MutableStateFlow(NotificationPreferences())
    val preferences: StateFlow<NotificationPreferences> = _preferences

    init {
        loadNotifications()
        loadReminders()
        loadPreferences()
    }

    fun createReminder(
        type: ReminderType,
        title: String,
        message: String,
        time: Date,
        frequency: ReminderFrequency
    ) {
        val reminder = Reminder(
            type = type,
            title = title,
            message = message,
            time = time,
            frequency = frequency
        )
        
        viewModelScope.launch {
            // TODO: Persistir en Room
            _reminders.value = _reminders.value + reminder
            notificationService.scheduleReminder(reminder)
        }
    }

    fun toggleReminder(reminderId: Long, enabled: Boolean) {
        viewModelScope.launch {
            val updatedReminders = _reminders.value.map { reminder ->
                if (reminder.id == reminderId) {
                    reminder.copy(isEnabled = enabled)
                } else reminder
            }
            _reminders.value = updatedReminders

            if (!enabled) {
                notificationService.cancelReminder(reminderId)
            } else {
                _reminders.value.find { it.id == reminderId }?.let {
                    notificationService.scheduleReminder(it)
                }
            }
        }
    }

    fun deleteReminder(reminderId: Long) {
        viewModelScope.launch {
            notificationService.cancelReminder(reminderId)
            _reminders.value = _reminders.value.filter { it.id != reminderId }
            // TODO: Eliminar de Room
        }
    }

    fun markNotificationAsRead(notificationId: Long) {
        viewModelScope.launch {
            val updatedNotifications = _notifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else notification
            }
            _notifications.value = updatedNotifications
            // TODO: Actualizar en Room
        }
    }

    fun updatePreferences(preferences: NotificationPreferences) {
        viewModelScope.launch {
            _preferences.value = preferences
            // TODO: Persistir preferencias
        }
    }

    private fun loadNotifications() {
        // TODO: Cargar desde Room
        _notifications.value = generateSampleNotifications()
    }

    private fun loadReminders() {
        // TODO: Cargar desde Room
        _reminders.value = generateSampleReminders()
    }

    private fun loadPreferences() {
        // TODO: Cargar preferencias guardadas
    }

    private fun generateSampleNotifications(): List<Notification> {
        return listOf(
            Notification(
                id = 1,
                type = NotificationType.BREATHING_REMINDER,
                title = "Momento de respirar",
                message = "Toma un momento para hacer ejercicios de respiración",
                timestamp = Date(),
                isRead = false
            ),
            Notification(
                id = 2,
                type = NotificationType.GROUP_ACTIVITY,
                title = "Nueva actividad en tu grupo",
                message = "Alguien compartió su experiencia en 'Mindfulness Diario'",
                timestamp = Date(System.currentTimeMillis() - 3600000),
                isRead = true
            )
        )
    }

    private fun generateSampleReminders(): List<Reminder> {
        return listOf(
            Reminder(
                id = 1,
                type = ReminderType.BREATHING_EXERCISE,
                title = "Ejercicio de respiración",
                message = "Es hora de tu ejercicio de respiración diario",
                time = Date(),
                frequency = ReminderFrequency.DAILY
            ),
            Reminder(
                id = 2,
                type = ReminderType.JOURNAL_ENTRY,
                title = "Registro diario",
                message = "¿Cómo te sientes hoy?",
                time = Date(),
                frequency = ReminderFrequency.DAILY
            )
        )
    }
} 