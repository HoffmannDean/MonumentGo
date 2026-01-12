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

class QuizResultViewModel(
    private val userRepo: UserRepository
) : ViewModel() {
    val level: UInt = 2u

    fun getUserPoints() : Int {
        if (userRepo.userProfile.value == null) {
            return 0
        }
        println("USER_PROFILE: " + userRepo.userProfile.value)
        return userRepo.getUserProfile().value!!.points
    }

    @OptIn(ExperimentalUuidApi::class)
    fun submitScore(newScore: Int) {
            viewModelScope.launch {
            userRepo.setUserScore(newScore)
        }
    }

    fun getQuestionsSize() : Int {
        return QuizRepository.currentQuestions.size
    }

    fun getUserLevel() : Int {
        return userRepo.getUserLevel()
    }

    companion object {
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            // val questions = listOf<Question>();
            initializer {
                val app = (this[APPLICATION_KEY] as MonumentGo)
                println("APP INFO: " + app.applicationInfo)
                QuizResultViewModel(app.userRepository)}
        }
    }
}