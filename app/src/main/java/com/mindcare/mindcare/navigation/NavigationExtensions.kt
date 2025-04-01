fun NavController.safeNavigate(
    route: String,
    popUpTo: String? = null,
    inclusive: Boolean = false,
    singleTop: Boolean = false
) {
    try {
        navigate(route) {
            popUpTo?.let { popUpTo(it) { this.inclusive = inclusive } }
            launchSingleTop = singleTop
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun rememberNavigationState(
    navController: NavHostController = rememberNavController()
): NavigationState {
    return remember(navController) {
        NavigationState(navController)
    }
}

class NavigationState(
    private val navController: NavHostController
) {
    val currentRoute: String?
        get() = navController.currentDestination?.route
        
    fun navigateBack() {
        navController.popBackStack()
    }
    
    fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
        navController.navigate(route, builder)
    }
} 