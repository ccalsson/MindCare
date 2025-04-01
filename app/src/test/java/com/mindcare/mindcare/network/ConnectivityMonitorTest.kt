class ConnectivityMonitorTest {
    private lateinit var connectivityMonitor: ConnectivityMonitor
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: NetworkCallback
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        
        connectivityMonitor = ConnectivityMonitor(context)
    }

    @Test
    fun `isNetworkAvailable returns correct state`() {
        // Given
        val networkCapabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        // When
        val isAvailable = connectivityMonitor.isNetworkAvailable()

        // Then
        assertThat(isAvailable).isTrue()
    }

    @Test
    fun `network state changes are reported correctly`() = runTest {
        // Given
        val stateFlow = connectivityMonitor.networkState

        // When
        connectivityMonitor.onCapabilitiesChanged(mockk(), mockk {
            every { hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        })

        // Then
        stateFlow.test {
            assertThat(awaitItem()).isEqualTo(NetworkState.AVAILABLE)
        }
    }
} 