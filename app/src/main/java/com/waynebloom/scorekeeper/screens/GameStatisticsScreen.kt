package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.data.GameObjectStatisticsPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.ExpandCollapseButton
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.data.model.ScoringStatisticsForCategory
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.ui.theme.color.deepOrange100
import com.waynebloom.scorekeeper.ui.theme.color.deepOrange500
import com.waynebloom.scorekeeper.ui.theme.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.sizeTransformWithDelay
import com.waynebloom.scorekeeper.viewmodel.SingleGameViewModel
import com.waynebloom.scorekeeper.viewmodel.SingleGameViewModel.Companion.numberOfItemsToShowExpanded
import com.waynebloom.scorekeeper.viewmodel.SingleGameViewModelFactory

@Composable
fun GameStatisticsScreen(
    gameObject: GameObject,
    themeColor: Color,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SingleGameViewModel>(
        key = SingleGameScreen.GameStatistics.name,
        factory = SingleGameViewModelFactory(
            gameObject = gameObject,
            resources = Resources.getSystem()
        )
    ).onRecompose(gameObject = gameObject)

    LazyColumn(
        contentPadding = PaddingValues(bottom = Spacing.betweenSections),
        verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
        modifier = modifier,
    ) {

        if (viewModel.matches.isNotEmpty()) {
            item {

                PlaysSection(
                    matchCount = viewModel.matches.size.toString(),
                    playerCount = viewModel.playerCount,
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenEdge)
                        .padding(top = Spacing.betweenSections)
                )

                Divider()
            }

            item {

                WinsSection(
                    playersWithHighScore = viewModel.getTotalScoreStatistics().getHighScorers(),
                    winningPlayers = viewModel.winners,
                    tieDegreeMostWins = viewModel.getMostWinsTieDegree(),
                    themeColor = themeColor,
                    bestWinnerIsExpanded = viewModel.bestWinnerIsExpanded,
                    highScoreIsExpanded = viewModel.highScoreIsExpanded,
                    uniqueWinnersIsExpanded = viewModel.uniqueWinnersIsExpanded,
                    onBestWinnerTap = {
                        viewModel.bestWinnerIsExpanded = !viewModel.bestWinnerIsExpanded
                    },
                    onHighScoreTap = { viewModel.highScoreIsExpanded = !viewModel.highScoreIsExpanded },
                    onUniqueWinnersTap = {
                        viewModel.uniqueWinnersIsExpanded = !viewModel.uniqueWinnersIsExpanded
                    },
                    modifier = Modifier.padding(horizontal = Spacing.screenEdge))

                Divider()
            }

            item {

                ScoringSection(
                    statisticsObjects = viewModel.statisticsObjects,
                    currentCategoryIndex = viewModel.currentCategoryIndex,
                    themeColor = themeColor,
                    onCategoryTap = { index -> viewModel.currentCategoryIndex = index }
                )
            }
        } else {
            item {

                HelperBox(
                    message = stringResource(R.string.helper_empty_data),
                    type = HelperBoxType.Missing,
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenEdge)
                        .padding(top = Spacing.sectionContent)
                )
            }
        }
    }
}

@Composable
private fun PlaysSection(
    matchCount: String,
    playerCount: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier.padding(bottom = Spacing.sectionContent),
    ) {

        Text(
            text = stringResource(id = R.string.header_plays),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        TwoLineListItem(
            startHeadline = stringResource(R.string.headline_matches),
            startSupportingText = stringResource(R.string.description_matches),
            endText = matchCount,
        )

        Divider()

        TwoLineListItem(
            startHeadline = stringResource(R.string.headline_players),
            startSupportingText = stringResource(R.string.description_players),
            endText = playerCount,
        )
    }
}

