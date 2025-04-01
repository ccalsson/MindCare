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
import com.mindcare.mindcare.community.models.*
import com.mindcare.mindcare.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportGroupsScreen(
    viewModel: CommunityViewModel = viewModel()
) {
    val groups by viewModel.supportGroups.collectAsState()
    var selectedCategory by remember { mutableStateOf<GroupCategory?>(null) }
    var showJoinDialog by remember { mutableStateOf<SupportGroup?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grupos de Apoyo") },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar búsqueda */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtro por categorías
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // Lista de grupos
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    groups.filter { 
                        selectedCategory == null || it.category == selectedCategory 
                    }
                ) { group ->
                    GroupCard(
                        group = group,
                        onClick = { showJoinDialog = group }
                    )
                }
            }
        }

        // Diálogo para unirse al grupo
        showJoinDialog?.let { group ->
            JoinGroupDialog(
                group = group,
                onDismiss = { showJoinDialog = null },
                onJoin = { /* TODO: Implementar unirse al grupo */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(
    selectedCategory: GroupCategory?,
    onCategorySelected: (GroupCategory?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("Todos") }
                    )
                }
                
                items(GroupCategory.values()) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { onCategorySelected(category) },
                        label = { Text(formatCategory(category)) }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    group: SupportGroup,
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatCategory(group.category),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (group.isPrivate) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Grupo privado",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${group.members} miembros",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Forum,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${group.posts} publicaciones",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun JoinGroupDialog(
    group: SupportGroup,
    onDismiss: () -> Unit,
    onJoin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unirse a ${group.name}") },
        text = {
            Column {
                Text(group.description)
                if (group.isPrivate) {
                    Text(
                        "Este es un grupo privado. Tu solicitud será revisada por los moderadores.",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onJoin()
                onDismiss()
            }) {
                Text(if (group.isPrivate) "Solicitar unirse" else "Unirse")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatCategory(category: GroupCategory): String {
    return when (category) {
        GroupCategory.ANXIETY -> "Ansiedad"
        GroupCategory.DEPRESSION -> "Depresión"
        GroupCategory.STRESS -> "Estrés"
        GroupCategory.MINDFULNESS -> "Mindfulness"
        GroupCategory.GENERAL_SUPPORT -> "Apoyo General"
        GroupCategory.RECOVERY -> "Recuperación"
        GroupCategory.SELF_IMPROVEMENT -> "Desarrollo Personal"
    }
} 