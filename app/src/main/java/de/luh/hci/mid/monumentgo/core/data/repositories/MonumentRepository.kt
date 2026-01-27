package de.luh.hci.mid.monumentgo.core.data.repositories

import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider.supabase
import de.luh.hci.mid.monumentgo.core.data.model.Monument
import de.luh.hci.mid.monumentgo.core.data.model.MonumentWithDetails
import de.luh.hci.mid.monumentgo.settings.data.SettingsProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MonumentRepository {

    private val _monuments = MutableStateFlow<Set<Monument>>(setOf())
    val monuments: StateFlow<Set<Monument>?> = _monuments.asStateFlow()

    private val _discoveredMonuments = MutableStateFlow<Set<Monument>>(setOf())
    val discoveredMonuments: StateFlow<Set<Monument>?> = _discoveredMonuments.asStateFlow()

    private val _monumentsAroundUser = MutableStateFlow<List<MonumentWithDetails>?>(null)
    val monumentsAroundUser: StateFlow<List<MonumentWithDetails>?> =
        _monumentsAroundUser.asStateFlow()

    val selectedMonument = mutableStateOf<MonumentWithDetails?>(null)

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
            val discoveredMonuments =
                supabase.postgrest.rpc("get_discovered_monuments").decodeList<Monument>()
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

    suspend fun getUndiscoveredMonumentsInRadius(
        userLocation: Location,
        radiusMeter: Float,
        limit: Int
    ): List<MonumentWithDetails> {
        try {
            val results = supabase.postgrest.rpc("get_monument_details_around", buildJsonObject {
                put("lat", userLocation.latitude)
                put("lon", userLocation.longitude)
                put("radiusmeter", radiusMeter)
                put("monumentlimit", limit)
            }).decodeList<MonumentWithDetails>()
            return results
        } catch (e: Exception) {
            Log.e("db", "Error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun getMonumentsAroundUser(userLocation: Location) {
        _monumentsAroundUser.value = null   // set null to indicate loading phase
        try {
            _monumentsAroundUser.value = getUndiscoveredMonumentsInRadius(
                userLocation,
                SettingsProvider.discoveryRadiusKm * 1000,
                SettingsProvider.discoveryMonumentsLimit
            )
            Log.d("db", "Fetched monuments around user: ${_monumentsAroundUser.value?.map{it.name}}")
        } catch (e: Exception) {
            Log.e("db", "Error: ${e.message}")
            _monumentsAroundUser.value = emptyList()
        }
    }

    suspend fun submitMonumentDiscovery() {
        val userId = supabase.auth.currentUserOrNull()?.id
        if (userId == null || selectedMonument.value == null) throw Exception("User is not logged in")
        try {
            supabase.postgrest.from("user_discovered_monuments").insert(
                buildJsonObject {
                    put("user_id", userId)
                    put("monument_id", selectedMonument.value!!.id)
                }
            )
            selectedMonument.value = null
            updateDiscoveredMonuments()
        } catch (e: Exception) {
            Log.e("db", "Error: ${e.message}")
        }
    }
}
