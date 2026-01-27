package de.luh.hci.mid.monumentgo.core.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MonumentWithDetails(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val qid: Int,
    val wikiUrl: String,
    val wikiMedia: String,
    val points: Int,
    val region: String,
    val osmTags: JsonObject
) {
    fun toMonument(): Monument {
        return Monument(id, lat, lon)
    }
}
