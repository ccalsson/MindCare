package com.mindcare.mindcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindcare.mindcare.navigation.Screen
import com.mindcare.mindcare.ui.screens.WelcomeScreen
import com.mindcare.mindcare.ui.screens.LoginScreen
import com.mindcare.mindcare.ui.screens.DashboardScreen
import com.mindcare.mindcare.ui.theme.MindCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MindCareApp()
                }
            }
        }
    }
}

@Composable
fun MindCareApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = { navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }},
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToMembership = { navController.navigate(Screen.Membership.route) }
            )
        }
    }
}