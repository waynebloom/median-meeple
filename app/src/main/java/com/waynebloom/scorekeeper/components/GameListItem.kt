package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameListItem(
    name: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
        modifier = modifier.height(64.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(color.copy(alpha = Alpha.textSelectionBackground))
            ) {

                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    color = color,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = name.ifEmpty { stringResource(id = R.string.text_no_game_name) },
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun GameListItemNew(
    name: String,
    gameColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.onBackground,
    onClick: () -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick).then(modifier)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(gameColor.copy(alpha = Alpha.textSelectionBackground))
        ) {

            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = gameColor,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Text(
            text = name.ifEmpty { stringResource(id = R.string.text_no_game_name) },
            style = MaterialTheme.typography.body1,
            color = textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Preview
@Composable
fun GameCardPreview() {
    MedianMeepleTheme {
        GameListItem(name = "Wingspan", color = MaterialTheme.colors.primary, onClick = {})
    }
}

@Preview
@Composable
fun GameCardNewPreview() {
    MedianMeepleTheme {
        Box(Modifier.background(MaterialTheme.colors.background)) {
            GameListItemNew(
                name = "Wingspan",
                gameColor = MaterialTheme.colors.primary,
                onClick = {}
            )
        }
    }
}
