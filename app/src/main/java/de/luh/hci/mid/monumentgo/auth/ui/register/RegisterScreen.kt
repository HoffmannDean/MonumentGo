package de.luh.hci.mid.monumentgo.auth.ui.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    return Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(36.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Registrieren",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                Column {
                    Text("Benutzername")
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.username,
                        onValueChange = { viewModel.changeUsername(it) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Email")
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.email,
                        onValueChange = { viewModel.changeEmail(it) },
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Passwort")
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.password,
                        onValueChange = { viewModel.changePassword(it) },
                        visualTransformation = PasswordVisualTransformation(),
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        Text(text = "Absenden")
                    }
                    TextButton(
                        onClick = {
                            Log.d("nav", "Navigate to LoginScreen")
                        }
                    ) {
                        Text("Oder in Konto einloggen")
                    }
                }
            }
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