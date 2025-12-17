package de.luh.hci.mid.monumentgo.map.ui.components

import android.preference.PreferenceManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun OSMMapView() {

    val context = LocalContext.current

    AndroidView(
        factory = {
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)

                controller.setZoom(16.0)
                controller.setCenter(GeoPoint(52.3784, 9.7416))
            }
        },
        update = {}
    )
}