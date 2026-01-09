package de.luh.hci.mid.monumentgo.quiz.data

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    navController: NavController,
    resultViewModel: QuizResultViewModel = viewModel(factory = QuizResultViewModel.Factory)
) {
    val currentScore : Int = QuizRepository.currentScore
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Quiz results") }) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("${currentScore} / ${resultViewModel.quizResults.size} correct")

            Text("${resultViewModel.getUserPoints()} + ${currentScore}")

            Text("Level ${resultViewModel.level}")

            ElevatedButton(
                onClick = {
                    resultViewModel.submitScore(currentScore)
                    navController.navigate(Screen.MainMap.route)
                }
            ) {
                Text("Back to Map")
            }
        }
    }
}

@Preview
@Composable
fun QuizResultScreenPreview() {
    QuizResultScreen(viewModel(factory = QuizResultViewModel.Factory))
}
