package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.breathing.models.BreathingPhase
import com.mindcare.mindcare.breathing.models.BreathingSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class BreathingExerciseViewModel : ViewModel() {
    private val _currentPhase = MutableStateFlow(BreathingPhase.REST)
    val currentPhase: StateFlow<BreathingPhase> = _currentPhase

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining

    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _isExerciseActive = MutableStateFlow(false)
    val isExerciseActive: StateFlow<Boolean> = _isExerciseActive

    private var currentSession: BreathingSession? = null

    fun startExercise() {
        viewModelScope.launch {
            _isExerciseActive.value = true
            currentSession = BreathingSession(
                startTime = Date(),
                duration = 0,
                averageHeartRate = null,
                heartRateVariability = null,
                stressReduction = null
            )

            repeat(5) { // 5 ciclos de respiración
                // Inhalar
                _currentPhase.value = BreathingPhase.INHALE
                countDown(4000) // 4 segundos

                // Mantener
                _currentPhase.value = BreathingPhase.HOLD
                countDown(4000) // 4 segundos

                // Exhalar
                _currentPhase.value = BreathingPhase.EXHALE
                countDown(4000) // 4 segundos

                // Descanso
                _currentPhase.value = BreathingPhase.REST
                countDown(2000) // 2 segundos
            }

            endExercise()
        }
    }

    private suspend fun countDown(duration: Long) {
        _timeRemaining.value = duration
        val interval = 100L // Actualizar cada 100ms
        var remaining = duration

        while (remaining > 0) {
            delay(interval)
            remaining -= interval
            _timeRemaining.value = remaining
            updateHeartRate() // Simular actualización del ritmo cardíaco
        }
    }

    private fun updateHeartRate() {
        // Simular lecturas de ritmo cardíaco
        // En una implementación real, esto vendría de sensores
        _heartRate.value = (60 + (Math.random() * 20)).toFloat()
    }

    private fun endExercise() {
        _isExerciseActive.value = false
        _currentPhase.value = BreathingPhase.REST
        _timeRemaining.value = 0
    }

    fun pauseExercise() {
        _isExerciseActive.value = false
    }

    fun resumeExercise() {
        _isExerciseActive.value = true
    }
} 