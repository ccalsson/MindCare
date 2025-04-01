@HiltAndroidTest
class JournalFlowTest : BaseInstrumentedTest() {
    @Inject
    lateinit var journalRepository: JournalRepository

    @Test
    fun createJournalEntry_savesSuccessfully() {
        val initialState = AppState(isAuthenticated = true)
        
        launchActivity(initialState) {
            // Navigate to journal
            onView(withId(R.id.journalTab)).perform(click())
            
            // Create new entry
            onView(withId(R.id.newEntryButton)).perform(click())
            
            // Fill entry
            onView(withId(R.id.entryContent))
                .perform(typeText("Today was a good day"), closeSoftKeyboard())
            
            // Select mood
            onView(withId(R.id.moodSelector))
                .perform(click())
            onView(withText("Happy"))
                .perform(click())
            
            // Save entry
            onView(withId(R.id.saveButton)).perform(click())
            
            // Verify entry saved
            val entries = journalRepository.getEntries()
            assertThat(entries).hasSize(1)
            assertThat(entries[0].content).isEqualTo("Today was a good day")
            assertThat(entries[0].mood).isEqualTo("HAPPY")
        }
    }
} 