package com.mindcare.mindcare.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class NotificationPreferencesManagerTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var preferencesManager: NotificationPreferencesManager
    private lateinit var context: Context
    private val testScope = TestScope(Job())

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val testFile = tempFolder.newFile("test_preferences.preferences_pb")
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )
        preferencesManager = NotificationPreferencesManager(context)
    }

    @Test
    fun `preferences returns default values when empty`() = runTest {
        val preferences = preferencesManager.preferences.first()

        assertEquals(true, preferences.breathingReminders)
        assertEquals(true, preferences.journalReminders)
        assertEquals(true, preferences.groupNotifications)
        assertEquals(true, preferences.achievementNotifications)
        assertEquals(true, preferences.soundEnabled)
        assertEquals(true, preferences.vibrationEnabled)
        assertEquals(false, preferences.quietHoursEnabled)
        assertEquals(22, preferences.quietHoursStart)
        assertEquals(8, preferences.quietHoursEnd)
    }

    @Test
    fun `updatePreferences updates values correctly`() = runTest {
        val newPreferences = NotificationPreferences(
            breathingReminders = false,
            journalReminders = false,
            groupNotifications = false,
            achievementNotifications = false,
            soundEnabled = false,
            vibrationEnabled = false,
            quietHoursEnabled = true,
            quietHoursStart = 20,
            quietHoursEnd = 7
        )

        preferencesManager.updatePreferences(newPreferences)
        val updatedPreferences = preferencesManager.preferences.first()

        assertEquals(newPreferences, updatedPreferences)
    }
} 