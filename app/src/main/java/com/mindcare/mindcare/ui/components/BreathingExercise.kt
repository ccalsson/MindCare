@Composable
fun BreathingExercise(
    pattern: BreathingPattern,
    isActive: Boolean,
    onStateChange: (BreathingState) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    val scale by animateFloatAsState(
        targetValue = if (currentPhase == BreathingPhase.INHALE) 1.5f else 1f,
        animationSpec = tween(
            durationMillis = when (currentPhase) {
                BreathingPhase.INHALE -> pattern.inhaleTime
                BreathingPhase.HOLD -> pattern.holdTime
                BreathingPhase.EXHALE -> pattern.exhaleTime
            }
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    radius = size.minDimension / 2
                )
                drawCircle(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    radius = size.minDimension / 3
                )
                drawCircle(
                    color = MaterialTheme.colorScheme.primary,
                    radius = size.minDimension / 4
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        Text(
            text = when (currentPhase) {
                BreathingPhase.INHALE -> "Inhale"
                BreathingPhase.HOLD -> "Hold"
                BreathingPhase.EXHALE -> "Exhale"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        LinearProgressIndicator(
            progress = when (currentPhase) {
                BreathingPhase.INHALE -> scale - 1f
                BreathingPhase.HOLD -> 1f
                BreathingPhase.EXHALE -> 2f - scale
            } / 0.5f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
} 