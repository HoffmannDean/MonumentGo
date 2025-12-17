package de.luh.hci.mid.monumentgo.map.domain

import android.content.Context
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

@Composable
fun OSMMap(location: Location?) {
    val context = LocalContext.current

    AndroidView(factory = { ctx ->
        org.osmdroid.config.Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
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
            marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
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
