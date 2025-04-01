@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        userInfoDao: UserInfoDao,
        apiService: ApiService,
        preferences: SharedPreferences
    ): UserRepository {
        return UserRepositoryImpl(userInfoDao, apiService, preferences)
    }

    @Provides
    @Singleton
    fun provideJournalRepository(
        journalDao: JournalDao,
        analytics: AnalyticsManager
    ): JournalRepository {
        return JournalRepositoryImpl(journalDao, analytics)
    }

    @Provides
    @Singleton
    fun provideMeditationRepository(
        meditationDao: MeditationDao
    ): MeditationRepository {
        return MeditationRepositoryImpl(meditationDao)
    }
} 