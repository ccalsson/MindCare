class MoodSelectorTest : BaseComposeTest() {
    @Test
    fun moodSelector_displaysAllMoods() {
        // Given
        launchComposable {
            MoodSelector(
                selectedMood = null,
                onMoodSelected = {}
            )
        }

        // Then
        Mood.values().forEach { mood ->
            composeRule.onNodeWithContentDescription(mood.description)
                .assertIsDisplayed()
                .assertHasNoClickAction()
        }
    }

    @Test
    fun moodSelector_updatesSelectionOnClick() {
        // Given
        var selectedMood: Mood? = null
        launchComposable {
            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )
        }

        // When
        composeRule.onNodeWithContentDescription(Mood.HAPPY.description)
            .performClick()

        // Then
        assertThat(selectedMood).isEqualTo(Mood.HAPPY)
        composeRule.onNodeWithContentDescription(Mood.HAPPY.description)
            .assertIsSelected()
    }
} 