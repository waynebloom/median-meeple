/*
package com.waynebloom.scorekeeper.singleGame TODO: remove, reference only

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.MatchesForSingleGameTopBarState
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.RadioButtonOption
import com.waynebloom.scorekeeper.components.SearchTopBar
import com.waynebloom.scorekeeper.singleGame.matchesForGame.MatchesForGameRoute
import com.waynebloom.scorekeeper.singleGame.matchesForGame.MatchesForSingleGameScreen
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameRoute
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.ui.StatisticsForGameScreen
import com.waynebloom.scorekeeper.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun SingleGameTopBar(
    name: String
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomIconButton(
            painter = painterResource(R.drawable.ic_x),
            backgroundColor = MaterialTheme.colors.onPrimary,
            onClick = {}
        )
        
        Text(text = name)
        
        CustomIconButton(
            painter = painterResource(R.drawable.ic_edit),
            backgroundColor = MaterialTheme.colors.onPrimary,
            onClick = {}
        )
    }
}

@Composable
fun SingleGameBottomNavigation(
    items: List<Destination.BottomNavDestination>,
    navController: NavController
) {

    BottomNavigation {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->

            BottomNavigationItem(
                icon = { Icon(painterResource(screen.iconResource), null) },
                label = { Text(stringResource(screen.labelResource)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun SingleGameScreen(
    gameObject: GameDataRelationModel,
    currentAd: NativeAd?,
    onEditGameClick: () -> Unit,
    onNewMatchClick: () -> Unit,
    onSingleMatchClick: (Long) -> Unit,
    viewModel: SingleGameViewModel = hiltViewModel(),
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


    val themeColor = LocalCustomThemeColors.current.getColorByKey(viewModel.color)
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
                    onClearFiltersClick = { viewModel.clearFilters() },
                    onEditGameClick = onEditGameClick,
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
                    onEditGameClick = onEditGameClick,
                    onTabSelected = { viewModel.selectedTab = it },
                )
            }
        },
    ) { innerPadding ->

        if (viewModel.selectedTab == SingleGameScreen.MatchesForSingleGame) {
            MatchesForSingleGameScreen(
                currentAd = currentAd,
                gameEntity = viewModel.game.entity,
                lazyListState = viewModel.matchesLazyListState,
                listDisplayState = viewModel.matchesListDisplayState,
                matches = viewModel.matchesToDisplay,
                searchString = viewModel.searchString,
                themeColor = themeColor,
                onNewMatchClick = onNewMatchClick,
                onSingleMatchClick = onSingleMatchClick,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            StatisticsForGameScreen(
                gameObject = viewModel.game,
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
    onEditGameClick: () -> Unit
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
                onClick = { onEditGameClick() }
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
    onClearFiltersClick: () -> Unit,
    onEditGameClick: () -> Unit,
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
                            onEditGameClick = onEditGameClick,
                            onSortClick = { onStateChanged(MatchesForSingleGameTopBarState.SortMenuOpen) },
                            onOpenSearchClick = { onStateChanged(MatchesForSingleGameTopBarState.SearchBarOpen) }
                        )
                    }
                    MatchesForSingleGameTopBarState.SearchBarOpen -> {
                        SearchTopBar(
                            isSearchBarFocused = isSearchBarFocused,
                            searchString = searchString,
                            themeColor = themeColor,
                            onClearFiltersClick = onClearFiltersClick,
                            onSearchBarFocusChanged = onSearchBarFocusChanged,
                            onSearchStringChanged = onSearchStringChanged,
                            onCloseClick = {
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
                            onCloseClick = { onStateChanged(MatchesForSingleGameTopBarState.Default) }
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
    onOpenSearchClick: () -> Unit,
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
                onClick = { onOpenSearchClick() }
            )

            CustomIconButton(
                painter = painterResource(id = R.drawable.ic_sort),
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onClick = { onSortClick() }
            )

            CustomIconButton(
                imageVector = Icons.Rounded.Edit,
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onClick = { onEditGameClick() }
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
    onCloseClick: () -> Unit
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
                onClick = { onCloseClick() }
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
                    isSelected = sortDirection == option,
                    onSelected = { onSortDirectionChanged(it as SortDirection) }
                )
            }
        }
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
*/
