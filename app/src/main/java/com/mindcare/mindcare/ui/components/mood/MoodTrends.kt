@Composable
fun MoodTrends(
    moodData: List<MoodEntry>,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf(timeRange) }
    val groupedData = remember(moodData, selectedRange) {
        moodData.groupByTimeRange(selectedRange)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        // Selector de rango de tiempo
        TimeRangeSelector(
            selectedRange = selectedRange,
            onRangeSelected = { selectedRange = it }
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Gráfico de tendencias
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val points = groupedData.map { entry ->
                    Offset(
                        x = entry.timestamp.toFloat() / groupedData.last().timestamp,
                        y = entry.mood.value.toFloat() / Mood.values().size
                    )
                }

                // Dibujar línea de tendencia
                drawPath(
                    path = Path().apply {
                        points.forEachIndexed { index, point ->
                            if (index == 0) moveTo(point.x * size.width, point.y * size.height)
                            else lineTo(point.x * size.width, point.y * size.height)
                        }
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.cornerPathEffect(16.dp.toPx())
                    )
                )

                // Dibujar puntos
                points.forEach { point ->
                    drawCircle(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 4.dp.toPx(),
                        center = Offset(point.x * size.width, point.y * size.height)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Estadísticas
        MoodStats(groupedData)
    }
}

@Composable
private fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeRange.values().forEach { range ->
            TextButton(
                onClick = { onRangeSelected(range) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (range == selectedRange) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            ) {
                Text(range.label)
            }
        }
    }
}

@Composable
private fun MoodStats(data: List<MoodEntry>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(
            title = "Average Mood",
            value = data.averageMood().toString(),
            icon = Icons.Rounded.TrendingUp
        )
        StatCard(
            title = "Most Common",
            value = data.mostCommonMood().label,
            icon = Icons.Rounded.Favorite
        )
        StatCard(
            title = "Entries",
            value = data.size.toString(),
            icon = Icons.Rounded.Assessment
        )
    }
} 