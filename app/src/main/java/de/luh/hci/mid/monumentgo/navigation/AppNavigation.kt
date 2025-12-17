package de.luh.hci.mid.monumentgo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.luh.hci.mid.monumentgo.ui.screens.MainMapScreen
import de.luh.hci.mid.monumentgo.ui.screens.ProfileScreen
import de.luh.hci.mid.monumentgo.ui.screens.SettingsScreen
import de.luh.hci.mid.monumentgo.ui.screens.StatsScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainMap.route) {

        composable(Screen.MainMap.route) {
            MainMapScreen(navController)
        }

        composable(Screen.Stats.route) {
            StatsScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

    }
}