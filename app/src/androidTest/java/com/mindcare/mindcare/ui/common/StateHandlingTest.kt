class StateHandlingTest : BaseComposeTest() {
    @Test
    fun errorState_showsRetryButton() {
        // Given
        val error = "Network error"
        launchComposable {
            ErrorState(
                error = error,
                onRetry = {}
            )
        }

        // Then
        composeRule.onNodeWithText(error).assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun loadingState_showsProgressIndicator() {
        // Given
        launchComposable {
            LoadingState()
        }

        // Then
        composeRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsMessage() {
        // Given
        val message = "No items found"
        launchComposable {
            EmptyState(message = message)
        }

        // Then
        composeRule.onNodeWithText(message).assertIsDisplayed()
    }
} 