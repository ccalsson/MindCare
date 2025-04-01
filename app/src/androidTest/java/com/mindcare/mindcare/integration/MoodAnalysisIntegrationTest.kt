@HiltAndroidTest
class MoodAnalysisIntegrationTest : BaseInstrumentedTest() {
    @Inject lateinit var journalRepository: JournalRepository
    @Inject lateinit var moodAnalyzer: MoodAnalyzer
    @Inject lateinit var recommendationEngine: RecommendationEngine

    @Test
    fun moodTracking_GeneratesAccurateRecommendations() = runTest {
        // Given - Serie de entradas de diario
        val entries = createJournalEntries()
        
        // When - Analizar y generar recomendaciones
        launchActivity {
            // Navegar al diario
            onView(withId(R.id.navigation_journal)).perform(click())
            
            // Crear entradas
            entries.forEach { entry ->
                addJournalEntry(entry)
                // Esperar análisis
                waitForAnalysisCompletion()
            }

            // Then - Verificar análisis y recomendaciones
            // 1. Verificar análisis de estado de ánimo
            val moodAnalysis = moodAnalyzer.analyzeMoodTrends(entries)
            assertThat(moodAnalysis.trend).isEqualTo(MoodTrend.IMPROVING)

            // 2. Verificar recomendaciones generadas
            val recommendations = recommendationEngine.getRecommendations()
            assertThat(recommendations).containsAtLeastOneOf(
                RecommendationType.MEDITATION,
                RecommendationType.EXERCISE,
                RecommendationType.SOCIAL_ACTIVITY
            )

            // 3. Verificar UI actualizada
            onView(withId(R.id.moodTrendChart))
                .check(matches(hasProgressLevel(moodAnalysis.trend.value)))
        }
    }

    @Test
    fun criticalMoodPattern_TriggersAppropriateResponse() = runTest {
        // Given - Patrón de estado de ánimo crítico
        val criticalEntries = createCriticalMoodEntries()
        
        launchActivity {
            // When - Registrar entradas críticas
            criticalEntries.forEach { entry ->
                addJournalEntry(entry)
                waitForAnalysisCompletion()
            }

            // Then - Verificar respuesta del sistema
            // 1. Verificar alerta de profesional
            onView(withId(R.id.professionalSupportCard))
                .check(matches(isDisplayed()))

            // 2. Verificar recomendaciones urgentes
            val urgentRecommendations = recommendationEngine
                .getUrgentRecommendations()
            assertThat(urgentRecommendations)
                .contains(RecommendationType.PROFESSIONAL_HELP)

            // 3. Verificar notificaciones
            val notifications = getTriggeredNotifications()
            assertThat(notifications)
                .anyMatch { it.type == NotificationType.URGENT_SUPPORT }
        }
    }

    private fun createJournalEntries(): List<JournalEntry> {
        return listOf(
            JournalEntry("Feeling down today", Mood.SAD),
            JournalEntry("Slightly better", Mood.NEUTRAL),
            JournalEntry("Much better now", Mood.HAPPY)
        )
    }

    private fun waitForAnalysisCompletion() {
        // Esperar a que el análisis asíncrono se complete
        SystemClock.sleep(1000)
        IdlingRegistry.getInstance().register(analysisIdlingResource)
    }
} 