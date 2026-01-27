package de.luh.hci.mid.monumentgo.infoscreen.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import de.luh.hci.mid.monumentgo.BuildConfig
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import de.luh.hci.mid.monumentgo.infoscreen.service.generateQuiz
import de.luh.hci.mid.monumentgo.infoscreen.service.generateSummary
import de.luh.hci.mid.monumentgo.infoscreen.service.matchMonument
import de.luh.hci.mid.monumentgo.infoscreen.service.generateTTS
import de.luh.hci.mid.monumentgo.infoscreen.service.getWikipediaArticle
import de.luh.hci.mid.monumentgo.quiz.data.Question
import de.luh.hci.mid.monumentgo.quiz.data.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Suppress("UNCHECKED_CAST")
class InfoViewModel(
    application: Application,
    val imageFile: File
) : AndroidViewModel(application) {
    var description: String = "Generating Information..."
        private set

    var ttsAudioFile: File? by mutableStateOf(null)
        private set

    var quiz by mutableStateOf<List<Triple<String, String, List<String>>>>(emptyList())
        private set

    var quizLoaded: Boolean by mutableStateOf(false)
        private set

    fun loadDescription(monumentRepository: MonumentRepository, onUpdate: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            matchMonument(imageFile, BuildConfig.OPENAI_API_KEY, monumentRepository.monumentsAroundUser.value!!) { monument ->
                if (monument == null) {
                    onUpdate("No monument matched the image.")
                    return@matchMonument
                }
                monumentRepository.selectedMonument.value = monument
                getWikipediaArticle(monument, BuildConfig.OPENAI_API_KEY) { article ->
                    Log.d("openai", "fetched article.")
                    if (article == null) {
                        onUpdate("Could not fetch article for monument.")
                        return@getWikipediaArticle
                    }
                    generateSummary(monument, article, BuildConfig.OPENAI_API_KEY) { summary ->
                        Log.d("openai", "generated summary.")
                        if (summary == null) {
                            onUpdate("Failed to create summary.")
                            return@generateSummary
                        }
                        description = summary
                        onUpdate(null)

//                        val audioFile = File(getApplication<Application>().cacheDir, "description.mp3")
//                        generateTTS(description, BuildConfig.OPENAI_API_KEY, audioFile) { success ->
//                            if (success) {
//                                viewModelScope.launch(Dispatchers.Main) {
//                                    ttsAudioFile = audioFile
//                                }
//                            }
//                        }
                        generateQuiz(monument, summary, BuildConfig.OPENAI_API_KEY) { quizResult ->
                            Log.d("openai", "generated quiz.")
                            if (quizResult == null) {
                                onUpdate("Failed to create quiz.")
                                return@generateQuiz
                            }
                            viewModelScope.launch(Dispatchers.Main) {
                                quiz = quizResult
                                quizLoaded = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun prepareQuizForNavigation() {
        val mappedQuestions = quiz.map { item: Triple<String, String, List<String>> ->

            val questionText = item.first
            val correctAnswer = item.second
            val wrongAnswers = item.third

            val allOptions = (wrongAnswers + correctAnswer).shuffled()

            val correctIndex = allOptions.indexOf(correctAnswer)

            Question(
                text = questionText,
                answers = allOptions,
                correctIndex = correctIndex
            )
        }

        QuizRepository.currentQuestions = mappedQuestions
    }


    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                val imageFile = File(application.filesDir, "imageToScan.jpg")
                return InfoViewModel(
                    application,
                    imageFile
                ) as T
            }
        }
    }
}
