package com.waynebloom.scorekeeper.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

// Fixme: Delete this on master
@Composable
fun GameCard(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
) {
    val adjustedColor = color
        .copy(alpha = 0.2f)
        .compositeOver(MaterialTheme.colorScheme.surfaceVariant)

    Surface(
        color = adjustedColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = name.ifEmpty { stringResource(id = R.string.text_no_game_name) },
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable(onClick = onClick)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 12.dp),
        )
    }
}

@Composable
fun NewGameCard(
    name: String,
    color: Color,
    highScore: String,
    noOfMatches: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = color,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.screenEdge)
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Max)
        ) {
            Text(text = name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.sectionContent))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Matches",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 4.dp)
                )
                Text(text = noOfMatches)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "High score",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 16.dp)
                )
                Text(text = highScore)
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    MedianMeepleTheme {
        NewGameCard(
            name = "Wingspan",
            color = GameDomainModel.DisplayColors[3],
            highScore = "100",
            noOfMatches = "5",
        )
    }
}
