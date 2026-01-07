package de.luh.hci.mid.monumentgo.camera.ui

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val surfaceRequests = remember { MutableStateFlow<SurfaceRequest?>(null) }
    val surfaceRequest by surfaceRequests.collectAsState(null)

    val lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.awaitInstance(context)

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequests.value = request }
        }

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(context.display.rotation)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            lensFacing,
            preview,
            imageCapture!!
        )
    }

    fun capturePhoto(context: Context, imageCapture: ImageCapture?) {
        val capture = imageCapture ?: return

        val name = "imageToScan.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            File(
                context.filesDir,
                name
            )
        ).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onCaptureStarted() {
                    outputOptions.file?.delete()
                    super.onCaptureStarted()
                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri?.path?.let {
                        Log.d("CameraCapture", it)
                        navController.navigate(Screen.Info.route)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Scan Monument")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton({
                capturePhoto(context, imageCapture)
            }) {
                Icon(
                    Icons.Default.Add,
                    "Capture Image"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                request,
                Modifier.fillMaxSize()
            )
        }
    }
}
