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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mindcare.mindcare.community.models.SupportGroup

@Composable
fun GroupMembersDialog(
    group: SupportGroup,
    onDismiss: () -> Unit
) {
    val members = remember {
        listOf(
            Member("user123", "María García", null, true),
            Member("user456", "Juan Pérez", "avatar_url", false),
            Member("user789", "Ana López", "avatar_url", false)
            // Más miembros...
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Miembros (${group.members})",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members) { member ->
                    MemberItem(member)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun MemberItem(member: Member) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        member.avatar?.let { avatar ->
            AsyncImage(
                model = avatar,
                contentDescription = "Avatar",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (member.isModerator) {
                Text(
                    text = "Moderador",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GroupRulesDialog(
    group: SupportGroup,
    onDismiss: () -> Unit
) {
    val rules = remember {
        listOf(
            "Respeta a todos los miembros del grupo",
            "Mantén la confidencialidad de las publicaciones",
            "No compartas información personal sensible",
            "Evita el lenguaje ofensivo o discriminatorio",
            "Las publicaciones deben estar relacionadas con el tema del grupo",
            "No promociones productos o servicios sin autorización",
            "Reporta cualquier comportamiento inapropiado",
            "Sé constructivo en tus comentarios"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reglas del Grupo",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Para mantener un ambiente seguro y de apoyo, todos los miembros deben seguir estas reglas:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rules) { rule ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (group.isPrivate) {
                    Text(
                        text = "Este es un grupo privado. El incumplimiento de las reglas puede resultar en la expulsión del grupo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}

private data class Member(
    val id: String,
    val name: String,
    val avatar: String?,
    val isModerator: Boolean
) 