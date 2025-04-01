class TimingAccessibilityTest : BaseComposeTest() {
    @Test
    fun timeouts_canBeExtended() {
        launchActivity {
            // Configurar tiempo extendido
            setAccessibilityTimeout(AccessibilityTimeout.EXTENDED)

            // Verificar que las alertas permanecen visibles
            showAlert("Test Message")
            SystemClock.sleep(5000) // Esperar 5 segundos
            onView(withId(R.id.alert))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun animations_canBeDisabled() {
        // Configurar preferencias sin animaciones
        setAnimationsEnabled(false)

        launchActivity {
            // Verificar transiciones sin animaciones
            onView(withId(R.id.navigation_meditation)).perform(click())
            
            // No debería haber demora en la transición
            onView(withId(R.id.meditationScreen))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun progressIndicators_haveTextAlternatives() {
        launchActivity {
            // Verificar indicadores de progreso
            onView(withId(R.id.meditationProgress))
                .check(matches(hasTextAlternative()))
                .check { view, _ ->
                    val progress = view.contentDescription.toString()
                    assertThat(progress).contains("%")
                }
        }
    }
} 