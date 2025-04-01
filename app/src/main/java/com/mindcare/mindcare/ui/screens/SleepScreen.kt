package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindcare.mindcare.sleep.models.SleepRecord
import com.mindcare.mindcare.viewmodel.SleepViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SleepScreen(
    viewModel: SleepViewModel = viewModel()
) {
    val sleepStats by viewModel.sleepStats.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val sleepRecords by viewModel.sleepRecords.collectAsState()
    var showRecordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Monitoreo del Sueño",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Estadísticas de sueño
        sleepStats?.let { stats ->
            SleepStatsCard(stats)
        }

        // Recomendaciones
        recommendations?.let { recs ->
            SleepRecommendationsCard(recs)
        }

        // Historial de sueño
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Historial de Sueño",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(sleepRecords.sortedByDescending { it.startTime }) { record ->
                SleepRecordCard(record)
            }
        }

        // Botón para registrar sueño
        Button(
            onClick = { showRecordDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Registrar Sueño")
        }
    }

    // Diálogo de registro
    if (showRecordDialog) {
        SleepRecordDialog(
            onDismiss = { showRecordDialog = false },
            onRecord = { startTime, endTime, quality, notes ->
                viewModel.recordSleep(startTime, endTime, quality, notes)
            }
        )
    }
}

@Composable
fun SleepStatsCard(stats: SleepStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estadísticas de Sueño",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Calidad",
                    value = "${(stats.averageQuality * 100).toInt()}%"
                )
                StatItem(
                    label = "Duración",
                    value = "${stats.averageDuration / 60}h ${stats.averageDuration % 60}m"
                )
                StatItem(
                    label = "Consistencia",
                    value = "${(stats.consistencyScore * 100).toInt()}%"
                )
            }
        }
    }
}

@Composable
fun SleepRecommendationsCard(recommendations: SleepRecommendations) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recomendaciones",
                style = MaterialTheme.typography.titleMedium
            )

            recommendations.recommendations.forEach { recommendation ->
                Text(
                    text = "• $recommendation",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hora de dormir sugerida: ${formatTime(recommendations.suggestedBedtime)}")
                Text("Puntuación: ${recommendations.sleepScore}")
            }
        }
    }
}

@Composable
fun SleepRecordCard(record: SleepRecord) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDate(record.startTime))
                Text("${calculateDuration(record)}h")
            }

            LinearProgressIndicator(
                progress = record.quality,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PhaseIndicator("Profundo", record.deepSleepMinutes)
                PhaseIndicator("Ligero", record.lightSleepMinutes)
                PhaseIndicator("REM", record.remSleepMinutes)
            }

            // Agregar notas si existen
            record.notes?.let { notes ->
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun PhaseIndicator(phase: String, minutes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = phase, style = MaterialTheme.typography.bodySmall)
        Text(
            text = "${minutes / 60}h ${minutes % 60}m",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
}

private fun formatTime(date: Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}

private fun calculateDuration(record: SleepRecord): String {
    val durationMinutes = ((record.endTime.time - record.startTime.time) / (1000 * 60)).toInt()
    return "${durationMinutes / 60}h ${durationMinutes % 60}m"
} 