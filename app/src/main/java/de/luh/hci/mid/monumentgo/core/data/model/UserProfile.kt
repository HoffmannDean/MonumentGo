package de.luh.hci.mid.monumentgo.core.data.model

import de.luh.hci.mid.monumentgo.core.data.util.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.ZonedDateTime
import kotlin.time.ExperimentalTime

@Serializable
data class UserProfile @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class) constructor(
    val id: Uuid,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val updatedAt: ZonedDateTime?,
    val username: String,
    val points: Int,
    val level: Int
)