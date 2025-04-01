package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.breathing.BreathingController
import com.mindcare.mindcare.breathing.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class BreathingViewModel : ViewModel() {
    private val breathingController = BreathingController()
    
    private val _currentExercise = MutableStateFlow<BreathingExercise?>(null)
    val currentExercise: StateFlow<BreathingExercise?> = _currentExercise
    
    private val _breathingProgress = MutableStateFlow<BreathingProgress?>(null)
    val breathingProgress: StateFlow<BreathingProgress?> = _breathingProgress
    
    private val _availableExercises = MutableStateFlow<List<BreathingExercise>>(emptyList())
    val availableExercises: StateFlow<List<BreathingExercise>> = _availableExercises

    val remainingTime = breathingController.remainingTime
    val currentProgress = breathingController.currentProgress
    val isActive = breathingController.isActive

    init {
        loadExercises()
        loadProgress()
    }

    fun startExercise(exercise: BreathingExercise) {
        _currentExercise.value = exercise
        breathingController.startExercise(exercise.pattern)
    }

    fun pauseExercise() {
        breathingController.pauseExercise()
    }

    fun resumeExercise() {
        _currentExercise.value?.let { exercise ->
            breathingController.resumeExercise(exercise.pattern)
        }
    }

    fun stopExercise() {
        breathingController.stopExercise()
        _currentExercise.value = null
    }

    private fun loadExercises() {
        // Aquí cargaríamos los ejercicios desde la base de datos
        // Por ahora, usamos datos de ejemplo
        _availableExercises.value = generateSampleExercises()
    }

    private fun loadProgress() {
        // Aquí cargaríamos el progreso desde la base de datos
        // Por ahora, usamos datos de ejemplo
        _breathingProgress.value = BreathingProgress(
            totalSessions = 15,
            totalMinutes = 180,
            averageStressReduction = 0.7f,
            favoriteExercise = "Respiración 4-7-8",
            lastSession = Date(),
            weeklyStats = listOf(3, 2, 3, 2, 2, 1, 2)
        )
    }

    private fun generateSampleExercises(): List<BreathingExercise> {
        return listOf(
            BreathingExercise(
                id = 1,
                name = "Respiración 4-7-8",
                description = "Técnica de relajación profunda",
                category = BreathingCategory.RELAXATION,
                pattern = BreathingPattern(
                    inhale = 4,
                    holdInhale = 7,
                    exhale = 8,
                    holdExhale = 0,
                    cycles = 4
                ),
                duration = 76, // (4+7+8) * 4 cycles
                difficulty = BreathingDifficulty.BEGINNER,
                benefits = listOf(
                    "Reduce el estrés",
                    "Mejora el sueño",
                    "Calma la ansiedad"
                ),
                imageUrl = "sample_image_url"
            )
            // Más ejercicios...
        )
    }

    override fun onCleared() {
        super.onCleared()
        breathingController.stopExercise()
    }
} 