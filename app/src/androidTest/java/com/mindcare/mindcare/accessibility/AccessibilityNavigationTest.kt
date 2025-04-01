@HiltAndroidTest
class AccessibilityNavigationTest : BaseComposeTest() {
    @Test
    fun keyboard_canNavigateAllElements() {
        launchActivity {
            // Verificar navegación con teclado
            onView(isRoot()).perform(pressTab())
            
            // Verificar que todos los elementos son alcanzables
            assertAllElementsAreKeyboardAccessible()
        }
    }

    @Test
    fun gestureNavigation_hasAlternatives() {
        launchActivity {
            // Verificar alternativas a gestos
            onView(withId(R.id.meditationProgress))
                .check(matches(hasAlternativeToGesture()))

            onView(withId(R.id.moodSelector))
                .check(matches(hasAlternativeToGesture()))
        }
    }

    @Test
    fun screenReader_announcesStateChanges() {
        launchActivity {
            // Iniciar meditación
            onView(withId(R.id.startButton)).perform(click())

            // Verificar anuncios de cambios de estado
            assertThat(getLastAnnouncement())
                .contains("Meditation started")

            // Pausar meditación
            onView(withId(R.id.pauseButton)).perform(click())

            assertThat(getLastAnnouncement())
                .contains("Meditation paused")
        }
    }

    private fun assertAllElementsAreKeyboardAccessible() {
        var foundElements = 0
        while (pressTab()) {
            val focused = findFocusedElement()
            assertThat(focused).isNotNull()
            foundElements++
        }
        assertThat(foundElements).isGreaterThan(0)
    }
} 