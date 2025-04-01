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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mindcare.mindcare.community.models.*
import com.mindcare.mindcare.viewmodel.CommunityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedPost by viewModel.selectedPost.collectAsState()
    var showNewPostDialog by remember { mutableStateOf(false) }

    if (selectedPost != null) {
        PostDetailScreen(
            post = selectedPost!!,
            onClose = { viewModel.clearSelectedPost() }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Comunidad") },
                    actions = {
                        IconButton(onClick = { showNewPostDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
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
                // Perfil del usuario actual
                currentUser?.let { user ->
                    UserProfileCard(user)
                }

                // Lista de publicaciones
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(posts) { post ->
                        PostCard(
                            post = post,
                            onPostClick = { viewModel.selectPost(post) },
                            onLikeClick = { viewModel.likePost(post.id) }
                        )
                    }
                }
            }

            if (showNewPostDialog) {
                NewPostDialog(
                    onDismiss = { showNewPostDialog = false },
                    onPost = { content, mood, tags, isAnonymous ->
                        viewModel.createPost(
                            content = content,
                            mood = mood,
                            tags = tags,
                            isAnonymous = isAnonymous
                        )
                        showNewPostDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun UserProfileCard(user: CommunityUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    user.bio?.let { bio ->
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                user.avatar?.let { avatar ->
                    AsyncImage(
                        model = avatar,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Publicaciones", user.stats.postsCount.toString())
                StatItem("Me gusta", user.stats.likesReceived.toString())
                StatItem("Apoyo", "${(user.stats.supportScore * 100).toInt()}%")
            }

            // Insignias
            if (user.badges.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    user.badges.forEach { badge ->
                        BadgeChip(badge)
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onPostClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Card(
        onClick = onPostClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    post.userAvatar?.let { avatar ->
                        AsyncImage(
                            model = avatar,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column {
                        Text(
                            text = post.userName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = formatDate(post.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                post.mood?.let { mood ->
                    Text(
                        text = mood,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Contenido
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Imagen
            post.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Etiquetas
            if (post.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    post.tags.forEach { tag ->
                        TagChip(tag)
                    }
                }
            }

            // Acciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onLikeClick,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Me gusta",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.likes.toString())
                }

                TextButton(
                    onClick = onPostClick,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = "Comentarios",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.comments.toString())
                }
            }
        }
    }
}

@Composable
fun BadgeChip(badge: Badge) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(badge.icon)
            Text(
                text = badge.name,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(date)
}

@Composable
fun NewPostDialog(
    onDismiss: () -> Unit,
    onPost: (String, String, List<String>, Boolean) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Post") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("¿Qué quieres compartir?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                
                OutlinedTextField(
                    value = mood,
                    onValueChange = { mood = it },
                    label = { Text("Estado de ánimo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (separados por comas)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Checkbox(
                    checked = isAnonymous,
                    onCheckedChange = { isAnonymous = it },
                    label = { Text("Anónimo") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPost(
                        content,
                        mood,
                        tags.split(",").map { it.trim() },
                        isAnonymous
                    )
                },
                enabled = content.isNotEmpty() && mood.isNotEmpty()
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