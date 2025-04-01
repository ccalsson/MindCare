package com.mindcare.mindcare.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mindcare.mindcare.MainActivity
import com.mindcare.mindcare.notifications.*
import com.mindcare.mindcare.viewmodel.NotificationViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
class NotificationScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var viewModel: NotificationViewModel

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testNotificationListDisplay() {
        // Agregar algunas notificaciones de prueba
        composeTestRule.runOnUiThread {
            repeat(3) { index ->
                viewModel.createNotification(
                    type = NotificationType.SYSTEM,
                    title = "Test Notification $index",
                    message = "Test Message $index"
                )
            }
        }

        // Verificar que las notificaciones se muestran
        composeTestRule
            .onAllNodesWithText("Test Notification", substring = true)
            .assertCountEquals(3)
    }

    @Test
    fun testReminderCreation() {
        // Abrir diálogo de nuevo recordatorio
        composeTestRule
            .onNodeWithContentDescription("Nuevo recordatorio")
            .performClick()

        // Llenar el formulario
        composeTestRule
            .onNodeWithText("Título")
            .performTextInput("Recordatorio de prueba")

        composeTestRule
            .onNodeWithText("Mensaje")
            .performTextInput("Mensaje de prueba")

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

        // Verificar que se creó el recordatorio
        composeTestRule
            .onNodeWithText("Recordatorio de prueba")
            .assertExists()
    }

    @Test
    fun testNotificationPreferences() {
        // Abrir preferencias
        composeTestRule
            .onNodeWithContentDescription("Preferencias")
            .performClick()

        // Cambiar algunas preferencias
        composeTestRule
            .onNodeWithText("Recordatorios de respiración")
            .performClick()

        composeTestRule
            .onNodeWithText("Horas silenciosas")
            .performClick()

        // Guardar cambios
        composeTestRule
            .onNodeWithText("Guardar")
            .performClick()

        // Verificar que el diálogo se cerró
        composeTestRule
            .onNodeWithText("Preferencias de notificaciones")
            .assertDoesNotExist()
    }

    @Test
    fun testMarkNotificationAsRead() {
        // Agregar una notificación
        composeTestRule.runOnUiThread {
            viewModel.createNotification(
                type = NotificationType.SYSTEM,
                title = "Test Read",
                message = "Test Message"
            )
        }

        // Hacer clic en la notificación
        composeTestRule
            .onNodeWithText("Test Read")
            .performClick()

        // Verificar que la notificación se marcó como leída
        composeTestRule
            .onNodeWithText("Test Read")
            .assertIsNotDisplayed()
    }

    @Test
    fun testReminderToggle() {
        // Crear un recordatorio
        composeTestRule.runOnUiThread {
            viewModel.createReminder(
                type = ReminderType.BREATHING_EXERCISE,
                title = "Toggle Test",
                message = "Test Message",
                time = Date(),
                frequency = ReminderFrequency.DAILY
            )
        }

        // Encontrar el switch del recordatorio y cambiarlo
        composeTestRule
            .onNodeWithTag("reminder_toggle")
            .performClick()

        // Verificar que el estado cambió
        composeTestRule
            .onNodeWithTag("reminder_toggle")
            .assertIsOff()
    }
} 