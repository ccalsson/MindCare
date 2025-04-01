@Composable
fun InAppNotification(
    message: String,
    type: NotificationType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    action: NotificationAction? = null
) {
    val backgroundColor = when (type) {
        NotificationType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
        NotificationType.ERROR -> MaterialTheme.colorScheme.errorContainer
        NotificationType.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        NotificationType.INFO -> MaterialTheme.colorScheme.secondaryContainer
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimensions.spacing.medium),
            color = backgroundColor,
            shape = RoundedCornerShape(Dimensions.radius.medium),
            tonalElevation = Dimensions.elevation.small
        ) {
            Row(
                modifier = Modifier
                    .padding(Dimensions.spacing.medium)
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(Dimensions.spacing.medium))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                action?.let { action ->
                    TextButton(onClick = action.onClick) {
                        Text(text = action.text)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Dismiss"
                    )
                }
            }
        }
    }
} 