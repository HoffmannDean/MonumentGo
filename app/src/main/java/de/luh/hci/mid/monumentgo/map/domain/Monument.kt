package de.luh.hci.mid.monumentgo.map.domain

import kotlinx.serialization.Serializable

@Serializable
data class Monument(
    val name: String,
    val location: String
)

