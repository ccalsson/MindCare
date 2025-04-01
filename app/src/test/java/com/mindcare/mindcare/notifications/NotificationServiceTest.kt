package com.mindcare.mindcare.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import com.mindcare.mindcare.data.preferences.NotificationPreferencesManager
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class NotificationServiceTest {
    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var preferencesManager: NotificationPreferencesManager
    private lateinit var notificationService: NotificationService

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        workManager = mockk(relaxed = true)
        preferencesManager = mockk()
        notificationService = NotificationService(context, workManager, preferencesManager)
    }

    @Test
    fun `showNotification respects quiet hours`() = runTest {
        // Configurar preferencias con horas silenciosas activas
        val preferences = NotificationPreferences(
            quietHoursEnabled = true,
            quietHoursStart = 22,
            quietHoursEnd = 8
        )
        every { preferencesManager.preferences } returns flowOf(preferences)

        // Crear una notificación de prueba
        val notification = Notification(
            id = 1,
            type = NotificationType.BREATHING_REMINDER,
            title = "Test",
            message = "Test Message",
            timestamp = Date()
        )

        // Simular que estamos en horas silenciosas
        mockkStatic(Calendar::class)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23) // 11 PM
        every { Calendar.getInstance() } returns calendar

        // Intentar mostrar la notificación
        notificationService.showNotification(notification)

        // Verificar que no se mostró la notificación
        verify(exactly = 0) { 
            any<NotificationManager>().notify(any(), any()) 
        }
    }

    @Test
    fun `showNotification respects notification type preferences`() = runTest {
        // Configurar preferencias con notificaciones de respiración desactivadas
        val preferences = NotificationPreferences(
            breathingReminders = false
        )
        every { preferencesManager.preferences } returns flowOf(preferences)

        // Crear una notificación de respiración
        val notification = Notification(
            id = 1,
            type = NotificationType.BREATHING_REMINDER,
            title = "Breathing Exercise",
            message = "Time to breathe",
            timestamp = Date()
        )

        // Intentar mostrar la notificación
        notificationService.showNotification(notification)

        // Verificar que no se mostró la notificación
        verify(exactly = 0) { 
            any<NotificationManager>().notify(any(), any()) 
        }
    }

    @Test
    fun `scheduleReminder creates work request with correct delay`() = runTest {
        val now = Calendar.getInstance()
        val reminderTime = Calendar.getInstance().apply {
            add(Calendar.HOUR, 2) // Programar para 2 horas después
        }

        val reminder = Reminder(
            id = 1,
            type = ReminderType.BREATHING_EXERCISE,
            title = "Test Reminder",
            message = "Test Message",
            time = reminderTime.time,
            frequency = ReminderFrequency.ONCE
        )

        notificationService.scheduleReminder(reminder)

        // Verificar que se creó el trabajo con el delay correcto
        verify { 
            workManager.enqueueUniqueWork(
                eq("reminder_1"),
                any(),
                match { request ->
                    request.workSpec.initialDelay >= TimeUnit.HOURS.toMillis(1) &&
                    request.workSpec.initialDelay <= TimeUnit.HOURS.toMillis(2)
                }
            )
        }
    }

    @Test
    fun `cancelReminder cancels work request`() {
        val reminderId = 1L
        notificationService.cancelReminder(reminderId)

        verify { 
            workManager.cancelUniqueWork("reminder_$reminderId")
        }
    }
} 