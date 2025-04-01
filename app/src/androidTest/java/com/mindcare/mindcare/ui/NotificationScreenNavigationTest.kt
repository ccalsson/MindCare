package com.mindcare.mindcare.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mindcare.mindcare.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NotificationScreenNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testNavigationToNotificationScreen() {
        // Navegar a la pantalla de notificaciones
        composeTestRule
            .onNodeWithContentDescription("Notificaciones")
            .performClick()

        // Verificar que estamos en la pantalla correcta
        composeTestRule
            .onNodeWithText("Notificaciones")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testDialogNavigation() {
        // Navegar a notificaciones
        composeTestRule
            .onNodeWithContentDescription("Notificaciones")
            .performClick()

        // Abrir preferencias
        composeTestRule
            .onNodeWithContentDescription("Preferencias")
            .performClick()

        // Verificar que el diálogo está abierto
        composeTestRule
            .onNodeWithText("Preferencias de notificaciones")
            .assertExists()
            .assertIsDisplayed()

        // Cerrar diálogo
        composeTestRule
            .onNodeWithText("Cancelar")
            .performClick()

        // Verificar que volvimos a la pantalla principal
        composeTestRule
            .onNodeWithText("Preferencias de notificaciones")
            .assertDoesNotExist()
    }

    @Test
    fun testBackNavigation() {
        // Navegar a notificaciones
        composeTestRule
            .onNodeWithContentDescription("Notificaciones")
            .performClick()

        // Presionar botón atrás
        composeTestRule
            .onNodeWithContentDescription("Volver")
            .performClick()

        // Verificar que salimos de la pantalla de notificaciones
        composeTestRule
            .onNodeWithText("Notificaciones")
            .assertDoesNotExist()
    }
} 