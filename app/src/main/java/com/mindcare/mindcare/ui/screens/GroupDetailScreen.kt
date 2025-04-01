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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindcare.mindcare.community.models.*
import com.mindcare.mindcare.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: SupportGroup,
    onClose: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()
    var showNewPostDialog by remember { mutableStateOf(false) }
    var showMembersDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showMembersDialog = true }) {
                        Icon(Icons.Default.Group, contentDescription = "Miembros")
                    }
                    IconButton(onClick = { showRulesDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Reglas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewPostDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Información del grupo
            GroupHeader(group)

            // Lista de publicaciones
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(posts.filter { it.tags.contains(group.id) }) { post ->
                    PostCard(
                        post = post,
                        onPostClick = { viewModel.selectPost(post) },
                        onLikeClick = { viewModel.likePost(post.id) }
                    )
                }
            }
        }

        if (showNewPostDialog) {
            NewGroupPostDialog(
                group = group,
                onDismiss = { showNewPostDialog = false },
                onPost = { content, mood, isAnonymous ->
                    viewModel.createPost(
                        content = content,
                        mood = mood,
                        tags = listOf(group.id),
                        isAnonymous = isAnonymous
                    )
                    showNewPostDialog = false
                }
            )
        }

        if (showMembersDialog) {
            GroupMembersDialog(
                group = group,
                onDismiss = { showMembersDialog = false }
            )
        }

        if (showRulesDialog) {
            GroupRulesDialog(
                group = group,
                onDismiss = { showRulesDialog = false }
            )
        }
    }
}

@Composable
fun GroupHeader(group: SupportGroup) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = formatCategory(group.category),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GroupStat(
                    icon = Icons.Default.Group,
                    value = group.members.toString(),
                    label = "Miembros"
                )
                GroupStat(
                    icon = Icons.Default.Forum,
                    value = group.posts.toString(),
                    label = "Publicaciones"
                )
                if (group.isPrivate) {
                    GroupStat(
                        icon = Icons.Default.Lock,
                        value = group.moderators.size.toString(),
                        label = "Moderadores"
                    )
                }
            }
        }
    }
}

@Composable
fun GroupStat(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupPostDialog(
    group: SupportGroup,
    onDismiss: () -> Unit,
    onPost: (String, String, Boolean) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva publicación en ${group.name}") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("¿Qué quieres compartir?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                OutlinedTextField(
                    value = mood,
                    onValueChange = { mood = it },
                    label = { Text("¿Cómo te sientes?") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAnonymous,
                        onCheckedChange = { isAnonymous = it }
                    )
                    Text("Publicar anónimamente")
                }

                if (group.isPrivate) {
                    Text(
                        text = "Esta publicación solo será visible para los miembros del grupo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPost(content, mood, isAnonymous) },
                enabled = content.isNotBlank()
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 