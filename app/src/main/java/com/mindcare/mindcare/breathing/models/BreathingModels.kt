package com.mindcare.mindcare.breathing.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class BreathingPhase {
    INHALE,
    HOLD,
    EXHALE,
    REST
}

@Entity(tableName = "breathing_sessions")
data class BreathingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val startTime: Date,
    val duration: Int, // en segundos
    val completed: Boolean,
    val stressReduction: Float?, // 0.0 a 1.0
    val notes: String?
)

data class BreathingExercise(
    val id: Long,
    val name: String,
    val description: String,
    val category: BreathingCategory,
    val pattern: BreathingPattern,
    val duration: Int, // en segundos
    val difficulty: BreathingDifficulty,
    val benefits: List<String>,
    val imageUrl: String?
)

data class BreathingPattern(
    val inhale: Int, // duración en segundos
    val holdInhale: Int,
    val exhale: Int,
    val holdExhale: Int,
    val cycles: Int
)

enum class BreathingCategory {
    RELAXATION,
    ENERGY,
    FOCUS,
    SLEEP,
    STRESS_RELIEF,
    ANXIETY_RELIEF
}

enum class BreathingDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

data class BreathingProgress(
    val totalSessions: Int,
    val totalMinutes: Int,
    val averageStressReduction: Float,
    val favoriteExercise: String?,
    val lastSession: Date?,
    val weeklyStats: List<Int> // sesiones por día de la semana
) 