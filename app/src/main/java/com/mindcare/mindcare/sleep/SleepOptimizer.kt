package com.mindcare.mindcare.sleep

import com.mindcare.mindcare.sleep.models.*
import java.util.*
import kotlin.math.abs

class SleepOptimizer {
    fun analyzeSleepPattern(records: List<SleepRecord>): SleepStats {
        if (records.isEmpty()) return createEmptySleepStats()

        val averageQuality = records.map { it.quality }.average().toFloat()
        val averageDuration = records.map { calculateDuration(it) }.average().toInt()
        val consistencyScore = calculateConsistencyScore(records)
        val weeklyTrend = calculateWeeklyTrend(records)
        
        val (bestTime, worstTime) = findBestAndWorstSleepTimes(records)

        return SleepStats(
            averageQuality = averageQuality,
            averageDuration = averageDuration,
            consistencyScore = consistencyScore,
            weeklyTrend = weeklyTrend,
            bestSleepTime = bestTime,
            worstSleepTime = worstTime
        )
    }

    fun generateRecommendations(
        recentRecords: List<SleepRecord>,
        userPreferences: Map<String, Any>
    ): SleepRecommendations {
        val calendar = Calendar.getInstance()
        val now = calendar.time

        // Calcular horario óptimo basado en patrones
        val optimalSleepTime = calculateOptimalSleepTime(recentRecords)
        val optimalDuration = calculateOptimalDuration(recentRecords)

        calendar.time = optimalSleepTime
        val suggestedBedtime = calendar.time

        calendar.add(Calendar.MINUTE, optimalDuration)
        val suggestedWakeTime = calendar.time

        return SleepRecommendations(
            suggestedBedtime = suggestedBedtime,
            suggestedWakeTime = suggestedWakeTime,
            optimalSleepDuration = optimalDuration,
            recommendations = generatePersonalizedRecommendations(recentRecords),
            sleepScore = calculateSleepScore(recentRecords.lastOrNull())
        )
    }

    private fun calculateConsistencyScore(records: List<SleepRecord>): Float {
        if (records.size < 2) return 1.0f

        var totalVariation = 0L
        for (i in 1 until records.size) {
            val timeDiff = abs(records[i].startTime.time - records[i-1].startTime.time)
            totalVariation += timeDiff
        }

        val averageVariation = totalVariation / (records.size - 1)
        return (1 - (averageVariation / (24 * 60 * 60 * 1000).toFloat())).coerceIn(0f, 1f)
    }

    private fun calculateWeeklyTrend(records: List<SleepRecord>): List<Float> {
        val lastWeekRecords = records.takeLast(7)
        return lastWeekRecords.map { it.quality }
    }

    private fun findBestAndWorstSleepTimes(records: List<SleepRecord>): Pair<Date, Date> {
        val bestRecord = records.maxByOrNull { it.quality }
        val worstRecord = records.minByOrNull { it.quality }

        return Pair(
            bestRecord?.startTime ?: Date(),
            worstRecord?.startTime ?: Date()
        )
    }

    private fun calculateOptimalSleepTime(records: List<SleepRecord>): Date {
        val highQualityRecords = records.filter { it.quality > 0.7f }
        if (highQualityRecords.isEmpty()) return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
        }.time

        return highQualityRecords
            .map { it.startTime }
            .reduce { acc, date -> 
                Date((acc.time + date.time) / 2)
            }
    }

    private fun calculateOptimalDuration(records: List<SleepRecord>): Int {
        if (records.isEmpty()) return 480 // 8 horas por defecto

        return records
            .filter { it.quality > 0.7f }
            .map { calculateDuration(it) }
            .average()
            .toInt()
    }

    private fun generatePersonalizedRecommendations(records: List<SleepRecord>): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Analizar patrones y generar recomendaciones
        if (records.isNotEmpty()) {
            val avgQuality = records.map { it.quality }.average()
            val lastRecord = records.last()

            when {
                avgQuality < 0.5 -> {
                    recommendations.add("Establece una rutina nocturna relajante")
                    recommendations.add("Evita las pantallas 1 hora antes de dormir")
                }
                lastRecord.interruptions > 3 -> {
                    recommendations.add("Considera usar tapones para los oídos")
                    recommendations.add("Mantén una temperatura óptima en tu habitación")
                }
                calculateDuration(lastRecord) < 420 -> { // menos de 7 horas
                    recommendations.add("Intenta acostarte 30 minutos antes")
                    recommendations.add("Evita la cafeína después del mediodía")
                }
            }
        }

        return recommendations.ifEmpty {
            listOf(
                "Mantén un horario regular de sueño",
                "Crea un ambiente oscuro y tranquilo",
                "Practica ejercicios de relajación antes de dormir"
            )
        }
    }

    private fun calculateSleepScore(record: SleepRecord?): Int {
        if (record == null) return 0

        val durationScore = calculateDurationScore(record)
        val qualityScore = (record.quality * 100).toInt()
        val interruptionScore = (100 - (record.interruptions * 10)).coerceIn(0, 100)

        return ((durationScore + qualityScore + interruptionScore) / 3.0).toInt()
    }

    private fun calculateDurationScore(record: SleepRecord): Int {
        val duration = calculateDuration(record)
        return when {
            duration in 420..540 -> 100 // 7-9 horas ideal
            duration in 360..420 -> 80  // 6-7 horas aceptable
            duration > 540 -> 70        // más de 9 horas
            duration < 360 -> 60        // menos de 6 horas
            else -> 50
        }
    }

    private fun calculateDuration(record: SleepRecord): Int {
        return ((record.endTime.time - record.startTime.time) / (1000 * 60)).toInt()
    }

    private fun createEmptySleepStats() = SleepStats(
        averageQuality = 0f,
        averageDuration = 0,
        consistencyScore = 0f,
        weeklyTrend = emptyList(),
        bestSleepTime = Date(),
        worstSleepTime = Date()
    )
} 