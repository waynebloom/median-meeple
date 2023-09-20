package com.waynebloom.scorekeeper.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.*
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.CustomIconButton
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ext.toShortScoreFormat
import com.waynebloom.scorekeeper.room.domain.model.EntityStateBundle
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.ui.theme.color.orange100
import com.waynebloom.scorekeeper.viewmodel.SingleMatchViewModel
import com.waynebloom.scorekeeper.viewmodel.SingleMatchViewModelFactory
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleMatchScreen(
    game: GameDataRelationModel,
    match: MatchDataRelationModel,
    onAddPlayerTap: () -> Unit,
    onDeleteMatchTap: (Long) -> Unit,
    onPlayerTap: (Long) -> Unit,
    onViewDetailedScoresTap: () -> Unit,
    saveMatch: (EntityStateBundle<MatchDataModel>) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<SingleMatchViewModel>(
        key = TopLevelScreen.SingleMatch.name,
        factory = SingleMatchViewModelFactory(
            matchEntity = match.entity,
            addPlayerCallback = onAddPlayerTap,
            saveCallback = saveMatch
        )
    )
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val themeColor: Color = LocalCustomThemeColors.current.getColorByKey(game.entity.color)
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = themeColor,
        focusedLabelColor = themeColor,
        cursorColor = themeColor,
        disabledBorderColor = themeColor.copy(0.75f)
    )
    val textSelectionColors = TextSelectionColors(
        handleColor = themeColor,
        backgroundColor = themeColor.copy(Alpha.textSelectionBackground)
    )

    Scaffold(
        topBar = {
            SingleMatchScreenTopBar(
                title = game.entity.name,
                themeColor = themeColor,
                onDoneTap = { viewModel.onSaveTap(keyboardController, focusManager) },
                onDeleteTap = { onDeleteMatchTap(match.entity.id) },
            )
        },
        modifier = modifier,
    ) { innerPadding ->

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = Spacing.screenEdge,
                vertical = Spacing.sectionContent),
            verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
            modifier = Modifier.padding(innerPadding),
        ) {

            item {

                PlayersSection(
                    players = match.players.map { it.entity },
                    scoringMode = game.getScoringMode(),
                    showDetailedScoresButton = viewModel.shouldShowDetailedScoresButton(
                        players = match.players.map { it.entity },
                        subscoreTitles = game.categories
                    ),
                    showMaximumPlayersErrorState = viewModel.showMaximumPlayersError,
                    themeColor = themeColor,
                    onAddPlayerTap = { viewModel.onAddPlayerTap(match.players.size) },
                    onPlayerTap = onPlayerTap,
                    onViewDetailedScoresTap = onViewDetailedScoresTap
                )
            }


            item {

                CompositionLocalProvider(LocalTextSelectionColors.provides(textSelectionColors)) {

                    OtherSection(
                        notes = viewModel.notes,
                        textFieldColors = textFieldColors,
                        onNotesChanged = { viewModel.onNotesChanged(it) },
                        onSaveTap = { viewModel.onSaveTap(keyboardController, focusManager) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleMatchScreenTopBar(
    title: String,
    themeColor: Color,
    onDoneTap: () -> Unit,
    onDeleteTap: () -> Unit,
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
                color = themeColor,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Row {

                CustomIconButton(
                    imageVector = Icons.Rounded.Done,
                    backgroundColor = Color.Transparent,
                    foregroundColor = themeColor,
                    onTap = onDoneTap,
                )

                CustomIconButton(
                    imageVector = Icons.Rounded.Delete,
                    backgroundColor = Color.Transparent,
                    foregroundColor = MaterialTheme.colors.error,
                    onTap = onDeleteTap,
                )
            }
        }

        Divider()
    }
}

@Composable
private fun PlayersSectionHeader(
    showDetailedScoresButton: Boolean,
    showMaximumPlayersErrorState: Boolean,
    themeColor: Color,
    onAddPlayerTap: () -> Unit,
    onViewDetailedScoresTap: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.header_players),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.medium)
        ) {

            if (showDetailedScoresButton) {
                CustomIconButton(
                    imageVector = Icons.Rounded.List,
                    foregroundColor = themeColor,
                    onTap = onViewDetailedScoresTap
                )
            }

            if (!showMaximumPlayersErrorState) {
                CustomIconButton(
                    imageVector = Icons.Rounded.Add,
                    foregroundColor = themeColor,
                    onTap = onAddPlayerTap
                )
            } else {
                CustomIconButton(
                    imageVector = Icons.Rounded.Warning,
                    foregroundColor = MaterialTheme.colors.error,
                    onTap = onAddPlayerTap
                )
            }
        }
    }
}

