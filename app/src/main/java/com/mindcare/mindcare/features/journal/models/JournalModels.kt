package com.mindcare.mindcare.features.journal.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val mood: String,
    val timestamp: Date,
    val tags: List<String>,
    val sentiment: Float, // -1.0 to 1.0
    val isPrivate: Boolean = true
)

data class JournalInsights(
    val sentiment: Float,
    val keywords: List<String>,
    val suggestions: List<String>,
    val moodTrend: String,
    val commonThemes: List<String>,
    val recommendedActions: List<String>
)

data class JournalPrompt(
    val question: String,
    val category: String,
    val tags: List<String>
) 