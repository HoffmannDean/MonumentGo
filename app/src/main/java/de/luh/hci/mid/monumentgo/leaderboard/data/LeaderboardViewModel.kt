package de.luh.hci.mid.monumentgo.leaderboard.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.luh.hci.mid.monumentgo.MonumentGo
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import de.luh.hci.mid.monumentgo.leaderboard.data.LeaderboardEntry

class LeaderboardViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _leaderboardEntries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardEntries: StateFlow<List<LeaderboardEntry>> = _leaderboardEntries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    init {
        fetchLeaderboard()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun fetchLeaderboard() {
        viewModelScope.launch {
            try {
                val profiles : List<UserProfile> = userRepository.getLeaderboard()
                println("profiles: $profiles")

                val currentUserId = DatabaseProvider.supabase.auth.currentUserOrNull()?.id
                println("currentUserId: $currentUserId")

                val sortedEntries = profiles
                    .mapIndexed { index, profile ->
                        LeaderboardEntry(
                            rank = index + 1,
                            name = profile.username,
                            score = profile.points,
                            isCurrentUser = profile.id.toString() == currentUserId
                        )
                    }

                _leaderboardEntries.value = sortedEntries

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUserLevel() : Int {
        return userRepository.userLevel
    }

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MonumentGo)
                println("APP INFO: " + app.applicationInfo)
                LeaderboardViewModel(app.userRepository)}
        }
    }
}