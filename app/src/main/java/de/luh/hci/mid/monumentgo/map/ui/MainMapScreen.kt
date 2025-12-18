package de.luh.hci.mid.monumentgo.map.ui

import android.location.Location
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.R
import de.luh.hci.mid.monumentgo.map.domain.OSMMap
import de.luh.hci.mid.monumentgo.navigation.domain.Screen
import de.luh.hci.mid.monumentgo.map.domain.GetCurrentLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMapScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Stats.route) }) {
                        Icon(painter= painterResource(id = R.drawable.outline_analytics), contentDescription = "Stats")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* GoTo Camera */ }) {
                Icon(painter= painterResource(id = R.drawable.outline_photo_camera), contentDescription = "Camera")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val context = LocalContext.current
            var currentLocation by remember{ mutableStateOf<Location?>(null) }

            GetCurrentLocation(context) { location ->
                currentLocation = location
            }

            OSMMap(location = currentLocation)
        }
    }
}
