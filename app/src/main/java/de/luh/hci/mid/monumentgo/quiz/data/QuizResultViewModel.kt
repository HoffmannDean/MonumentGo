package de.luh.hci.mid.monumentgo.quiz.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/*
data class Question(
    val text: String,
    val answers: List<String>,
    val correctIndex: Int, // store which answer is actually correct
    val selectedIndex: Int // tapped answer by user
)
*/

class QuizResultViewModel(/*questions: List<Question>*/) : ViewModel() {
    val quizResults: Map<String, Boolean> = mapOf(
        // questions.forEach { entry -> entry.name to entry.correctIndex == entry.selectedIndex }
        "Q1" to true,
        "Q2" to false,
        "Q3" to true
    )

    var points: UInt = 12345u

    val level: UInt = 2u

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            // val questions = listOf<Question>();
            initializer { QuizResultViewModel(/*questions*/) }
        }
    }
}