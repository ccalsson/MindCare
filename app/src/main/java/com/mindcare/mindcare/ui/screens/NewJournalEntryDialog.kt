package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindcare.mindcare.journal.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJournalEntryDialog(
    onDismiss: () -> Unit,
    onSave: (Mood, String, List<Activity>, List<Emotion>, List<String>?, List<String>?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedMoodLevel by remember { mutableStateOf(3) }
    var selectedActivities by remember { mutableStateOf(emptyList<Activity>()) }
    var selectedEmotions by remember { mutableStateOf(emptyList<Emotion>()) }
    var gratitudeList by remember { mutableStateOf(emptyList<String>()) }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Entrada") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de estado de ánimo
                Text("¿Cómo te sientes?")
                MoodSelector(
                    selectedLevel = selectedMoodLevel,
                    onMoodSelected = { selectedMoodLevel = it }
                )

                // Campo de texto principal
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("¿Qué está en tu mente?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Selector de actividades
                Text("Actividades")
                ActivitySelector(
                    selectedActivities = selectedActivities,
                    onActivitySelected = { activity ->
                        selectedActivities = if (selectedActivities.contains(activity)) {
                            selectedActivities - activity
                        } else {
                            selectedActivities + activity
                        }
                    }
                )

                // Selector de emociones
                Text("Emociones")
                EmotionSelector(
                    selectedEmotions = selectedEmotions,
                    onEmotionSelected = { emotion ->
                        selectedEmotions = if (selectedEmotions.contains(emotion)) {
                            selectedEmotions - emotion
                        } else {
                            selectedEmotions + emotion
                        }
                    }
                )

                // Campo de gratitud
                OutlinedTextField(
                    value = gratitudeList.joinToString("\n"),
                    onValueChange = { text ->
                        gratitudeList = text.split("\n").filter { it.isNotBlank() }
                    },
                    label = { Text("¿Por qué estás agradecido/a hoy?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Campo de etiquetas
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Etiquetas (separadas por espacios)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mood = Mood(
                        level = selectedMoodLevel,
                        description = getMoodDescription(selectedMoodLevel),
                        icon = getMoodIcon(selectedMoodLevel)
                    )
                    onSave(
                        mood,
                        content,
                        selectedActivities,
                        selectedEmotions,
                        gratitudeList.takeIf { it.isNotEmpty() },
                        tags.split(" ").filter { it.isNotBlank() }
                    )
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
fun MoodSelector(
    selectedLevel: Int,
    onMoodSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..5).forEach { level ->
            IconButton(
                onClick = { onMoodSelected(level) },
                modifier = Modifier.size(48.dp)
            ) {
                Text(
                    text = getMoodIcon(level),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (level == selectedLevel) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            }
        }
    }
}

private fun getMoodDescription(level: Int): String = when (level) {
    1 -> "Muy mal"
    2 -> "Mal"
    3 -> "Normal"
    4 -> "Bien"
    5 -> "Muy bien"
    else -> "Normal"
}

private fun getMoodIcon(level: Int): String = when (level) {
    1 -> "😢"
    2 -> "😕"
    3 -> "😐"
    4 -> "😊"
    5 -> "😄"
    else -> "😐"
} 