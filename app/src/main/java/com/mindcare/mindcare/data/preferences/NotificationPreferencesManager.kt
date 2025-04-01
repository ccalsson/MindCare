package com.mindcare.mindcare.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mindcare.mindcare.notifications.NotificationPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

@Singleton
class NotificationPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val preferences: Flow<NotificationPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            NotificationPreferences(
                breathingReminders = preferences[PreferencesKeys.BREATHING_REMINDERS] ?: true,
                journalReminders = preferences[PreferencesKeys.JOURNAL_REMINDERS] ?: true,
                groupNotifications = preferences[PreferencesKeys.GROUP_NOTIFICATIONS] ?: true,
                achievementNotifications = preferences[PreferencesKeys.ACHIEVEMENT_NOTIFICATIONS] ?: true,
                soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
                quietHoursEnabled = preferences[PreferencesKeys.QUIET_HOURS_ENABLED] ?: false,
                quietHoursStart = preferences[PreferencesKeys.QUIET_HOURS_START] ?: 22,
                quietHoursEnd = preferences[PreferencesKeys.QUIET_HOURS_END] ?: 8
            )
        }

    suspend fun updatePreferences(preferences: NotificationPreferences) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.BREATHING_REMINDERS] = preferences.breathingReminders
            prefs[PreferencesKeys.JOURNAL_REMINDERS] = preferences.journalReminders
            prefs[PreferencesKeys.GROUP_NOTIFICATIONS] = preferences.groupNotifications
            prefs[PreferencesKeys.ACHIEVEMENT_NOTIFICATIONS] = preferences.achievementNotifications
            prefs[PreferencesKeys.SOUND_ENABLED] = preferences.soundEnabled
            prefs[PreferencesKeys.VIBRATION_ENABLED] = preferences.vibrationEnabled
            prefs[PreferencesKeys.QUIET_HOURS_ENABLED] = preferences.quietHoursEnabled
            prefs[PreferencesKeys.QUIET_HOURS_START] = preferences.quietHoursStart
            prefs[PreferencesKeys.QUIET_HOURS_END] = preferences.quietHoursEnd
        }
    }

    private object PreferencesKeys {
        val BREATHING_REMINDERS = booleanPreferencesKey("breathing_reminders")
        val JOURNAL_REMINDERS = booleanPreferencesKey("journal_reminders")
        val GROUP_NOTIFICATIONS = booleanPreferencesKey("group_notifications")
        val ACHIEVEMENT_NOTIFICATIONS = booleanPreferencesKey("achievement_notifications")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        val QUIET_HOURS_START = intPreferencesKey("quiet_hours_start")
        val QUIET_HOURS_END = intPreferencesKey("quiet_hours_end")
    }
} 