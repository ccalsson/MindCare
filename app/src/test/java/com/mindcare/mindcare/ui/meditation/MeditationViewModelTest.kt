@OptIn(ExperimentalCoroutinesApi::class)
class MeditationViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MeditationViewModel
    private lateinit var repository: MeditationRepository
    private lateinit var analyticsManager: AnalyticsManager

    @Before
    fun setup() {
        repository = mockk()
        analyticsManager = mockk(relaxed = true)
        viewModel = MeditationViewModel(repository, analyticsManager)
    }

    @Test
    fun `when starting meditation session, state updates correctly`() = runTest {
        // Given
        val meditation = Meditation(id = "1", duration = 600, type = "mindfulness")
        coEvery { repository.getMeditation("1") } returns meditation

        // When
        viewModel.startMeditation("1")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.currentMeditation).isEqualTo(meditation)
            assertThat(state.isSessionActive).isTrue()
            assertThat(state.remainingTime).isEqualTo(600)
        }
    }

    @Test
    fun `when completing meditation, analytics event is tracked`() = runTest {
        // Given
        val meditation = Meditation(id = "1", duration = 600, type = "mindfulness")
        coEvery { repository.getMeditation("1") } returns meditation
        coEvery { repository.saveMeditationSession(any()) } returns Unit

        // When
        viewModel.startMeditation("1")
        viewModel.completeMeditation()

        // Then
        verify {
            analyticsManager.trackMeditationProgress(
                meditation = meditation,
                progress = 1.0f
            )
        }
    }

    @Test
    fun `when error occurs, error state is updated`() = runTest {
        // Given
        coEvery { repository.getMeditation("1") } throws IOException()

        // When
        viewModel.startMeditation("1")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNotNull()
            assertThat(state.isLoading).isFalse()
        }
    }
} 