package de.luh.hci.mid.monumentgo.quiz.data

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(resultViewModel: QuizResultViewModel = viewModel()) {
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
            Text("${resultViewModel.quizResults.count { (_, value) -> value }} / ${resultViewModel.quizResults.size} correct")

            Text(resultViewModel.points.toString())

            Text("Level ${resultViewModel.level}")

            ElevatedButton(onClick = {}) { Text("Back to Map") }
        }
    }
}

@Preview
@Composable
fun QuizResultScreenPreview() {
    QuizResultScreen(viewModel(factory = QuizResultViewModel.Factory))
}
