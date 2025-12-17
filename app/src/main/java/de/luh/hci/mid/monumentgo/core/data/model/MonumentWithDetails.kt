package de.luh.hci.mid.monumentgo.core.data.model

import kotlinx.serialization.json.JsonObject

data class MonumentWithDetails(
    val id: Int,
    val coordinate: GisCoordinate,
    val name: String,
    val qid: Int,
    val wikiUrl: String,
    val wikiMedia: String,
    val points: Int,
    val region: String,
    val osmTags: JsonObject
)
