class RecommendationEngineTest {
    private lateinit var recommendationEngine: RecommendationEngine
    private lateinit var userPreferences: UserPreferences
    private lateinit var activityHistory: ActivityHistory
    
    @Before
    fun setup() {
        userPreferences = mockk()
        activityHistory = mockk()
        recommendationEngine = RecommendationEngine(userPreferences, activityHistory)
    }

    @Test
    fun `generateDailyRecommendations considers user preferences`() = runTest {
        // Given
        val preferences = UserPreferences(
            preferredMeditationTime = TimeOfDay.MORNING,
            meditationDuration = 15,
            focusAreas = listOf(FocusArea.STRESS, FocusArea.SLEEP)
        )
        coEvery { userPreferences.getPreferences() } returns preferences

        // When
        val recommendations = recommendationEngine.generateDailyRecommendations()

        // Then
        assertThat(recommendations.timeOfDay).isEqualTo(TimeOfDay.MORNING)
        assertThat(recommendations.activities).anyMatch { 
            it.type == ActivityType.MEDITATION && it.duration <= 15
        }
    }

    @Test
    fun `recommendations adapt to user progress`() = runTest {
        // Given
        val history = ActivityHistory(
            completedActivities = listOf(
                CompletedActivity(ActivityType.MEDITATION, duration = 5, date = Date()),
                CompletedActivity(ActivityType.MEDITATION, duration = 10, date = Date())
            )
        )
        coEvery { activityHistory.getRecentActivities() } returns history

        // When
        val recommendations = recommendationEngine.generateProgressiveRecommendations()

        // Then
        assertThat(recommendations.activities.first().duration)
            .isGreaterThan(10)
            .isLessThan(20)
    }
} 