@Composable
fun NavigationHandler(
    navController: NavHostController,
    navigationManager: NavigationManager
) {
    LaunchedEffect(Unit) {
        navigationManager.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.NavigateTo -> {
                    navController.navigate(command.route) {
                        command.popUpTo?.let { popUpTo(it) { inclusive = command.inclusive } }
                        launchSingleTop = command.singleTop
                    }
                }
                is NavigationCommand.NavigateBack -> {
                    navController.popBackStack()
                }
                is NavigationCommand.NavigateToDeepLink -> {
                    navController.navigate(command.uri)
                }
            }
        }
    }
} 