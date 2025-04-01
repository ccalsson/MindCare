package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class Feature(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val isPremium: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMembership: () -> Unit,
    onNavigateToMeditation: () -> Unit,
    onNavigateToBreathing: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToSleep: () -> Unit,
    userName: String = "User"
) {
    var loadedFeatures by remember { mutableStateOf(setOf<String>()) }

    val basicFeatures = listOf(
        Feature(
            id = "meditation",
            title = "Meditación",
            icon = Icons.Default.SelfImprovement,
            onClick = onNavigateToMeditation
        ),
        Feature(
            id = "breathing",
            title = "Ejercicios de Respiración",
            icon = Icons.Default.Air,
            onClick = onNavigateToBreathing
        ),
        Feature(
            id = "mood",
            title = "Estado de Ánimo",
            icon = Icons.Default.Psychology,
            onClick = onNavigateToJournal
        ),
        Feature(
            id = "chat",
            title = "Chat IA",
            icon = Icons.Default.Chat,
            onClick = onNavigateToChat
        )
    )

    val premiumFeatures = listOf(
        Feature(
            id = "sleep",
            title = "Monitoreo de Sueño",
            icon = Icons.Default.Bedtime,
            isPremium = true,
            onClick = onNavigateToSleep
        ),
        Feature(
            id = "community",
            title = "Comunidad",
            icon = Icons.Default.Group,
            isPremium = true,
            onClick = onNavigateToCommunity
        ),
        Feature(
            id = "analytics",
            title = "Análisis Avanzado",
            icon = Icons.Default.Analytics,
            isPremium = true,
            onClick = { /* TODO */ }
        ),
        Feature(
            id = "goals",
            title = "Objetivos y Logros",
            icon = Icons.Default.EmojiEvents,
            isPremium = true,
            onClick = { /* TODO */ }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hola, $userName",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "¿Cómo te sientes hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(Icons.Default.Person, "Perfil")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(basicFeatures) { feature ->
                FeatureCard(feature = feature)
            }
            
            items(premiumFeatures) { feature ->
                FeatureCard(
                    feature = feature,
                    onClick = { 
                        if (feature.isPremium) {
                            onNavigateToMembership()
                        } else {
                            feature.onClick()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: Feature,
    onClick: () -> Unit = feature.onClick
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            if (feature.isPremium) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Premium",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
} 