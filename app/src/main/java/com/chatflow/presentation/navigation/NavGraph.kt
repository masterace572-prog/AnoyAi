package com.chatflow.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable

sealed class Screen(val route: String) {
    object Chat : Screen("chat")
    object Settings : Screen("settings")
    object ApiKeys : Screen("api_keys")
}

@Composable
fun ChatFlowNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Chat.route
    ) {
        composable(Screen.Chat.route) {
            // ChatScreen(navController) // To be implemented
            PlaceholderScreen("Chat Screen")
        }
        composable(Screen.Settings.route) {
            // SettingsScreen(navController) // To be implemented
            PlaceholderScreen("Settings Screen")
        }
        composable(Screen.ApiKeys.route) {
            // ApiKeyScreen(navController) // To be implemented
            PlaceholderScreen("API Keys Screen")
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    androidx.compose.material3.Text(text = text)
}
