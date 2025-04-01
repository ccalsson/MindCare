package com.mindcare.mindcare.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.TestListenableWorkerBuilder
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
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotificationSyncWorkerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var database: AppDatabase

    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testNotificationCleanup() = runTest {
        // Crear notificaciones antiguas
        val oldDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -31)
        }.time

        repeat(5) { index ->
            val notification = Notification(
                id = index.toLong(),
                type = NotificationType.SYSTEM,
                title = "Old Notification $index",
                message = "Test Message",
                timestamp = oldDate
            )
            database.notificationDao().insertNotification(notification)
        }

        // Ejecutar worker de limpieza
        val worker = TestListenableWorkerBuilder<NotificationCleanupWorker>(context).build()
        val result = worker.doWork()

        // Verificar resultado
        assert(result is ListenableWorker.Result.Success)

        // Verificar que las notificaciones antiguas fueron eliminadas
        val remainingNotifications = database.notificationDao().getAllNotifications().first()
        assert(remainingNotifications.isEmpty())
    }

    @Test
    fun testReminderSyncAfterTimeChange() = runTest {
        // Crear recordatorio
        val reminder = Reminder(
            type = ReminderType.BREATHING_EXERCISE,
            title = "Sync Test",
            message = "Test Message",
            time = Date(),
            frequency = ReminderFrequency.DAILY
        )

        val reminderId = repository.createReminder(reminder)

        // Simular cambio de zona horaria
        val worker = TestListenableWorkerBuilder<ReminderSyncWorker>(context)
            .setInputData(workDataOf("time_zone_changed" to true))
            .build()
        
        val result = worker.doWork()

        // Verificar que el recordatorio fue reprogramado
        assert(result is ListenableWorker.Result.Success)
        val workInfo = workManager.getWorkInfosForUniqueWork("reminder_$reminderId").get()
        assert(workInfo.isNotEmpty())
        assert(workInfo.first().state == WorkInfo.State.ENQUEUED)
    }

    @Test
    fun testBatchNotificationProcessing() = runTest {
        // Crear múltiples notificaciones pendientes
        val pendingNotifications = List(10) { index ->
            Notification(
                id = index.toLong(),
                type = NotificationType.SYSTEM,
                title = "Batch Test $index",
                message = "Test Message",
                timestamp = Date(),
                status = NotificationStatus.PENDING
            )
        }

        pendingNotifications.forEach {
            database.notificationDao().insertNotification(it)
        }

        // Ejecutar worker de procesamiento por lotes
        val worker = TestListenableWorkerBuilder<NotificationBatchWorker>(context).build()
        val result = worker.doWork()

        // Verificar que todas las notificaciones fueron procesadas
        assert(result is ListenableWorker.Result.Success)
        val processedNotifications = database.notificationDao().getAllNotifications().first()
        assert(processedNotifications.all { it.status == NotificationStatus.PROCESSED })
    }

    @Test
    fun testPeriodicSyncScheduling() {
        // Configurar sincronización periódica
        val syncWork = PeriodicWorkRequestBuilder<NotificationSyncWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES // Flexibilidad
        )
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

        workManager.enqueueUniquePeriodicWork(
            "notification_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )

        // Verificar que la sincronización está programada
        val workInfo = workManager.getWorkInfoById(syncWork.id).get()
        assert(workInfo.state == WorkInfo.State.ENQUEUED)
    }

    @Test
    fun testFailedNotificationRetry() = runTest {
        // Crear una notificación fallida
        val failedNotification = Notification(
            id = 1L,
            type = NotificationType.SYSTEM,
            title = "Failed Notification",
            message = "Test Message",
            timestamp = Date(),
            status = NotificationStatus.FAILED,
            retryCount = 2
        )

        database.notificationDao().insertNotification(failedNotification)

        // Ejecutar worker de reintento
        val worker = TestListenableWorkerBuilder<NotificationRetryWorker>(context).build()
        val result = worker.doWork()

        // Verificar que se intentó reenviar
        assert(result is ListenableWorker.Result.Success)
        val updatedNotification = database.notificationDao().getNotificationById(1L).first()
        assert(updatedNotification.retryCount == 3)
    }
} 