package com.mindcare.mindcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcare.mindcare.sleep.SleepOptimizer
import com.mindcare.mindcare.sleep.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class SleepViewModel : ViewModel() {
    private val sleepOptimizer = SleepOptimizer()
    
    private val _sleepRecords = MutableStateFlow<List<SleepRecord>>(emptyList())
    val sleepRecords: StateFlow<List<SleepRecord>> = _sleepRecords
    
    private val _sleepStats = MutableStateFlow<SleepStats?>(null)
    val sleepStats: StateFlow<SleepStats?> = _sleepStats
    
    private val _recommendations = MutableStateFlow<SleepRecommendations?>(null)
    val recommendations: StateFlow<SleepRecommendations?> = _recommendations

    init {
        loadSleepRecords()
    }

    private fun loadSleepRecords() {
        viewModelScope.launch {
            // Aquí se implementará la carga desde Room
            // Por ahora, datos de ejemplo
            val sampleRecords = generateSampleRecords()
            _sleepRecords.value = sampleRecords
            updateSleepAnalysis()
        }
    }

    fun recordSleep(
        startTime: Date,
        endTime: Date,
        quality: Float,
        notes: String? = null
    ) {
        viewModelScope.launch {
            val newRecord = SleepRecord(
                startTime = startTime,
                endTime = endTime,
                quality = quality,
                deepSleepMinutes = calculateDeepSleep(startTime, endTime),
                lightSleepMinutes = calculateLightSleep(startTime, endTime),
                remSleepMinutes = calculateRemSleep(startTime, endTime),
                interruptions = 0, // Se implementará con sensores
                heartRate = emptyList(), // Se implementará con sensores
                notes = notes
            )

            _sleepRecords.value = _sleepRecords.value + newRecord
            updateSleepAnalysis()
        }
    }

    private fun updateSleepAnalysis() {
        viewModelScope.launch {
            val records = _sleepRecords.value
            _sleepStats.value = sleepOptimizer.analyzeSleepPattern(records)
            _recommendations.value = sleepOptimizer.generateRecommendations(
                records,
                getUserPreferences()
            )
        }
    }

    private fun calculateDeepSleep(start: Date, end: Date): Int {
        // Simulación - en realidad vendría de sensores
        val totalMinutes = ((end.time - start.time) / (1000 * 60)).toInt()
        return (totalMinutes * 0.2).toInt() // Aproximadamente 20% del tiempo total
    }

    private fun calculateLightSleep(start: Date, end: Date): Int {
        val totalMinutes = ((end.time - start.time) / (1000 * 60)).toInt()
        return (totalMinutes * 0.5).toInt() // Aproximadamente 50% del tiempo total
    }

    private fun calculateRemSleep(start: Date, end: Date): Int {
        val totalMinutes = ((end.time - start.time) / (1000 * 60)).toInt()
        return (totalMinutes * 0.3).toInt() // Aproximadamente 30% del tiempo total
    }

    private fun getUserPreferences(): Map<String, Any> {
        // Aquí se implementará la obtención de preferencias del usuario
        return mapOf(
            "preferredBedtime" to "22:00",
            "preferredWakeTime" to "07:00",
            "targetSleepDuration" to 480 // 8 horas en minutos
        )
    }

    private fun generateSampleRecords(): List<SleepRecord> {
        val calendar = Calendar.getInstance()
        val records = mutableListOf<SleepRecord>()

        // Generar registros de la última semana
        for (i in 7 downTo 1) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val startTime = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 22)
                set(Calendar.MINUTE, 0)
            }.time
            
            val endTime = calendar.apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 7)
                set(Calendar.MINUTE, 0)
            }.time

            records.add(
                SleepRecord(
                    startTime = startTime,
                    endTime = endTime,
                    quality = (0.6f..0.9f).random(),
                    deepSleepMinutes = 120,
                    lightSleepMinutes = 240,
                    remSleepMinutes = 120,
                    interruptions = (0..3).random(),
                    heartRate = emptyList(),
                    notes = null
                )
            )
        }

        return records
    }
} 