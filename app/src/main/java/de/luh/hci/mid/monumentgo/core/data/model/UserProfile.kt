package de.luh.hci.mid.monumentgo.core.data.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class UserProfile (
    val id: Int,
    val updatedAt: LocalDate,
    val username: String,
    val points: Int,
    val level: Int
)