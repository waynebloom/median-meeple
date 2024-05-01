package com.waynebloom.scorekeeper.singleGame.statisticsForGame.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.components.ExpandCollapseButton
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.IconButton
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.ext.toStringForDisplay
import com.waynebloom.scorekeeper.singleGame.StatisticsForGameUiState
import com.waynebloom.scorekeeper.singleGame.matchesForGame.SingleGameTabBar
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.ScoringPlayerDomainModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.WinningPlayerDomainModel
import com.waynebloom.scorekeeper.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun StatisticsForGameScreen(
    uiState: StatisticsForGameUiState,
    onEditGameClick: () -> Unit,
    onMatchesTabClick: () -> Unit,
    onBestWinnerButtonClick: () -> Unit,
    onHighScoreButtonClick: () -> Unit,
    onUniqueWinnersButtonClick: () -> Unit,
    onCategoryClick: (Int) -> Unit
) {

    Scaffold(
        topBar = {
            StatisticsForGameTopBar(
                title = uiState.screenTitle,
                selectedTab = SingleGameScreen.StatisticsForGame,
                onEditGameClick = onEditGameClick,
                onTabClick = {
                    when(it) {
                        SingleGameScreen.StatisticsForGame -> {}
                        SingleGameScreen.MatchesForGame -> onMatchesTabClick()
                    }
                },
            )
        }
    ) { innerPadding ->

        when(uiState) {

            is StatisticsForGameUiState.Loading -> Loading()

            is StatisticsForGameUiState.Empty -> {
                HelperBox(
                    message = stringResource(R.string.helper_empty_data),
                    type = HelperBoxType.Missing,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = Spacing.screenEdge)
                        .padding(top = Spacing.sectionContent)
                )
            }

            is StatisticsForGameUiState.Content -> {
                StatisticsForGameScreen(
                    matchCount = uiState.matchCount.toString(),
                    playCount = uiState.playCount.toString(),
                    uniquePlayerCount = uiState.uniquePlayerCount.toString(),
                    isBestWinnerExpanded = uiState.isBestWinnerExpanded,
                    playersWithMostWins = uiState.playersWithMostWins,
                    playersWithMostWinsOverflow = uiState.playersWithMostWinsOverflow,
                    isHighScoreExpanded = uiState.isHighScoreExpanded,
                    playersWithHighScore = uiState.playersWithHighScore,
                    playersWithHighScoreOverflow = uiState.playersWithHighScoreOverflow,
                    isUniqueWinnersExpanded = uiState.isUniqueWinnersExpanded,
                    uniqueWinners = uiState.winners,
                    uniqueWinnersOverflow = uiState.winnersOverflow,
                    categoryNames = uiState.categoryNames,
                    indexOfSelectedCategory = uiState.indexOfSelectedCategory,
                    isCategoryDataEmpty = uiState.isCategoryDataEmpty,
                    categoryTopScorers = uiState.categoryTopScorers,
                    categoryLow = uiState.categoryLow,
                    categoryMean = uiState.categoryMean,
                    categoryRange = uiState.categoryRange,
                    onBestWinnerButtonClick = onBestWinnerButtonClick,
                    onHighScoreButtonClick = onHighScoreButtonClick,
                    onUniqueWinnerButtonClick = onUniqueWinnersButtonClick,
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
fun StatisticsForGameScreen(
    matchCount: String,
    playCount: String,
    uniquePlayerCount: String,
    isBestWinnerExpanded: Boolean,
    playersWithMostWins: List<WinningPlayerDomainModel>,
    playersWithMostWinsOverflow: Int,
    isHighScoreExpanded: Boolean,
    playersWithHighScore: List<ScoringPlayerDomainModel>,
    playersWithHighScoreOverflow: Int,
    isUniqueWinnersExpanded: Boolean,
    uniqueWinners: List<WinningPlayerDomainModel>,
    uniqueWinnersOverflow: Int,
    categoryNames: List<String>,
    indexOfSelectedCategory: Int,
    isCategoryDataEmpty: Boolean,
    categoryTopScorers: List<ScoringPlayerDomainModel>,
    categoryLow: String,
    categoryMean: String,
    categoryRange: String,
    onBestWinnerButtonClick: () -> Unit,
    onHighScoreButtonClick: () -> Unit,
    onUniqueWinnerButtonClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        contentPadding = PaddingValues(bottom = Spacing.betweenSections),
        verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
        modifier = modifier,
    ) {

        item {

            PlaysSection(
                matchCount = matchCount,
                playerCount = uniquePlayerCount,
                modifier = Modifier
                    .padding(horizontal = Spacing.screenEdge)
                    .padding(top = Spacing.betweenSections)
            )

            Divider()
        }

        item {

            WinsSection(
                isBestWinnerExpanded = isBestWinnerExpanded,
                playersWithMostWins = playersWithMostWins,
                playersWithMostWinsOverflow = playersWithMostWinsOverflow,
                isHighScoreExpanded = isHighScoreExpanded,
                playersWithHighScore = playersWithHighScore,
                playersWithHighScoreOverflow = playersWithHighScoreOverflow,
                isUniqueWinnersExpanded = isUniqueWinnersExpanded,
                uniqueWinners = uniqueWinners,
                uniqueWinnersOverflow = uniqueWinnersOverflow,
                onBestWinnerButtonClick = onBestWinnerButtonClick,
                onHighScoreButtonClick = onHighScoreButtonClick,
                onUniqueWinnerButtonClick = onUniqueWinnerButtonClick,
                modifier = Modifier.padding(horizontal = Spacing.screenEdge)
            )

            Divider()
        }

        item {

            ScoringSection(
                categories = categoryNames,
                indexOfSelectedCategory = indexOfSelectedCategory,
                isCategoryDataEmpty = isCategoryDataEmpty,
                topScorers = categoryTopScorers,
                low = categoryLow,
                mean = categoryMean,
                range = categoryRange,
                onCategoryClick = onCategoryClick
            )
        }
    }
}

@Composable
fun StatisticsForGameTopBar(
    title: String,
    selectedTab: SingleGameScreen,
    onEditGameClick: () -> Unit,
    onTabClick: (SingleGameScreen) -> Unit,
) {

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .defaultMinSize(minHeight = Size.topBarHeight)
                .fillMaxWidth()
        ) {

            Text(
                text = title,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            IconButton(
                imageVector = Icons.Rounded.Edit,
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                onClick = onEditGameClick
            )
        }

        SingleGameTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabClick
        )
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
    isBestWinnerExpanded: Boolean,
    playersWithMostWins: List<WinningPlayerDomainModel>,
    playersWithMostWinsOverflow: Int,
    isHighScoreExpanded: Boolean,
    playersWithHighScore: List<ScoringPlayerDomainModel>,
    playersWithHighScoreOverflow: Int,
    isUniqueWinnersExpanded: Boolean,
    uniqueWinners: List<WinningPlayerDomainModel>,
    uniqueWinnersOverflow: Int,
    onBestWinnerButtonClick: () -> Unit,
    onHighScoreButtonClick: () -> Unit,
    onUniqueWinnerButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val bestWinnerEndText = if (playersWithMostWins.size < 2) {
        playersWithMostWins.first().name
    } else {
        stringResource(R.string.text_tied)
    }
    val highScoreEndText = if (playersWithHighScore.size < 2) {
        playersWithHighScore.first().score.toStringForDisplay()
    } else {
        stringResource(R.string.text_tied)
    }
    val personIcon: @Composable (() -> Unit) = {
        Icon(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
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

        // Best Winner

        TwoLineExpandableListItem(
            startHeadline = stringResource(R.string.headline_best_winner),
            startSupportingText = stringResource(R.string.description_best_winner),
            buttonText = bestWinnerEndText,
            expanded = isBestWinnerExpanded,
            onItemClick = onBestWinnerButtonClick
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                playersWithMostWins.forEach {
                    SingleLineListItem(
                        startText = it.name,
                        startIcon = personIcon,
                        endText = pluralStringResource(
                            id = R.plurals.number_with_wins,
                            count = it.numberOfWins,
                            it.numberOfWins
                        ),
                        showEndBackground = false
                    )
                }

                if (playersWithMostWinsOverflow > 0) {
                    Text(
                        text = stringResource(
                            id = R.string.number_list_overflow,
                            playersWithMostWinsOverflow
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Divider()

        // High Score

        TwoLineExpandableListItem(
            startHeadline = stringResource(R.string.headline_high_score),
            startSupportingText = stringResource(R.string.description_high_score),
            buttonText = highScoreEndText,
            onItemClick = onHighScoreButtonClick,
            expanded = isHighScoreExpanded
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                playersWithHighScore.forEach {
                    SingleLineListItem(
                        startText = it.name,
                        startIcon = personIcon,
                        endText = it.score.toStringForDisplay(),
                        showEndBackground = false
                    )
                }

                if (playersWithHighScoreOverflow > 0) {
                    Text(
                        text = stringResource(
                            id = R.string.number_list_overflow,
                            playersWithHighScoreOverflow
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Divider()

        // Unique Winners

        TwoLineExpandableListItem(
            startHeadline = stringResource(R.string.headline_unique_winners),
            startSupportingText = stringResource(R.string.description_unique_winners),
            buttonText = uniqueWinners.size.toString(),
            onItemClick = onUniqueWinnerButtonClick,
            expanded = isUniqueWinnersExpanded
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                uniqueWinners.forEach {
                    SingleLineListItem(
                        startText = it.name,
                        startIcon = personIcon,
                        endText = pluralStringResource(
                            id = R.plurals.number_with_wins,
                            count = it.numberOfWins,
                            it.numberOfWins
                        ),
                        showEndBackground = false
                    )
                }

                if (uniqueWinnersOverflow > 0) {
                    Text(
                        text = stringResource(
                            id = R.string.number_list_overflow,
                            uniqueWinnersOverflow
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
    categories: List<String>,
    indexOfSelectedCategory: Int,
    isCategoryDataEmpty: Boolean,
    topScorers: List<ScoringPlayerDomainModel>,
    low: String,
    mean: String,
    range: String,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.header_scoring),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = Spacing.screenEdge)
        )

        if (categories.isNotEmpty()) {
            CategoryChips(
                categories = categories,
                currentCategoryIndex = indexOfSelectedCategory,
                onClick = onCategoryClick,
            )
        }

        if (isCategoryDataEmpty) {
            HelperBox(
                message = "There is no data recorded for this category",
                type = HelperBoxType.Missing,
                modifier = Modifier.padding(horizontal = Spacing.screenEdge)
            )
        } else {
            ScoringStatisticsColumn(
                high = topScorers[0].score.toStringForDisplay(),
                mean = mean,
                low = low,
                range = range,
                topScorers = topScorers,
                modifier = Modifier.padding(horizontal = Spacing.screenEdge)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryChips(
    categories: List<String>,
    currentCategoryIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    val chipColors = ChipDefaults.outlinedFilterChipColors(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        leadingIconColor = MaterialTheme.colors.onBackground,
        selectedBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.25f),
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
                    color = MaterialTheme.colors.onBackground.copy(Alpha.disabled)
                )
            } else null

            FilterChip(
                selected = isSelected,
                onClick = { onClick(index) },
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
    topScorers: List<ScoringPlayerDomainModel>,
    modifier: Modifier = Modifier,
) {
    var highSectionExpanded by rememberSaveable { mutableStateOf(false) }
    val highStartText = if (highSectionExpanded) {
        stringResource(R.string.headline_top_scores)
    } else {
        stringResource(R.string.headline_high)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier.padding(bottom = Spacing.sectionContent),
    ) {

        SingleLineExpandableListItem(
            startText = highStartText,
            buttonText = high,
            onItemClick = { highSectionExpanded = !highSectionExpanded },
            expanded = highSectionExpanded,
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                topScorers.forEach { scorer ->

                    SingleLineListItem(
                        startText = scorer.name,
                        startIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp)
                            )
                        },
                        endText = scorer.score.toStringForDisplay(),
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TwoLineExpandableListItem(
    modifier: Modifier = Modifier,
    startHeadline: String,
    startSupportingText: String? = null,
    buttonText: String? = null,
    onItemClick: () -> Unit,
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
                    onClick = onItemClick
                )
            }

            AnimatedContent(
                targetState = expanded,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
                label = "ExpandableListItemExpand"
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SingleLineExpandableListItem(
    modifier: Modifier = Modifier,
    startText: String,
    buttonText: String? = null,
    expanded: Boolean,
    onItemClick: () -> Unit,
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
                .padding(end = 16.dp)
            )

            ExpandCollapseButton(
                text = buttonText,
                expanded = expanded,
                onClick = onItemClick
            )
        }

        AnimatedContent(
            targetState = expanded,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
            label = "ExpandableListItemExpand"
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
                        .defaultMinSize(
                            minWidth = Size.minTappableSize,
                            minHeight = Size.minTappableSize
                        )
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StatisticsForGameTopBarPreview() {
    MedianMeepleTheme {
        Box(Modifier.background(MaterialTheme.colors.background)) {
            StatisticsForGameTopBar(
                title = "Wingspan",
                selectedTab = SingleGameScreen.StatisticsForGame,
                onEditGameClick = {},
                onTabClick = {},
            )
        }
    }
}

@Suppress("MagicNumber")
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
                expanded = expanded,
                onItemClick = { expanded = !expanded},
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

@Suppress("MagicNumber")
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
                expanded = expanded,
                onItemClick = { expanded = !expanded},
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
