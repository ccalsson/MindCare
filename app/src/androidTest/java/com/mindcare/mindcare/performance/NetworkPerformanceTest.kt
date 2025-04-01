@HiltAndroidTest
class NetworkPerformanceTest : BaseInstrumentedTest() {
    @Inject
    lateinit var apiService: ApiService

    @Test
    fun `concurrent api calls performance`() = runTest {
        // Given
        val requests = List(20) { index ->
            async {
                apiService.getMeditation("meditation_$index")
            }
        }

        // When
        val startTime = System.nanoTime()
        val results = requests.awaitAll()
        val endTime = System.nanoTime()

        // Then
        val totalTimeMs = (endTime - startTime) / 1_000_000
        assertThat(totalTimeMs).isLessThan(5000) // Menos de 5 segundos para 20 llamadas
        assertThat(results).hasSize(20)
    }

    @Test
    fun `large payload handling`() = runTest {
        // Given
        val largeData = generateLargePayload()

        // When
        val startTime = System.nanoTime()
        val response = apiService.uploadData(largeData)
        val endTime = System.nanoTime()

        // Then
        val uploadTimeMs = (endTime - startTime) / 1_000_000
        assertThat(uploadTimeMs).isLessThan(10000) // Menos de 10 segundos para subida grande
        assertThat(response.isSuccessful).isTrue()
    }

    private fun generateLargePayload(): RequestBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("data", "large_file.dat", 
                RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    ByteArray(5 * 1024 * 1024) // 5MB de datos
                )
            )
            .build()
    }
} 