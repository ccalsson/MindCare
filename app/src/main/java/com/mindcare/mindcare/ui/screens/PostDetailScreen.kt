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
fun PostDetailScreen(
    post: Post,
    onClose: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val comments by viewModel.comments.collectAsState()
    var showCommentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicación") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCommentDialog = true }) {
                Icon(Icons.Default.Comment, contentDescription = "Comentar")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Publicación principal
            item {
                DetailedPostCard(post)
            }

            // Comentarios
            items(comments) { comment ->
                CommentCard(comment)
            }
        }

        if (showCommentDialog) {
            NewCommentDialog(
                onDismiss = { showCommentDialog = false },
                onComment = { content, isAnonymous ->
                    viewModel.addComment(post.id, content, isAnonymous)
                    showCommentDialog = false
                }
            )
        }
    }
}

@Composable
fun DetailedPostCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado con información del usuario
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
                            modifier = Modifier.size(40.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido principal
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )

            // Imagen adjunta
            post.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Etiquetas
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    post.tags.forEach { tag ->
                        TagChip(tag)
                    }
                }
            }

            // Estadísticas
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.likes} me gusta")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.comments} comentarios")
                }
            }
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    comment.userAvatar?.let { avatar ->
                        AsyncImage(
                            model = avatar,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column {
                        Text(
                            text = comment.userName,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = formatDate(comment.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (comment.likes > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = comment.likes.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCommentDialog(
    onDismiss: () -> Unit,
    onComment: (String, Boolean) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Comentario") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Tu comentario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(
                        checked = isAnonymous,
                        onCheckedChange = { isAnonymous = it }
                    )
                    Text("Comentar anónimamente")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onComment(content, isAnonymous) },
                enabled = content.isNotBlank()
            ) {
                Text("Comentar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 