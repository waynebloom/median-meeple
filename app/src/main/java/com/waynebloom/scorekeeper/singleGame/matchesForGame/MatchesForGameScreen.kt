package com.waynebloom.scorekeeper.singleGame.matchesForGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.components.AdCard
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.MatchListItem
import com.waynebloom.scorekeeper.components.MedianMeepleFab
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.toAdSeparatedSubLists
import com.waynebloom.scorekeeper.components.IconButton
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.RadioButtonOption
import com.waynebloom.scorekeeper.components.SingleGameDestinationTopBar
import com.waynebloom.scorekeeper.components.SmallIconButton
import com.waynebloom.scorekeeper.components.TopBarWithSearch
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.singleGame.MatchesForGameUiState
import com.waynebloom.scorekeeper.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme
import com.waynebloom.scorekeeper.theme.color.deepOrange100
import com.waynebloom.scorekeeper.theme.color.deepOrange500

@Composable
fun MatchesForGameScreen(
    uiState: MatchesForGameUiState,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onEditGameClick: () -> Unit,
    onStatisticsTabClick: () -> Unit,
    onSortButtonClick: () -> Unit,
    onMatchClick: (Long) -> Unit,
    onAddMatchClick: () -> Unit,
    onSortDialogDismiss: () -> Unit,
) {

    when (uiState) {

        is MatchesForGameUiState.Content -> {
            MatchesForGameScreen(
                screenTitle = uiState.screenTitle,
                searchInput = uiState.searchInput,
                isSortDialogShowing = uiState.isSortDialogShowing,
                sortDirection = uiState.sortDirection,
                sortMode = uiState.sortMode,
                ad = uiState.ad,
                matches = uiState.matches,
                listState = rememberLazyListState(),
                scoringMode = uiState.scoringMode,
                onEditGameClick = onEditGameClick,
                onSortButtonClick = onSortButtonClick,
                onStatisticsTabClick = onStatisticsTabClick,
                onMatchClick = onMatchClick,
                onAddMatchClick = onAddMatchClick,
                onSearchInputChanged = onSearchInputChanged,
                onSortModeChanged = onSortModeChanged,
                onSortDirectionChanged = onSortDirectionChanged,
                onSortDialogDismiss = onSortDialogDismiss,
            )
        }
        is MatchesForGameUiState.Loading -> Loading()
    }
}

// region Top Bar

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun MatchesForSingleGameTopBar(
    searchInput: TextFieldValue,
    selectedTab: SingleGameScreen,
    title: String,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onSortClick: () -> Unit,
    onEditGameClick: () -> Unit,
    onTabClick: (SingleGameScreen) -> Unit,
) {

    var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .defaultMinSize(minHeight = Dimensions.Size.topBarHeight)
                .fillMaxWidth()
        ) {

            AnimatedContent(
                targetState = isSearchBarVisible,
                transitionSpec = { fadeInWithFadeOut },
                label = "MatchesForGameTopBarTransition"
            ) { visible ->

                if (visible) {
                    TopBarWithSearch(
                        searchInput = searchInput,
                        onSearchInputChanged = onSearchInputChanged,
                        onCloseClick = {
                            isSearchBarVisible = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        onClearClick = {
                            onSearchInputChanged(TextFieldValue())
                        },
                    )
                } else {
                    MatchesForSingleGameDefaultActionBar(
                        title = title,
                        onSearchClick = { isSearchBarVisible = true },
                        onSortClick = onSortClick,
                        onEditGameClick = onEditGameClick,
                    )
                }
            }
        }

        SingleGameTabBar(selectedTab, onTabClick)
    }
}

@Composable
fun MatchesForSingleGameDefaultActionBar(
    title: String,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onEditGameClick: () -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.Size.topBarHeight)
    ) {

        Text(
            text = title,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Row {

            IconButton(
                imageVector = Icons.Rounded.Search,
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                onClick = onSearchClick
            )

            IconButton(
                painter = painterResource(id = R.drawable.ic_sort),
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                onClick = onSortClick
            )

            IconButton(
                imageVector = Icons.Rounded.Edit,
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                onClick = onEditGameClick
            )
        }
    }
}

@Composable
fun SingleGameTabBar(
    selectedTab: SingleGameScreen,
    onTabSelected: (SingleGameScreen) -> Unit
) {

    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        backgroundColor = MaterialTheme.colors.background,
    ) {
        SingleGameScreen.entries.forEachIndexed { index, screen ->
            Tab(
                selected = index == selectedTab.ordinal,
                onClick = { onTabSelected(screen) },
                text = { Text(text = stringResource(id = screen.titleResource)) },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconResource),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selectedContentColor = MaterialTheme.colors.primary,
            )
        }
    }
}

// endregion

