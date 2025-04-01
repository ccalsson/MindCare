class PermissionManagerTest {
    private lateinit var permissionManager: PermissionManager
    private lateinit var context: Context
    private lateinit var activityProvider: ActivityProvider
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        activityProvider = mockk()
        permissionManager = PermissionManager(context, activityProvider)
    }

    @Test
    fun `checkAndRequestPermissions handles all required permissions`() = runTest {
        // Given
        val requiredPermissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        
        every { context.checkSelfPermission(any()) } returns PackageManager.PERMISSION_DENIED
        coEvery { activityProvider.requestPermissions(any(), any()) } returns mapOf(
            Manifest.permission.CAMERA to true,
            Manifest.permission.RECORD_AUDIO to true,
            Manifest.permission.ACCESS_FINE_LOCATION to false
        )

        // When
        val result = permissionManager.checkAndRequestPermissions(requiredPermissions)

        // Then
        assertThat(result.granted).containsExactly(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        assertThat(result.denied).containsExactly(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @Test
    fun `shouldShowRationale returns correct state`() {
        // Given
        val activity = mockk<Activity>()
        every { activityProvider.getCurrentActivity() } returns activity
        every { activity.shouldShowRequestPermissionRationale(any()) } returns true

        // When
        val shouldShow = permissionManager.shouldShowRationale(Manifest.permission.CAMERA)

        // Then
        assertThat(shouldShow).isTrue()
    }
} 