@Composable
private fun WinsSection(
    playersWithHighScore: List<Pair<String, String>>,
    winningPlayers: Map<String, Int>,
    tieDegreeMostWins: Int,
    themeColor: Color,
    bestWinnerIsExpanded: Boolean,
    highScoreIsExpanded: Boolean,
    uniqueWinnersIsExpanded: Boolean,
    onBestWinnerTap: () -> Unit,
    onHighScoreTap: () -> Unit,
    onUniqueWinnersTap: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val bestWinnerEndText = if (tieDegreeMostWins < 2) {
        winningPlayers.keys.first()
    } else stringResource(R.string.text_tied)
    val highScoreEndText = if (playersWithHighScore.size < 2) {
        playersWithHighScore.first().second
    } else stringResource(R.string.text_tied)
    val personIcon: @Composable (() -> Unit) = {
        Icon(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = null,
            tint = themeColor,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(16.dp)
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier.padding(bottom = Spacing.sectionContent),
    ) {

        Text(
            text = stringResource(id = R.string.header_wins),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        // Best winner
        TwoLineExpandableListItem(
            startHeadline = stringResource(R.string.headline_best_winner),
            startSupportingText = stringResource(R.string.description_best_winner),
            buttonText = bestWinnerEndText,
            expanded = bestWinnerIsExpanded,
            onItemTap = onBestWinnerTap,
            themeColor = themeColor,
        ) {
            val playersWithMostWins = winningPlayers.toList().take(tieDegreeMostWins)

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                playersWithMostWins.take(numberOfItemsToShowExpanded).forEach { (name, wins) ->
                    SingleLineListItem(
                        startText = name,
                        startIcon = personIcon,
                        endText = pluralStringResource(
                            id = R.plurals.number_with_wins,
                            count = wins,
                            wins
                        ),
                        showEndBackground = false,
                    )
                }

                if (playersWithMostWins.size > numberOfItemsToShowExpanded) {
                    Text(
                        text = stringResource(
                            id = R.string.number_list_overflow,
                            playersWithMostWins.size - numberOfItemsToShowExpanded
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }

        Divider()

        // High score
        TwoLineExpandableListItem(
            startHeadline = stringResource(R.string.headline_high_score),
            startSupportingText = stringResource(R.string.description_high_score),
            buttonText = highScoreEndText,
            themeColor = themeColor,
            onItemTap = onHighScoreTap,
            expanded = highScoreIsExpanded
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                playersWithHighScore.take(numberOfItemsToShowExpanded).forEach {
                    SingleLineListItem(
                        startText = it.first,
                        startIcon = personIcon,
                        endText = it.second,
                        showEndBackground = false,
                    )
                }

                if (playersWithHighScore.size > numberOfItemsToShowExpanded) {
                    Text(
                        text = stringResource(
                            id = R.string.number_list_overflow,
                            playersWithHighScore.size - numberOfItemsToShowExpanded
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }

        Divider()

        // Unique winners
        TwoLineExpandableListItem(
            startHeadline = stringResource(R.string.headline_unique_winners),
            startSupportingText = stringResource(R.string.description_unique_winners),
            buttonText = winningPlayers.size.toString(),
            themeColor = themeColor,
            onItemTap = onUniqueWinnersTap,
            expanded = uniqueWinnersIsExpanded
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                winningPlayers.toList().take(numberOfItemsToShowExpanded).forEach {
                    SingleLineListItem(
                        startText = it.first,
                        startIcon = personIcon,
                        endText = pluralStringResource(
                            id = R.plurals.number_with_wins,
                            count = it.second,
                            it.second
                        ),
                        showEndBackground = false
                    )
                }

                if (winningPlayers.size > numberOfItemsToShowExpanded) {
                    Text(
                        text = stringResource(
                            id = R.string.number_list_overflow,
                            winningPlayers.size - numberOfItemsToShowExpanded
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

// region Scoring Section

@Composable
private fun ScoringSection(
    statisticsObjects: List<ScoringStatisticsForCategory>,
    currentCategoryIndex: Int,
    themeColor: Color,
    onCategoryTap: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier.fillMaxWidth()
    ) {

        val currentSummary = statisticsObjects[currentCategoryIndex]

        Text(
            text = stringResource(id = R.string.header_scoring),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = Spacing.screenEdge)
        )

        CategoryChipsMenu(
            categories = statisticsObjects.map { it.categoryTitle },
            currentCategoryIndex = currentCategoryIndex,
            onItemTap = onCategoryTap,
            themeColor = themeColor,
        )

        ScoringStatisticsColumn(
            high = currentSummary.getHigh(),
            mean = currentSummary.getMean(),
            low = currentSummary.getLow(),
            range = currentSummary.getRange(),
            topScorers = currentSummary.getTopSelection(),
            themeColor = themeColor,
            modifier = Modifier.padding(horizontal = Spacing.screenEdge)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryChipsMenu(
    categories: List<String>,
    currentCategoryIndex: Int,
    onItemTap: (Int) -> Unit,
    themeColor: Color,
    modifier: Modifier = Modifier,
) {

    val chipColors = ChipDefaults.outlinedFilterChipColors(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        leadingIconColor = MaterialTheme.colors.onBackground,
        selectedBackgroundColor = themeColor.copy(alpha = 0.25f),
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {

        itemsIndexed(categories) { index, category ->

            val isSelected = index == currentCategoryIndex
            val chipBorderStroke = if (!isSelected) {
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colors.onBackground.copy(0.5f)
                )
            } else null

            FilterChip(
                selected = isSelected,
                onClick = { onItemTap(index) },
                shape = MaterialTheme.shapes.small,
                border = chipBorderStroke,
                colors = chipColors,
                selectedIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_checkmark),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp)
                    )
                },
                content = { Text(text = category) }
            )
        }
    }
}

@Composable
private fun ScoringStatisticsColumn(
    high: String,
    low: String,
    mean: String,
    range: String,
    topScorers: List<Pair<String, String>>,
    themeColor: Color,
    modifier: Modifier = Modifier,
) {
    var highSectionExpanded by rememberSaveable { mutableStateOf(false) }
    val highStartText = if (highSectionExpanded)
        stringResource(R.string.headline_top_scores)
    else stringResource(R.string.headline_high)

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier.padding(bottom = Spacing.sectionContent),
    ) {

        SingleLineExpandableListItem(
            startText = highStartText,
            buttonText = high,
            themeColor = themeColor,
            onItemTap = { highSectionExpanded = !highSectionExpanded },
            expanded = highSectionExpanded,
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                topScorers.forEach { (name, score) ->

                    SingleLineListItem(
                        startText = name,
                        startIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = null,
                                tint = themeColor,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp)
                            )
                        },
                        endText = score,
                        showEndBackground = false,
                    )
                }
            }
        }

        Divider()

        SingleLineListItem(
            startText = stringResource(R.string.headline_low),
            endText = low,
        )

        Divider()

        SingleLineListItem(
            startText = stringResource(R.string.headline_mean),
            endText = mean,
        )

        Divider()

        SingleLineListItem(
            startText = stringResource(R.string.headline_range),
            endText = range,
        )
    }
}

// endregion

@Composable
fun TwoLineExpandableListItem(
    modifier: Modifier = Modifier,
    startHeadline: String,
    startSupportingText: String? = null,
    buttonText: String? = null,
    themeColor: Color,
    onItemTap: () -> Unit,
    expanded: Boolean,
    expandedContent: @Composable () -> Unit,
) {

        Column {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                ) {

                    Text(text = startHeadline)

                    if (startSupportingText != null) {
                        Text(
                            text = startSupportingText,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }

                ExpandCollapseButton(
                    text = buttonText,
                    expanded = expanded,
                    themeColor = themeColor,
                    onTap = onItemTap
                )
            }

            AnimatedContent(
                targetState = expanded,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
            ) {
                if (it) {

                    Column {
                        Spacer(modifier = Modifier.height(Spacing.sectionContent))
                        expandedContent()
                    }
                }
            }
        }
}

@Composable
fun TwoLineListItem(
    startHeadline: String? = null,
    startSupportingText: String? = null,
    endText: String? = null,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.weight(1f)) {

            if (startHeadline != null) {
                Text(text = startHeadline)
            }

            if (startSupportingText != null) {
                Text(
                    text = startSupportingText,
                    style = MaterialTheme.typography.body2,
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .defaultMinSize(minWidth = Size.minTappableSize, minHeight = Size.minTappableSize)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {

            if (endText != null)
                Text(text = endText)
        }
    }
}

@Composable
fun SingleLineExpandableListItem(
    modifier: Modifier = Modifier,
    startText: String,
    buttonText: String? = null,
    themeColor: Color,
    expanded: Boolean,
    onItemTap: () -> Unit,
    expandedContent: @Composable () -> Unit,
) {

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {

            Text(text = startText, modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp))

            ExpandCollapseButton(
                text = buttonText,
                expanded = expanded,
                themeColor = themeColor,
                onTap = onItemTap)
        }

        AnimatedContent(
            targetState = expanded,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
        ) {
            if (it) {

                Column {
                    Spacer(modifier = Modifier.height(Spacing.sectionContent))
                    expandedContent()
                }
            }
        }
    }
}

@Composable
fun SingleLineListItem(
    startText: String? = null,
    startIcon: @Composable (() -> Unit)? = null,
    endText: String? = null,
    showEndBackground: Boolean = true,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        if (startIcon != null)
            startIcon()

        if (startText != null)
            Text(text = startText, modifier = Modifier.weight(1f))

        if (endText != null) {
            if (showEndBackground) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .defaultMinSize(minWidth = Size.minTappableSize, minHeight = Size.minTappableSize)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.surface)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    content = { Text(text = endText) }
                )
            } else {
                Text(text = endText)
            }
        }
    }
}

@Preview(
    name = "SingleLineExpandableListItem, interactive",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SingleLineExpandableListItemPreview() {
    MedianMeepleTheme {
        Surface(color = MaterialTheme.colors.background) {
            var expanded by remember { mutableStateOf(false) }

            SingleLineExpandableListItem(
                startText = "Item title",
                buttonText = "Value",
                themeColor = deepOrange100,
                expanded = expanded,
                onItemTap = { expanded = !expanded},
                expandedContent = {
                    mapOf(
                        Pair("Wayne", 6),
                        Pair("Alyssa", 6)
                    ).forEach {
                        SingleLineListItem(
                            startText = it.key,
                            endText = pluralStringResource(
                                id = R.plurals.number_with_wins,
                                it.value
                            ),
                        )
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(
    name = "TwoLineExpandableListItem, interactive",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun TwoLineExpandableListItemPreview() {
    MedianMeepleTheme {
        Surface(color = MaterialTheme.colors.background) {
            var expanded by remember { mutableStateOf(false) }

            TwoLineExpandableListItem(
                startHeadline = "Item title",
                startSupportingText = "Supporting text",
                buttonText = "Value",
                themeColor = deepOrange100,
                expanded = expanded,
                onItemTap = { expanded = !expanded},
                expandedContent = {
                    mapOf(
                        Pair("Wayne", 6),
                        Pair("Alyssa", 6)
                    ).forEach {
                        SingleLineListItem(
                            startText = it.key,
                            endText = pluralStringResource(
                                id = R.plurals.number_with_wins,
                                it.value
                            ),
                        )
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameStatisticsScreenPreview() {
    MedianMeepleTheme {
        Scaffold {
            GameStatisticsScreen(
                gameObject = GameObjectStatisticsPreview,
                themeColor = deepOrange100,
                modifier = Modifier
                    .padding(it)
                    .padding(vertical = 16.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun GameStatisticsScreenPreviewLight() {
    MedianMeepleTheme {
        Scaffold {
            GameStatisticsScreen(
                gameObject = GameObjectStatisticsPreview,
                themeColor = deepOrange500,
                modifier = Modifier
                    .padding(it)
                    .padding(vertical = 16.dp)
            )
        }
    }
}