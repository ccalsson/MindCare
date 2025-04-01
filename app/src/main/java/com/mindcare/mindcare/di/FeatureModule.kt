@Module
@InstallIn(SingletonComponent::class)
object FeatureModule {
    @Provides
    @Singleton
    fun provideMoodPredictor(
        analytics: AnalyticsManager
    ): MoodPredictor {
        return MoodPredictor(analytics)
    }

    @Provides
    @Singleton
    fun provideBreathingController(): BreathingController {
        return BreathingController()
    }

    @Provides
    @Singleton
    fun provideMeditationPlayer(
        @ApplicationContext context: Context
    ): MeditationPlayer {
        return MeditationPlayer(context)
    }
} 