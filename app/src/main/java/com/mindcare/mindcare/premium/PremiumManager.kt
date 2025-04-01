@Singleton
class PremiumManager @Inject constructor(
    private val userPreferences: UserPreferences,
    private val analytics: AnalyticsManager
) {
    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()

    fun checkPremiumStatus() {
        viewModelScope.launch {
            _isPremium.value = userPreferences.isPremiumUser()
        }
    }

    fun unlockPremium() {
        viewModelScope.launch {
            userPreferences.setPremiumStatus(true)
            _isPremium.value = true
            analytics.logEvent(AnalyticsEvent.PremiumUnlocked)
        }
    }
} 