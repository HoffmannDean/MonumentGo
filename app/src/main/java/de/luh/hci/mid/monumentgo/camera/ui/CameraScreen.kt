package de.luh.hci.mid.monumentgo.camera.ui

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    val viewModel: CameraViewModel = viewModel(factory = CameraViewModel.Factory)
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Scan Monument") }) },
        floatingActionButton = { FloatingActionButton ({} ) {
            Icon(Icons.Default.Add, "Take Picture")
        } },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        CameraPreview(
            viewModel,
            modifier = Modifier.padding(paddingValues)
        )
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
                                cameraProviderFuture,
                                lifecycleOwner,
                                this
                            )
                        }, ContextCompat.getMainExecutor(context)
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
