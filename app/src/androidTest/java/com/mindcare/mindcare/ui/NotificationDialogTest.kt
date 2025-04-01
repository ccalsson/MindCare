package com.mindcare.mindcare.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mindcare.mindcare.notifications.NotificationPreferences
import com.mindcare.mindcare.notifications.ReminderFrequency
import com.mindcare.mindcare.notifications.ReminderType
import com.mindcare.mindcare.ui.screens.NewReminderDialog
import com.mindcare.mindcare.ui.screens.NotificationPreferencesDialog
import org.junit.Rule
import org.junit.Test
import java.util.*

class NotificationDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNewReminderDialog() {
        var dialogResult: Triple<ReminderType, String, String>? = null

        composeTestRule.setContent {
            NewReminderDialog(
                onDismiss = { },
                onSave = { type, title, message, time, frequency ->
                    dialogResult = Triple(type, title, message)
                }
            )
        }

        // Llenar el formulario
        composeTestRule
            .onNodeWithText("Título")
            .performTextInput("Test Reminder")

        composeTestRule
            .onNodeWithText("Mensaje")
            .performTextInput("Test Message")

        // Seleccionar tipo
        composeTestRule
            .onNodeWithText("Tipo")
            .performClick()

        composeTestRule
            .onNodeWithText("Ejercicio de respiración")
            .performClick()

        // Guardar
        composeTestRule
            .onNodeWithText("Guardar")
            .performClick()

        // Verificar resultado
        assert(dialogResult?.first == ReminderType.BREATHING_EXERCISE)
        assert(dialogResult?.second == "Test Reminder")
        assert(dialogResult?.third == "Test Message")
    }

    @Test
    fun testPreferencesDialog() {
        var savedPreferences: NotificationPreferences? = null

        composeTestRule.setContent {
            NotificationPreferencesDialog(
                preferences = NotificationPreferences(),
                onDismiss = { },
                onSave = { preferences ->
                    savedPreferences = preferences
                }
            )
        }

        // Cambiar preferencias
        composeTestRule
            .onNodeWithText("Recordatorios de respiración")
            .performClick()

        composeTestRule
            .onNodeWithText("Horas silenciosas")
            .performClick()

        // Guardar
        composeTestRule
            .onNodeWithText("Guardar")
            .performClick()

        // Verificar resultado
        assert(savedPreferences?.breathingReminders == false)
        assert(savedPreferences?.quietHoursEnabled == true)
    }
} 