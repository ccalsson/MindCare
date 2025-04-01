package com.mindcare.mindcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mindcare.mindcare.notifications.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NotificationIcon(type = notification.type)
                Column {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatDateTime(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (!notification.isRead) {
                Badge(
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReminderIcon(type = reminder.type)
            Column {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = formatTime(reminder.time),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatFrequency(reminder.frequency),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = reminder.isEnabled,
                onCheckedChange = onToggle
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar recordatorio",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun NotificationIcon(type: NotificationType) {
    val icon = when (type) {
        NotificationType.BREATHING_REMINDER -> Icons.Default.Air
        NotificationType.JOURNAL_REMINDER -> Icons.Default.Edit
        NotificationType.GROUP_ACTIVITY -> Icons.Default.Group
        NotificationType.SUPPORT_MESSAGE -> Icons.Default.Message
        NotificationType.ACHIEVEMENT -> Icons.Default.Star
        NotificationType.SYSTEM -> Icons.Default.Info
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ReminderIcon(type: ReminderType) {
    val icon = when (type) {
        ReminderType.BREATHING_EXERCISE -> Icons.Default.Air
        ReminderType.JOURNAL_ENTRY -> Icons.Default.Edit
        ReminderType.MEDITATION -> Icons.Default.SelfImprovement
        ReminderType.WATER -> Icons.Default.WaterDrop
        ReminderType.MEDICATION -> Icons.Default.Medication
        ReminderType.CUSTOM -> Icons.Default.Notifications
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.secondary
    )
}

private fun formatDateTime(date: Date): String {
    val now = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply { time = date }
    
    return when {
        calendar.get(Calendar.DATE) == now.get(Calendar.DATE) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        }
        calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
            SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(date)
        }
        else -> {
            SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(date)
        }
    }
}

private fun formatTime(date: Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}

private fun formatFrequency(frequency: ReminderFrequency): String {
    return when (frequency) {
        ReminderFrequency.ONCE -> "Una vez"
        ReminderFrequency.DAILY -> "Diariamente"
        ReminderFrequency.WEEKLY -> "Semanalmente"
        ReminderFrequency.CUSTOM -> "Personalizado"
    }
} 