package de.luh.hci.mid.monumentgo.userstats.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.luh.hci.mid.monumentgo.userstats.data.UserStatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStatsScreen(
    viewModel: UserStatsViewModel = viewModel(factory = UserStatsViewModel.Factory)
) {
    val state = viewModel.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> Text("Fehler: ${state.error}", color = MaterialTheme.colorScheme.error)
            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("User: ${state.username}", style = MaterialTheme.typography.titleMedium)
                        Text("Level: ${state.level}", style = MaterialTheme.typography.bodyMedium)
                        Text("Points: ${state.points}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}