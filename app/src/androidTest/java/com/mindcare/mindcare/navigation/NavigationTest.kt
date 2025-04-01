@HiltAndroidTest
class NavigationTest : BaseComposeTest() {
    @Test
    fun navigation_startsAtWelcomeScreen() {
        // Given
        launchComposable {
            AppNavigation()
        }

        // Then
        composeRule.onNodeWithTag("welcome_screen").assertIsDisplayed()
    }

    @Test
    fun navigation_navigatesToLoginFromWelcome() {
        // Given
        launchComposable {
            AppNavigation()
        }

        // When
        composeRule.onNodeWithText("Login").performClick()

        // Then
        composeRule.onNodeWithTag("login_screen").assertIsDisplayed()
    }

    @Test
    fun navigation_navigatesToDashboardAfterLogin() {
        // Given
        launchComposable {
            AppNavigation(startDestination = Screen.Login.route)
        }

        // When - Simulate successful login
        composeRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeRule.onNodeWithText("Password").performTextInput("password123")
        composeRule.onNodeWithText("Sign In").performClick()

        // Then
        composeRule.onNodeWithTag("dashboard_screen").assertIsDisplayed()
    }
} 