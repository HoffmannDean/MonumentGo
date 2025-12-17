package de.luh.hci.mid.monumentgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.luh.hci.mid.monumentgo.navigation.AppNavigation
import de.luh.hci.mid.monumentgo.infoscreen.ImageInfoScreen
import de.luh.hci.mid.monumentgo.ui.theme.MonumentGoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonumentGoTheme {
                AppNavigation()
            }
        }
    }
}