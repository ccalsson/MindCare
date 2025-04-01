sealed class Screen(val route: String) {
    // Rutas de autenticación
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Rutas principales
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Membership : Screen("membership")
    
    // Rutas de ejercicios
    object BreathingExercise : Screen("breathing_exercise")
    object Meditation : Screen("meditation")
    object MeditationSession : Screen("meditation_session")
    object ExerciseComplete : Screen("exercise_complete")
    
    // Rutas de comunidad
    object Community : Screen("community")
    object Post : Screen("post")
    object Group : Screen("group")
    
    // Rutas de chat y diario
    object Chat : Screen("chat")
    object Journal : Screen("journal")
} 