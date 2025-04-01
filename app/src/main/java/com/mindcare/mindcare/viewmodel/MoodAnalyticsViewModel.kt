package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.analytics.MoodPredictor
import com.mindcare.mindcare.analytics.models.MoodEntry
import com.mindcare.mindcare.analytics.models.MoodPrediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class MoodAnalyticsViewModel : ViewModel() {
    private val moodPredictor = MoodPredictor()
    
    private val _currentMoodPrediction = MutableStateFlow<MoodPrediction?>(null)
    val currentMoodPrediction: StateFlow<MoodPrediction?> = _currentMoodPrediction
    
    private val _moodHistory = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodHistory: StateFlow<List<MoodEntry>> = _moodHistory

    fun addMoodEntry(mood: String, intensity: Float, notes: String? = null) {
        val newEntry = MoodEntry(
            mood = mood,
            intensity = intensity,
            timestamp = Date(),
            notes = notes,
            activities = emptyList(),
            triggers = null
        )
        
        viewModelScope.launch {
            _moodHistory.value = _moodHistory.value + newEntry
            updatePrediction()
        }
    }

    private fun updatePrediction() {
        viewModelScope.launch {
            moodPredictor.predictMoodTrends(_moodHistory.value).collect { prediction ->
                _currentMoodPrediction.value = prediction
            }
        }
    }
} 