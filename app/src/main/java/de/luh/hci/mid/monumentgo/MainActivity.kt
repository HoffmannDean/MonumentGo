package de.luh.hci.mid.monumentgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import de.luh.hci.mid.monumentgo.core.navigation.AppNavigation
import de.luh.hci.mid.monumentgo.core.ui.theme.MonumentGoTheme
import de.luh.hci.mid.monumentgo.infoscreen.ui.ImageInfoScreen
import de.luh.hci.mid.monumentgo.infoscreen.ui.InfoViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonumentGoTheme {
//                AppNavigation()
                ImageInfoScreen()
            }
        }
    }
}