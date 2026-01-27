package de.luh.hci.mid.monumentgo.core.navigation


sealed class Screen(val route: String, val arg: String = "") {

    object Leaderboard : Screen("leaderboard")
    object MainMap : Screen("main_map")
    object Profile : Screen("profile")
    object Settings : Screen("settings")

    object Camera : Screen("camera")

    object Info : Screen("info")

    object Quiz : Screen("quiz")

    object QuizResult : Screen("quiz_result")

    object Login : Screen("login")

    object Register : Screen("register")

}