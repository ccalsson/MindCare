package com.mindcare.mindcare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialTime: LocalTime = LocalTime.now()
) {
    var selectedHour by remember { mutableStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }
    var timePickerState by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = initialTime.hour,
                initialMinute = initialTime.minute,
                is24Hour = true
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleccionar hora",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeDisplay(
    hour: Int,
    minute: Int,
    modifier: Modifier = Modifier
) {
    val time = LocalTime.of(hour, minute)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    
    Text(
        text = time.format(formatter),
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}

@Composable
fun TimeRangeDisplay(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeDisplay(startHour, startMinute)
        Text("-")
        TimeDisplay(endHour, endMinute)
    }
}

fun formatTimeRange(startHour: Int, endHour: Int): String {
    return "${formatHour(startHour)} - ${formatHour(endHour)}"
}

private fun formatHour(hour: Int): String {
    return when (hour) {
        0 -> "12 AM"
        12 -> "12 PM"
        in 1..11 -> "$hour AM"
        else -> "${hour - 12} PM"
    }
}

@Composable
fun TimePickerButton(
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    TextButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        TimeDisplay(hour = hour, minute = minute)
    }

    if (showDialog) {
        TimePickerDialog(
            onDismiss = { showDialog = false },
            onConfirm = { selectedHour, selectedMinute ->
                onTimeSelected(selectedHour, selectedMinute)
                showDialog = false
            },
            initialTime = LocalTime.of(hour, minute)
        )
    }
} 