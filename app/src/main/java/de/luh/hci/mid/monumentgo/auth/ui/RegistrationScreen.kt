package de.luh.hci.mid.monumentgo.auth.ui

import android.R.attr.onClick
import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.luh.hci.mid.monumentgo.core.data.db.AuthResponse
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider
import de.luh.hci.mid.monumentgo.ui.theme.MonumentGoTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun RegisterScreen() {
    var usernameValue: String by remember {
        mutableStateOf("")
    }
    var emailValue: String by remember {
        mutableStateOf("")
    }
    var passwordValue: String by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

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
                value = usernameValue,
                onValueChange = { newValue ->
                    usernameValue = newValue
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Email")
            TextField(
                value = emailValue,
                onValueChange = { newValue ->
                    emailValue = newValue
                },
                placeholder = {
                    Text(
                        text = "email@example.com"
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Passwort")
            TextField(
                value = passwordValue,
                onValueChange = { newValue ->
                    passwordValue = newValue
                },
                visualTransformation = PasswordVisualTransformation(),
            )
        }
        ElevatedButton(
            onClick = {
                DatabaseProvider.signUpNewUser(usernameValue, emailValue, passwordValue)
                    .onEach { result ->
                        if (result is AuthResponse.Success) {
                            Log.d("auth", "Registration Success: " + result.profile.toString())
                        } else if(result is AuthResponse.Error) {
                            Log.d("auth", "Registration Failed: " + result.message)
                        }
                    }
                    .launchIn(coroutineScope)
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