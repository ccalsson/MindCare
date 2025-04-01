package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindcare.mindcare.notifications.*
import com.mindcare.mindcare.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    var showPreferences by remember { mutableStateOf(false) }
    var showNewReminder by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                actions = {
                    IconButton(onClick = { showPreferences = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Preferencias")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewReminder = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo recordatorio")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sección de recordatorios activos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recordatorios Activos",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    reminders.filter { it.isEnabled }.forEach { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onToggle = { enabled ->
                                viewModel.toggleReminder(reminder.id, enabled)
                            },
                            onDelete = {
                                viewModel.deleteReminder(reminder.id)
                            }
                        )
                    }
                }
            }

            // Lista de notificaciones
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications.sortedByDescending { it.timestamp }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = {
                            viewModel.markNotificationAsRead(notification.id)
                        }
                    )
                }
            }
        }

        if (showPreferences) {
            NotificationPreferencesDialog(
                preferences = viewModel.preferences.collectAsState().value,
                onDismiss = { showPreferences = false },
                onSave = { preferences ->
                    viewModel.updatePreferences(preferences)
                    showPreferences = false
                }
            )
        }

        if (showNewReminder) {
            NewReminderDialog(
                onDismiss = { showNewReminder = false },
                onSave = { type, title, message, time, frequency ->
                    viewModel.createReminder(type, title, message, time, frequency)
                    showNewReminder = false
                }
            )
        }
    }
} 