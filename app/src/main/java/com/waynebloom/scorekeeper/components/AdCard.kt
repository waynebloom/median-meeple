package com.waynebloom.scorekeeper.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.PreviewScoreData
import com.waynebloom.highscores.R
import com.waynebloom.highscores.data.EMPTY_MATCH_ENTITY
import com.waynebloom.highscores.data.EMPTY_MATCH_OBJECT
import com.waynebloom.highscores.data.MatchObject
import com.waynebloom.highscores.ui.theme.HighScoresTheme
import com.waynebloom.highscores.ui.theme.orange100

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdCard(
    // ad
    // onAdTap
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = gameColor.copy(alpha = 0.3f),
        onClick = { onSingleMatchTap(match.entity.gameOwnerId, match.entity.id) }
    ) {

    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddCardPreview() {
    HighScoresTheme() {
        AdCard(
            match = EMPTY_MATCH_OBJECT.apply {
                scores = PreviewScoreData
            },
            gameInitial = "W",
            gameColor = orange100,
            onSingleMatchTap = {_,_->}
        )
    }
}