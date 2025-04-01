@Composable
fun GratitudeJournal(
    entries: List<GratitudeEntry>,
    onAddEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newEntry by remember { mutableStateOf("") }
    var isAddingEntry by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gratitude Journal",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                onClick = { isAddingEntry = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add entry"
                )
            }
        }

        AnimatedVisibility(
            visible = isAddingEntry,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.spacing.medium)
            ) {
                MindCareInput(
                    value = newEntry,
                    onValueChange = { newEntry = it },
                    label = "What are you grateful for?",
                    maxLines = 3
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.spacing.medium),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { isAddingEntry = false }
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onAddEntry(newEntry)
                            newEntry = ""
                            isAddingEntry = false
                        },
                        enabled = newEntry.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing.medium)
        ) {
            items(entries) { entry ->
                GratitudeEntryItem(entry = entry)
            }
        }
    }
} 