package com.waynebloom.highscores.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.data.Score

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScoreCard(
    score: Score,
    onSingleScoreTap: (Score) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = { onSingleScoreTap(score) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row() {
                Text(
                    text = score.name,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "<game>",
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
            Row() {
                Text(
                    text = score.score.toString(),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "<rank>",
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}