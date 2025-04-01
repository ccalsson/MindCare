package com.mindcare.mindcare.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindcare.mindcare.breathing.models.BreathingPhase
import com.mindcare.mindcare.viewmodel.BreathingExerciseViewModel
import kotlin.math.min

@Composable
fun BreathingExerciseScreen(
    viewModel: BreathingExerciseViewModel = viewModel()
) {
    val currentPhase by viewModel.currentPhase.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val isActive by viewModel.isExerciseActive.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Breathing Exercise",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BreathingCircle(
            phase = currentPhase,
            timeRemaining = timeRemaining,
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Phase: ${currentPhase.name}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Heart Rate: ${heartRate.toInt()} BPM",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { 
                    if (isActive) viewModel.pauseExercise()
                    else viewModel.startExercise()
                }
            ) {
                Text(if (isActive) "Pause" else "Start")
            }

            if (!isActive) {
                Button(onClick = { viewModel.resumeExercise() }) {
                    Text("Resume")
                }
            }
        }
    }
}

@Composable
fun BreathingCircle(
    phase: BreathingPhase,
    timeRemaining: Long,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    
    val scale by transition.animateFloat(
        initialValue = when (phase) {
            BreathingPhase.INHALE -> 0.5f
            BreathingPhase.HOLD -> 1f
            BreathingPhase.EXHALE -> 1f
            BreathingPhase.REST -> 0.5f
        },
        targetValue = when (phase) {
            BreathingPhase.INHALE -> 1f
            BreathingPhase.HOLD -> 1f
            BreathingPhase.EXHALE -> 0.5f
            BreathingPhase.REST -> 0.5f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = timeRemaining.toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val radius = min(size.width, size.height) / 2
        drawCircle(
            color = Color(0xFF6200EE),
            radius = radius * scale,
            style = Stroke(width = 8f)
        )
    }
} 