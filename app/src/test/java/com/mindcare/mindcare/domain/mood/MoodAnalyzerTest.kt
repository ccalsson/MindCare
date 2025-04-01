class MoodAnalyzerTest {
    private lateinit var moodAnalyzer: MoodAnalyzer
    private lateinit var nlpProcessor: NLPProcessor
    
    @Before
    fun setup() {
        nlpProcessor = mockk()
        moodAnalyzer = MoodAnalyzer(nlpProcessor)
    }

    @Test
    fun `analyzeJournalEntry detects correct sentiment`() {
        // Given
        val entry = "Today was a wonderful day, I feel very happy and energetic"
        coEvery { nlpProcessor.analyzeSentiment(entry) } returns 0.8f

        // When
        val result = moodAnalyzer.analyzeJournalEntry(entry)

        // Then
        assertThat(result.sentiment).isGreaterThan(0.7f)
        assertThat(result.detectedMood).isEqualTo(Mood.HAPPY)
    }

    @Test
    fun `detectMoodTrends identifies patterns`() {
        // Given
        val moodHistory = listOf(
            MoodEntry(Mood.SAD, timestamp = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000),
            MoodEntry(Mood.SAD, timestamp = System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000),
            MoodEntry(Mood.NEUTRAL, timestamp = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),
            MoodEntry(Mood.HAPPY, timestamp = System.currentTimeMillis())
        )

        // When
        val trend = moodAnalyzer.detectMoodTrends(moodHistory)

        // Then
        assertThat(trend.direction).isEqualTo(TrendDirection.IMPROVING)
        assertThat(trend.significance).isGreaterThan(0.5f)
    }
} 