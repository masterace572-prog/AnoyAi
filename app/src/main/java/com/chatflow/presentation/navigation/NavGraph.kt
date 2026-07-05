package com.chatflow.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.chatflow.presentation.chat.ChatScreen
import com.chatflow.presentation.settings.ApiKeyScreen
import com.chatflow.presentation.settings.SettingsScreen

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
            ChatScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.ApiKeys.route) {
            ApiKeyScreen()
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    androidx.compose.material3.Text(text = text)
}
