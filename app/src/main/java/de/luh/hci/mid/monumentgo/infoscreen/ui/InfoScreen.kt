package de.luh.hci.mid.monumentgo.infoscreen.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
                name = viewModel.monumentName.ifBlank { "Loading Name" },
                onBackClicked = {
                    navController.navigate(Screen.Camera.route)
                },
                onVolumeClicked = {
                    println("Text will be played")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    println("FAB clicked!")
                }
            ) {
                Text("Quiz!")
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
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = viewModel.description)

            }
        }
    )
}

//        viewModel.loadDescription {
//            refresh++
//        }