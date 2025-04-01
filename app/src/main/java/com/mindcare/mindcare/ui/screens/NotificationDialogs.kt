package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindcare.mindcare.notifications.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReminderDialog(
    onDismiss: () -> Unit,
    onSave: (ReminderType, String, String, Date, ReminderFrequency) -> Unit
) {
    var selectedType by remember { mutableStateOf(ReminderType.BREATHING_EXERCISE) }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedFrequency by remember { mutableStateOf(ReminderFrequency.DAILY) }
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Recordatorio") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tipo de recordatorio
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = formatReminderType(selectedType),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Tipo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        ReminderType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(formatReminderType(type)) },
                                onClick = { selectedType = type }
                            )
                        }
                    }
                }

                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hora: ${String.format("%02d:%02d", selectedHour, selectedMinute)}")
                    TextButton(onClick = { showTimePicker = true }) {
                        Text("Cambiar")
                    }
                }

                // Frecuencia
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = formatFrequency(selectedFrequency),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Frecuencia") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        ReminderFrequency.values().forEach { frequency ->
                            DropdownMenuItem(
                                text = { Text(formatFrequency(frequency)) },
                                onClick = { selectedFrequency = frequency }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                    }
                    onSave(selectedType, title, message, calendar.time, selectedFrequency)
                },
                enabled = title.isNotBlank() && message.isNotBlank()
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

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                showTimePicker = false
            }
        )
    }
}

@Composable
fun NotificationPreferencesDialog(
    preferences: NotificationPreferences,
    onDismiss: () -> Unit,
    onSave: (NotificationPreferences) -> Unit
) {
    var updatedPreferences by remember { mutableStateOf(preferences) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preferencias de Notificaciones") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Switches para cada tipo de notificación
                PreferenceSwitch(
                    text = "Recordatorios de respiración",
                    checked = updatedPreferences.breathingReminders,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(breathingReminders = it)
                    }
                )

                PreferenceSwitch(
                    text = "Recordatorios del diario",
                    checked = updatedPreferences.journalReminders,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(journalReminders = it)
                    }
                )

                PreferenceSwitch(
                    text = "Notificaciones de grupos",
                    checked = updatedPreferences.groupNotifications,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(groupNotifications = it)
                    }
                )

                PreferenceSwitch(
                    text = "Notificaciones de logros",
                    checked = updatedPreferences.achievementNotifications,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(achievementNotifications = it)
                    }
                )

                Divider()

                // Opciones de sonido y vibración
                PreferenceSwitch(
                    text = "Sonido",
                    checked = updatedPreferences.soundEnabled,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(soundEnabled = it)
                    }
                )

                PreferenceSwitch(
                    text = "Vibración",
                    checked = updatedPreferences.vibrationEnabled,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(vibrationEnabled = it)
                    }
                )

                Divider()

                // Modo silencioso
                PreferenceSwitch(
                    text = "Horas silenciosas",
                    checked = updatedPreferences.quietHoursEnabled,
                    onCheckedChange = {
                        updatedPreferences = updatedPreferences.copy(quietHoursEnabled = it)
                    }
                )

                if (updatedPreferences.quietHoursEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Desde:")
                            TimePickerButton(
                                hour = updatedPreferences.quietHoursStart,
                                minute = 0,
                                onTimeSelected = { hour, _ ->
                                    updatedPreferences = updatedPreferences.copy(
                                        quietHoursStart = hour
                                    )
                                }
                            )
                        }

                        Column {
                            Text("Hasta:")
                            TimePickerButton(
                                hour = updatedPreferences.quietHoursEnd,
                                minute = 0,
                                onTimeSelected = { hour, _ ->
                                    updatedPreferences = updatedPreferences.copy(
                                        quietHoursEnd = hour
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(updatedPreferences) }) {
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
private fun PreferenceSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun formatReminderType(type: ReminderType): String {
    return when (type) {
        ReminderType.BREATHING_EXERCISE -> "Ejercicio de respiración"
        ReminderType.JOURNAL_ENTRY -> "Entrada del diario"
        ReminderType.MEDITATION -> "Meditación"
        ReminderType.WATER -> "Beber agua"
        ReminderType.MEDICATION -> "Medicación"
        ReminderType.CUSTOM -> "Personalizado"
    }
} 