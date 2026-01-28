package de.luh.hci.mid.monumentgo.auth.ui.register

import androidx.lifecycle.ViewModel
import de.luh.hci.mid.monumentgo.core.data.repositories.AuthResponse
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val error: String? = null
)

class RegisterViewModel(
    private val userRepo: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun changeUsername(username: String) {
        _uiState.update { currentState ->
            currentState.copy(
                username = username
            )
        }
    }

    fun changeEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email
            )
        }
    }

    fun changePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    suspend fun register(): AuthResponse {
        val response = userRepo.signUpNewUser(
            _uiState.value.username,
            _uiState.value.email,
            _uiState.value.password
        )
        _uiState.update { currentState ->
            currentState.copy(
                error = if (response is AuthResponse.Error) response.message else null
            )
        }
        return response
    }
}