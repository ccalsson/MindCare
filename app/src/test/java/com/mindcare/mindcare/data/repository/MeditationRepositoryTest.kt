class MeditationRepositoryTest {
    private lateinit var repository: MeditationRepositoryImpl
    private lateinit var meditationDao: MeditationDao
    private lateinit var apiService: ApiService
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        meditationDao = mockk()
        apiService = mockk()
        database = mockk()
        repository = MeditationRepositoryImpl(meditationDao, apiService)
    }

    @Test
    fun `getMeditation returns combined local and remote data`() = runTest {
        // Given
        val meditationId = "1"
        val localMeditation = MeditationEntity(
            id = meditationId,
            type = "mindfulness",
            duration = 600
        )
        val remoteMeditation = MeditationDto(
            id = meditationId,
            type = "mindfulness",
            duration = 600,
            audioUrl = "http://example.com/audio.mp3"
        )

        coEvery { meditationDao.getMeditation(meditationId) } returns localMeditation
        coEvery { apiService.getMeditation(meditationId) } returns remoteMeditation

        // When
        val result = repository.getMeditation(meditationId)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo(meditationId)
        assertThat(result.audioUrl).isEqualTo(remoteMeditation.audioUrl)
    }

    @Test
    fun `saveMeditationSession stores session and syncs with server`() = runTest {
        // Given
        val session = MeditationSession(
            id = 1L,
            userId = "user1",
            duration = 600,
            completionRate = 1.0f
        )

        coEvery { meditationDao.insertSession(any()) } returns Unit
        coEvery { apiService.syncMeditationSession(any()) } returns Unit

        // When
        repository.saveMeditationSession(session)

        // Then
        coVerify { 
            meditationDao.insertSession(any())
            apiService.syncMeditationSession(any())
        }
    }

    @Test
    fun `when network error occurs, session is stored locally only`() = runTest {
        // Given
        val session = MeditationSession(
            id = 1L,
            userId = "user1",
            duration = 600,
            completionRate = 1.0f
        )

        coEvery { meditationDao.insertSession(any()) } returns Unit
        coEvery { apiService.syncMeditationSession(any()) } throws IOException()

        // When
        repository.saveMeditationSession(session)

        // Then
        coVerify { meditationDao.insertSession(any()) }
        coVerify(exactly = 1) { apiService.syncMeditationSession(any()) }
    }
} 