@Composable
fun PlayersSection(
    players: List<PlayerDataModel>,
    scoringMode: ScoringMode,
    showDetailedScoresButton: Boolean,
    showMaximumPlayersErrorState: Boolean,
    themeColor: Color,
    onAddPlayerTap: () -> Unit,
    onPlayerTap: (Long) -> Unit,
    onViewDetailedScoresTap: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        PlayersSectionHeader(
            showDetailedScoresButton = showDetailedScoresButton,
            showMaximumPlayersErrorState = showMaximumPlayersErrorState,
            themeColor = themeColor,
            onAddPlayerTap = onAddPlayerTap,
            onViewDetailedScoresTap = onViewDetailedScoresTap,
        )

        if (showMaximumPlayersErrorState) HelperBox(
            message = stringResource(id = R.string.error_maximum_players_reached),
            type = HelperBoxType.Error,
        )

        if (players.isNotEmpty()) {
            val playersInRankOrder = when(scoringMode) {
                ScoringMode.Ascending -> players.sortedBy { it.totalScore.toBigDecimal() }
                ScoringMode.Descending -> players.sortedBy { it.totalScore.toBigDecimal() }.reversed()
                ScoringMode.Manual -> players.sortedBy { it.position }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

                playersInRankOrder.forEachIndexed { index, player ->
                    val displayedRank = if (scoringMode == ScoringMode.Manual) {
                        player.position
                    } else index + 1

                    RankedListItem(
                        player = player,
                        rank = displayedRank,
                        themeColor = themeColor,
                        onPlayerTap = onPlayerTap
                    )
                }
            }
        } else {

            HelperBox(
                message = stringResource(id = R.string.info_empty_players),
                type = HelperBoxType.Missing
            )
        }
    }
}

@Composable
private fun OtherSection(
    notes: String,
    textFieldColors: TextFieldColors,
    onNotesChanged: (String) -> Unit,
    onSaveTap: () -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_other),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        OutlinedTextField(
            value = notes,
            label = { Text(text = stringResource(id = R.string.field_notes)) },
            colors = textFieldColors,
            onValueChange = { onNotesChanged(it) },
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSaveTap() }
            ),
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RankedListItem(
    player: PlayerDataModel,
    rank: Int,
    themeColor: Color,
    onPlayerTap: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = rank.toString(),
            modifier = Modifier
                .padding(end = 16.dp)
                .sizeIn(minWidth = 16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
        )

        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { onPlayerTap(player.id) },
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_person),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(themeColor),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp)
                    )
                    Text(
                        text = player.name,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                        text = player.totalScore.toShortScoreFormat(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(
    backgroundColor = 0xFF333333,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ScoresSectionPreview() {
    MedianMeepleTheme {
        PlayersSection(
            players = PlayerEntitiesDefaultPreview,
            scoringMode = ScoringMode.Descending,
            showDetailedScoresButton = true,
            showMaximumPlayersErrorState = true,
            themeColor = orange100,
            onAddPlayerTap = {},
            onPlayerTap = {},
            onViewDetailedScoresTap = {},
        )
    }
}

@Preview(
    backgroundColor = 0xFF333333,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SingleMatchScreenPreview() {
    MedianMeepleTheme {
        SingleMatchScreen(
            game = GameObjectsDefaultPreview[0],
            match = MatchDataRelationModel(
                entity = MatchEntitiesDefaultPreview[0],
                players = PlayerEntitiesDefaultPreview.map { PlayerDataRelationModel(entity = it) }
            ),
            onAddPlayerTap = {},
            onDeleteMatchTap = {},
            onPlayerTap = {},
            onViewDetailedScoresTap = {},
            saveMatch = {}
        )
    }
}
