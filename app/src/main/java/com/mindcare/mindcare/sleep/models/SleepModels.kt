package com.mindcare.mindcare.sleep.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sleep_records")
data class SleepRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Date,
    val endTime: Date,
    val quality: Float, // 0.0 a 1.0
    val deepSleepMinutes: Int,
    val lightSleepMinutes: Int,
    val remSleepMinutes: Int,
    val interruptions: Int,
    val heartRate: List<Float>,
    val notes: String?
)

data class SleepRecommendations(
    val suggestedBedtime: Date,
    val suggestedWakeTime: Date,
    val optimalSleepDuration: Int, // en minutos
    val recommendations: List<String>,
    val sleepScore: Int // 0 a 100
)

data class SleepStats(
    val averageQuality: Float,
    val averageDuration: Int,
    val consistencyScore: Float,
    val weeklyTrend: List<Float>,
    val bestSleepTime: Date,
    val worstSleepTime: Date
)

enum class SleepPhase {
    LIGHT,
    DEEP,
    REM,
    AWAKE
} 