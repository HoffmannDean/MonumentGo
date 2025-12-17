package de.luh.hci.mid.monumentgo.leaderboard.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardTopBar(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {},
        navigationIcon = {
            Spacer(modifier = Modifier.size(48.dp))
/*
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = ArrowLe,
                    contentDescription = "Zurück"
                )
            }
* */
        },
        actions = {
            Spacer(modifier = Modifier.size(48.dp))
            /*
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Profil"
                )
        }
            * */

        }
    )
}