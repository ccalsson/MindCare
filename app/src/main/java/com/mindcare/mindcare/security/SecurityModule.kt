@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideEncryption(): Encryption {
        return AesEncryption()
    }

    @Provides
    @Singleton
    fun provideDataProtection(): DataProtection {
        return DataProtectionImpl()
    }
} 