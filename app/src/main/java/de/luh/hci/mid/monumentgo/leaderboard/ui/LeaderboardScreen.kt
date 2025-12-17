package de.luh.hci.mid.monumentgo.leaderboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val isCurrentUser: Boolean = false
)
@Composable
fun LeaderboardScreen() {
    val allPlayers = listOf(

        LeaderboardEntry(1, "Max", 121798),

        LeaderboardEntry(2, "Florian", 68277),

        LeaderboardEntry(3, "Thorsten", 51050),

        LeaderboardEntry(4, "Sarah", 49500),

        LeaderboardEntry(5, "Andreas", 48210),

        LeaderboardEntry(6, "Julia", 47800),

        LeaderboardEntry(7, "Daniel", 46550),

        LeaderboardEntry(8, "Lisa", 45100),

        LeaderboardEntry(9, "Kevin", 44900),

        LeaderboardEntry(10, "Maria", 43250),

        LeaderboardEntry(11, "Stefan", 42100),

        LeaderboardEntry(12, "Anna", 41500),

        LeaderboardEntry(13, "Patrick", 40200),

        LeaderboardEntry(14, "Laura", 39800),

        LeaderboardEntry(15, "Christian", 38500),

        LeaderboardEntry(16, "Nadine", 37900),

        LeaderboardEntry(17, "Tobias", 36400),

        LeaderboardEntry(18, "Vanessa", 35200),

        LeaderboardEntry(19, "Marco", 34800),

        LeaderboardEntry(20, "Sabrina", 33500),

        LeaderboardEntry(21, "Alexander", 32100, true), // Target User

        LeaderboardEntry(22, "Melanie", 31400),

        LeaderboardEntry(23, "Philipp", 30500),

        LeaderboardEntry(24, "Jana", 29800),

        LeaderboardEntry(25, "Tim", 28500),

        LeaderboardEntry(26, "Christina", 27200),

        LeaderboardEntry(27, "Dennis", 26400),

        LeaderboardEntry(28, "Nicole", 25100),

        LeaderboardEntry(29, "Jan", 24500),

        LeaderboardEntry(30, "Katharina", 232)

    )
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        val userIndex = allPlayers.indexOfFirst { it.isCurrentUser }
        if (userIndex >= 0) listState.animateScrollToItem(userIndex)
    }

    Scaffold(
        topBar = { LeaderboardTopBar(onHomeClick = {}, onProfileClick = {}) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp), // Global side padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Level 2",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // take up remaining space
                    .padding(bottom = 16.dp), // space from bottom of screen
                shape = RoundedCornerShape(12.dp), // rounded corners
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface // card background
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // subtle shadow
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Text("Rank", modifier = Modifier.weight(0.20f), style = MaterialTheme.typography.titleLarge)
                            Text("Player", modifier = Modifier.weight(0.50f), style = MaterialTheme.typography.titleLarge)
                            Text("Score", modifier = Modifier.weight(0.30f), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.End)
                        }
                        HorizontalDivider()
                    }

                    items(allPlayers) { player ->
                        LeaderboardRow(player)
                    }
                }
            }
        }
    }
}