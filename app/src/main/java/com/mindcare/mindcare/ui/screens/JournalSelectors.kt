package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindcare.mindcare.journal.models.*

@Composable
fun ActivitySelector(
    selectedActivities: List<Activity>,
    onActivitySelected: (Activity) -> Unit
) {
    val activities = remember {
        ActivityType.values().flatMap { type ->
            when (type) {
                ActivityType.EXERCISE -> listOf(
                    Activity("Caminar", type, 1),
                    Activity("Correr", type, 2),
                    Activity("Yoga", type, 2)
                )
                ActivityType.SOCIAL -> listOf(
                    Activity("Familia", type, 1),
                    Activity("Amigos", type, 1),
                    Activity("Cita", type, 2)
                )
                ActivityType.WORK -> listOf(
                    Activity("Trabajo", type, 0),
                    Activity("Estudio", type, 1),
                    Activity("Proyecto", type, 1)
                )
                ActivityType.HOBBY -> listOf(
                    Activity("Lectura", type, 1),
                    Activity("Música", type, 1),
                    Activity("Arte", type, 2)
                )
                ActivityType.SELF_CARE -> listOf(
                    Activity("Meditación", type, 2),
                    Activity("Descanso", type, 1),
                    Activity("Spa", type, 2)
                )
                else -> listOf(Activity(type.name.lowercase().capitalize(), type, 0))
            }
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(activities) { activity ->
            FilterChip(
                selected = selectedActivities.contains(activity),
                onClick = { onActivitySelected(activity) },
                label = { Text(activity.name) }
            )
        }
    }
}

@Composable
fun EmotionSelector(
    selectedEmotions: List<Emotion>,
    onEmotionSelected: (Emotion) -> Unit
) {
    val emotions = remember {
        EmotionCategory.values().flatMap { category ->
            when (category) {
                EmotionCategory.JOY -> listOf(
                    Emotion("Feliz", 4, category),
                    Emotion("Entusiasmado", 5, category),
                    Emotion("Tranquilo", 3, category)
                )
                EmotionCategory.SADNESS -> listOf(
                    Emotion("Triste", 3, category),
                    Emotion("Melancólico", 2, category),
                    Emotion("Desanimado", 3, category)
                )
                EmotionCategory.ANGER -> listOf(
                    Emotion("Frustrado", 3, category),
                    Emotion("Irritado", 2, category),
                    Emotion("Enojado", 4, category)
                )
                EmotionCategory.FEAR -> listOf(
                    Emotion("Ansioso", 3, category),
                    Emotion("Preocupado", 2, category),
                    Emotion("Inseguro", 2, category)
                )
                else -> listOf(
                    Emotion(category.name.lowercase().capitalize(), 3, category)
                )
            }
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(emotions) { emotion ->
            FilterChip(
                selected = selectedEmotions.contains(emotion),
                onClick = { onEmotionSelected(emotion) },
                label = { Text(emotion.name) }
            )
        }
    }
} 