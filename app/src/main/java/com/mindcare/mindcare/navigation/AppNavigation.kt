@Composable
fun AppNavigation(
    startDestination: String = Screen.Dashboard.route,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Core
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToMeditation = { navController.navigate(Screen.Meditation.route) },
                onNavigateToBreathing = { navController.navigate(Screen.Breathing.route) },
                onNavigateToJournal = { navController.navigate(Screen.Journal.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToMembership = { navController.navigate(Screen.Membership.route) },
                onNavigateToCommunity = { navController.navigate(Screen.Community.route) },
                onNavigateToSleep = { navController.navigate(Screen.Sleep.route) }
            )
        }

        // Features básicos
        meditationGraph(navController)
        breathingGraph(navController)
        moodGraph(navController)
        chatGraph(navController)

        // Features premium
        sleepGraph(navController)
        communityGraph(navController)
        analyticsGraph(navController)
        achievementsGraph(navController)
    }
} 