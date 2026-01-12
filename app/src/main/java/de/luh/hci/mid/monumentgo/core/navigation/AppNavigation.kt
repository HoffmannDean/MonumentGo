package de.luh.hci.mid.monumentgo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.luh.hci.mid.monumentgo.MonumentGo
import de.luh.hci.mid.monumentgo.analytics.ui.AnalyticsScreen
import de.luh.hci.mid.monumentgo.auth.ui.login.LoginScreen
import de.luh.hci.mid.monumentgo.auth.ui.register.RegisterScreen
import de.luh.hci.mid.monumentgo.camera.ui.CameraScreen
import de.luh.hci.mid.monumentgo.infoscreen.ui.ImageInfoScreen
import de.luh.hci.mid.monumentgo.infoscreen.ui.InfoViewModel
import de.luh.hci.mid.monumentgo.leaderboard.ui.LeaderboardScreen
import de.luh.hci.mid.monumentgo.map.ui.screen.MainMapScreen
import de.luh.hci.mid.monumentgo.profile.ui.ProfileScreen
import de.luh.hci.mid.monumentgo.quiz.data.QuizResultScreen
import de.luh.hci.mid.monumentgo.quiz.ui.QuizScreen
import de.luh.hci.mid.monumentgo.settings.ui.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as MonumentGo

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Analytics.route) {
            AnalyticsScreen(navController)
        }

        composable(Screen.MainMap.route) {
            MainMapScreen(navController, app.monumentRepository)
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

        composable(Screen.Quiz.route) {
            QuizScreen(navController)
        }

        composable(Screen.QuizResult.route) {
            QuizResultScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController, app.userRepository)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController)
        }

    }
}