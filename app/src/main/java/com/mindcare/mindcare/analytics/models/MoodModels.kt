package com.mindcare.mindcare.analytics.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mood: String,
    val intensity: Float,
    val timestamp: Date,
    val notes: String?,
    val activities: List<String>,
    val triggers: List<String>?
)

data class MoodPrediction(
    val predictedMood: String,
    val confidence: Float,
    val suggestedActions: List<String>,
    val triggers: List<String>?,
    val timeOfDay: String
) 