package de.luh.hci.mid.monumentgo.map.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.luh.hci.mid.monumentgo.core.data.model.Monument
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface MapState {
    data class Loading(val message: String? = null) : MapState
    data class Ready(val monuments: Set<Monument>) : MapState
}

class MainMapViewModel(
    private val monumentRepository: MonumentRepository = MonumentRepository()
) : ViewModel() {
    val uiState: StateFlow<MapState> = monumentRepository.monuments.map { monuments ->
        if (monuments.isNullOrEmpty()) MapState.Loading() else MapState.Ready(monuments)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapState.Loading()
    )
}
