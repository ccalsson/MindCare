@Composable
fun SuccessFeedback(
    message: String,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium),
        shape = RoundedCornerShape(Dimensions.radius.medium),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = Dimensions.elevation.small
    ) {
        Row(
            modifier = Modifier
                .padding(Dimensions.spacing.medium)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Dimensions.spacing.medium))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Dismiss"
                )
            }
        }
    }
} 