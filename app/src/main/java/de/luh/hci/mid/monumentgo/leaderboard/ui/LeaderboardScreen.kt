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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.leaderboard.data.LeaderboardEntry
import de.luh.hci.mid.monumentgo.leaderboard.data.LeaderboardViewModel


@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = viewModel(factory = LeaderboardViewModel.Factory)) {
    val allPlayers by viewModel.leaderboardEntries.collectAsState(initial = emptyList())

    // Auto-scroll to current user logic
    val listState = rememberLazyListState()

    LaunchedEffect(allPlayers) {
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
                text = "Level ${viewModel.getUserLevel()}",
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