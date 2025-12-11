package de.luh.hci.mid.monumentgo.auth.ui.login

import androidx.lifecycle.ViewModel
import de.luh.hci.mid.monumentgo.auth.ui.register.RegisterState
import de.luh.hci.mid.monumentgo.core.data.repositories.AuthResponse
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val email: String = "",
    val password: String = ""
)

class LoginViewModel(
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

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

    suspend fun login(): AuthResponse {
        return userRepo.signInWithEmail(
            _uiState.value.email,
            _uiState.value.password,
        )
    }
}