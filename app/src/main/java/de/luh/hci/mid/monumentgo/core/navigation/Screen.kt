package de.luh.hci.mid.monumentgo.core.navigation

sealed class Screen(val route: String) {
    object MainMap : Screen("main_map")
    object Stats : Screen("stats")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}