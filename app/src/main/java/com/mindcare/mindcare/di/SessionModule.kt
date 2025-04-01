@Module
@InstallIn(SingletonComponent::class)
object SessionModule {
    @Provides
    @Singleton
    fun provideSessionManager(
        preferences: SharedPreferences,
        userRepository: UserRepository
    ): SessionManager {
        return SessionManager(preferences, userRepository)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sessionManager: SessionManager
    ): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }
} 