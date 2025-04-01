package com.mindcare.mindcare.gamification

import com.mindcare.mindcare.gamification.models.*
import java.util.*
import kotlin.math.min

class TherapeuticQuests {
    fun generateDailyQuests(userProfile: UserProgress): List<Quest> {
        val quests = mutableListOf<Quest>()
        val currentTime = Calendar.getInstance()

        // Quest de Mindfulness diario
        quests.add(createMindfulnessQuest(userProfile.level))

        // Quest basado en el día de la semana
        when (currentTime.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> quests.add(createMotivationMonday())
            Calendar.WEDNESDAY -> quests.add(createWellnessWednesday())
            Calendar.FRIDAY -> quests.add(createFitnessFriday())
            Calendar.SUNDAY -> quests.add(createSelfCareSunday())
        }

        // Quest personalizado basado en el nivel del usuario
        quests.add(createLevelBasedQuest(userProfile.level))

        return quests
    }

    private fun createMindfulnessQuest(userLevel: Int): Quest {
        val duration = min(5 + userLevel, 20) // Aumenta con el nivel, máximo 20 minutos

        return Quest(
            title = "Momento Mindful",
            description = "Toma un momento para centrarte y respirar",
            activities = listOf(
                Activity(
                    questId = 0,
                    title = "Meditación Guiada",
                    description = "Realiza una meditación de $duration minutos",
                    type = ActivityType.MEDITATION,
                    duration = duration
                )
            ),
            rewards = listOf(
                Reward(
                    type = RewardType.EXPERIENCE,
                    value = 50,
                    description = "50 XP",
                    icon = "xp_icon"
                )
            ),
            difficulty = QuestDifficulty.EASY,
            category = QuestCategory.MINDFULNESS,
            deadline = getEndOfDay()
        )
    }

    private fun createMotivationMonday(): Quest {
        return Quest(
            title = "Lunes Motivador",
            description = "¡Comienza la semana con energía!",
            activities = listOf(
                Activity(
                    questId = 0,
                    title = "Establecer Metas",
                    description = "Escribe 3 metas para esta semana",
                    type = ActivityType.JOURNALING,
                    duration = 15
                ),
                Activity(
                    questId = 0,
                    title = "Ejercicio Matutino",
                    description = "10 minutos de ejercicios suaves",
                    type = ActivityType.EXERCISE,
                    duration = 10
                )
            ),
            rewards = listOf(
                Reward(
                    type = RewardType.EXPERIENCE,
                    value = 100,
                    description = "100 XP",
                    icon = "xp_icon"
                ),
                Reward(
                    type = RewardType.BADGE,
                    value = 1,
                    description = "Insignia de Motivación",
                    icon = "motivation_badge"
                )
            ),
            difficulty = QuestDifficulty.MEDIUM,
            category = QuestCategory.SELF_CARE,
            deadline = getEndOfDay()
        )
    }

    private fun createWellnessWednesday(): Quest {
        // Implementar quest de bienestar
        return Quest(
            title = "Miércoles de Bienestar",
            description = "Dedica tiempo a tu bienestar",
            activities = listOf(
                Activity(
                    questId = 0,
                    title = "Ejercicio de Respiración",
                    description = "5 minutos de respiración profunda",
                    type = ActivityType.BREATHING_EXERCISE,
                    duration = 5
                ),
                Activity(
                    questId = 0,
                    title = "Diario de Gratitud",
                    description = "Escribe 3 cosas por las que estás agradecido",
                    type = ActivityType.JOURNALING,
                    duration = 10
                )
            ),
            rewards = listOf(
                Reward(
                    type = RewardType.EXPERIENCE,
                    value = 75,
                    description = "75 XP",
                    icon = "xp_icon"
                )
            ),
            difficulty = QuestDifficulty.EASY,
            category = QuestCategory.SELF_CARE,
            deadline = getEndOfDay()
        )
    }

    private fun createFitnessFriday(): Quest {
        // Implementar quest de ejercicio
        return Quest(
            title = "Viernes Activo",
            description = "¡Mueve tu cuerpo!",
            activities = listOf(
                Activity(
                    questId = 0,
                    title = "Ejercicio Suave",
                    description = "20 minutos de actividad física",
                    type = ActivityType.EXERCISE,
                    duration = 20
                )
            ),
            rewards = listOf(
                Reward(
                    type = RewardType.EXPERIENCE,
                    value = 100,
                    description = "100 XP",
                    icon = "xp_icon"
                )
            ),
            difficulty = QuestDifficulty.MEDIUM,
            category = QuestCategory.EXERCISE,
            deadline = getEndOfDay()
        )
    }

    private fun createSelfCareSunday(): Quest {
        // Implementar quest de autocuidado
        return Quest(
            title = "Domingo de Autocuidado",
            description = "Dedica tiempo a ti mismo",
            activities = listOf(
                Activity(
                    questId = 0,
                    title = "Rutina de Relajación",
                    description = "Realiza una actividad relajante",
                    type = ActivityType.CREATIVE_EXPRESSION,
                    duration = 30
                )
            ),
            rewards = listOf(
                Reward(
                    type = RewardType.EXPERIENCE,
                    value = 50,
                    description = "50 XP",
                    icon = "xp_icon"
                )
            ),
            difficulty = QuestDifficulty.EASY,
            category = QuestCategory.SELF_CARE,
            deadline = getEndOfDay()
        )
    }

    private fun createLevelBasedQuest(level: Int): Quest {
        // Implementar quest basado en nivel
        val difficulty = when {
            level < 5 -> QuestDifficulty.EASY
            level < 10 -> QuestDifficulty.MEDIUM
            else -> QuestDifficulty.HARD
        }

        return Quest(
            title = "Desafío Nivel $level",
            description = "Un desafío adaptado a tu nivel",
            activities = generateLevelBasedActivities(level),
            rewards = listOf(
                Reward(
                    type = RewardType.EXPERIENCE,
                    value = 50 * level,
                    description = "${50 * level} XP",
                    icon = "xp_icon"
                )
            ),
            difficulty = difficulty,
            category = QuestCategory.LEARNING,
            deadline = getEndOfDay()
        )
    }

    private fun generateLevelBasedActivities(level: Int): List<Activity> {
        // Implementar generación de actividades basadas en nivel
        return listOf(
            Activity(
                questId = 0,
                title = "Actividad Personalizada",
                description = "Actividad adaptada al nivel $level",
                type = ActivityType.MEDITATION,
                duration = 5 + level
            )
        )
    }

    private fun getEndOfDay(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.time
    }
} 