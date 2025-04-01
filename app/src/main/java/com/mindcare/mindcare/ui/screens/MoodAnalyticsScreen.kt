package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindcare.mindcare.viewmodel.MoodAnalyticsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodAnalyticsScreen(
    viewModel: MoodAnalyticsViewModel = viewModel()
) {
    val moodPrediction by viewModel.currentMoodPrediction.collectAsState()
    val moodHistory by viewModel.moodHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mood Analytics",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mood Input Section
        MoodInputSection(
            onMoodSelected = { mood, intensity ->
                viewModel.addMoodEntry(mood, intensity)
            }
        )

        // Prediction Section
        moodPrediction?.let { prediction ->
            PredictionCard(prediction)
        }

        // History Section
        MoodHistorySection(moodHistory)
    }
}

@Composable
fun MoodInputSection(
    onMoodSelected: (String, Float) -> Unit
) {
    var selectedMood by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("How are you feeling?")
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoodButton("😊 Happy") { selectedMood = "happy" }
            MoodButton("😐 Neutral") { selectedMood = "neutral" }
            MoodButton("😢 Sad") { selectedMood = "sad" }
            MoodButton("😰 Anxious") { selectedMood = "anxious" }
        }

        Slider(
            value = intensity,
            onValueChange = { intensity = it },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = { onMoodSelected(selectedMood, intensity) },
            enabled = selectedMood.isNotEmpty(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Mood")
        }
    }
}

@Composable
fun PredictionCard(prediction: MoodPrediction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Prediction",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text("Predicted mood: ${prediction.predictedMood}")
            Text("Confidence: ${(prediction.confidence * 100).toInt()}%")
            
            Text(
                text = "Suggested Actions:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            prediction.suggestedActions.forEach { action ->
                Text("• $action")
            }
        }
    }
}

@Composable
fun MoodHistorySection(history: List<MoodEntry>) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Mood History",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        history.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(entry.mood)
                Text(dateFormat.format(entry.timestamp))
            }
        }
    }
}

@Composable
fun MoodButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text)
    }
} 