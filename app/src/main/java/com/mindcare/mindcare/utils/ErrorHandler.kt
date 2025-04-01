class ErrorHandler @Inject constructor(
    private val context: Context,
    private val crashReporting: CrashReporting
) {
    fun handleError(error: Throwable, showUser: Boolean = true) {
        crashReporting.logException(error)
        
        if (showUser) {
            showErrorDialog(context, error.localizedMessage ?: "Error desconocido")
        }
    }
} 