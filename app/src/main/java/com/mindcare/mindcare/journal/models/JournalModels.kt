package com.mindcare.mindcare.journal.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Date,
    val mood: Mood,
    val content: String,
    val activities: List<Activity>,
    val emotions: List<Emotion>,
    val gratitude: List<String>?,
    val tags: List<String>?,
    val imageUrls: List<String>?
)

data class Mood(
    val level: Int, // 1-5
    val description: String,
    val icon: String
)

data class Activity(
    val name: String,
    val type: ActivityType,
    val impact: Int // -2 to 2
)

data class Emotion(
    val name: String,
    val intensity: Int, // 1-5
    val category: EmotionCategory
)

enum class ActivityType {
    EXERCISE,
    SOCIAL,
    WORK,
    HOBBY,
    SELF_CARE,
    SLEEP,
    NUTRITION,
    OTHER
}

enum class EmotionCategory {
    JOY,
    SADNESS,
    ANGER,
    FEAR,
    SURPRISE,
    DISGUST,
    NEUTRAL
}

data class JournalStats(
    val totalEntries: Int,
    val averageMood: Float,
    val moodTrend: List<Float>,
    val commonEmotions: List<String>,
    val commonActivities: List<String>,
    val streakDays: Int
)

data class MoodSuggestion(
    val activities: List<Activity>,
    val recommendations: List<String>,
    val resources: List<String>
) 