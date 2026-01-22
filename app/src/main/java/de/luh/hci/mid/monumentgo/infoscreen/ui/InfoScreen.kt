package de.luh.hci.mid.monumentgo.infoscreen.ui

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.luh.hci.mid.monumentgo.core.navigation.Screen

@Composable
fun ImageInfoScreen(
    navController: NavController,
    viewModel: InfoViewModel = viewModel()
) {
    var refresh by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val audioFile = viewModel.ttsAudioFile

    val isPlayerReady = remember { mutableStateOf(false)}

    val mediaPlayer = remember(audioFile) {
        audioFile?.let {
            MediaPlayer().apply {
                setDataSource(it.absolutePath)
//                prepare()
                playbackParams.setSpeed(10.0f)

                prepareAsync()
            }
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer?.release()
        }
    }


    LaunchedEffect(Unit) {
        viewModel.loadDescription {
            refresh++
        }
    }

    val bitmap = remember(refresh) {
        if (viewModel.imageFile.exists()) {
            BitmapFactory
                .decodeFile(viewModel.imageFile.absolutePath)
                ?.asImageBitmap()
        } else null
    }

    Scaffold(
        topBar = {
            InfoTopBar (
                name = "Informationen",
                onBackClicked = {
                    navController.navigate(Screen.Camera.route)
                },
                onVolumeClicked = {
                    if (mediaPlayer != null)
                    {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                        }
                        else {
                            mediaPlayer.start()
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (viewModel.quizLoaded)
            {
                FloatingActionButton(
                    onClick = {
                        viewModel.prepareQuizForNavigation()
                        navController.navigate(Screen.Quiz.route)
                    }
                ) {
                    Text("Quiz!")
                }
            }
            else {
                FloatingActionButton(
                    onClick = { }
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(90f)
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(24.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = viewModel.description)

            }
        }
    )
}
