package de.luh.hci.mid.monumentgo.quiz.ui

import androidx.compose.material3.CenterAlignedTopAppBar // Neuer Import
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(
    onBackClicked: () -> Unit,
    quizIdx: String,
    quizMax: String,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Frage " + quizIdx + "/" + quizMax)
        },
        navigationIcon = {
            /*
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück"
                )
            }
            * */

        },
        actions = {}
    )
}