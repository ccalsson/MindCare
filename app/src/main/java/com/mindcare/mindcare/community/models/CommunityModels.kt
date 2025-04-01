package com.mindcare.mindcare.community.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val mood: String?,
    val imageUrl: String?,
    val tags: List<String>,
    val timestamp: Date,
    val likes: Int = 0,
    val comments: Int = 0,
    val isAnonymous: Boolean = false
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val timestamp: Date,
    val likes: Int = 0,
    val isAnonymous: Boolean = false
)

data class CommunityUser(
    val id: String,
    val name: String,
    val avatar: String?,
    val bio: String?,
    val joinDate: Date,
    val badges: List<Badge>,
    val stats: UserStats
)

data class Badge(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val category: BadgeCategory
)

enum class BadgeCategory {
    ENGAGEMENT,
    SUPPORT,
    ACHIEVEMENT,
    MILESTONE
}

data class UserStats(
    val postsCount: Int,
    val commentsCount: Int,
    val likesGiven: Int,
    val likesReceived: Int,
    val supportScore: Float // 0.0 a 1.0
)

data class SupportGroup(
    val id: String,
    val name: String,
    val description: String,
    val category: GroupCategory,
    val members: Int,
    val posts: Int,
    val isPrivate: Boolean,
    val moderators: List<String>
)

enum class GroupCategory {
    ANXIETY,
    DEPRESSION,
    STRESS,
    MINDFULNESS,
    GENERAL_SUPPORT,
    RECOVERY,
    SELF_IMPROVEMENT
} 