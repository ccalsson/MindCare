interface Analytics {
    fun logEvent(name: String, params: Map<String, Any>)
    fun setUserProperty(property: String, value: String)
    fun logScreenView(screenName: String)
    fun logUserAction(action: String, details: Map<String, Any> = emptyMap())
} 