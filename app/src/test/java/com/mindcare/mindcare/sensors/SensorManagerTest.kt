class SensorManagerTest {
    private lateinit var sensorManager: SensorManager
    private lateinit var systemSensorManager: android.hardware.SensorManager
    private lateinit var heartRateCallback: HeartRateCallback
    
    @Before
    fun setup() {
        systemSensorManager = mockk(relaxed = true)
        heartRateCallback = mockk(relaxed = true)
        sensorManager = SensorManager(systemSensorManager)
    }

    @Test
    fun `registerHeartRateSensor succeeds with available sensor`() {
        // Given
        val sensor = mockk<Sensor>()
        every { systemSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) } returns sensor
        every { systemSensorManager.registerListener(any(), sensor, any()) } returns true

        // When
        val result = sensorManager.registerHeartRateSensor(heartRateCallback)

        // Then
        assertThat(result.isSuccess).isTrue()
        verify { systemSensorManager.registerListener(any(), sensor, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    @Test
    fun `handleSensorEvent processes heart rate data correctly`() {
        // Given
        val sensorEvent = mockk<SensorEvent> {
            every { values } returns floatArrayOf(75.0f)
            every { sensor.type } returns Sensor.TYPE_HEART_RATE
        }

        // When
        sensorManager.onSensorChanged(sensorEvent)

        // Then
        verify { heartRateCallback.onHeartRateChanged(75.0f) }
    }
} 