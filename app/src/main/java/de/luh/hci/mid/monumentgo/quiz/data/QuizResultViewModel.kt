package de.luh.hci.mid.monumentgo.quiz.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import de.luh.hci.mid.monumentgo.MonumentGo
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository

class QuizResultViewModel(
    private val userRepo: UserRepository,
    private val monumentRepo: MonumentRepository
) : ViewModel() {
    val level: UInt = 2u

    fun getUserPoints() : Int {
        return userRepo.userProfile.value?.points ?: -1
    }

    @OptIn(ExperimentalUuidApi::class)
    fun submitScore(answeredQuestions: Int) {
        val monumentPoints = monumentRepo.selectedMonument?.points ?: 0
        viewModelScope.launch {
            userRepo.addToUserScore(monumentPoints + 25 * answeredQuestions)
            monumentRepo.submitMonumentDiscovery()
        }
    }

    fun getQuestionsSize() : Int {
        return QuizRepository.currentQuestions.size
    }

    fun getUserLevel() : Int {
        return userRepo.userLevel
    }

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            // val questions = listOf<Question>();
            initializer {
                val app = (this[APPLICATION_KEY] as MonumentGo)
                println("APP INFO: " + app.applicationInfo)
                QuizResultViewModel(app.userRepository, monumentRepo = app.monumentRepository)}
        }
    }
}