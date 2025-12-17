package de.luh.hci.mid.monumentgo.leaderboard.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    val backgroundColor = if (entry.isCurrentUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (entry.isCurrentUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.20f),
                textAlign = TextAlign.Center
            )
            // Name
            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(0.50f).padding(start = 12.dp)
            )
            // Score
            Text(
                text = entry.score.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(0.30f),
                textAlign = TextAlign.End
            )
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}