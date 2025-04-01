package com.mindcare.mindcare.notifications

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mindcare.mindcare.data.repository.NotificationRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotificationPerformanceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testBulkNotificationCreation() = runTest {
        val time = measureTimeMillis {
            repeat(100) { index ->
                notificationRepository.createNotification(
                    type = NotificationType.SYSTEM,
                    title = "Performance Test $index",
                    message = "Test message $index"
                )
            }
        }
        
        // La creación de 100 notificaciones no debería tomar más de 5 segundos
        assert(time < 5000) { "Bulk notification creation took too long: $time ms" }
    }

    @Test
    fun testBulkReminderCreation() = runTest {
        val time = measureTimeMillis {
            repeat(50) { index ->
                val reminder = Reminder(
                    type = ReminderType.BREATHING_EXERCISE,
                    title = "Performance Test $index",
                    message = "Test message $index",
                    time = Date(),
                    frequency = ReminderFrequency.DAILY
                )
                notificationRepository.createReminder(reminder)
            }
        }
        
        // La creación de 50 recordatorios no debería tomar más de 3 segundos
        assert(time < 3000) { "Bulk reminder creation took too long: $time ms" }
    }

    @Test
    fun testNotificationQueryPerformance() = runTest {
        // Primero crear algunas notificaciones
        repeat(100) { index ->
            notificationRepository.createNotification(
                type = NotificationType.SYSTEM,
                title = "Query Test $index",
                message = "Test message $index"
            )
        }

        val queryTime = measureTimeMillis {
            notificationRepository.notifications.first()
        }

        // La consulta de 100 notificaciones no debería tomar más de 1 segundo
        assert(queryTime < 1000) { "Notification query took too long: $queryTime ms" }
    }
} 