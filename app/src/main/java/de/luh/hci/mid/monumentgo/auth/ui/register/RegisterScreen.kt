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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import de.luh.hci.mid.monumentgo.core.ui.theme.MonumentGoTheme
import de.luh.hci.mid.monumentgo.core.data.repositories.AuthResponse
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    return Scaffold { contentPaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddingValues),
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
                            text = "Register",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    Column {
                        Text("Username")
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    passwordFocusRequester.requestFocus()
                                }
                            ),
                            value = uiState.username,
                            onValueChange = { viewModel.changeUsername(it) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Email")
                        TextField(
                            modifier = Modifier.fillMaxWidth().focusRequester(emailFocusRequester),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    passwordFocusRequester.requestFocus()
                                }
                            ),
                            value = uiState.email,
                            onValueChange = { viewModel.changeEmail(it) },
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Password")
                        TextField(
                            modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            value = uiState.password,
                            onValueChange = { viewModel.changePassword(it) },
                            visualTransformation = PasswordVisualTransformation(),
                        )
                        if (uiState.error != null) {
                            Text(
                                text = uiState.error ?: "Error registering user.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FilledTonalButton(
                            onClick = {
                                viewModel.viewModelScope.launch {
                                    val response = viewModel.register()
                                    if (response is AuthResponse.Success) {
                                        Log.d("auth", "Registered successfully.")
                                        navController.navigate(Screen.MainMap.route)
                                    } else if (response is AuthResponse.Error) {
                                        Log.d("auth", response.message ?: "Error registering user.")
                                    }
                                }
                            }
                        ) {
                            Text(text = "Create account")
                        }
                        TextButton(
                            onClick = {
                                navController.navigate(Screen.Login.route)
                            }
                        ) {
                            Text("Or login instead")
                        }
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
        RegisterScreen(rememberNavController())
    }
}