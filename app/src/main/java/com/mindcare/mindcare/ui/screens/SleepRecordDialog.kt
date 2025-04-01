package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*
import java.text.SimpleDateFormat

@Composable
fun SleepRecordDialog(
    onDismiss: () -> Unit,
    onRecord: (startTime: Date, endTime: Date, quality: Float, notes: String?) -> Unit
) {
    var startTime by remember { mutableStateOf(Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
        set(Calendar.HOUR_OF_DAY, 22)
        set(Calendar.MINUTE, 0)
    }.time) }
    
    var endTime by remember { mutableStateOf(Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 7)
        set(Calendar.MINUTE, 0)
    }.time) }
    
    var quality by remember { mutableStateOf(0.7f) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Sueño") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de hora de inicio
                TimeSelector(
                    label = "Hora de dormir",
                    time = startTime,
                    onTimeSelected = { startTime = it }
                )

                // Selector de hora de despertar
                TimeSelector(
                    label = "Hora de despertar",
                    time = endTime,
                    onTimeSelected = { endTime = it }
                )

                // Calidad del sueño
                Column {
                    Text(
                        text = "Calidad del sueño: ${(quality * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = quality,
                        onValueChange = { quality = it },
                        valueRange = 0f..1f,
                        steps = 9
                    )
                }

                // Notas
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Duración calculada
                val duration = calculateDurationText(startTime, endTime)
                Text(
                    text = "Duración: $duration",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onRecord(startTime, endTime, quality, notes.takeIf { it.isNotBlank() })
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun TimeSelector(
    label: String,
    time: Date,
    onTimeSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = time

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        TextButton(
            onClick = {
                // Aquí se implementaría un TimePicker nativo
                // Por ahora solo simulamos el cambio
                calendar.add(Calendar.HOUR, 1)
                onTimeSelected(calendar.time)
            }
        ) {
            Text(formatTime(time))
        }
    }
}

private fun calculateDurationText(start: Date, end: Date): String {
    val durationMinutes = ((end.time - start.time) / (1000 * 60)).toInt()
    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    return "${hours}h ${minutes}m"
}

private fun formatTime(date: Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
} 