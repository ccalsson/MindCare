class FirebaseAnalytics @Inject constructor(
    private val firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics
) : Analytics {
    override fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    override fun setUserProperty(property: String, value: String) {
        firebaseAnalytics.setUserProperty(property, value)
    }

    override fun logScreenView(screenName: String) {
        logEvent(
            "screen_view",
            mapOf("screen_name" to screenName)
        )
    }

    override fun logUserAction(action: String, details: Map<String, Any>) {
        logEvent(
            "user_action",
            details + mapOf("action" to action)
        )
    }
} 