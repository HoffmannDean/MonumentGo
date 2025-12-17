package de.luh.hci.mid.monumentgo.quiz.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizButton(
    onClick: () -> Unit,
    buttonText: String,
    containerColor: Color
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor
        ),
        shape = RectangleShape
    ) {
        Text(buttonText, fontSize = 32.sp)
    }
}
