package com.mindcare.mindcare.ui.screens

import androidx.compose.animation.*
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
import com.mindcare.mindcare.journal.models.*
import com.mindcare.mindcare.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = viewModel()
) {
    val entries by viewModel.journalEntries.collectAsState()
    val stats by viewModel.journalStats.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    var showNewEntryDialog by remember { mutableStateOf(false) }

    if (selectedEntry != null) {
        JournalEntryDetail(
            entry = selectedEntry!!,
            onClose = { viewModel.clearSelectedEntry() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diario de Emociones",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = { showNewEntryDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva entrada")
                }
            }

            // Estadísticas
            stats?.let { journalStats ->
                JournalStatsCard(journalStats)
            }

            // Lista de entradas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries.sortedByDescending { it.date }) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        onClick = { viewModel.selectEntry(entry) }
                    )
                }
            }
        }

        if (showNewEntryDialog) {
            NewJournalEntryDialog(
                onDismiss = { showNewEntryDialog = false },
                onSave = { mood, content, activities, emotions, gratitude, tags ->
                    viewModel.addEntry(
                        mood = mood,
                        content = content,
                        activities = activities,
                        emotions = emotions,
                        gratitude = gratitude,
                        tags = tags
                    )
                    showNewEntryDialog = false
                }
            )
        }
    }
}

@Composable
fun JournalStatsCard(stats: JournalStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Entradas",
                    value = stats.totalEntries.toString()
                )
                StatItem(
                    label = "Estado de ánimo",
                    value = String.format("%.1f", stats.averageMood)
                )
                StatItem(
                    label = "Racha",
                    value = "${stats.streakDays} días"
                )
            }

            // Emociones comunes
            Text(
                text = "Emociones frecuentes: ${stats.commonEmotions.take(3).joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(entry.date),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = entry.mood.icon,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Tags
            entry.tags?.let { tags ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.take(3).forEach { tag ->
                        TagChip(tag)
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(tag: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "#$tag",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(date)
} 