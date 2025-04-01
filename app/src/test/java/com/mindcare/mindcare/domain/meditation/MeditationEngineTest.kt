class MeditationEngineTest {
    private lateinit var meditationEngine: MeditationEngine
    private lateinit var audioManager: AudioManager
    private lateinit var vibrationManager: VibrationManager
    
    @Before
    fun setup() {
        audioManager = mockk(relaxed = true)
        vibrationManager = mockk(relaxed = true)
        meditationEngine = MeditationEngine(audioManager, vibrationManager)
    }

    @Test
    fun `breathing exercise follows correct pattern`() = runTest {
        // Given
        val pattern = BreathingPattern.BOX_BREATHING
        
        // When
        meditationEngine.startBreathingExercise(pattern)
        
        // Then
        verifySequence {
            vibrationManager.vibrate(pattern.inhaleTime)
            vibrationManager.pause(pattern.holdTime)
            vibrationManager.vibrate(pattern.exhaleTime)
            vibrationManager.pause(pattern.holdTime)
        }
    }

    @Test
    fun `guided meditation plays correct audio segments`() = runTest {
        // Given
        val meditation = GuidedMeditation(
            segments = listOf(
                AudioSegment("intro.mp3", 10),
                AudioSegment("main.mp3", 300),
                AudioSegment("outro.mp3", 10)
            )
        )
        
        // When
        meditationEngine.startGuidedMeditation(meditation)
        
        // Then
        verifySequence {
            audioManager.playAudio("intro.mp3")
            audioManager.playAudio("main.mp3")
            audioManager.playAudio("outro.mp3")
        }
    }
} 