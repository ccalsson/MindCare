@Composable
fun StatsCard(
    stats: UserStats,
    modifier: Modifier = Modifier
) {
    MindCareCard(
        title = "Your Progress",
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing.medium)
        ) {
            StatItem(
                icon = Icons.Rounded.Timer,
                label = "Total Meditation Time",
                value = "${stats.totalMeditationMinutes} mins"
            )
            
            StatItem(
                icon = Icons.Rounded.Whatshot,
                label = "Current Streak",
                value = "${stats.currentStreak} days"
            )
            
            StatItem(
                icon = Icons.Rounded.EmojiEmotions,
                label = "Mood Improvement",
                value = "${stats.moodImprovement}%",
                color = if (stats.moodImprovement > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(Dimensions.spacing.medium))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
} 