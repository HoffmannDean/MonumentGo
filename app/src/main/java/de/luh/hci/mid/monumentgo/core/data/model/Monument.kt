package de.luh.hci.mid.monumentgo.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Monument(
    val id: Int,
    val coordinate: GisCoordinate
)
