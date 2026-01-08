package de.luh.hci.mid.monumentgo.map.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await


@Composable
fun OSMMap(location: Location?) {
    val context = LocalContext.current

    AndroidView(factory = { ctx ->
        org.osmdroid.config.Configuration.getInstance()
            .load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
        org.osmdroid.views.MapView(ctx).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }, update = { map ->
        location?.let {
            val mapController = map.controller
            mapController.setZoom(15.0)
            mapController.setCenter(org.osmdroid.util.GeoPoint(it.latitude, it.longitude))

            // Marker hinzufügen
            val marker = org.osmdroid.views.overlay.Marker(map)
            marker.position = org.osmdroid.util.GeoPoint(it.latitude, it.longitude)
            marker.title = "Mein Standort"
            marker.setAnchor(
                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
            )
            map.overlays.clear()
            map.overlays.add(marker)

            map.invalidate()
        }
    })
}

@Composable
fun GetCurrentLocation(context: Context, onLocation: (Location) -> Unit) {
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(Unit) {
        try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let { onLocation(it) }
        } catch (e: SecurityException) {
        }
    }
}

@Composable
fun LocationPermission(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val permission = Manifest.permission.ACCESS_FINE_LOCATION

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }

            else -> launcher.launch(permission)
        }
    }
}
