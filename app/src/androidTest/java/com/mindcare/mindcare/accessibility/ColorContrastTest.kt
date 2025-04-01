class ColorContrastTest : BaseComposeTest() {
    @Test
    fun allScreens_meetContrastRequirements() {
        val screens = listOf(
            Screen.Home,
            Screen.Meditation,
            Screen.Journal,
            Screen.Profile
        )

        screens.forEach { screen ->
            launchComposable {
                AppScreen(screen = screen)
            }

            // Verificar ratio de contraste
            composeRule.onAllNodes(hasAnyAncestor(isRoot()))
                .assertAll(hasMinimumContrastRatio(4.5f))
        }
    }

    @Test
    fun colorBlindMode_isAccessible() {
        // Simular modo daltónico
        simulateColorBlindness(ColorBlindnessType.PROTANOPIA)

        launchComposable {
            MoodSelector(
                selectedMood = null,
                onMoodSelected = {}
            )
        }

        // Verificar que los elementos son distinguibles
        composeRule.onAllNodes(hasTestTag("mood_icon"))
            .assertAll(isDistinguishableInColorBlindMode())
    }

    @Test
    fun darkMode_maintainsReadability() {
        // Configurar modo oscuro
        setDarkMode(true)

        launchComposable {
            JournalEntry(
                content = "Test content",
                mood = Mood.HAPPY
            )
        }

        // Verificar legibilidad en modo oscuro
        composeRule.onNode(hasTestTag("journal_text"))
            .assertTextContrast(minContrastRatio = 7.0f)
    }
} 