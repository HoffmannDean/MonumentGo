package de.luh.hci.mid.monumentgo.map.ui.screen

import android.R.attr.padding
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.R
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import de.luh.hci.mid.monumentgo.map.ui.components.GetCurrentLocation
import de.luh.hci.mid.monumentgo.map.ui.components.LocationPermission
import de.luh.hci.mid.monumentgo.map.ui.components.OSMMap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// we have to suppress the warning in order for OSMMapView to work properly
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMapScreen(
    navController: NavController,
    monumentRepository: MonumentRepository,
    viewModel: MainMapViewModel = MainMapViewModel(monumentRepository)
) {
    // consume error if present
    val error = navController.previousBackStackEntry?.savedStateHandle?.get<String>("error")
    val snackbarHostState = remember { SnackbarHostState() }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.updateMonuments()
    }
    LaunchedEffect(error) {
        error?.let {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.remove<String>("error")
            snackbarHostState.showSnackbar(it)
        }
    }

    return Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Leaderboard.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_analytics),
                            contentDescription = "Stats"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.Camera.route)
                coroutineScope.launch {
                    if (currentLocation == null) {
                        Log.e("Location", "Location is null")
                        return@launch
                    }
                    monumentRepository.getMonumentsAroundUser(currentLocation!!)
                    if (monumentRepository.monumentsAroundUser.value.isNullOrEmpty()) {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "error", "No undiscovered monuments found nearby.")
                        navController.navigate(Screen.MainMap.route)
                    }
                }
            }) {
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
        ) {
            val context = LocalContext.current
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
