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
import de.luh.hci.mid.monumentgo.settings.data.SettingsProvider

class QuizResultViewModel(
    private val userRepo: UserRepository,
    private val monumentRepo: MonumentRepository
) : ViewModel() {
    val level: UInt = 2u

    fun getUserPoints() : Int {
        return userRepo.userProfile.value?.points ?: -1
    }

    fun calculateScore(answeredQuestions: Int) : Int {
        val monumentPoints = monumentRepo.selectedMonument.value?.points ?: 0
        return monumentPoints + SettingsProvider.pointsPerCorrectAnswer * answeredQuestions
    }

    @OptIn(ExperimentalUuidApi::class)
    fun submitScore(answeredQuestions: Int) {
        if (userRepo.userProfile.value == null || monumentRepo.selectedMonument.value == null) return;
        if (monumentRepo.discoveredMonuments.value?.contains(monumentRepo.selectedMonument.value?.toMonument()) != false) return;
        viewModelScope.launch {
            userRepo.addToUserScore(calculateScore(answeredQuestions))
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