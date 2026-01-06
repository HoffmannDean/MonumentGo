package de.luh.hci.mid.monumentgo.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.luh.hci.mid.monumentgo.camera.ui.CameraScreen
import de.luh.hci.mid.monumentgo.infoscreen.ui.ImageInfoScreen
import de.luh.hci.mid.monumentgo.infoscreen.ui.InfoViewModel
import de.luh.hci.mid.monumentgo.map.ui.MainMapScreen
import de.luh.hci.mid.monumentgo.profile.ui.ProfileScreen
import de.luh.hci.mid.monumentgo.settings.ui.SettingsScreen

@Composable
fun AppNavigation() {
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

        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }

        composable(Screen.Info.route) {
            val viewModel: InfoViewModel = viewModel(factory = InfoViewModel.Factory)
            ImageInfoScreen(
                navController,
                viewModel
            )
        }
    }
}