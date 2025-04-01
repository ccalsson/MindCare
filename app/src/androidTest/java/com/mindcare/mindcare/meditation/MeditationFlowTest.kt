@HiltAndroidTest
class MeditationFlowTest : BaseInstrumentedTest() {
    @Inject
    lateinit var meditationRepository: MeditationRepository

    @Test
    fun completeMeditationSession_updatesProgress() {
        // Setup authenticated state
        val initialState = AppState(isAuthenticated = true)
        
        launchActivity(initialState) {
            // Navigate to meditation
            onView(withId(R.id.meditationTab)).perform(click())
            
            // Select meditation
            onView(withText("Mindfulness Meditation"))
                .perform(click())
            
            // Start session
            onView(withId(R.id.startButton)).perform(click())
            
            // Wait for session completion
            SystemClock.sleep(5000) // 5 seconds meditation
            
            // Complete session
            onView(withId(R.id.completeButton)).perform(click())
            
            // Verify completion screen
            onView(withId(R.id.completionScreen))
                .check(matches(isDisplayed()))
            
            // Verify progress update
            val sessions = meditationRepository.getUserSessions()
            assertThat(sessions).hasSize(1)
            assertThat(sessions[0].completionRate).isEqualTo(1.0f)
        }
    }
} 