package de.luh.hci.mid.monumentgo.core.data.repositories

import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider.supabase
import de.luh.hci.mid.monumentgo.core.data.model.Monument
import de.luh.hci.mid.monumentgo.core.data.model.MonumentWithDetails
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonumentRepository {

    private val _monuments = MutableStateFlow<Set<Monument>>(setOf())
    val monuments: StateFlow<Set<Monument>?> = _monuments.asStateFlow()

    private val _discoveredMonuments = MutableStateFlow<Set<Monument>>(setOf())
    val discoveredMonuments: StateFlow<Set<Monument>?> = _monuments.asStateFlow()

    suspend fun updateMonuments() {
        val monuments = supabase.postgrest.rpc("get_monuments").decodeList<Monument>()
        _monuments.value = monuments.toSet()
    }

    suspend fun updateDiscoveredMonuments() {
        val userId = supabase.auth.currentUserOrNull()?.id
        if (userId == null) throw Exception("User is not logged in")
        val discoveredMonuments = supabase.from("monuments").select {
            Columns.raw("""
                *,
                user_discovered_monuments!inner(
                    user_id
                )
            """.trimIndent())
            filter {
                eq("user_discovered_monuments.user_id", userId)
            }
        }.decodeList<Monument>()
        _discoveredMonuments.value = discoveredMonuments.toSet()
    }

    suspend fun getMonumentDetails(monument: Monument): MonumentWithDetails {
        return supabase.from("monument_details").select {
            filter {
                eq("id", monument.id)
            }
        }.decodeSingle<MonumentWithDetails>()
    }
}
