package de.luh.hci.mid.monumentgo.map.ui.screen

import android.location.Location
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.R
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import de.luh.hci.mid.monumentgo.map.ui.components.GetCurrentLocation
import de.luh.hci.mid.monumentgo.map.ui.components.LocationPermission
import de.luh.hci.mid.monumentgo.map.ui.components.OSMMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMapScreen(
    navController: NavController,
    monumentRepository: MonumentRepository,
    viewModel: MainMapViewModel = MainMapViewModel(monumentRepository)
) {
    LaunchedEffect(Unit) {
        monumentRepository.updateMonuments()
        Log.d("map", monumentRepository.monuments.value?.count().toString())
    }

    return Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Analytics.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_analytics),
                            contentDescription = "Stats"
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Camera.route) }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_photo_camera),
                    contentDescription = "Camera"
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val context = LocalContext.current
            var currentLocation by remember { mutableStateOf<Location?>(null) }
            var hasLocationPermission by remember { mutableStateOf(false) }

            LocationPermission {
                hasLocationPermission = true
            }

            if (hasLocationPermission) {
                GetCurrentLocation(context) {
                    Log.d("Location", "Received location: $it")
                    currentLocation = it
                }
            }

            OSMMap(currentLocation, viewModel)
        }
    }
}
