package de.luh.hci.mid.monumentgo.quiz.ui

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import de.luh.hci.mid.monumentgo.quiz.data.QuizRepository
import de.luh.hci.mid.monumentgo.quiz.data.QuizRepository.currentScore
import de.luh.hci.mid.monumentgo.quiz.data.QuizResultViewModel
import de.luh.hci.mid.monumentgo.settings.data.SettingsProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    navController: NavController,
    resultViewModel: QuizResultViewModel = viewModel(factory = QuizResultViewModel.Companion.Factory)
) {
    val correctAnswers: Int = QuizRepository.currentScore
    val userPoints: Int = resultViewModel.getUserPoints()
    val addedScore = resultViewModel.calculateScore(correctAnswers)
    Log.e("quiz", "userPoints: $userPoints")
    LaunchedEffect(Unit) {
        resultViewModel.submitScore(currentScore)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quiz Results",
                style = MaterialTheme.typography.headlineLarge
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$correctAnswers / ${resultViewModel.getQuestionsSize()} Correct",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    "$userPoints Points (+ ${addedScore})",
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
