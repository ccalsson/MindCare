@Composable
fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacing.medium),
            contentPadding = PaddingValues(horizontal = Dimensions.spacing.medium)
        ) {
            items(Mood.values()) { mood ->
                MoodItem(
                    mood = mood,
                    isSelected = mood == selectedMood,
                    onSelect = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
private fun MoodItem(
    mood: Mood,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(Dimensions.radius.medium))
            .clickable(onClick = onSelect)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .padding(Dimensions.spacing.medium)
    ) {
        Icon(
            imageVector = mood.icon,
            contentDescription = mood.description,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacing.small))
        
        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
} 