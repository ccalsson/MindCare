fun NavGraphBuilder.communityGraph(navController: NavController) {
    navigation(
        startDestination = Screen.Community.route,
        route = "community"
    ) {
        composable(Screen.Community.route) {
            CommunityScreen(
                onNavigateToPost = { postId ->
                    navController.navigate("${Screen.Post.route}/$postId")
                },
                onNavigateToGroup = { groupId ->
                    navController.navigate("${Screen.Group.route}/$groupId")
                }
            )
        }

        composable(
            route = "${Screen.Post.route}/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Screen.Group.route}/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")
            GroupDetailScreen(
                groupId = groupId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    navController.navigate("${Screen.Post.route}/$postId")
                }
            )
        }
    }
} 