package com.waynebloom.scorekeeper.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.PlayerEntitiesDefaultPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.convertToShortFormatScore
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.toShortFormatString
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.model.GameUiModel
import com.waynebloom.scorekeeper.ui.model.MatchUiModel
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun MatchListItem(
    match: MatchUiModel,
    scoringMode: ScoringMode,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.clickable { onClick(match.id) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {

            if (match.players.isNotEmpty()) {
                val winningPlayer = match.players.getWinningPlayer(scoringMode)

                VictorCard(
                    name = winningPlayer.name.value.text,
                    score = winningPlayer.totalScore.toShortFormatString(),
                    modifier = Modifier.weight(1f, fill = false)
                )
            } else {
                EmptyPlayersCard()
            }

            PlayerCountCard(count = match.players.size)
        }
    }
}

// TODO: change this to use UiModels and cleaner code
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchListItem(
    gameEntity: GameDataModel,
    match: MatchDataRelationModel,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showGameIdentifier: Boolean = true
) {
    val gameInitial = if (gameEntity.name.isNotEmpty()) {
        gameEntity.name.first().uppercase()
    } else "?"
    val gameColor = LocalCustomThemeColors.current.getColorByKey(gameEntity.color)

    Surface(
        shape = MaterialTheme.shapes.large,
        onClick = { onSingleMatchTap(match.entity.id) },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (showGameIdentifier) {
                GameIdentifier(
                    initial = gameInitial,
                    color = gameColor
                )
            }

            if (match.players.isNotEmpty()) {
                val winningPlayer = match.players.getWinningPlayer(
                    scoringMode = ScoringMode.getModeByOrdinal(gameEntity.scoringMode)
                )

                VictorCard(
                    name = winningPlayer.entity.name,
                    score = winningPlayer.entity.totalScore.convertToShortFormatScore(),
                    color = gameColor,
                    modifier = Modifier.weight(1f, fill = false)
                )
            } else {
                EmptyPlayersCard()
            }

            PlayerCountCard(
                count = match.players.size,
                color = gameColor
            )
        }
    }
}

@Composable
fun GameIdentifier(
    initial: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .sizeIn(minHeight = 44.dp, minWidth = 16.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = initial,
            color = color,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyPlayersCard() {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colors.error),
        modifier = Modifier
            .sizeIn(minHeight = 44.dp, minWidth = 44.dp)
            .fillMaxHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = Spacing.sectionContent)
        ) {

            Image(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.error),
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = stringResource(id = R.string.text_empty_match_players),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Composable
fun VictorCard(
    name: String,
    score: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color),
        modifier = modifier
            .sizeIn(minHeight = 44.dp, minWidth = 44.dp)
            .fillMaxHeight()
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = Spacing.sectionContent, vertical = Spacing.sectionContent / 2)
        ) {

            Image(
                painterResource(id = R.mipmap.ic_crown),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = name,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f, fill = false)
            )

            Image(
                painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = score,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body1,
            )
        }
    }
}

@Composable
fun PlayerCountCard(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color),
        modifier = modifier
            .sizeIn(minHeight = 44.dp, minWidth = 44.dp)
            .fillMaxHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = Spacing.sectionContent, vertical = Spacing.sectionContent / 2)
        ) {

            Image(
                painterResource(id = if (count == 1) R.drawable.ic_person else R.drawable.ic_group),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = count.toString(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body1,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScoreCardPreview() {
    MedianMeepleTheme {
        MatchListItem(
            gameEntity = GameDataModel(name = "WWWW"),
            match = MatchDataRelationModel(
                players = PlayerEntitiesDefaultPreview.map { PlayerDataRelationModel(entity = it) }
            ),
            onSingleMatchTap = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmptyScoreCardPreview() {
    MedianMeepleTheme {
        MatchListItem(
            gameEntity = GameDataModel(name = "WWWW"),
            match = MatchDataRelationModel(),
            onSingleMatchTap = {}
        )
    }
}
