package de.luh.hci.mid.monumentgo.infoscreen.ui


import android.R.attr.contentDescription
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.luh.hci.mid.monumentgo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTopBar(
    name: String,
    playerEnabled: Boolean,
    onBackClicked: () -> Unit,
    onVolumeClicked: () -> Unit
) {
    var playing by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(name)
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(enabled = playerEnabled, onClick = {
                if (!playerEnabled) return@IconButton
                playing = !playing
                onVolumeClicked()
            }) {
                if (playing) {
                    Icon(
                        painterResource(R.drawable.media_pause),
                        contentDescription = "Stop player"
                    )
                } else {
                    Icon(
                        painterResource(R.drawable.media_output),
                        contentDescription = "Listen to text"
                    )
                }
            }
        }
    )
}