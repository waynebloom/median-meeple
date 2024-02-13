package com.waynebloom.scorekeeper.ui.singleGame.matchesForGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.AdCard
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.MatchListItem
import com.waynebloom.scorekeeper.ui.components.MedianMeepleFab
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.toAdSeparatedSubLists
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.components.RadioButtonOption
import com.waynebloom.scorekeeper.ui.components.SingleGameDestinationTopBar
import com.waynebloom.scorekeeper.ui.components.SmallIconButton
import com.waynebloom.scorekeeper.ui.model.MatchUiModel
import com.waynebloom.scorekeeper.ui.singleGame.MatchesForGameUiState
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun MatchesForGameScreen(
    uiState: MatchesForGameUiState,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onSortButtonClick: () -> Unit,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onSortDialogDismiss: () -> Unit,
    onMatchClick: (Long) -> Unit,
    onAddMatchClick: () -> Unit
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
                scoringMode = uiState.scoringMode,
                listState = rememberLazyListState(),
                onSearchInputChanged,
                onSortButtonClick,
                onSortModeChanged,
                onSortDirectionChanged,
                onSortDialogDismiss,
                onMatchClick,
                onAddMatchClick
            )
        }
        is MatchesForGameUiState.Empty -> Loading() // TODO: create a real empty state here
        is MatchesForGameUiState.Loading -> Loading()
    }
}

/*@Composable
fun MatchesForGameTopBar(
    title: String,
    onOpenSearchTap: () -> Unit,
    onSortTap: () -> Unit,
    onEditGameTap: () -> Unit,
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
                onClick = { onOpenSearchTap() }
            )

            CustomIconButton(
                painter = painterResource(id = R.drawable.ic_sort),
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onClick = { onSortTap() }
            )

            CustomIconButton(
                imageVector = Icons.Rounded.Edit,
                backgroundColor = Color.Transparent,
                foregroundColor = themeColor,
                onClick = { onEditGameTap() }
            )
        }
    }
}

@Composable
fun MatchesForGameSortMenu(
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
                onClick = { onCloseTap() }
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
}*/

@Composable
fun MatchesForGameTopBar(
    screenTitle: String,
    searchFieldValue: TextFieldValue,
    onSortButtonClick: () -> Unit,
    onSearchInputChanged: (TextFieldValue) -> Unit
) {

    var isSearchFieldFocused by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val searchFieldFocusRequester = FocusRequester()

    Surface(color = MaterialTheme.colors.primary) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (!isSearchFieldFocused) {
                SingleGameDestinationTopBar(title = screenTitle)
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row {
                if (isSearchFieldFocused) {
                    SmallIconButton(
                        painter = painterResource(R.drawable.ic_back),
                        backgroundColor = Color.Transparent,
                        foregroundColor = MaterialTheme.colors.onPrimary,
                        onClick = { focusManager.clearFocus() }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .height(40.dp)
                        .padding(horizontal = 4.dp) // Adjustment to match the base top bar
                        .background(MaterialTheme.colors.onPrimary, CircleShape)
                        .padding(horizontal = Spacing.sectionContent)
                        .clickable { searchFieldFocusRequester.requestFocus() }
                ) {

                    if (!isSearchFieldFocused) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    BasicTextField(
                        value = searchFieldValue,
                        onValueChange = { onSearchInputChanged(it) },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(searchFieldFocusRequester)
                            .onFocusChanged { isSearchFieldFocused = it.isFocused },
                        textStyle = MaterialTheme.typography.body1,
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }

                if (isSearchFieldFocused) {
                    SmallIconButton(
                        painter = painterResource(id = R.drawable.ic_sort),
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        onClick = onSortButtonClick,
                    )
                }
            }
        }
    }
}

@Composable
fun MatchesForGameSortOptionsDialog(
    sortMode: MatchSortMode,
    sortDirection: SortDirection,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp)
            ) {

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
                            onSelected = { onSortModeChanged(option) }
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
    }
}

@Composable
private fun MatchesForGameHelperBoxListHeader(message: String, type: HelperBoxType) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = Modifier.padding(top = Spacing.sectionContent)
    ) {

        HelperBox(message = message, type = type, maxLines = 2)
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
    matches: List<MatchUiModel>,
    scoringMode: ScoringMode,
    listState: LazyListState,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onSortButtonClick: () -> Unit,
    onSortModeChanged: (MatchSortMode) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onSortDialogDismiss: () -> Unit,
    onMatchClick: (Long) -> Unit,
    onAddMatchClick: () -> Unit
) {

    if (isSortDialogShowing) {
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
            MatchesForGameTopBar(
                screenTitle,
                searchInput,
                onSortButtonClick,
                onSearchInputChanged,
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
                                searchInput),
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
                                searchInput),
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

/*@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun MatchesForSingleGameScreen(
    currentAd: NativeAd?,
    gameEntity: GameDataModel,
    lazyListState: LazyListState,
    listDisplayState: ListDisplayState,
    matches: List<MatchDataRelationModel>,
    searchString: String,
    themeColor: Color,
    onNewMatchTap: () -> Unit,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = { MedianMeepleFab(
            backgroundColor = themeColor,
            onClick = onNewMatchTap,
        ) },
        modifier = modifier,
    ) { contentPadding ->

        Column(modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = Spacing.screenEdge)
        ) {

            AnimatedContent(
                targetState = listDisplayState,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay }
            ) {

                if (it != ListDisplayState.ShowAll) {

                    val type = if (it == ListDisplayState.ShowFiltered) {
                        HelperBoxType.Info
                    } else HelperBoxType.Missing

                    val text = when (it) {
                        ListDisplayState.Empty -> stringResource(R.string.text_empty_matches)
                        ListDisplayState.EmptyFiltered -> stringResource(
                            id = R.string.text_empty_match_search_results,
                            searchString
                        )
                        ListDisplayState.ShowFiltered -> stringResource(
                            R.string.text_showing_search_results,
                            searchString
                        )
                        else -> ""
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                        modifier = Modifier.padding(top = Spacing.sectionContent)
                    ) {

                        HelperBox(message = text, type = type, maxLines = 2)

                        Divider()
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(
                    top = Spacing.sectionContent, bottom = Spacing.paddingForFab),
                verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
            ) {

                val adSeparatedSubLists = matches.toAdSeparatedSubLists()

                adSeparatedSubLists.forEachIndexed { index, subList ->

                    items(
                        items = subList,
                        key = { item -> item.entity.id }
                    ) { match ->

                        MatchListItem(
                            gameEntity = gameEntity,
                            match = match,
                            onClick = onSingleMatchTap,
                            showGameIdentifier = false,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = DurationMs.medium,
                                    easing = Ease)),
                        )
                    }

                    item {
                        if (index == adSeparatedSubLists.lastIndex) {
                            AdCard(
                                ad = currentAd,
                                primaryColor = themeColor.toArgb()
                            )
                        }
                    }
                }
            }
        }
    }
}*/

@Preview
@Composable
fun MatchesForGameTopBarPreview() {
    MedianMeepleTheme {
        MatchesForGameTopBar(
            screenTitle = "Wingspan",
            searchFieldValue = TextFieldValue("Search for something"),
            onSortButtonClick = {},
            onSearchInputChanged = {}
        )
    }
}

@Preview
@Composable
fun MatchesForGameSortDialogPreview() {
    MedianMeepleTheme {
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
