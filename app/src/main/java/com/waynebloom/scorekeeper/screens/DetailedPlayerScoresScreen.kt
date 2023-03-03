package com.waynebloom.scorekeeper.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.*
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.DullColoredTextCard
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.waynebloom.scorekeeper.viewmodel.DetailedPlayerScoresViewModel
import com.waynebloom.scorekeeper.viewmodel.DetailedPlayerScoresViewModelFactory
import java.util.*

const val expandedPlayerNameScreenWeight = 0.4f
const val collapsedPlayerNameScreenWeight = 0.15f

@Composable
fun DetailedPlayerScoresScreen(
    players: List<PlayerObject>,
    subscoreTitles: List<SubscoreTitleEntity>,
    themeColor: Color = MaterialTheme.colors.primary,
    onExistingPlayerTap: (Long) -> Unit,
    onAddPlayerTap: () -> Unit
) {
    val viewModel = viewModel<DetailedPlayerScoresViewModel>(
        key = ScorekeeperScreen.DetailedPlayerScores.name,
        factory = DetailedPlayerScoresViewModelFactory(
            initialSubscoreTitles = subscoreTitles,
            players = players,
            resources = LocalContext.current.resources
        )
    )

    // creates a 3 by (subscoreTitles.size - 2 / 3) + 1 matrix where the first column contains only
    // the first 2 indices
    val firstPage = viewModel.subscoreTitleStrings.indices.take(2)
    val subscoreIndicesForPageMatrix = listOf(firstPage).plus(
        viewModel.subscoreTitleStrings.indices
            .drop(2)
            .chunked(3)
    )

    Column {
        val fromIndex = subscoreIndicesForPageMatrix[viewModel.activePage].first()
        val toIndex = subscoreIndicesForPageMatrix[viewModel.activePage].last()
        val subscoreIndicesToDisplay = (fromIndex..toIndex)
        val isFirstSubscoreDisplayed = subscoreIndicesToDisplay.contains(0)
        val playerIdentifierWeight = if (isFirstSubscoreDisplayed) {
            expandedPlayerNameScreenWeight
        } else collapsedPlayerNameScreenWeight
        val subscoresSectionWeight = if (isFirstSubscoreDisplayed) {
            1 - expandedPlayerNameScreenWeight
        } else 1 - collapsedPlayerNameScreenWeight

        EditScoresTopBar(
            subscoreTitles = viewModel.subscoreTitleStrings.slice(subscoreIndicesToDisplay),
            playerIdentifierScreenPortion = playerIdentifierWeight,
            scoresScreenPortion = subscoresSectionWeight,
            themeColor = themeColor
        )

        if (players.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val detailedPlayers = players.filter { it.entity.showDetailedScore }
                val simplePlayers = players.filter { !it.entity.showDetailedScore }

                items(detailedPlayers) { player ->
                    DetailedPlayerCard(
                        playerName = player.entity.name,
                        subscores = viewModel
                            .getSubscoresInOrder(player)
                            .slice(subscoreIndicesToDisplay),
                        isFirstSubscoreDisplayed = isFirstSubscoreDisplayed,
                        playerIdentifierScreenPortion = playerIdentifierWeight,
                        scoresScreenPortion = subscoresSectionWeight,
                        onPlayerTap = { onExistingPlayerTap(player.entity.id) },
                        getScoreString = { viewModel.getScoreToDisplay(it) }
                    )
                }

                if (simplePlayers.isNotEmpty() && detailedPlayers.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.header_simple_scores)
                                .uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.paddingFromBaseline(top = 32.dp, bottom = 8.dp)
                        )
                    }
                }

                items(simplePlayers) { player ->
                    SimplePlayerCard(
                        playerEntity = player.entity,
                        playerTotalScoreString = viewModel.getScoreToDisplay(player.entity.score),
                        themeColor = themeColor,
                        onPlayerTap = { onExistingPlayerTap(player.entity.id) }
                    )
                }
            }
        } else {
            DullColoredTextCard(
                text = stringResource(id = R.string.text_empty_players),
                color = themeColor,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    EditScoresPageActions(
        activePageNumber = viewModel.activePage + 1,
        totalPages = subscoreIndicesForPageMatrix.size,
        themeColor = themeColor,
        onAddPlayerTap = onAddPlayerTap,
        onPageUpTap = {
            if (subscoreIndicesForPageMatrix.size - 1 > viewModel.activePage) {
                viewModel.activePage += 1
            }
        },
        onPageDownTap = { if (viewModel.activePage >= 1) viewModel.activePage -= 1 },
    )
}

@Composable
fun DetailedPlayerCard(
    playerName: String,
    subscores: List<SubscoreEntity>,
    isFirstSubscoreDisplayed: Boolean,
    playerIdentifierScreenPortion: Float,
    scoresScreenPortion: Float,
    onPlayerTap: () -> Unit,
    getScoreString: (String) -> String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable { onPlayerTap() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Text(
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = if (isFirstSubscoreDisplayed) {
                    TextOverflow.Ellipsis
                } else TextOverflow.Clip,
                text = playerName,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(playerIdentifierScreenPortion)
                    .padding(horizontal = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(scoresScreenPortion)
            ) {
                subscores.forEach { subscore ->
                    Text(
                        text = getScoreString(subscore.value),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SimplePlayerCard(
    playerEntity: PlayerEntity,
    playerTotalScoreString: String,
    themeColor: Color,
    onPlayerTap: (Long) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayerTap(playerEntity.id) },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = playerEntity.name,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Image(
                    painterResource(id = R.drawable.ic_star),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(themeColor),
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(24.dp)
                )

                Text(
                    text = playerTotalScoreString,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EditScoresTopBar(
    subscoreTitles: List<String>,
    playerIdentifierScreenPortion: Float,
    scoresScreenPortion: Float,
    themeColor: Color,
) {
    Surface(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.small.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(vertical = 8.dp, horizontal = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(24.dp)
                    .weight(playerIdentifierScreenPortion)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(themeColor),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(scoresScreenPortion)
            ) {
                subscoreTitles.forEach {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subtitle1,
                        text = it.uppercase(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditScoresPageActions(
    activePageNumber: Int,
    totalPages: Int,
    themeColor: Color,
    onAddPlayerTap: () -> Unit,
    onPageUpTap: () -> Unit,
    onPageDownTap: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.small)
                    .background(
                        color = themeColor,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Button(
                    onClick = { onPageDownTap() },
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = themeColor,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }

                Text(
                    text = "$activePageNumber/$totalPages",
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.body1,
                )

                Button(
                    onClick = { onPageUpTap() },
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = themeColor,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }

            Button(
                onClick = { onAddPlayerTap() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = themeColor,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 2.dp
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(backgroundColor = 0xFFF0EAE2, showBackground = true)
@Composable
fun EditScoresScreenPreview() {
    ScoreKeeperTheme {
        DetailedPlayerScoresScreen(
            players = PreviewPlayerObjects.plus(
                PreviewPlayerObjects[0].apply {
                    entity.showDetailedScore = true
                    score = PreviewSubscoreEntities
                }
            ),
            subscoreTitles = PreviewSubscoreTitleEntities,
            onExistingPlayerTap = {},
            onAddPlayerTap = {}
        )
    }
}