@Composable
fun MatchesForGameSortOptionsDialog(
    sortMode: MatchSortMode,
    sortDirection: SortDirection,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismiss) {
        Column(
            Modifier
                .clip(shape = MaterialTheme.shapes.large)
                .background(MaterialTheme.colors.background)
                .padding(Spacing.screenEdge)
        ) {
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.subSectionContent))
            MatchSortMode.entries.forEach { option ->
                RadioButtonOption(
                    menuOption = option,
                    isSelected = sortMode == option,
                    onSelected = {
                        onSortModeChanged(option)
                    },
                    unselectedColor = MaterialTheme.colors.onBackground,
                )
            }
            Spacer(Modifier.height(Spacing.betweenSections))
            Text(
                text = "Sort direction",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.subSectionContent))
            SortDirection.entries.forEach { option ->
                RadioButtonOption(
                    menuOption = option,
                    isSelected = sortDirection == option,
                    onSelected = {
                        onSortDirectionChanged(option)
                    },
                    unselectedColor = MaterialTheme.colors.onBackground,
                )
            }
        }
    }
}

@Composable
private fun MatchesForGameHelperBoxListHeader(message: String, type: HelperBoxType) {
    Column {

        HelperBox(
            message = message,
            type = type,
            modifier = Modifier.padding(vertical = Spacing.sectionContent),
            maxLines = 2,
        )
        Divider()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchesForGameScreen(
    screenTitle: String,
    searchInput: TextFieldValue,
    isSortDialogShowing: Boolean,
    sortDirection: SortDirection,
    sortMode: MatchSortMode,
    ad: NativeAd?,
    matches: List<MatchDomainModel>,
    listState: LazyListState,
    scoringMode: ScoringMode,
    onEditGameClick: () -> Unit,
    onSortButtonClick: () -> Unit,
    onStatisticsTabClick: () -> Unit,
    onMatchClick: (Long) -> Unit,
    onAddMatchClick: () -> Unit,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onSortDialogDismiss: () -> Unit,
) {

    Box {

        if (isSortDialogShowing) {
            BackHandler {
                onSortDialogDismiss()
            }

            MatchesForGameSortOptionsDialog(
                sortMode,
                sortDirection,
                onSortModeChanged,
                onSortDirectionChanged,
                onSortDialogDismiss
            )
        }

        Scaffold(
            topBar = {
                MatchesForSingleGameTopBar(
                    searchInput = searchInput,
                    selectedTab = SingleGameScreen.MatchesForGame,
                    title = screenTitle,
                    onSearchInputChanged = onSearchInputChanged,
                    onTabClick = {
                        when (it) {
                            SingleGameScreen.MatchesForGame -> {}
                            SingleGameScreen.StatisticsForGame -> onStatisticsTabClick()
                        }
                    },
                    onSortClick = onSortButtonClick,
                    onEditGameClick = onEditGameClick,
                )
            },
            floatingActionButton = {
                MedianMeepleFab(
                    // backgroundColor = MaterialTheme.colors.primary,
                    onClick = onAddMatchClick,
                )
            }
        ) { innerPadding ->

            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Spacing.screenEdge)
            ) {

                AnimatedContent(
                    targetState = matches.isNotEmpty() to searchInput.text.isNotBlank(),
                    transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
                    label = MatchesForGameConstants.AnimationLabel.HelperBox
                ) {

                    when(it) {

                        // There are matches and there is search input
                        true to true -> {
                            MatchesForGameHelperBoxListHeader(
                                message = stringResource(
                                    id = R.string.text_showing_search_results,
                                    searchInput.text),
                                type = HelperBoxType.Info
                            )
                        }

                        // There are matches and there is no search input
                        true to false -> {}

                        // There are no matches and there is search input
                        false to true -> {
                            MatchesForGameHelperBoxListHeader(
                                message = stringResource(
                                    id = R.string.text_empty_match_search_results,
                                    searchInput.text),
                                type = HelperBoxType.Missing
                            )
                        }

                        // There are no matches and no search input
                        false to false -> {
                            MatchesForGameHelperBoxListHeader(
                                message = stringResource(R.string.text_empty_matches),
                                type = HelperBoxType.Missing
                            )
                        }
                    }
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        top = Spacing.sectionContent, bottom = Spacing.paddingForFab),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                ) {

                    val adSeparatedSubLists = matches.toAdSeparatedSubLists()

                    adSeparatedSubLists.forEachIndexed { index, subList ->

                        items(
                            items = subList,
                            key = { item -> item.id }
                        ) { match ->

                            MatchListItem(
                                match,
                                scoringMode,
                                onClick = onMatchClick,
                                modifier = Modifier.animateItemPlacement(
                                    animationSpec = tween(
                                        durationMillis = DurationMs.medium,
                                        easing = Ease
                                    )
                                )
                            )
                        }

                        if (index == adSeparatedSubLists.lastIndex) {
                            item {
                                AdCard(ad)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MatchesForGameSortDialogPreviewDarkMode() {
    UserSelectedPrimaryColorTheme(primaryColor = deepOrange100) {
        MatchesForGameSortOptionsDialog(
            sortMode = MatchSortMode.ByMatchAge,
            sortDirection = SortDirection.Descending,
            onSortModeChanged = {},
            onSortDirectionChanged = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun MatchesForGameSortDialogPreview() {
    UserSelectedPrimaryColorTheme(primaryColor = deepOrange500) {
        MatchesForGameSortOptionsDialog(
            sortMode = MatchSortMode.ByMatchAge,
            sortDirection = SortDirection.Descending,
            onSortModeChanged = {},
            onSortDirectionChanged = {},
            onDismiss = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MatchesForGameScreenPreview() {
    MedianMeepleTheme {
        // TODO: add this back
    }
}
