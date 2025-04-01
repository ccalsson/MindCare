@Composable
fun PreferencesSection(
    preferences: UserPreferences,
    onPreferenceChanged: (PreferenceKey, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        Text(
            text = "App Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Notificaciones
        PreferenceSwitch(
            title = "Daily Reminders",
            description = "Receive daily meditation reminders",
            isChecked = preferences.dailyReminders,
            onCheckedChange = { onPreferenceChanged(PreferenceKey.DAILY_REMINDERS, it) }
        )

        PreferenceTimeSelector(
            title = "Reminder Time",
            selectedTime = preferences.reminderTime,
            enabled = preferences.dailyReminders,
            onTimeSelected = { onPreferenceChanged(PreferenceKey.REMINDER_TIME, it) }
        )

        PreferenceDivider()

        // Temas
        PreferenceRadioGroup(
            title = "Theme",
            options = listOf(
                "System" to ThemeMode.SYSTEM,
                "Light" to ThemeMode.LIGHT,
                "Dark" to ThemeMode.DARK
            ),
            selectedOption = preferences.themeMode,
            onOptionSelected = { onPreferenceChanged(PreferenceKey.THEME_MODE, it) }
        )

        PreferenceDivider()

        // Privacidad
        PreferenceSwitch(
            title = "Biometric Lock",
            description = "Require authentication to open app",
            isChecked = preferences.biometricLock,
            onCheckedChange = { onPreferenceChanged(PreferenceKey.BIOMETRIC_LOCK, it) }
        )

        PreferenceSwitch(
            title = "Data Sync",
            description = "Sync data across devices",
            isChecked = preferences.dataSync,
            onCheckedChange = { onPreferenceChanged(PreferenceKey.DATA_SYNC, it) }
        )
    }
}

@Composable
private fun PreferenceSwitch(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!isChecked) }
            .padding(Dimensions.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun PreferenceTimeSelector(
    title: String,
    selectedTime: LocalTime,
    enabled: Boolean,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { showTimePicker = true }
            .padding(Dimensions.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            }
        )
        Text(
            text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedTime,
            onTimeSelected = {
                onTimeSelected(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
private fun PreferenceDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.spacing.medium),
        color = MaterialTheme.colorScheme.surfaceVariant
    )
} 