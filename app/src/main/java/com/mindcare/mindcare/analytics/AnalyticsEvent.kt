sealed class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap()
) {
    data class SessionStart(val sessionId: String) : AnalyticsEvent(
        name = "session_start",
        params = mapOf("session_id" to sessionId)
    )

    data class MeditationCompleted(
        val meditationId: String,
        val duration: Int,
        val completionRate: Float
    ) : AnalyticsEvent(
        name = "meditation_completed",
        params = mapOf(
            "meditation_id" to meditationId,
            "duration" to duration,
            "completion_rate" to completionRate
        )
    )

    data class JournalEntryCreated(
        val wordCount: Int,
        val mood: String
    ) : AnalyticsEvent(
        name = "journal_entry_created",
        params = mapOf(
            "word_count" to wordCount,
            "mood" to mood
        )
    )

    data class BreathingExerciseCompleted(
        val exerciseId: String,
        val cycles: Int,
        val duration: Int
    ) : AnalyticsEvent(
        name = "breathing_exercise_completed",
        params = mapOf(
            "exercise_id" to exerciseId,
            "cycles" to cycles,
            "duration" to duration
        )
    )
} 