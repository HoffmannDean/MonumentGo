package de.luh.hci.mid.monumentgo.quiz.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.min


@Composable
fun QuizScreen(
    viewModel: QuizViewModel = viewModel()
) {
    val currentQuestion = viewModel.getCurrentQuestion()

    Scaffold(
        topBar = {
            QuizTopBar(
                onBackClicked = {
                    println("Zurück-Pfeil geklickt! Navigation ausführen...")
                },
                quizIdx = (min(
                    viewModel.currentQuestionIndex + 1,
                    viewModel.questionsSize
                )).toString(),
                quizMax = viewModel.questionsSize.toString()
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            if (!viewModel.isQuizFinished) {
                Text(
                    text = currentQuestion?.text ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                currentQuestion?.answers?.forEachIndexed { index, answerText ->
                    val buttonColor = when (viewModel.selectedAnswerIndex){
                        // not clicked
                        -1 -> MaterialTheme.colorScheme.secondaryContainer
                        else -> {
                            // clicked
                            when(index) {
                                currentQuestion.correctIndex -> Color.Green
                                viewModel.selectedAnswerIndex -> Color.Red
                                else -> MaterialTheme.colorScheme.secondaryContainer

                            }
                        }
                    }

                    QuizButton(
                        buttonText = answerText,
                        containerColor = buttonColor,
                        onClick = {
                            viewModel.handleAnswer(index)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                Text(
                    text = "Quiz Beendet!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Alle 3 Fragen wurden bearbeitet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}