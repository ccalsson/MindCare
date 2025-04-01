fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        startDestination = Screen.Dashboard.route,
        route = "main"
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToMembership = { navController.navigate(Screen.Membership.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Membership.route) {
            MembershipScreen(
                onNavigateBack = { navController.popBackStack() },
                onMembershipPurchased = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
} 