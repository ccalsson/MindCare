@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideSavedStateHandle(): SavedStateHandle {
        return SavedStateHandle()
    }

    @Provides
    fun provideViewModelScope(): CoroutineScope {
        return viewModelScope
    }
} 