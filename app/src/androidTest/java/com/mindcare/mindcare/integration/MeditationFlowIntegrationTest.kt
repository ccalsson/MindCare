@HiltAndroidTest
class MeditationFlowIntegrationTest : BaseInstrumentedTest() {
    @Inject lateinit var meditationRepository: MeditationRepository
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var audioManager: AudioManager

    @Test
    fun completeMeditationFlow_updatesAllSystems() = runTest {
        // Given - Usuario autenticado y meditación seleccionada
        val user = createAndAuthenticateUser()
        val meditation = meditationRepository.getMeditation("mindfulness_1")

        // When - Completar sesión de meditación
        launchActivity {
            // Navegar a meditación
            onView(withId(R.id.navigation_meditation)).perform(click())
            
            // Seleccionar y comenzar meditación
            onView(withText(meditation.title)).perform(click())
            onView(withId(R.id.startButton)).perform(click())

            // Simular progreso de meditación
            advanceTimeBy(meditation.duration * 1000L)
            
            // Completar meditación
            onView(withId(R.id.completeButton)).perform(click())
        }

        // Then - Verificar actualizaciones en todos los sistemas
        // 1. Verificar registro en base de datos
        val sessions = meditationRepository.getUserSessions(user.id)
        assertThat(sessions).hasSize(1)
        assertThat(sessions[0].completionRate).isEqualTo(1.0f)

        // 2. Verificar estadísticas de usuario
        val updatedStats = userRepository.getUserStats(user.id)
        assertThat(updatedStats.totalMeditationMinutes)
            .isEqualTo(meditation.duration / 60)

        // 3. Verificar eventos de analytics
        verify {
            analyticsManager.trackEvent(
                event = "meditation_completed",
                params = match { it["meditation_id"] == meditation.id }
            )
        }

        // 4. Verificar limpieza de recursos
        verify {
            audioManager.releaseResources()
        }
    }

    @Test
    fun interruptedMeditationFlow_handlesStateCorrectly() = runTest {
        // Given
        val user = createAndAuthenticateUser()
        
        launchActivity {
            // Iniciar meditación
            navigateToMeditation("mindfulness_1")
            onView(withId(R.id.startButton)).perform(click())

            // Simular interrupción (llamada telefónica)
            simulatePhoneCall()

            // Then - Verificar manejo de interrupción
            onView(withId(R.id.pausedState)).check(matches(isDisplayed()))
            onView(withText("Meditation Paused")).check(matches(isDisplayed()))

            // Verificar guardado de progreso
            val session = meditationRepository.getCurrentSession(user.id)
            assertThat(session?.status).isEqualTo(SessionStatus.PAUSED)
        }
    }

    private fun createAndAuthenticateUser(): User {
        return runBlocking {
            userRepository.createUser(
                email = "test@example.com",
                password = "password123"
            ).also { user ->
                userRepository.setCurrentUser(user)
            }
        }
    }

    private fun simulatePhoneCall() {
        val intent = Intent().apply {
            action = "android.intent.action.PHONE_STATE"
            putExtra("state", TelephonyManager.EXTRA_STATE_RINGING)
        }
        context.sendBroadcast(intent)
    }
} 