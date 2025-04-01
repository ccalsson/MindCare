@HiltAndroidTest
class AuthenticationFlowTest : BaseInstrumentedTest() {
    @Inject
    lateinit var authManager: AuthManager

    @Test
    fun completeLoginFlow_navigatesToDashboard() {
        launchActivity {
            // Start from welcome screen
            onView(withId(R.id.loginButton)).perform(click())

            // Fill login form
            onView(withId(R.id.emailInput))
                .perform(typeText("test@example.com"), closeSoftKeyboard())
            onView(withId(R.id.passwordInput))
                .perform(typeText("password123"), closeSoftKeyboard())
            onView(withId(R.id.signInButton)).perform(click())

            // Verify navigation to dashboard
            onView(withId(R.id.dashboardScreen))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun invalidCredentials_showsError() {
        launchActivity {
            onView(withId(R.id.loginButton)).perform(click())
            onView(withId(R.id.emailInput))
                .perform(typeText("invalid@email.com"), closeSoftKeyboard())
            onView(withId(R.id.passwordInput))
                .perform(typeText("wrong"), closeSoftKeyboard())
            onView(withId(R.id.signInButton)).perform(click())

            // Verify error message
            onView(withText(containsString("Invalid credentials")))
                .check(matches(isDisplayed()))
        }
    }
} 