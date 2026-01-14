package de.luh.hci.mid.monumentgo.analytics.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import de.luh.hci.mid.monumentgo.leaderboard.ui.LeaderboardScreen
import de.luh.hci.mid.monumentgo.userstats.ui.UserStatsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(x0: NavHostController) {

    val tabs = listOf("Personal", "Leaderboard")
    var selectedTabIndex by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analytics") }, navigationIcon = {
                IconButton(onClick = {
                    x0.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) })
                }
            }

            when (selectedTabIndex) {
                0 -> UserStatsScreen()
                1 -> LeaderboardScreen(x0)
            }
        }
    }
}