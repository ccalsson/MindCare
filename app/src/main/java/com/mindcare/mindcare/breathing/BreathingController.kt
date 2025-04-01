package com.mindcare.mindcare.breathing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.mindcare.mindcare.breathing.models.BreathingPattern
import java.util.*
import kotlin.concurrent.fixedRateTimer

class BreathingController {
    private var timer: Timer? = null
    private var currentPhase = BreathingPhase.INHALE
    private var currentCycle = 1
    
    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime
    
    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress
    
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive

    fun startExercise(pattern: BreathingPattern) {
        stopExercise()
        _isActive.value = true
        currentCycle = 1
        
        timer = fixedRateTimer(period = 100) {
            updateBreathingPhase(pattern)
        }
    }

    private fun updateBreathingPhase(pattern: BreathingPattern) {
        when (currentPhase) {
            BreathingPhase.INHALE -> {
                if (_remainingTime.value <= 0) {
                    currentPhase = BreathingPhase.HOLD_INHALE
                    _remainingTime.value = pattern.holdInhale
                } else {
                    _remainingTime.value--
                    _currentProgress.value = 1f - (_remainingTime.value.toFloat() / pattern.inhale)
                }
            }
            BreathingPhase.HOLD_INHALE -> {
                if (_remainingTime.value <= 0) {
                    currentPhase = BreathingPhase.EXHALE
                    _remainingTime.value = pattern.exhale
                } else {
                    _remainingTime.value--
                    _currentProgress.value = 1f
                }
            }
            BreathingPhase.EXHALE -> {
                if (_remainingTime.value <= 0) {
                    currentPhase = BreathingPhase.HOLD_EXHALE
                    _remainingTime.value = pattern.holdExhale
                } else {
                    _remainingTime.value--
                    _currentProgress.value = _remainingTime.value.toFloat() / pattern.exhale
                }
            }
            BreathingPhase.HOLD_EXHALE -> {
                if (_remainingTime.value <= 0) {
                    if (currentCycle >= pattern.cycles) {
                        stopExercise()
                    } else {
                        currentCycle++
                        currentPhase = BreathingPhase.INHALE
                        _remainingTime.value = pattern.inhale
                    }
                } else {
                    _remainingTime.value--
                    _currentProgress.value = 0f
                }
            }
        }
    }

    fun pauseExercise() {
        timer?.cancel()
        timer = null
        _isActive.value = false
    }

    fun resumeExercise(pattern: BreathingPattern) {
        if (!_isActive.value) {
            startExercise(pattern)
        }
    }

    fun stopExercise() {
        timer?.cancel()
        timer = null
        _isActive.value = false
        _remainingTime.value = 0
        _currentProgress.value = 0f
        currentPhase = BreathingPhase.INHALE
        currentCycle = 1
    }
}

enum class BreathingPhase {
    INHALE,
    HOLD_INHALE,
    EXHALE,
    HOLD_EXHALE
} 