package de.luh.hci.mid.monumentgo.analytics.ui


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(x0: NavHostController) {

    val tabs = listOf("Personal", "Leaderboard")
    var selectedTabIndex by remember { mutableStateOf(0) }

    SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index, // Ist dieser Tab der ausgewählte?
                onClick = { selectedTabIndex = index }, // Tab anklickbar machen
                text = { Text(title) } // Den Tab-Titel anzeigen
            )
        }
    }
}