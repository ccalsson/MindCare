@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): com.google.firebase.analytics.FirebaseAnalytics {
        return com.google.firebase.analytics.FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalytics(firebaseAnalytics: FirebaseAnalytics): Analytics {
        return firebaseAnalytics
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(analytics: Analytics): AnalyticsManager {
        return AnalyticsManager(analytics)
    }
} 