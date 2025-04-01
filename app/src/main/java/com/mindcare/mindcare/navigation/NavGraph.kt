package com.mindcare.mindcare.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
    object Membership : Screen("membership")
    object BreathingExercise : Screen("breathing_exercise")
    object Journal : Screen("journal")
    object Community : Screen("community")
    object Quests : Screen("quests")
    object Sleep : Screen("sleep")
    object Meditation : Screen("meditation")
} 