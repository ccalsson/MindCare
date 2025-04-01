package com.mindcare.mindcare.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mindcare.mindcare.breathing.BreathingPhase
import com.mindcare.mindcare.breathing.models.*
import com.mindcare.mindcare.viewmodel.BreathingViewModel

@Composable
fun BreathingScreen(
    viewModel: BreathingViewModel = viewModel()
) {
    val currentExercise by viewModel.currentExercise.collectAsState()
    val breathingProgress by viewModel.breathingProgress.collectAsState()
    val availableExercises by viewModel.availableExercises.collectAsState()

    if (currentExercise != null) {
        BreathingExerciseScreen(
            exercise = currentExercise!!,
            onClose = { viewModel.stopExercise() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Ejercicios de Respiración",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Progreso del usuario
            breathingProgress?.let { progress ->
                BreathingProgressCard(progress)
            }

            // Lista de ejercicios
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(availableExercises) { exercise ->
                    BreathingExerciseCard(
                        exercise = exercise,
                        onClick = { viewModel.startExercise(exercise) }
                    )
                }
            }
        }
    }
}

@Composable
fun BreathingProgressCard(progress: BreathingProgress) {
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
                    label = "Reducción de Estrés",
                    value = "${(progress.averageStressReduction * 100).toInt()}%"
                )
            }
        }
    }
}

@Composable
fun BreathingExerciseCard(
    exercise: BreathingExercise,
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
                model = exercise.imageUrl,
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
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${exercise.duration / 60}:${exercise.duration % 60}",
                    style = MaterialTheme.typography.bodySmall
                )
                DifficultyChip(exercise.difficulty)
            }
        }
    }
}

@Composable
fun BreathingExerciseScreen(
    exercise: BreathingExercise,
    onClose: () -> Unit
) {
    val viewModel: BreathingViewModel = viewModel()
    val isActive by viewModel.isActive.collectAsState()
    val progress by viewModel.currentProgress.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = when {
                progress < 0.5f -> exercise.pattern.inhale * 1000
                else -> exercise.pattern.exhale * 1000
            }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Círculo animado de respiración
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp
            )
            Text(
                text = remainingTime.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    if (isActive) viewModel.pauseExercise()
                    else viewModel.resumeExercise()
                }
            ) {
                Icon(
                    if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isActive) "Pausar" else "Comenzar"
                )
            }

            IconButton(onClick = { viewModel.stopExercise() }) {
                Icon(Icons.Default.Stop, contentDescription = "Detener")
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
fun DifficultyChip(difficulty: BreathingDifficulty) {
    Surface(
        color = when (difficulty) {
            BreathingDifficulty.BEGINNER -> MaterialTheme.colorScheme.primary
            BreathingDifficulty.INTERMEDIATE -> MaterialTheme.colorScheme.secondary
            BreathingDifficulty.ADVANCED -> MaterialTheme.colorScheme.tertiary
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