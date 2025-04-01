package com.mindcare.mindcare.notifications

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mindcare.mindcare.data.AppDatabase
import com.mindcare.mindcare.data.repository.NotificationRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotificationRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var database: AppDatabase

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testCreateAndRetrieveNotification() = runTest {
        // Crear notificación
        repository.createNotification(
            type = NotificationType.BREATHING_REMINDER,
            title = "Test Notification",
            message = "Test Message"
        )

        // Recuperar notificaciones
        val notifications = repository.notifications.first()
        
        assertTrue(notifications.isNotEmpty())
        assertEquals("Test Notification", notifications.first().title)
        assertEquals("Test Message", notifications.first().message)
        assertEquals(NotificationType.BREATHING_REMINDER, notifications.first().type)
    }

    @Test
    fun testMarkNotificationAsRead() = runTest {
        // Crear notificación
        repository.createNotification(
            type = NotificationType.JOURNAL_REMINDER,
            title = "Read Test",
            message = "Test Message"
        )

        // Obtener la notificación
        val notification = repository.notifications.first().first()
        assertFalse(notification.isRead)

        // Marcar como leída
        repository.markNotificationAsRead(notification.id)

        // Verificar que está marcada como leída
        val updatedNotification = repository.notifications.first().first()
        assertTrue(updatedNotification.isRead)
    }

    @Test
    fun testReminderLifecycle() = runTest {
        // Crear recordatorio
        val reminder = Reminder(
            type = ReminderType.BREATHING_EXERCISE,
            title = "Test Reminder",
            message = "Test Message",
            time = Date(),
            frequency = ReminderFrequency.DAILY
        )

        val reminderId = repository.createReminder(reminder)

        // Verificar que se creó
        val activeReminders = repository.activeReminders.first()
        assertTrue(activeReminders.any { it.id == reminderId })

        // Desactivar recordatorio
        repository.toggleReminder(reminderId, false)

        // Verificar que está desactivado
        val updatedReminders = repository.activeReminders.first()
        assertFalse(updatedReminders.any { it.id == reminderId })

        // Eliminar recordatorio
        repository.deleteReminder(reminderId)

        // Verificar que se eliminó
        val allReminders = repository.allReminders.first()
        assertFalse(allReminders.any { it.id == reminderId })
    }

    @Test
    fun testCleanOldNotifications() = runTest {
        // Crear notificaciones antiguas
        val oldDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -31)
        }.time

        val notification = Notification(
            type = NotificationType.SYSTEM,
            title = "Old Notification",
            message = "Test Message",
            timestamp = oldDate
        )

        database.notificationDao().insertNotification(notification)

        // Limpiar notificaciones antiguas
        repository.cleanOldNotifications(30)

        // Verificar que se eliminaron
        val notifications = repository.notifications.first()
        assertTrue(notifications.isEmpty())
    }

    @Test
    fun testUnreadNotificationsFilter() = runTest {
        // Crear varias notificaciones
        repeat(3) { index ->
            repository.createNotification(
                type = NotificationType.SYSTEM,
                title = "Test $index",
                message = "Message $index"
            )
        }

        // Marcar una como leída
        val firstNotification = repository.notifications.first().first()
        repository.markNotificationAsRead(firstNotification.id)

        // Verificar filtro de no leídas
        val unreadNotifications = repository.unreadNotifications.first()
        assertEquals(2, unreadNotifications.size)
    }
} 