package de.luh.hci.mid.monumentgo.core.data.model

import kotlinx.serialization.Serializable

// Change this to need your needs, maybe add a Serializer
@Serializable
data class GisCoordinate(
    val lat: Double,
    val lon: Double,
)
