package com.mindcare.mindcare.features.journal

import com.mindcare.mindcare.features.journal.models.JournalEntry
import com.mindcare.mindcare.features.journal.models.JournalInsights
import com.mindcare.mindcare.features.journal.models.JournalPrompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class SmartJournal {
    private val prompts = listOf(
        JournalPrompt(
            "¿Qué te hizo sonreír hoy?",
            "Gratitud",
            listOf("positivo", "reflexión", "diario")
        ),
        JournalPrompt(
            "¿Qué situación te resultó desafiante hoy?",
            "Crecimiento",
            listOf("desafíos", "aprendizaje", "superación")
        ),
        // Más prompts...
    )

    fun analyzeEntry(entry: JournalEntry): Flow<JournalInsights> = flow {
        val sentiment = analyzeSentiment(entry.content)
        val keywords = extractKeywords(entry.content)
        val suggestions = generateSuggestions(entry)

        val insights = JournalInsights(
            sentiment = sentiment,
            keywords = keywords,
            suggestions = suggestions,
            moodTrend = analyzeMoodTrend(entry),
            commonThemes = identifyThemes(entry),
            recommendedActions = generateRecommendations(entry)
        )

        emit(insights)
    }

    private fun analyzeSentiment(text: String): Float {
        // Implementar análisis de sentimientos usando NLP
        // Por ahora, un análisis simple basado en palabras clave
        val positiveWords = setOf("feliz", "alegre", "contento", "emocionado", "agradecido")
        val negativeWords = setOf("triste", "enojado", "frustrado", "ansioso", "preocupado")

        val words = text.toLowerCase().split(" ")
        var sentiment = 0f

        words.forEach { word ->
            when {
                positiveWords.contains(word) -> sentiment += 0.2f
                negativeWords.contains(word) -> sentiment -= 0.2f
            }
        }

        return sentiment.coerceIn(-1f, 1f)
    }

    private fun extractKeywords(text: String): List<String> {
        // Implementar extracción de palabras clave
        // Por ahora, un enfoque simple
        return text.toLowerCase()
            .split(" ")
            .filter { it.length > 4 }
            .distinct()
            .take(5)
    }

    private fun generateSuggestions(entry: JournalEntry): List<String> {
        val sentiment = analyzeSentiment(entry.content)
        return when {
            sentiment < -0.5 -> listOf(
                "Considera hablar con un profesional",
                "Practica ejercicios de respiración",
                "Contacta a un amigo cercano"
            )
            sentiment < 0 -> listOf(
                "Intenta una caminata corta",
                "Escucha música relajante",
                "Escribe tres cosas positivas"
            )
            else -> listOf(
                "¡Sigue así!",
                "Comparte tu experiencia positiva",
                "Establece una nueva meta"
            )
        }
    }

    private fun analyzeMoodTrend(entry: JournalEntry): String {
        // Implementar análisis de tendencias
        return "estable" // Placeholder
    }

    private fun identifyThemes(entry: JournalEntry): List<String> {
        // Implementar identificación de temas recurrentes
        return entry.tags
    }

    private fun generateRecommendations(entry: JournalEntry): List<String> {
        val sentiment = analyzeSentiment(entry.content)
        val timeOfDay = getTimeOfDay()

        return when {
            timeOfDay == "morning" && sentiment < 0 -> listOf(
                "Meditación matutina",
                "Ejercicio suave",
                "Desayuno nutritivo"
            )
            timeOfDay == "evening" && sentiment < 0 -> listOf(
                "Rutina de relajación",
                "Baño caliente",
                "Lectura ligera"
            )
            else -> listOf(
                "Continúa con tus actividades habituales",
                "Mantén un registro de gratitud",
                "Comparte tiempo con seres queridos"
            )
        }
    }

    private fun getTimeOfDay(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "morning"
            in 12..17 -> "afternoon"
            in 18..21 -> "evening"
            else -> "night"
        }
    }

    fun getPromptForMood(mood: String): JournalPrompt {
        return prompts.random() // Por ahora, aleatorio
    }
} 