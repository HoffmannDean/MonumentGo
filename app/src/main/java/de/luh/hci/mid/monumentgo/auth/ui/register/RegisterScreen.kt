package de.luh.hci.mid.monumentgo.auth.ui.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import de.luh.hci.mid.monumentgo.ui.theme.MonumentGoTheme
import de.luh.hci.mid.monumentgo.core.data.repositories.AuthResponse
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Account erstellen",
            style = MaterialTheme.typography.displaySmall
        )
        Column {
            Text("Benutzername")
            TextField(
                value = uiState.username,
                onValueChange = { viewModel.changeUsername(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Email")
            TextField(
                value = uiState.email,
                onValueChange = { viewModel.changeEmail(it) },
                placeholder = {
                    Text(
                        text = "email@example.com"
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Passwort")
            TextField(
                value = uiState.password,
                onValueChange = { viewModel.changePassword(it) },
                visualTransformation = PasswordVisualTransformation(),
            )
        }
        ElevatedButton(
            onClick = {
                viewModel.viewModelScope.launch {
                    val response = viewModel.register()
                    if (response is AuthResponse.Success) {
                        Log.d("auth", response.profile.toString())
                    } else if (response is AuthResponse.Error) {
                        Log.d("auth", response.message ?: "Error registering user.")
                    }
                }
            }
        ) {
            Text(text = "erstellen")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MonumentGoTheme {
        RegisterScreen()
    }
}