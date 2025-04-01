package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.gamification.TherapeuticQuests
import com.mindcare.mindcare.gamification.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class QuestViewModel : ViewModel() {
    private val therapeuticQuests = TherapeuticQuests()
    
    private val _dailyQuests = MutableStateFlow<List<Quest>>(emptyList())
    val dailyQuests: StateFlow<List<Quest>> = _dailyQuests
    
    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress

    init {
        loadUserProgress()
        refreshDailyQuests()
    }

    private fun loadUserProgress() {
        // Simular carga de progreso del usuario
        _userProgress.value = UserProgress(
            userId = "user_1",
            level = 1,
            experience = 0,
            badges = emptyList(),
            achievements = emptyList(),
            streakDays = 0,
            lastActivityDate = Date()
        )
    }

    private fun refreshDailyQuests() {
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                _dailyQuests.value = therapeuticQuests.generateDailyQuests(progress)
            }
        }
    }

    fun completeActivity(questId: Long, activityId: Long) {
        viewModelScope.launch {
            // Implementar lógica de completar actividad
            // Actualizar progreso del usuario
            // Otorgar recompensas
        }
    }

    fun claimReward(questId: Long, reward: Reward) {
        viewModelScope.launch {
            // Implementar lógica de reclamar recompensa
        }
    }
} 