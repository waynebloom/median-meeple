package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.SearchTopBar
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.data.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.MatchesForSingleGameTopBarState
import com.waynebloom.scorekeeper.enums.MenuOption
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.viewmodel.SingleGameViewModel
import com.waynebloom.scorekeeper.viewmodel.SingleGameViewModelFactory

@Composable
fun SingleGameScreen(
    gameObject: GameObject,
    currentAd: NativeAd?,
    onEditGameTap: () -> Unit,
    onNewMatchTap: () -> Unit,
    onSingleMatchTap: (Long) -> Unit,
) {
    val viewModel = viewModel<SingleGameViewModel>(
        key = SingleGameScreen.GameStatistics.name,
        factory = SingleGameViewModelFactory(
            gameObject = gameObject,
            resources = LocalContext.current.resources
        )
    ).onRecompose(
        gameObject = gameObject
    )

    val themeColor = LocalGameColors.current.getColorByKey(gameObject.entity.color)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (viewModel.selectedTab == SingleGameScreen.MatchesForSingleGame) {
                MatchesForSingleGameTopBar(
                    isSearchBarFocused = viewModel.isSearchBarFocused,
                    searchString = viewModel.searchString,
                    selectedTab = viewModel.selectedTab,
                    sortDirection = viewModel.sortDirection,
                    sortingMode = viewModel.sortMode,
                    state = viewModel.matchesTopBarState,
                    title = viewModel.screenTitle,
                    themeColor = themeColor,
                    onClearFiltersTap = { viewModel.clearFilters() },
                    onEditGameTap = onEditGameTap,
                    onSearchBarFocusChanged = { viewModel.isSearchBarFocused = it },
                    onSearchStringChanged = { viewModel.onSearchStringChanged(it, coroutineScope) },
                    onSortDirectionChanged = { viewModel.onSortDirectionChanged(it, coroutineScope) },
                    onSortModeChanged = { viewModel.onSortModeChanged(it, coroutineScope) },
                    onStateChanged = { viewModel.matchesTopBarState = it },
                    onTabSelected = { viewModel.selectedTab = it },
                )
            } else {
                GameStatisticsTopBar(
                    selectedTab = viewModel.selectedTab,
                    title = viewModel.screenTitle,
                    themeColor = themeColor,
                    onEditGameTap = onEditGameTap,
                    onTabSelected = { viewModel.selectedTab = it },
                )
            }
        },
    ) { innerPadding ->

        if (viewModel.selectedTab == SingleGameScreen.MatchesForSingleGame) {
            MatchesForSingleGameScreen(
                currentAd = currentAd,
                gameEntity = gameObject.entity,
                lazyListState = viewModel.matchesLazyListState,
                listState = viewModel.matchesListState,
                matches = viewModel.matchesToDisplay,
                searchString = viewModel.searchString,
                themeColor = themeColor,
                onNewMatchTap = onNewMatchTap,
                onSingleMatchTap = onSingleMatchTap,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            GameStatisticsScreen(
                gameObject = gameObject,
                themeColor = themeColor,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

// region Top Bar

@Composable
fun GameStatisticsTopBar(
    selectedTab: SingleGameScreen,
    onTabSelected: (SingleGameScreen) -> Unit,
    title: String,
    themeColor: Color,
    onEditGameTap: () -> Unit
) {

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .defaultMinSize(minHeight = Dimensions.Size.topBarHeight)
                .fillMaxWidth()
        ) {

            Text(
                text = title,
                color = themeColor,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            CustomIconButton(
                imageVector = Icons.Rounded.Edit,
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onTap = { onEditGameTap() }
            )
        }

        SingleGameTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            themeColor = themeColor,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun MatchesForSingleGameTopBar(
    isSearchBarFocused: Boolean,
    searchString: String,
    selectedTab: SingleGameScreen,
    sortDirection: SortDirection,
    sortingMode: MatchSortMode,
    state: MatchesForSingleGameTopBarState,
    themeColor: Color,
    title: String,
    onClearFiltersTap: () -> Unit,
    onEditGameTap: () -> Unit,
    onSearchBarFocusChanged: (Boolean) -> Unit,
    onSearchStringChanged: (String) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onStateChanged: (MatchesForSingleGameTopBarState) -> Unit,
    onTabSelected: (SingleGameScreen) -> Unit,
) {

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
                targetState = state,
                transitionSpec = {
                    when {
                        MatchesForSingleGameTopBarState.Default isTransitioningTo
                            MatchesForSingleGameTopBarState.SearchBarOpen -> fadeInWithFadeOut
                        MatchesForSingleGameTopBarState.SearchBarOpen isTransitioningTo
                            MatchesForSingleGameTopBarState.Default -> fadeInWithFadeOut
                        else -> delayedFadeInWithFadeOut using sizeTransformWithDelay
                    }
                }
            ) { state ->

                when (state) {

                    MatchesForSingleGameTopBarState.Default -> {
                        MatchesForSingleGameDefaultActionBar(
                            title = title,
                            themeColor = themeColor,
                            onEditGameTap = onEditGameTap,
                            onSortTap = { onStateChanged(MatchesForSingleGameTopBarState.SortMenuOpen) },
                            onOpenSearchTap = { onStateChanged(MatchesForSingleGameTopBarState.SearchBarOpen) }
                        )
                    }
                    MatchesForSingleGameTopBarState.SearchBarOpen -> {
                        SearchTopBar(
                            isSearchBarFocused = isSearchBarFocused,
                            searchString = searchString,
                            themeColor = themeColor,
                            onClearFiltersTap = onClearFiltersTap,
                            onSearchBarFocusChanged = onSearchBarFocusChanged,
                            onSearchStringChanged = onSearchStringChanged,
                            onCloseTap = {
                                onStateChanged(MatchesForSingleGameTopBarState.Default)
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    }
                    MatchesForSingleGameTopBarState.SortMenuOpen -> {
                        MatchesForSingleGameSortMenuActionBar(
                            themeColor = themeColor,
                            sortDirection = sortDirection,
                            sortMode = sortingMode,
                            onSortDirectionChanged = onSortDirectionChanged,
                            onSortModeChanged = onSortModeChanged,
                            onCloseTap = { onStateChanged(MatchesForSingleGameTopBarState.Default) }
                        )
                    }
                }
            }
        }

        SingleGameTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            themeColor = themeColor,
        )
    }
}

@Composable
fun MatchesForSingleGameDefaultActionBar(
    themeColor: Color,
    title: String,
    onOpenSearchTap: () -> Unit,
    onSortTap: () -> Unit,
    onEditGameTap: () -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(Dimensions.Size.topBarHeight)
    ) {

        Text(
            text = title,
            color = themeColor,
            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Row {

            CustomIconButton(
                imageVector = Icons.Rounded.Search,
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onTap = { onOpenSearchTap() }
            )

            CustomIconButton(
                painter = painterResource(id = R.drawable.ic_sort),
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onTap = { onSortTap() }
            )

            CustomIconButton(
                imageVector = Icons.Rounded.Edit,
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onTap = { onEditGameTap() }
            )
        }
    }
}

@Composable
fun MatchesForSingleGameSortMenuActionBar(
    themeColor: Color,
    sortDirection: SortDirection,
    sortMode: MatchSortMode,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onCloseTap: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = stringResource(R.string.header_sort_menu),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )

            CustomIconButton(
                imageVector = Icons.Rounded.Close,
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onTap = { onCloseTap() }
            )
        }

        Text(text = "Sort by...")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            MatchSortMode.values().forEach { option ->
                RadioButtonOption(
                    menuOption = option,
                    themeColor = themeColor,
                    isSelected = sortMode == option,
                    onSelected = { onSortModeChanged(it as MatchSortMode) }
                )
            }
        }

        Text(text = "Sort direction")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            SortDirection.values().forEach { option ->
                RadioButtonOption(
                    menuOption = option,
                    themeColor = themeColor,
                    isSelected = sortDirection == option,
                    onSelected = { onSortDirectionChanged(it as SortDirection) }
                )
            }
        }
    }
}

@Composable
fun RadioButtonOption(
    menuOption: MenuOption,
    themeColor: Color,
    isSelected: Boolean,
    onSelected: (MenuOption) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onSelected(menuOption) }
            .fillMaxWidth()
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(menuOption) },
            colors = RadioButtonDefaults.colors(
                selectedColor = themeColor,
                unselectedColor = MaterialTheme.colors.onSurface
            )
        )
        Text(text = stringResource(menuOption.label))
    }
}

// endregion

@Composable
fun SingleGameTabBar(
    selectedTab: SingleGameScreen,
    onTabSelected: (SingleGameScreen) -> Unit,
    themeColor: Color,
) {

    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        backgroundColor = MaterialTheme.colors.background,
    ) {
        SingleGameScreen.values().forEachIndexed { index, screen ->
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
                selectedContentColor = themeColor,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SingleGameScreenStatsPreview() {
    MedianMeepleTheme {
        SingleGameScreen(
            gameObject = GameObjectsDefaultPreview[0],
            currentAd = null,
            onEditGameTap = {},
            onNewMatchTap = {},
            onSingleMatchTap = {},
        )
    }
}