package de.luh.hci.mid.monumentgo.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.luh.hci.mid.monumentgo.settings.data.SettingsProvider
import kotlin.math.roundToInt


fun Float.format(digits: Int) = "%.${digits}f".format(this)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(x0: NavHostController) {
    var sliderPositionRadius by remember { mutableFloatStateOf(SettingsProvider.discoveryRadiusKm) }
    var sliderPositionLimit by remember { mutableFloatStateOf(SettingsProvider.discoveryMonumentsLimit.toFloat()) }
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
            Text("Monument Discovery Radius: ${sliderPositionRadius.format(2)} km")
            Slider(
                value = sliderPositionRadius,
                valueRange = 0.1f .. 5f,
                steps = 48,
                onValueChange = {
                    sliderPositionRadius = it
                    SettingsProvider.discoveryRadiusKm = it
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Limit number of monuments in radius: ${sliderPositionLimit.roundToInt()}")
            Slider(
                value = sliderPositionLimit,
                valueRange = 1f .. 20f,
                steps = 18,
                onValueChange = {
                    sliderPositionLimit = it
                    SettingsProvider.discoveryMonumentsLimit = it.roundToInt()
                },
            )
        }
    }
}