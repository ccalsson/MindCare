@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences("mindcare_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAnalytics(): Analytics = FirebaseAnalytics()

    @Provides
    @Singleton
    fun provideCrashReporting(): CrashReporting = FirebaseCrashlytics()
} 