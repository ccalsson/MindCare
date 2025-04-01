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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindcare.mindcare.gamification.models.*
import com.mindcare.mindcare.viewmodel.QuestViewModel

@Composable
fun QuestScreen(
    viewModel: QuestViewModel = viewModel()
) {
    val dailyQuests by viewModel.dailyQuests.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barra superior con progreso del usuario
        UserProgressBar(userProgress)

        // Lista de misiones diarias
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dailyQuests) { quest ->
                QuestCard(
                    quest = quest,
                    onActivityComplete = { activityId ->
                        viewModel.completeActivity(quest.id, activityId)
                    },
                    onClaimReward = { reward ->
                        viewModel.claimReward(quest.id, reward)
                    }
                )
            }
        }
    }
}

@Composable
fun UserProgressBar(userProgress: UserProgress?) {
    userProgress?.let { progress ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
                        text = "Nivel ${progress.level}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "XP: ${progress.experience}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                LinearProgressIndicator(
                    progress = calculateLevelProgress(progress),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Racha: ${progress.streakDays} días")
                    Text("Logros: ${progress.achievements.size}")
                }
            }
        }
    }
}

@Composable
fun QuestCard(
    quest: Quest,
    onActivityComplete: (Long) -> Unit,
    onClaimReward: (Reward) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado de la misión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = quest.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = quest.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir"
                    )
                }
            }

            // Detalles expandibles
            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Lista de actividades
                Text(
                    text = "Actividades",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                quest.activities.forEach { activity ->
                    ActivityItem(
                        activity = activity,
                        onComplete = { onActivityComplete(activity.id) }
                    )
                }

                // Recompensas
                if (quest.rewards.isNotEmpty()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Recompensas",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    quest.rewards.forEach { reward ->
                        RewardItem(
                            reward = reward,
                            onClaim = { onClaimReward(reward) }
                        )
                    }
                }
            }

            // Indicador de dificultad
            DifficultyBadge(quest.difficulty)
        }
    }
}

@Composable
fun ActivityItem(
    activity: Activity,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${activity.duration} min",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Checkbox(
            checked = activity.isCompleted,
            onCheckedChange = { if (!activity.isCompleted) onComplete() }
        )
    }
}

@Composable
fun RewardItem(
    reward: Reward,
    onClaim: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = reward.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onClaim,
            enabled = true // Aquí podrías agregar lógica para determinar si se puede reclamar
        ) {
            Text("Reclamar")
        }
    }
}

@Composable
fun DifficultyBadge(difficulty: QuestDifficulty) {
    val color = when (difficulty) {
        QuestDifficulty.EASY -> Color.Green
        QuestDifficulty.MEDIUM -> Color.Yellow
        QuestDifficulty.HARD -> Color.Red
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = difficulty.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color
        )
    }
}

private fun calculateLevelProgress(progress: UserProgress): Float {
    val xpPerLevel = 1000 // Ejemplo simple
    return (progress.experience % xpPerLevel) / xpPerLevel.toFloat()
} 