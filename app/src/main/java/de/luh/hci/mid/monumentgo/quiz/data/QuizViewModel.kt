package de.luh.hci.mid.monumentgo.quiz.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.luh.hci.mid.monumentgo.MonumentGo
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel(
    private val userRepo: UserRepository
): ViewModel() {
    init{
        QuizRepository.currentScore = 0
    }
    private val questions: List<Question> = QuizRepository.currentQuestions.ifEmpty {
        // if there are no question
        listOf(
            Question("Failed to load data", emptyList(), 0)
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
            delay(1000)

            if (currentQuestionIndex < questions.size){
                currentQuestionIndex++
            }

            selectedAnswerIndex = -1
        }
    }

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            // val questions = listOf<Question>();
            initializer {
                val app = (this[APPLICATION_KEY] as MonumentGo)
                println("APP INFO: " + app.applicationInfo)
                QuizViewModel(app.userRepository)}
        }
    }
}