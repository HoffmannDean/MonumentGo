package de.luh.hci.mid.monumentgo.userstats.data

data class PersonalStatsUIState(
    val isLoading: Boolean = false,
    val username: String = "",
    val level: Int? = null,
    val points: Int? = null,
    val error: String? = null
)
