package com.mindcare.mindcare.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.mindcare.mindcare.data.repository.NotificationRepository
import com.mindcare.mindcare.notifications.NotificationType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReminderWorkerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var repository: NotificationRepository

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        repository = mockk(relaxed = true)

        // Configurar WorkManager para pruebas
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testReminderWorkerSuccess() = runTest {
        // Crear datos de entrada
        val input = workDataOf(
            ReminderWorker.KEY_REMINDER_ID to 1L,
            ReminderWorker.KEY_TITLE to "Test Reminder",
            ReminderWorker.KEY_MESSAGE to "Test Message",
            ReminderWorker.KEY_TYPE to NotificationType.BREATHING_REMINDER.name
        )

        // Crear worker
        val worker = TestListenableWorkerBuilder<ReminderWorker>(context)
            .setInputData(input)
            .build()

        // Ejecutar worker
        val result = worker.doWork()

        // Verificar resultado
        assert(result is ListenableWorker.Result.Success)

        // Verificar que se creó la notificación
        coVerify {
            repository.createNotification(
                type = NotificationType.BREATHING_REMINDER,
                title = "Test Reminder",
                message = "Test Message",
                any()
            )
        }
    }

    @Test
    fun testPeriodicReminderScheduling() {
        // Crear trabajo periódico
        val periodicWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS
        ).setInputData(
            workDataOf(
                ReminderWorker.KEY_REMINDER_ID to 1L,
                ReminderWorker.KEY_TITLE to "Daily Reminder",
                ReminderWorker.KEY_MESSAGE to "Daily Message",
                ReminderWorker.KEY_TYPE to NotificationType.BREATHING_REMINDER.name
            )
        ).build()

        // Programar trabajo
        workManager.enqueueUniquePeriodicWork(
            "test_periodic_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWork
        )

        // Verificar que el trabajo está programado
        val workInfo = workManager.getWorkInfoById(periodicWork.id).get()
        assert(workInfo.state == WorkInfo.State.ENQUEUED)
    }

    @Test
    fun testReminderWorkerRetry() = runTest {
        // Crear datos de entrada inválidos para forzar un retry
        val input = workDataOf(
            ReminderWorker.KEY_REMINDER_ID to 1L
            // Omitir datos requeridos
        )

        // Crear worker
        val worker = TestListenableWorkerBuilder<ReminderWorker>(context)
            .setInputData(input)
            .build()

        // Ejecutar worker
        val result = worker.doWork()

        // Verificar que el worker solicita retry
        assert(result is ListenableWorker.Result.Retry)
    }

    @Test
    fun testReminderWorkerConstraints() {
        // Crear trabajo con restricciones
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    ReminderWorker.KEY_REMINDER_ID to 1L,
                    ReminderWorker.KEY_TITLE to "Test",
                    ReminderWorker.KEY_MESSAGE to "Test",
                    ReminderWorker.KEY_TYPE to NotificationType.BREATHING_REMINDER.name
                )
            )
            .build()

        // Programar trabajo
        workManager.enqueue(work)

        // Verificar que las restricciones se aplicaron
        val workInfo = workManager.getWorkInfoById(work.id).get()
        assert(workInfo.state == WorkInfo.State.ENQUEUED)
        assert(workInfo.constraints.requiredNetworkType == NetworkType.CONNECTED)
    }

    @Test
    fun testConcurrentReminders() {
        // Crear múltiples recordatorios concurrentes
        val reminders = List(5) { index ->
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(
                    workDataOf(
                        ReminderWorker.KEY_REMINDER_ID to index.toLong(),
                        ReminderWorker.KEY_TITLE to "Test $index",
                        ReminderWorker.KEY_MESSAGE to "Message $index",
                        ReminderWorker.KEY_TYPE to NotificationType.BREATHING_REMINDER.name
                    )
                )
                .build()
        }

        // Programar todos los recordatorios
        workManager.enqueue(reminders)

        // Verificar que todos están programados
        reminders.forEach { work ->
            val workInfo = workManager.getWorkInfoById(work.id).get()
            assert(workInfo.state == WorkInfo.State.ENQUEUED)
        }
    }
} 