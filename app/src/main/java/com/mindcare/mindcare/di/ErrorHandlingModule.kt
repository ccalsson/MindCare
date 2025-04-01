@Module
@InstallIn(SingletonComponent::class)
object ErrorHandlingModule {
    @Provides
    @Singleton
    fun provideErrorHandler(
        @ApplicationContext context: Context,
        crashReporting: CrashReporting
    ): ErrorHandler {
        return ErrorHandler(context, crashReporting)
    }

    @Provides
    @Singleton
    fun provideNetworkErrorHandler(
        errorHandler: ErrorHandler
    ): NetworkErrorHandler {
        return NetworkErrorHandler(errorHandler)
    }
} 