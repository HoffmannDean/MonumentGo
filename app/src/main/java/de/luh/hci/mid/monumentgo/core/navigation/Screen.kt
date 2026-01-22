package de.luh.hci.mid.monumentgo.core.navigation

import java.util.Objects

sealed class Screen(val route: String, val arg: String = "") {

    object AnalyticsPersonal : Screen("analytics_personal")
    object AnalyticsLeaderBoard : Screen("analytics_leaderboard")
    object MainMap : Screen("main_map")
    object Profile : Screen("profile")
    object Settings : Screen("settings")

    object Camera : Screen("camera")

    object Info : Screen("info")

    object Quiz : Screen("quiz")

    object QuizResult : Screen("quiz_result")

    object Login : Screen("login")

    object Register : Screen("register")
    object Leaderboard : Screen("leaderboard")

}