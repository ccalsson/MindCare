package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mindcare.mindcare.meditation.models.*
import com.mindcare.mindcare.viewmodel.MeditationViewModel

@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel = viewModel()
) {
    val currentMeditation by viewModel.currentMeditation.collectAsState()
    val meditationProgress by viewModel.meditationProgress.collectAsState()
    val availableMeditations by viewModel.availableMeditations.collectAsState()

    if (currentMeditation != null) {
        MeditationPlayerScreen(
            meditation = currentMeditation!!,
            onClose = { viewModel.stopMeditation() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra superior con progreso
            meditationProgress?.let { progress ->
                MeditationProgressCard(progress)
            }

            // Categorías de meditación
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(availableMeditations) { meditation ->
                    MeditationCard(
                        meditation = meditation,
                        onClick = { viewModel.startMeditation(meditation) }
                    )
                }
            }
        }
    }
}

@Composable
fun MeditationProgressCard(progress: MeditationProgress) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tu Progreso",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat(
                    label = "Sesiones",
                    value = progress.totalSessions.toString()
                )
                ProgressStat(
                    label = "Minutos",
                    value = progress.totalMinutes.toString()
                )
                ProgressStat(
                    label = "Racha",
                    value = "${progress.currentStreak} días"
                )
            }
        }
    }
}

@Composable
fun MeditationCard(
    meditation: Meditation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
    ) {
        Column {
            AsyncImage(
                model = meditation.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = meditation.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${meditation.duration} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                    DifficultyChip(meditation.difficulty)
                }
            }
        }
    }
}

@Composable
fun MeditationPlayerScreen(
    meditation: Meditation,
    onClose: () -> Unit
) {
    val viewModel: MeditationViewModel = viewModel()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()

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
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
            Text(
                text = meditation.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Imagen de meditación
        AsyncImage(
            model = meditation.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f/9f)
                .padding(vertical = 16.dp),
            contentScale = ContentScale.Crop
        )

        // Instrucciones actuales
        currentStep?.let { step ->
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Controles de reproducción
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                value = progress,
                onValueChange = { viewModel.seekTo(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.seekTo(progress - 0.1f) }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Retroceder")
                }

                IconButton(
                    onClick = {
                        if (isPlaying) viewModel.pauseMeditation()
                        else viewModel.resumeMeditation()
                    }
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                    )
                }

                IconButton(onClick = { viewModel.seekTo(progress + 0.1f) }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Avanzar")
                }
            }
        }
    }
}

@Composable
fun ProgressStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun DifficultyChip(difficulty: MeditationDifficulty) {
    Surface(
        color = when (difficulty) {
            MeditationDifficulty.BEGINNER -> MaterialTheme.colorScheme.primary
            MeditationDifficulty.INTERMEDIATE -> MaterialTheme.colorScheme.secondary
            MeditationDifficulty.ADVANCED -> MaterialTheme.colorScheme.tertiary
        }.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = difficulty.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
} 