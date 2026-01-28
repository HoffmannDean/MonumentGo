package de.luh.hci.mid.monumentgo.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.luh.hci.mid.monumentgo.auth.ui.login.LoginScreen
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import de.luh.hci.mid.monumentgo.core.ui.theme.MonumentGoTheme
import de.luh.hci.mid.monumentgo.settings.data.SettingsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


fun Float.format(digits: Int) = "%.${digits}f".format(this)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(x0: NavHostController, userRepository: UserRepository) {
    val coroutineScope = rememberCoroutineScope()
    var sliderPositionRadius by remember {
        mutableFloatStateOf(SettingsProvider.discoveryRadiusKm)
    }
    var sliderPositionLimit by remember {
        mutableFloatStateOf(SettingsProvider.discoveryMonumentsLimit.toFloat())
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") }, navigationIcon = {
                IconButton(onClick = {
                    x0.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
                    Text("Monument Discovery Radius: ${sliderPositionRadius.format(2)} km")
                    Slider(
                        value = sliderPositionRadius,
                        valueRange = 0.1f..5f,
                        steps = 48,
                        onValueChange = {
                            sliderPositionRadius = it
                            SettingsProvider.discoveryRadiusKm = it
                        },
                    )
                    Text(
                        "Sets the radius in which monuments are being considered for image detection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Limit number of monuments in radius: ${sliderPositionLimit.roundToInt()}")
                    Slider(
                        value = sliderPositionLimit,
                        valueRange = 1f..20f,
                        steps = 18,
                        onValueChange = {
                            sliderPositionLimit = it
                            SettingsProvider.discoveryMonumentsLimit = it.roundToInt()
                        },
                    )
                    Text(
                        "Limits the number of monuments to be considered for image detection to the n nearest monuments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledTonalButton(
                            onClick = {
                                coroutineScope.launch {
                                    userRepository.signOut()
                                    x0.navigate(Screen.Login.route)
                                }
                            }
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val appScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    MonumentGoTheme {
        SettingsScreen(rememberNavController(), UserRepository(appScope))
    }
}