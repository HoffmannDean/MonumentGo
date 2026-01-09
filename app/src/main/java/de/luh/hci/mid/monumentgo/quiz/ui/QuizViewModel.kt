package de.luh.hci.mid.monumentgo.quiz.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import de.luh.hci.mid.monumentgo.quiz.data.Question
import de.luh.hci.mid.monumentgo.quiz.data.QuizRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel(
    private val userRepo: UserRepository = UserRepository()
): ViewModel() {
    init{
        QuizRepository.currentScore = 0
    }
    private val questions: List<Question> = QuizRepository.currentQuestions.ifEmpty {
        // if there are no question
        listOf(
            Question("Keine Daten geladen", listOf("Ok"), 0)
        )
    }

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
        val currentQuestion = getCurrentQuestion()
        if (index == currentQuestion?.correctIndex){
            QuizRepository.currentScore++
        }

        viewModelScope.launch {
            delay(2000)

            if (currentQuestionIndex < questions.size){
                currentQuestionIndex++
            }

            selectedAnswerIndex = -1
        }
    }
}