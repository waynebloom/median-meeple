package com.waynebloom.scorekeeper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.data.PlayerObjectsDefaultPreview
import com.waynebloom.scorekeeper.data.SubscoreEntitiesDefaultPreview
import com.waynebloom.scorekeeper.data.SubscoreTitleEntitiesDefaultPreview
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.CategoryScoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.viewmodel.DetailedPlayerScoresViewModel
import com.waynebloom.scorekeeper.viewmodel.DetailedPlayerScoresViewModelFactory

private const val ExpandedPlayerNameScreenWeight = 0.4f
private const val CollapsedPlayerNameScreenWeight = 0.15f
private const val NumberOfItemsOnFirstPage = 2
private const val NumberOfItemsPerPage = 3

@Composable
fun DetailedPlayerScoresScreen(
    players: List<PlayerObject>,
    subscoreTitles: List<CategoryTitleEntity>,
    themeColor: Color = MaterialTheme.colors.primary,
    onExistingPlayerTap: (Long) -> Unit,
) {
    val viewModel = viewModel<DetailedPlayerScoresViewModel>(
        key = TopLevelScreen.DetailedPlayerScores.name,
        factory = DetailedPlayerScoresViewModelFactory(
            initialSubscoreTitles = subscoreTitles,
            players = players,
            resources = LocalContext.current.resources
        )
    )

    // creates a 3 by (subscoreTitles.size - 2 / 3) + 1 matrix where the first column contains only
    // the first 2 indices
    val firstPage = viewModel.subscoreTitleStrings.indices.take(NumberOfItemsOnFirstPage)
    val subscoreIndicesForPageMatrix = listOf(firstPage).plus(
        viewModel.subscoreTitleStrings.indices
            .drop(NumberOfItemsOnFirstPage)
            .chunked(NumberOfItemsPerPage)
    )

    Column {
        val fromIndex = subscoreIndicesForPageMatrix[viewModel.activePage].first()
        val toIndex = subscoreIndicesForPageMatrix[viewModel.activePage].last()
        val subscoreIndicesToDisplay = (fromIndex..toIndex)
        val isFirstSubscoreDisplayed = subscoreIndicesToDisplay.contains(0)
        val playerIdentifierWeight = if (isFirstSubscoreDisplayed) {
            ExpandedPlayerNameScreenWeight
        } else CollapsedPlayerNameScreenWeight
        val subscoresSectionWeight = if (isFirstSubscoreDisplayed) {
            1 - ExpandedPlayerNameScreenWeight
        } else 1 - CollapsedPlayerNameScreenWeight

        EditScoresTopBar(
            subscoreTitles = viewModel.subscoreTitleStrings.slice(subscoreIndicesToDisplay),
            playerIdentifierScreenPortion = playerIdentifierWeight,
            scoresScreenPortion = subscoresSectionWeight,
            themeColor = themeColor
        )

        LazyColumn(
            contentPadding = PaddingValues(
                start = Spacing.screenEdge,
                top = Spacing.screenEdge,
                end = Spacing.screenEdge,
                bottom = Spacing.paddingForFab
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
            modifier = Modifier.fillMaxWidth()
        ) {
            val detailedPlayers = players.filter { it.entity.showDetailedScore }
            val simplePlayers = players.filter { !it.entity.showDetailedScore }

            if (players.isEmpty()) {

                item {

                    HelperBox(
                        message = stringResource(R.string.info_empty_players),
                        type = HelperBoxType.Missing
                    )
                }
            }

            item {

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                    if (detailedPlayers.isNotEmpty()) {

                        detailedPlayers.forEach { player ->

                            DetailedPlayerCard(
                                playerName = player.entity.name,
                                categoryData = viewModel
                                    .getSubscoresInOrder(player)
                                    .slice(subscoreIndicesToDisplay),
                                isFirstSubscoreDisplayed = isFirstSubscoreDisplayed,
                                playerIdentifierScreenPortion = playerIdentifierWeight,
                                scoresScreenPortion = subscoresSectionWeight,
                                onPlayerTap = { onExistingPlayerTap(player.entity.id) },
                                getScoreString = { viewModel.getScoreToDisplay(it) }
                            )
                        }
                    } else {
                        HelperBox(
                            message = stringResource(R.string.text_no_detailed_scores),
                            type = HelperBoxType.Missing
                        )
                    }
                }
            }

            item {

                if (simplePlayers.isNotEmpty()) {

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                        Text(
                            text = stringResource(id = R.string.header_simple_scores),
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.SemiBold,
                        )

                        simplePlayers.forEach { player ->

                            SimplePlayerCard(
                                playerEntity = player.entity,
                                playerTotalScoreString = viewModel.getScoreToDisplay(player.entity.score),
                                themeColor = themeColor,
                                onPlayerTap = { onExistingPlayerTap(player.entity.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    EditScoresPageActions(
        activePageNumber = viewModel.activePage + 1,
        totalPages = subscoreIndicesForPageMatrix.size,
        themeColor = themeColor,
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
    categoryData: List<CategoryScoreEntity>,
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
            .heightIn(min = Dimensions.Size.minTappableSize)
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

                categoryData.forEach { subscore ->

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
                .height(Dimensions.Size.topBarHeight)
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
    onPageUpTap: () -> Unit,
    onPageDownTap: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(Dimensions.Size.minTappableSize)
                .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.small)
                .background(
                    color = themeColor,
                    shape = MaterialTheme.shapes.small
                )
                .fillMaxWidth(Alpha.disabled)
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
    }
}

@Preview(backgroundColor = 0xFFF0EAE2, showBackground = true)
@Composable
fun EditScoresScreenPreview() {
    MedianMeepleTheme {
        DetailedPlayerScoresScreen(
            players = PlayerObjectsDefaultPreview.plus(
                PlayerObjectsDefaultPreview[0].apply {
                    entity.showDetailedScore = true
                    score = SubscoreEntitiesDefaultPreview
                }
            ),
            subscoreTitles = SubscoreTitleEntitiesDefaultPreview,
            onExistingPlayerTap = {},
        )
    }
}
