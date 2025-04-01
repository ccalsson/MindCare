package com.mindcare.mindcare.gamification.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val activities: List<Activity>,
    val rewards: List<Reward>,
    val difficulty: QuestDifficulty,
    val category: QuestCategory,
    val deadline: Date?,
    val isCompleted: Boolean = false
)

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questId: Long,
    val title: String,
    val description: String,
    val type: ActivityType,
    val duration: Int, // en minutos
    val isCompleted: Boolean = false,
    val completedAt: Date? = null
)

data class Reward(
    val type: RewardType,
    val value: Int,
    val description: String,
    val icon: String
)

enum class QuestDifficulty {
    EASY, MEDIUM, HARD
}

enum class QuestCategory {
    MINDFULNESS,
    EXERCISE,
    SOCIAL,
    CREATIVITY,
    LEARNING,
    SELF_CARE
}

enum class ActivityType {
    MEDITATION,
    EXERCISE,
    JOURNALING,
    SOCIAL_INTERACTION,
    CREATIVE_EXPRESSION,
    BREATHING_EXERCISE
}

enum class RewardType {
    EXPERIENCE,
    BADGE,
    ACHIEVEMENT,
    UNLOCK_FEATURE
}

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val userId: String,
    val level: Int,
    val experience: Int,
    val badges: List<String>,
    val achievements: List<String>,
    val streakDays: Int,
    val lastActivityDate: Date
) 