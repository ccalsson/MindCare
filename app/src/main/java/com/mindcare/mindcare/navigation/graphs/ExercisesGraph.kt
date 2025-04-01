fun NavGraphBuilder.exercisesGraph(navController: NavController) {
    navigation(
        startDestination = Screen.BreathingExercise.route,
        route = "exercises"
    ) {
        composable(Screen.BreathingExercise.route) {
            BreathingExerciseScreen(
                onNavigateBack = { navController.popBackStack() },
                onExerciseComplete = { 
                    navController.navigate(Screen.ExerciseComplete.route)
                }
            )
        }

        composable(Screen.Meditation.route) {
            MeditationScreen(
                onNavigateBack = { navController.popBackStack() },
                onMeditationSelected = { meditationId ->
                    navController.navigate("${Screen.MeditationSession.route}/$meditationId")
                }
            )
        }

        composable(
            route = "${Screen.MeditationSession.route}/{meditationId}",
            arguments = listOf(navArgument("meditationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val meditationId = backStackEntry.arguments?.getString("meditationId")
            MeditationSessionScreen(
                meditationId = meditationId,
                onNavigateBack = { navController.popBackStack() },
                onSessionComplete = { 
                    navController.navigate(Screen.ExerciseComplete.route)
                }
            )
        }
    }
} 