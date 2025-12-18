package de.luh.hci.mid.monumentgo.core.navigation

import java.util.Objects

sealed class Screen(val route: String, val arg: String = "") {
    object MainMap : Screen("main_map")
    object Stats : Screen("stats")
    object Profile : Screen("profile")
    object Settings : Screen("settings")

    object Camera : Screen("camera")

    object Info : Screen("info")
}