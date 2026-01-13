package de.luh.hci.mid.monumentgo.core.data.repositories

import android.util.Log
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
import kotlinx.coroutines.flow.toSet
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MonumentRepository {

    private val _monuments = MutableStateFlow<Set<Monument>>(setOf())
    val monuments: StateFlow<Set<Monument>?> = _monuments.asStateFlow()

    private val _discoveredMonuments = MutableStateFlow<Set<Monument>>(setOf())
    val discoveredMonuments: StateFlow<Set<Monument>?> = _discoveredMonuments.asStateFlow()

    suspend fun updateMonuments() {
        try {
            val monuments = supabase.postgrest.rpc("get_monuments").decodeList<Monument>()
            _monuments.value = monuments.toSet()
        } catch (e: Exception) {
            Log.e("db", "Error: ${e.message}")
        }
    }

    suspend fun updateDiscoveredMonuments() {
        val userId = supabase.auth.currentUserOrNull()?.id
        if (userId == null) throw Exception("User is not logged in")
        try {
            val discoveredMonuments = supabase.postgrest.rpc("get_discovered_monuments").decodeList<Monument>()
            _discoveredMonuments.value = discoveredMonuments.toSet()
        } catch (e: Exception) {
            Log.e("db", "Error: ${e.message}")
        }
    }

    suspend fun getMonumentDetails(monumentId: Int): MonumentWithDetails? {
        try {
            return supabase.postgrest.rpc("get_monument_details", buildJsonObject {
                put("monument_id", monumentId)
            }).decodeSingle<MonumentWithDetails>()
        } catch (e: Exception) {
            return null
        }
    }
}
