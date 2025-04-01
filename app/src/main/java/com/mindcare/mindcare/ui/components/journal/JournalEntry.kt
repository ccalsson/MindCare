@Composable
fun JournalEntryComposer(
    onSave: (JournalEntryData) -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showTagSelector by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        // Selector de estado de ánimo
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        MoodSelector(
            selectedMood = selectedMood,
            onMoodSelected = { selectedMood = it }
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Campo de texto para la entrada
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            placeholder = {
                Text("Write your thoughts here...")
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        // Selector de etiquetas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacing.small)
            ) {
                selectedTags.forEach { tag ->
                    TagChip(
                        text = tag,
                        onRemove = { selectedTags -= tag }
                    )
                }
                IconButton(
                    onClick = { showTagSelector = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add tag"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Botón de guardar
        Button(
            onClick = {
                selectedMood?.let { mood ->
                    onSave(
                        JournalEntryData(
                            content = content,
                            mood = mood,
                            tags = selectedTags,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = content.isNotBlank() && selectedMood != null
        ) {
            Icon(
                imageVector = Icons.Rounded.Save,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(Dimensions.spacing.small))
            Text("Save Entry")
        }
    }

    if (showTagSelector) {
        TagSelectorDialog(
            selectedTags = selectedTags,
            onTagsSelected = { selectedTags = it },
            onDismiss = { showTagSelector = false }
        )
    }
}

@Composable
private fun TagChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(
                start = Dimensions.spacing.medium,
                end = Dimensions.spacing.small,
                vertical = Dimensions.spacing.small
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove tag",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
} 