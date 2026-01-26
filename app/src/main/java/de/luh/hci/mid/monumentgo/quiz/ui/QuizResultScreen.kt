package de.luh.hci.mid.monumentgo.quiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import de.luh.hci.mid.monumentgo.quiz.data.QuizRepository
import de.luh.hci.mid.monumentgo.quiz.data.QuizResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    navController: NavController,
    resultViewModel: QuizResultViewModel = viewModel(factory = QuizResultViewModel.Companion.Factory)
) {
    val currentScore: Int = QuizRepository.currentScore
    val storedScore: Int = resultViewModel.getUserPoints()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    "Quiz Results",
                    style = MaterialTheme.typography.headlineLarge
                )
            })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${currentScore} / ${resultViewModel.getQuestionsSize()} Correct",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    "${storedScore} Points + ${currentScore}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    "Level ${resultViewModel.getUserLevel()}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = {
                        resultViewModel.submitScore(currentScore)
                        navController.navigate(Screen.MainMap.route)
                    }
                ) {
                    Text("Back to Map")
                }
                ElevatedButton(
                    onClick = {
                        resultViewModel.submitScore(currentScore)
                        navController.navigate(Screen.Leaderboard.route)
                    }
                ) {
                    Text("Leaderboard")
                }
            }
        }
    }
}
