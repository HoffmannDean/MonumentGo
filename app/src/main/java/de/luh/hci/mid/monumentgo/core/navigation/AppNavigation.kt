package de.luh.hci.mid.monumentgo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.luh.hci.mid.monumentgo.map.ui.MainMapScreen
import de.luh.hci.mid.monumentgo.profile.ui.ProfileScreen
import de.luh.hci.mid.monumentgo.settings.ui.SettingsScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainMap.route) {

        composable(Screen.MainMap.route) {
            MainMapScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

    }
}