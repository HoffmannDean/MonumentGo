package de.luh.hci.mid.monumentgo.map.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.luh.hci.mid.monumentgo.core.data.model.Monument
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface MapState {
    data class Loading(val message: String? = null) : MapState
    data class Ready(val monuments: Set<Monument>, val discoveredMonuments: Set<Monument>) : MapState
}

class MainMapViewModel(
    private val monumentRepository: MonumentRepository = MonumentRepository()
) : ViewModel() {
    val _uiState: MutableStateFlow<MapState> = MutableStateFlow(MapState.Loading())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    suspend fun updateMonuments() {
        monumentRepository.updateDiscoveredMonuments()
        monumentRepository.updateMonuments()
        Log.d("map", monumentRepository.discoveredMonuments.value?.count().toString())
        _uiState.value = MapState.Ready(
            monumentRepository.monuments.value ?: setOf(),
            monumentRepository.discoveredMonuments.value ?: setOf()
        )
    }


    suspend fun getDetails(monument: Monument) = monumentRepository.getMonumentDetails(monument.id)
}
