package de.luh.hci.mid.monumentgo.map.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import de.luh.hci.mid.monumentgo.map.ui.screen.MainMapViewModel
import de.luh.hci.mid.monumentgo.map.ui.screen.MapState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow


fun createCircleDrawable(
    diameterPx: Int,
    color: Int
): Drawable {
    return ShapeDrawable(OvalShape()).apply {
        intrinsicWidth = diameterPx
        intrinsicHeight = diameterPx
        paint.apply {
            this.color = color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }
}


@Composable
fun OSMMap(
    location: Location?,
    viewModel: MainMapViewModel = MainMapViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AndroidView(factory = { ctx ->
        org.osmdroid.config.Configuration.getInstance()
            .load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
        org.osmdroid.views.MapView(ctx).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }, update = { map ->
        location?.let {
            val currentState = uiState

            val mapController = map.controller
            mapController.setZoom(15.0)
            mapController.setCenter(org.osmdroid.util.GeoPoint(it.latitude, it.longitude))

            // Marker hinzufügen
            val monumentIcon = createCircleDrawable(25, 0xFFFF0000.toInt())
            map.overlays.clear()
            if (currentState is MapState.Ready) {
                map.overlays.addAll(currentState.monuments.map {
                    Marker(map).apply {
                        position = GeoPoint(it.lat, it.lon)
                        icon = monumentIcon
                        setOnMarkerClickListener { marker, mapView ->
                            scope.launch {
                                val details = viewModel.getDetails(it)
                                if (details != null) {
                                    marker.title = details.name
                                    marker.snippet = "Punkte: ${details.points}"
                                } else {
                                    marker.title = "Daten konnten nicht geladen werden."
                                }
                                marker.showInfoWindow()
                            }
                            true
                        }
                    }
                })
            }
            val marker = Marker(map)
            marker.position = GeoPoint(it.latitude, it.longitude)
            marker.title = "Mein Standort"
            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
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
