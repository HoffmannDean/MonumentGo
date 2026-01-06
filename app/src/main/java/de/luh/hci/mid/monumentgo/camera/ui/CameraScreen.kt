package de.luh.hci.mid.monumentgo.camera.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("CameraScreen", "PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d("CameraScreen", "PERMISSION DENIED")
        }
    }
    val context = LocalContext.current

    val viewModel: CameraViewModel = viewModel(factory = CameraViewModel.Factory)
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
                    IconButton(onClick = { navController.navigate(Screen.MainMap.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton({
                val imageCapture = ImageCapture.Builder()
                    .setTargetRotation(context.display.rotation)
                    .build()

                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                    File(
                        context.filesDir,
                        "imageToScan.jpg"
                    )
                ).build()

                /*
                    imageCapture.takePicture(
                        cameraExecutor,
                        outputFileOptions,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exception: ImageCaptureException) {
                                TODO("Not yet implemented")
                            }

                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                TODO("Not yet implemented")
                            }
                        }
                    )
                    */
                navController.navigate(Screen.Info.route)
            }) {
                Icon(Icons.Default.Add, "Take Picture")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                CameraPreview(
                    viewModel, modifier = Modifier.padding(paddingValues)
                )
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity, Manifest.permission.CAMERA
            ) -> Log.i("CameraScreen", "Shop camera permissions dialog")

            else -> {
                // Asking for permission
                launcher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

@Composable
fun CameraPreview(viewModel: CameraViewModel, modifier: Modifier = Modifier) {
    val cameraProviderFuture = viewModel.cameraProviderFuture
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    cameraProviderFuture.addListener(
                        {
                            viewModel.bindPreview(
                                cameraProviderFuture, lifecycleOwner, this
                            )
                        }, ContextCompat.getMainExecutor(context)
                    )
                }
            }, modifier = Modifier.fillMaxSize()
        )
    }
}
