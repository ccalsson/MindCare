package com.mindcare.mindcare.meditation.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "meditation_sessions")
data class MeditationSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val meditationId: Long,
    val startTime: Date,
    val duration: Int, // en minutos
    val completed: Boolean,
    val focusScore: Float?, // 0.0 a 1.0
    val notes: String?
)

data class Meditation(
    val id: Long,
    val title: String,
    val description: String,
    val category: MeditationCategory,
    val duration: Int, // en minutos
    val audioUrl: String,
    val imageUrl: String,
    val difficulty: MeditationDifficulty,
    val tags: List<String>,
    val guidedSteps: List<GuidedStep>
)

data class GuidedStep(
    val timestamp: Int, // segundos desde el inicio
    val instruction: String,
    val duration: Int // duración de la instrucción en segundos
)

enum class MeditationCategory {
    MINDFULNESS,
    BREATHING,
    BODY_SCAN,
    LOVING_KINDNESS,
    STRESS_RELIEF,
    SLEEP,
    FOCUS,
    ANXIETY_RELIEF
}

enum class MeditationDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

data class MeditationProgress(
    val totalSessions: Int,
    val totalMinutes: Int,
    val averageFocusScore: Float,
    val longestStreak: Int,
    val currentStreak: Int,
    val favoriteCategory: MeditationCategory?,
    val lastSession: Date?
) 