package de.luh.hci.mid.monumentgo.quiz.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Question(
    val text: String,
    val answers: List<String>,
    val correctIndex: Int // store which answer is actually correct
)
class QuizViewModel : ViewModel() {
    private val questions = listOf(
        Question(
            text = "Wann wurde der Triumphbogen erbaut?",
            answers = listOf("1906", "1810", "1836"),
            correctIndex = 2
        ),
        Question(
            text = "Wie hoch ist der Eiffelturm (inkl. Spitze)?",
            answers = listOf("330m", "250m", "400m"),
            correctIndex = 0
        ),
        Question(
            text = "Welches Element hat die Ordnungszahl 1?",
            answers = listOf("Helium", "Wasserstoff", "Lithium"),
            correctIndex = 1
        )
    )

    var currentQuestionIndex by mutableIntStateOf(0)
        private set

    var selectedAnswerIndex by mutableIntStateOf(-1)
        private set

    val isQuizFinished: Boolean
        get() = currentQuestionIndex == questions.size

    val questionsSize: Int
        get() = questions.size

    fun getCurrentQuestion(): Question? {
        if (isQuizFinished) return null
        return questions[currentQuestionIndex]
    }

    fun handleAnswer(index: Int) {
        if (selectedAnswerIndex != -1) return // already clicked

        selectedAnswerIndex = index

        viewModelScope.launch {
            delay(3000)

            if (currentQuestionIndex < questions.size){
                currentQuestionIndex++
            }

            selectedAnswerIndex = -1
        }
    }
}