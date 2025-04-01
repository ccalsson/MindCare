@Composable
fun WeeklySummary(
    weekData: WeeklyData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        Text(
            text = "Weekly Overview",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryMetric(
                icon = Icons.Rounded.Timer,
                value = "${weekData.totalMeditationMinutes}",
                label = "Minutes\nMeditated",
                color = MaterialTheme.colorScheme.primary
            )

            SummaryMetric(
                icon = Icons.Rounded.Mood,
                value = weekData.averageMood.toString(),
                label = "Average\nMood",
                color = MaterialTheme.colorScheme.tertiary
            )

            SummaryMetric(
                icon = Icons.Rounded.CheckCircle,
                value = "${weekData.completedHabits}",
                label = "Habits\nCompleted",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Gráfico de progreso diario
        WeeklyProgressChart(
            data = weekData.dailyProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        if (weekData.insights.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Dimensions.spacing.large))

            Text(
                text = "Weekly Insights",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimensions.spacing.small))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing.small)
            ) {
                items(weekData.insights) { insight ->
                    InsightCard(insight = insight)
                }
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimensions.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.small))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
} 