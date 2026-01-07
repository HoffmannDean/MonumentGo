package de.luh.hci.mid.monumentgo.infoscreen.ui

import android.app.Application
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
import de.luh.hci.mid.monumentgo.infoscreen.service.describeImage
import de.luh.hci.mid.monumentgo.infoscreen.service.extractMonumentName
import de.luh.hci.mid.monumentgo.infoscreen.service.generateQuiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Suppress("UNCHECKED_CAST")
class InfoViewModel(
    application: Application,
    val imageFile: File
) : AndroidViewModel(application) {
    var description: String = "Loading..."
        private set

    var monumentName: String by mutableStateOf("Loading...")
        private set

    var quiz by mutableStateOf<List<Triple<String, String, List<String>>>>(emptyList())
        private set

    fun loadDescription(onUpdate: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            describeImage(imageFile, BuildConfig.OPENAI_API_KEY) {
                description = it ?: "Failed to load description"
                onUpdate()

                extractMonumentName(imageFile, BuildConfig.OPENAI_API_KEY) { name ->
                    viewModelScope.launch(Dispatchers.Main) {
                        monumentName = name ?: "Unknown Monument"
                    }
                }

                generateQuiz(description, BuildConfig.OPENAI_API_KEY) { quizResult ->
                    viewModelScope.launch(Dispatchers.Main) {
                        quiz = quizResult ?: emptyList()
                        println(quiz)
                    }
                }
            }
        }
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
