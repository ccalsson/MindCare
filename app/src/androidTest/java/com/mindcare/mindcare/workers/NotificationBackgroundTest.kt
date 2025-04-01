package com.mindcare.mindcare.workers

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.WorkManagerTestInitHelper
import com.mindcare.mindcare.data.AppDatabase
import com.mindcare.mindcare.data.repository.NotificationRepository
import com.mindcare.mindcare.notifications.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotificationBackgroundTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var database: AppDatabase

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testBackgroundReminderChain() = runTest {
        // Crear una cadena de recordatorios
        val reminderChain = createReminderChain()
        
        // Verificar que todos los recordatorios se programaron
        val workInfos = workManager.getWorkInfosByTag("reminder_chain").get()
        assert(workInfos.size == 3)
        assert(workInfos.all { it.state == WorkInfo.State.ENQUEUED })

        // Verificar que se respeta el orden
        val firstWork = workInfos.find { it.tags.contains("first") }
        val secondWork = workInfos.find { it.tags.contains("second") }
        assert(firstWork != null && secondWork != null)
        assert(firstWork!!.id != secondWork!!.id)
    }

    @Test
    fun testReminderPersistenceAfterReboot() = runTest {
        // Crear un recordatorio persistente
        val reminder = Reminder(
            type = ReminderType.BREATHING_EXERCISE,
            title = "Persistent Reminder",
            message = "Should survive reboot",
            time = Date(System.currentTimeMillis() + 3600000), // 1 hora en el futuro
            frequency = ReminderFrequency.DAILY
        )

        val reminderId = repository.createReminder(reminder)

        // Simular reinicio del sistema
        WorkManagerTestInitHelper.initializeTestWorkManager(context)

        // Verificar que el recordatorio sigue programado
        val workInfo = workManager.getWorkInfosForUniqueWork("reminder_$reminderId").get()
        assert(workInfo.isNotEmpty())
        assert(workInfo.first().state == WorkInfo.State.ENQUEUED)
    }

    @Test
    fun testBatteryOptimizationHandling() = runTest {
        // Crear recordatorio con restricciones de batería
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setConstraints(constraints)
            .setInputData(createTestReminderData())
            .build()

        workManager.enqueue(work)

        // Verificar que las restricciones se aplican
        val workInfo = workManager.getWorkInfoById(work.id).get()
        assert(workInfo.constraints.requiresBatteryNotLow())
    }

    @Test
    fun testNotificationGrouping() = runTest {
        // Crear múltiples notificaciones del mismo tipo
        repeat(3) { index ->
            repository.createNotification(
                type = NotificationType.BREATHING_REMINDER,
                title = "Group Test $index",
                message = "Test Message"
            )
        }

        // Verificar que se agruparon correctamente
        val activeNotifications = notificationManager.activeNotifications
        val groupedNotifications = activeNotifications.filter { 
            it.notification.group == "breathing_reminders"
        }
        assert(groupedNotifications.size == 3)
    }

    private fun createReminderChain(): WorkContinuation {
        val data = createTestReminderData()

        val firstWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .addTag("reminder_chain")
            .addTag("first")
            .build()

        val secondWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .addTag("reminder_chain")
            .addTag("second")
            .build()

        val thirdWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .addTag("reminder_chain")
            .addTag("third")
            .build()

        return workManager
            .beginWith(firstWork)
            .then(secondWork)
            .then(thirdWork)
            .enqueue()

        return continuation
    }

    private fun createTestReminderData() = workDataOf(
        ReminderWorker.KEY_REMINDER_ID to 1L,
        ReminderWorker.KEY_TITLE to "Test Reminder",
        ReminderWorker.KEY_MESSAGE to "Test Message",
        ReminderWorker.KEY_TYPE to NotificationType.BREATHING_REMINDER.name
    )
} 