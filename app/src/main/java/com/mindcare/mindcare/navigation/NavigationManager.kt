@Singleton
class NavigationManager @Inject constructor() {
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands = _navigationCommands.asSharedFlow()

    suspend fun navigate(command: NavigationCommand) {
        _navigationCommands.emit(command)
    }
}

sealed class NavigationCommand {
    data class NavigateTo(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationCommand()
    
    object NavigateBack : NavigationCommand()
    
    data class NavigateToDeepLink(val uri: String) : NavigationCommand()
} 