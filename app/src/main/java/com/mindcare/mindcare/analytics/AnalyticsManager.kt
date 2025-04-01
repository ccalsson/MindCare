class AnalyticsManager @Inject constructor(
    private val analytics: Analytics
) {
    fun logEvent(event: AnalyticsEvent) {
        analytics.logEvent(event.name, event.params)
    }

    fun logUserProperty(property: String, value: String) {
        analytics.setUserProperty(property, value)
    }

    fun trackScreenView(screen: Screen) {
        analytics.logScreenView(screen.route)
    }

    fun trackMeditationProgress(meditation: Meditation, progress: Float) {
        logEvent(AnalyticsEvent.MeditationCompleted(
            meditationId = meditation.id.toString(),
            duration = meditation.duration,
            completionRate = progress
        ))
    }

    fun trackJournalEntry(entry: JournalEntry) {
        logEvent(AnalyticsEvent.JournalEntryCreated(
            wordCount = entry.content.split(" ").size,
            mood = entry.mood.description
        ))
    }

    fun trackBreathingExercise(exercise: BreathingExercise, cycles: Int) {
        logEvent(AnalyticsEvent.BreathingExerciseCompleted(
            exerciseId = exercise.id.toString(),
            cycles = cycles,
            duration = exercise.duration
        ))
    }
} 