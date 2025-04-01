@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideCryptoManager(
        @ApplicationContext context: Context
    ): CryptoManager {
        return CryptoManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideSecurePreferences(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): SecureSharedPreferences {
        return SecureSharedPreferences(context, cryptoManager)
    }

    @Provides
    @Singleton
    fun provideBiometricManager(
        @ApplicationContext context: Context
    ): BiometricManager {
        return BiometricManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideSecurityPolicy(): SecurityPolicy {
        return SecurityPolicy(
            passwordMinLength = 8,
            requireSpecialCharacter = true,
            requireNumber = true,
            requireUpperCase = true,
            maxLoginAttempts = 3,
            sessionTimeout = 30 * 60 * 1000L // 30 minutos
        )
    }

    @Provides
    @Singleton
    fun provideSecurityValidator(
        securityPolicy: SecurityPolicy
    ): SecurityValidator {
        return SecurityValidatorImpl(securityPolicy)
    }
} 