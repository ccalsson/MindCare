@HiltAndroidTest
class AccessibilityBasicTest : BaseComposeTest() {
    @Test
    fun allScreens_haveContentDescriptions() {
        launchActivity {
            // Verificar pantalla principal
            onView(withId(R.id.mainScreen))
                .check(AccessibilityChecks.accessibilityScore(90.0))

            // Verificar navegación
            listOf(
                R.id.navigation_home,
                R.id.navigation_meditation,
                R.id.navigation_journal,
                R.id.navigation_profile
            ).forEach { navigationId ->
                onView(withId(navigationId))
                    .check(matches(hasContentDescription()))
            }
        }
    }

    @Test
    fun meditation_isAccessibleWithTalkback() {
        launchActivity {
            // Navegar a meditación
            onView(withId(R.id.navigation_meditation)).perform(click())

            // Verificar elementos de meditación
            onView(withId(R.id.meditationTitle))
                .check(matches(isTalkBackFocusable()))
                .check(matches(withContentDescription(containsString("Meditation"))))

            onView(withId(R.id.startButton))
                .check(matches(isTalkBackFocusable()))
                .check(matches(withContentDescription(containsString("Start meditation"))))
        }
    }

    @Test
    fun journal_supportsTextScaling() {
        // Configurar escala de texto grande
        setTextScale(1.5f)

        launchActivity {
            // Verificar que los textos se ajustan correctamente
            onView(withId(R.id.journalEntry))
                .check(matches(isCompletelyDisplayed()))
                .check(matches(not(isTextTruncated())))
        }
    }
} 