package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.journal.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class JournalViewModel : ViewModel() {
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries
    
    private val _journalStats = MutableStateFlow<JournalStats?>(null)
    val journalStats: StateFlow<JournalStats?> = _journalStats
    
    private val _moodSuggestions = MutableStateFlow<MoodSuggestion?>(null)
    val moodSuggestions: StateFlow<MoodSuggestion?> = _moodSuggestions

    private val _selectedEntry = MutableStateFlow<JournalEntry?>(null)
    val selectedEntry: StateFlow<JournalEntry?> = _selectedEntry

    init {
        loadEntries()
        loadStats()
    }

    fun addEntry(
        mood: Mood,
        content: String,
        activities: List<Activity>,
        emotions: List<Emotion>,
        gratitude: List<String>? = null,
        tags: List<String>? = null,
        imageUrls: List<String>? = null
    ) {
        val newEntry = JournalEntry(
            date = Date(),
            mood = mood,
            content = content,
            activities = activities,
            emotions = emotions,
            gratitude = gratitude,
            tags = tags,
            imageUrls = imageUrls
        )

        viewModelScope.launch {
            // Aquí se implementará la persistencia en Room
            _journalEntries.value = _journalEntries.value + newEntry
            updateStats()
            generateSuggestions(mood)
        }
    }

    fun selectEntry(entry: JournalEntry) {
        _selectedEntry.value = entry
    }

    fun clearSelectedEntry() {
        _selectedEntry.value = null
    }

    private fun loadEntries() {
        // Aquí se implementará la carga desde Room
        // Por ahora, datos de ejemplo
        _journalEntries.value = generateSampleEntries()
    }

    private fun loadStats() {
        // Aquí se implementará el cálculo real de estadísticas
        _journalStats.value = JournalStats(
            totalEntries = 30,
            averageMood = 3.8f,
            moodTrend = listOf(3.5f, 3.7f, 3.8f, 4.0f, 3.9f, 3.8f, 4.1f),
            commonEmotions = listOf("Alegría", "Gratitud", "Calma"),
            commonActivities = listOf("Ejercicio", "Lectura", "Meditación"),
            streakDays = 7
        )
    }

    private fun updateStats() {
        // Aquí se implementará la actualización de estadísticas
        loadStats()
    }

    private fun generateSuggestions(mood: Mood) {
        // Aquí se implementará la generación de sugerencias basada en el estado de ánimo
        _moodSuggestions.value = MoodSuggestion(
            activities = listOf(
                Activity("Meditación", ActivityType.SELF_CARE, 2),
                Activity("Caminata", ActivityType.EXERCISE, 1),
                Activity("Escribir", ActivityType.HOBBY, 1)
            ),
            recommendations = listOf(
                "Toma un momento para respirar profundamente",
                "Considera hacer ejercicio suave",
                "Contacta a un amigo o familiar"
            ),
            resources = listOf(
                "Guía de meditación",
                "Ejercicios de respiración",
                "Contactos de apoyo"
            )
        )
    }

    private fun generateSampleEntries(): List<JournalEntry> {
        return listOf(
            JournalEntry(
                date = Date(),
                mood = Mood(4, "Feliz", "😊"),
                content = "Hoy fue un día productivo y positivo",
                activities = listOf(
                    Activity("Ejercicio matutino", ActivityType.EXERCISE, 2),
                    Activity("Proyecto completado", ActivityType.WORK, 1)
                ),
                emotions = listOf(
                    Emotion("Alegría", 4, EmotionCategory.JOY),
                    Emotion("Satisfacción", 4, EmotionCategory.JOY)
                ),
                gratitude = listOf(
                    "Familia",
                    "Salud",
                    "Oportunidades"
                ),
                tags = listOf("trabajo", "ejercicio", "logros"),
                imageUrls = null
            )
            // Más entradas de ejemplo...
        )
    }
} 