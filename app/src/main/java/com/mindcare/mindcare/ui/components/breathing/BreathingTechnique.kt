@Composable
fun BreathingTechnique(
    technique: BreathingTechniqueData,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPhase by remember { mutableStateOf(BreathingPhase.PREPARE) }
    var cyclesCompleted by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título y descripción
        Text(
            text = technique.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        Text(
            text = technique.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Animación de respiración
        BreathingAnimation(
            phase = currentPhase,
            onPhaseComplete = {
                currentPhase = when (currentPhase) {
                    BreathingPhase.PREPARE -> BreathingPhase.INHALE
                    BreathingPhase.INHALE -> BreathingPhase.HOLD
                    BreathingPhase.HOLD -> BreathingPhase.EXHALE
                    BreathingPhase.EXHALE -> {
                        cyclesCompleted++
                        if (cyclesCompleted >= technique.cycles) {
                            onComplete()
                            BreathingPhase.PREPARE
                        } else {
                            BreathingPhase.INHALE
                        }
                    }
                }
            },
            phaseDuration = when (currentPhase) {
                BreathingPhase.PREPARE -> 3000
                BreathingPhase.INHALE -> technique.inhaleTime
                BreathingPhase.HOLD -> technique.holdTime
                BreathingPhase.EXHALE -> technique.exhaleTime
            }
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Indicador de progreso
        Text(
            text = when (currentPhase) {
                BreathingPhase.PREPARE -> "Get ready..."
                BreathingPhase.INHALE -> "Inhale"
                BreathingPhase.HOLD -> "Hold"
                BreathingPhase.EXHALE -> "Exhale"
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing.medium))

        // Progreso de ciclos
        LinearProgressIndicator(
            progress = cyclesCompleted.toFloat() / technique.cycles,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Text(
            text = "Cycle ${cyclesCompleted + 1} of ${technique.cycles}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 