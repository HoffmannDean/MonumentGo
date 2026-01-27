package de.luh.hci.mid.monumentgo.userstats.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.luh.hci.mid.monumentgo.MonumentGo
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import kotlinx.coroutines.launch


class UserStatsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(PersonalStatsUIState())
        private set

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val profile = userRepository.userProfile.value
                if (profile != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        username = profile.username,
                        level = profile.level,
                        points = profile.points
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "User not loaded"
                    )
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MonumentGo)
                println("APP INFO: " + app.applicationInfo)
                UserStatsViewModel(app.userRepository)}
        }
    }
}