class BiometricManagerTest {
    private lateinit var biometricManager: BiometricManager
    private lateinit var context: Context
    private lateinit var systemBiometricManager: androidx.biometric.BiometricManager
    private lateinit var biometricPrompt: BiometricPrompt
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        systemBiometricManager = mockk()
        biometricPrompt = mockk()
        
        every { context.getSystemService(Context.BIOMETRIC_SERVICE) } returns systemBiometricManager
        biometricManager = BiometricManager(context)
    }

    @Test
    fun `canAuthenticate returns correct capability`() {
        // Given
        every { 
            systemBiometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        } returns BiometricManager.BIOMETRIC_SUCCESS

        // When
        val canAuthenticate = biometricManager.canAuthenticate()

        // Then
        assertThat(canAuthenticate).isTrue()
    }

    @Test
    fun `authenticate handles success correctly`() = runTest {
        // Given
        val result = mockk<BiometricPrompt.AuthenticationResult>()
        
        // When
        biometricManager.authenticate()
        biometricManager.onAuthenticationSucceeded(result)

        // Then
        assertThat(biometricManager.authenticationState.value)
            .isEqualTo(AuthenticationState.AUTHENTICATED)
    }
} 