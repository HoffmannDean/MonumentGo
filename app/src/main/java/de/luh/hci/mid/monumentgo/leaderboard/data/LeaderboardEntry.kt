package de.luh.hci.mid.monumentgo.leaderboard.data

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val isCurrentUser: Boolean = false
)