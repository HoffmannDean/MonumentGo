package de.luh.hci.mid.monumentgo.quiz.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class QuizResultViewModel() : ViewModel() {
    val quizResults: Map<String, Boolean> = mapOf(
        "Q1" to true,
        "Q2" to false,
        "Q3" to true
    )

    var points: UInt = 12345u

    val level: UInt = 2u

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { QuizResultViewModel() }
        }
    }
}