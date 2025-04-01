@Composable
fun ProgressChart(
    data: List<ProgressPoint>,
    modifier: Modifier = Modifier,
    maxValue: Float = data.maxOf { it.value },
    showLabels: Boolean = true
) {
    val points = remember(data) {
        data.mapIndexed { index, point ->
            Offset(
                x = index * (1f / (data.size - 1)),
                y = 1f - (point.value / maxValue)
            )
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(Dimensions.spacing.medium)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Dibujar líneas de guía
                val strokePath = Path().apply {
                    points.forEachIndexed { index, point ->
                        val x = point.x * size.width
                        val y = point.y * size.height
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }

                // Área bajo la curva
                drawPath(
                    path = strokePath.addRect(
                        Rect(
                            offset = Offset(0f, size.height),
                            size = Size(size.width, 0f)
                        )
                    ),
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
                        )
                    )
                )

                // Línea de progreso
                drawPath(
                    path = strokePath,
                    color = MaterialTheme.colorScheme.primary,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Puntos de datos
                points.forEach { point ->
                    drawCircle(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 6.dp.toPx(),
                        center = Offset(
                            x = point.x * size.width,
                            y = point.y * size.height
                        )
                    )
                }
            }
        }

        if (showLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { point ->
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
} 