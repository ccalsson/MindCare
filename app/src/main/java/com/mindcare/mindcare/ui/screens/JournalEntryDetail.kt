package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindcare.mindcare.journal.models.JournalEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetail(
    entry: JournalEntry,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(formatDate(entry.date)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Estado de ánimo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.mood.description,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = entry.mood.icon,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido principal
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Actividades
            if (entry.activities.isNotEmpty()) {
                Text(
                    text = "Actividades",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(entry.activities) { activity ->
                        ActivityChip(activity)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emociones
            if (entry.emotions.isNotEmpty()) {
                Text(
                    text = "Emociones",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(entry.emotions) { emotion ->
                        EmotionChip(emotion)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gratitud
            entry.gratitude?.let { gratitudeList ->
                if (gratitudeList.isNotEmpty()) {
                    Text(
                        text = "Gratitud",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        gratitudeList.forEach { item ->
                            Text("• $item")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            entry.tags?.let { tags ->
                if (tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        tags.forEach { tag ->
                            TagChip(tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityChip(activity: Activity) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = activity.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmotionChip(emotion: Emotion) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "${emotion.name} (${emotion.intensity})",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 