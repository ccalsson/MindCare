package com.mindcare.mindcare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.meditation.MeditationPlayer
import com.mindcare.mindcare.meditation.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class MeditationViewModel(application: Application) : AndroidViewModel(application) {
    private val meditationPlayer = MeditationPlayer(application)
    
    private val _currentMeditation = MutableStateFlow<Meditation?>(null)
    val currentMeditation: StateFlow<Meditation?> = _currentMeditation
    
    private val _meditationProgress = MutableStateFlow<MeditationProgress?>(null)
    val meditationProgress: StateFlow<MeditationProgress?> = _meditationProgress
    
    private val _availableMeditations = MutableStateFlow<List<Meditation>>(emptyList())
    val availableMeditations: StateFlow<List<Meditation>> = _availableMeditations

    private val _currentStep = MutableStateFlow<GuidedStep?>(null)
    val currentStep: StateFlow<GuidedStep?> = _currentStep

    val isPlaying = meditationPlayer.isPlaying
    val progress = meditationPlayer.progress

    init {
        loadMeditations()
        loadProgress()
    }

    fun startMeditation(meditation: Meditation) {
        _currentMeditation.value = meditation
        viewModelScope.launch {
            meditationPlayer.prepare(meditation.audioUrl)
            trackSteps(meditation)
        }
    }

    private fun trackSteps(meditation: Meditation) {
        viewModelScope.launch {
            meditation.guidedSteps.forEach { step ->
                // Esperar hasta el timestamp del paso
                // Actualizar el paso actual
                _currentStep.value = step
            }
        }
    }

    fun pauseMeditation() {
        meditationPlayer.pause()
    }

    fun resumeMeditation() {
        meditationPlayer.play()
    }

    fun stopMeditation() {
        meditationPlayer.release()
        _currentMeditation.value = null
        _currentStep.value = null
    }

    fun seekTo(position: Float) {
        _currentMeditation.value?.let { meditation ->
            val duration = meditation.duration * 60 * 1000
            meditationPlayer.seekTo((position * duration).toInt())
        }
    }

    private fun loadMeditations() {
        // Aquí cargaríamos las meditaciones desde la base de datos
        // Por ahora, usamos datos de ejemplo
        _availableMeditations.value = generateSampleMeditations()
    }

    private fun loadProgress() {
        // Aquí cargaríamos el progreso desde la base de datos
        // Por ahora, usamos datos de ejemplo
        _meditationProgress.value = MeditationProgress(
            totalSessions = 10,
            totalMinutes = 120,
            averageFocusScore = 0.8f,
            longestStreak = 5,
            currentStreak = 2,
            favoriteCategory = MeditationCategory.MINDFULNESS,
            lastSession = Date()
        )
    }

    private fun generateSampleMeditations(): List<Meditation> {
        return listOf(
            Meditation(
                id = 1,
                title = "Respiración Consciente",
                description = "Una meditación guiada enfocada en la respiración",
                category = MeditationCategory.BREATHING,
                duration = 10,
                audioUrl = "sample_url",
                imageUrl = "sample_image",
                difficulty = MeditationDifficulty.BEGINNER,
                tags = listOf("respiración", "principiante", "relajación"),
                guidedSteps = listOf(
                    GuidedStep(0, "Siéntate cómodamente", 30),
                    GuidedStep(30, "Respira profundamente", 60),
                    GuidedStep(90, "Observa tu respiración", 120)
                )
            )
            // Más meditaciones...
        )
    }

    override fun onCleared() {
        super.onCleared()
        meditationPlayer.release()
    }
} 