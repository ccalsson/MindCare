package com.mindcare.mindcare.viewmodel

import app.cash.turbine.test
import com.mindcare.mindcare.data.repository.NotificationRepository
import com.mindcare.mindcare.notifications.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var repository: NotificationRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = NotificationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `createNotification should call repository`() = runTest {
        // Given
        val type = NotificationType.BREATHING_REMINDER
        val title = "Test"
        val message = "Test Message"

        // When
        viewModel.createNotification(type, title, message)

        // Then
        coVerify { 
            repository.createNotification(type, title, message, null)
        }
    }

    @Test
    fun `notifications flow should emit updates`() = runTest {
        // Given
        val notifications = listOf(
            Notification(
                id = 1,
                type = NotificationType.SYSTEM,
                title = "Test",
                message = "Message",
                timestamp = Date()
            )
        )
        every { repository.notifications } returns flowOf(notifications)

        // When/Then
        viewModel.notifications.test {
            val emitted = awaitItem()
            assert(emitted == notifications)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markAsRead should update notification state`() = runTest {
        // Given
        val notificationId = 1L

        // When
        viewModel.markNotificationAsRead(notificationId)

        // Then
        coVerify { repository.markNotificationAsRead(notificationId) }
    }

    @Test
    fun `createReminder should handle different frequencies`() = runTest {
        // Given
        val baseReminder = Reminder(
            type = ReminderType.BREATHING_EXERCISE,
            title = "Test",
            message = "Message",
            time = Date(),
            frequency = ReminderFrequency.DAILY
        )

        coEvery { repository.createReminder(any()) } returns 1L

        // When
        viewModel.createReminder(
            type = baseReminder.type,
            title = baseReminder.title,
            message = baseReminder.message,
            time = baseReminder.time,
            frequency = baseReminder.frequency
        )

        // Then
        coVerify { 
            repository.createReminder(match { 
                it.frequency == ReminderFrequency.DAILY &&
                it.type == ReminderType.BREATHING_EXERCISE
            })
        }
    }

    @Test
    fun `toggleReminder should update reminder state`() = runTest {
        // Given
        val reminderId = 1L
        val isEnabled = true

        // When
        viewModel.toggleReminder(reminderId, isEnabled)

        // Then
        coVerify { repository.toggleReminder(reminderId, isEnabled) }
    }

    @Test
    fun `updatePreferences should save new preferences`() = runTest {
        // Given
        val preferences = NotificationPreferences(
            breathingReminders = false,
            quietHoursEnabled = true
        )

        // When
        viewModel.updatePreferences(preferences)

        // Then
        coVerify { repository.updatePreferences(preferences) }
    }

    @Test
    fun `deleteReminder should remove reminder`() = runTest {
        // Given
        val reminderId = 1L

        // When
        viewModel.deleteReminder(reminderId)

        // Then
        coVerify { repository.deleteReminder(reminderId) }
    }

    @Test
    fun `unreadNotifications should filter correctly`() = runTest {
        // Given
        val notifications = listOf(
            Notification(
                id = 1,
                type = NotificationType.SYSTEM,
                title = "Read",
                message = "Message",
                timestamp = Date(),
                isRead = true
            ),
            Notification(
                id = 2,
                type = NotificationType.SYSTEM,
                title = "Unread",
                message = "Message",
                timestamp = Date(),
                isRead = false
            )
        )
        every { repository.unreadNotifications } returns flowOf(
            notifications.filter { !it.isRead }
        )

        // When/Then
        viewModel.unreadNotifications.test {
            val emitted = awaitItem()
            assert(emitted.size == 1)
            assert(emitted.first().title == "Unread")
            cancelAndIgnoreRemainingEvents()
        }
    }
} 