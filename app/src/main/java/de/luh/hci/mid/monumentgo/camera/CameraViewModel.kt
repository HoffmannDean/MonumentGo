package de.luh.hci.mid.monumentgo.camera

import android.app.Application
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraViewModel(app: Application) : ViewModel() {
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    val cameraProviderFuture = ProcessCameraProvider.getInstance(app)

    fun bindPreview(
        cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProvider = cameraProviderFuture.get()

        val preview: Preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
        } catch (ex: Exception) {
            Log.d(javaClass.simpleName, "Failed binding: $ex")
        }
    }

    override fun onCleared() {
        cameraExecutor.shutdown()

        super.onCleared()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                CameraViewModel(app)
            }
        }
    }
}