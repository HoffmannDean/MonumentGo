package de.luh.hci.mid.monumentgo.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import de.luh.hci.mid.monumentgo.settings.data.SettingsProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(x0: NavHostController) {
    var sliderPosition by remember { mutableFloatStateOf(1f) }
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
            modifier = Modifier.padding(paddingValues)
        ) {
            Text(text = "Monument Discovery Radius: $sliderPosition km")
            Slider(
                value = sliderPosition,
                valueRange = 0.1f .. 5f,
                steps = 48,
                onValueChange = {
                    sliderPosition = it
                    SettingsProvider.discoveryRadiusKm = it
                },
            )
        }
    }
}