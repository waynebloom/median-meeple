package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.*
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.DullColoredTextCard
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.toShortScoreFormat
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.ui.theme.orange100
import com.waynebloom.scorekeeper.viewmodel.SingleMatchViewModel
import com.waynebloom.scorekeeper.viewmodel.SingleMatchViewModelFactory
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleMatchScreen(
    game: GameObject,
    match: MatchObject,
    onAddPlayerTap: () -> Unit,
    onDeleteMatchTap: (Long) -> Unit,
    onPlayerTap: (Long) -> Unit,
    onViewDetailedScoresTap: () -> Unit,
    saveMatch: (EntityStateBundle<MatchEntity>) -> Unit,
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
    val themeColor: Color = LocalGameColors.current.getColorByKey(game.entity.color)
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = themeColor,
        focusedLabelColor = themeColor,
        cursorColor = themeColor,
        disabledBorderColor = themeColor.copy(0.75f)
    )
    val textSelectionColors = TextSelectionColors(
        handleColor = themeColor,
        backgroundColor = themeColor.copy(0.3f)
    )

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {

        SingleMatchScreenTopBar(
            title = game.entity.name,
            themeColor = themeColor,
            onDoneTap = { viewModel.onSaveTap(keyboardController, focusManager) },
            onDeleteTap = { onDeleteMatchTap(match.entity.id) }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            SingleMatchScreenInfoBox(gameName = game.entity.name, themeColor = themeColor)

            PlayersSection(
                players = match.players.map { it.entity },
                scoringMode = game.getScoringMode(),
                showDetailedScoresButton = viewModel.shouldShowDetailedScoresButton(
                    players = match.players.map { it.entity },
                    subscoreTitles = game.subscoreTitles
                ),
                showMaximumPlayersErrorState = viewModel.showMaximumPlayersErrorState,
                themeColor = themeColor,
                onAddPlayerTap = { viewModel.onAddPlayerTap(match.players.size) },
                onPlayerTap = onPlayerTap,
                onViewDetailedScoresTap = onViewDetailedScoresTap
            )

            HeadedSection(
                title = R.string.header_other,
                topPadding = 40
            ) {
                CompositionLocalProvider(
                    LocalTextSelectionColors.provides(textSelectionColors)
                ) {
                    OutlinedTextField(
                        value = viewModel.notesState,
                        label = { Text(text = stringResource(id = R.string.field_notes)) },
                        colors = textFieldColors,
                        onValueChange = { viewModel.updateNotes(it) },
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = true,
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.onSaveTap(keyboardController, focusManager) }
                        ),
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth()
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
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            color = themeColor
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(
                    MaterialTheme.colors.surface,
                    MaterialTheme.shapes.small
                )
            ) {

                CustomIconButton(
                    imageVector = Icons.Rounded.Done,
                    foregroundColor = themeColor,
                    onTap = onDoneTap,
                )

                CustomIconButton(
                    imageVector = Icons.Rounded.Delete,
                    foregroundColor = MaterialTheme.colors.error,
                    onTap = onDeleteTap,
                )
            }
        }
    }
}

@Composable
private fun SingleMatchScreenInfoBox(
    gameName: String,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = MaterialTheme.shapes.small
            )
            .fillMaxWidth()
    ) {
        val firstText = stringResource(id = R.string.info_single_match_screen_helper_1)
        val secondText = stringResource(id = R.string.info_single_match_screen_helper_2)

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                modifier = Modifier.padding(top = 2.dp),
            )

            Text(
                text = buildAnnotatedString {
                    append(firstText)
                    withStyle(
                        style = SpanStyle(
                            color = themeColor,
                            fontStyle = FontStyle.Italic
                        ),
                        block = { append(" $gameName ") }
                    )
                    append(secondText)
                },
            )
        }
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
            text = stringResource(id = R.string.header_players)
                .uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
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
private fun MaximumPlayersReachedErrorBox(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.error,
                shape = MaterialTheme.shapes.small
            )
            .fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                tint = MaterialTheme.colors.error,
                contentDescription = null,
                modifier = Modifier.padding(top = 2.dp),
            )

            Text(text = stringResource(id = R.string.error_maximum_players_reached))
        }
    }
}

@Composable
fun PlayersSection(
    players: List<PlayerEntity>,
    scoringMode: ScoringMode,
    showDetailedScoresButton: Boolean,
    showMaximumPlayersErrorState: Boolean,
    themeColor: Color,
    onAddPlayerTap: () -> Unit,
    onPlayerTap: (Long) -> Unit,
    onViewDetailedScoresTap: () -> Unit
) {
    Column {

        PlayersSectionHeader(
            showDetailedScoresButton = showDetailedScoresButton,
            showMaximumPlayersErrorState = showMaximumPlayersErrorState,
            themeColor = themeColor,
            onAddPlayerTap = onAddPlayerTap,
            onViewDetailedScoresTap = onViewDetailedScoresTap,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (showMaximumPlayersErrorState) {
            MaximumPlayersReachedErrorBox()

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (players.isNotEmpty()) {
            val playersInRankOrder = when(scoringMode) {
                ScoringMode.Ascending -> players.sortedBy { it.score.toBigDecimal() }
                ScoringMode.Descending -> players.sortedBy { it.score.toBigDecimal() }.reversed()
                ScoringMode.Manual -> players.sortedBy { it.position }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

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
            DullColoredTextCard(
                text = stringResource(id = R.string.text_empty_players),
                color = themeColor
            )
        }
    }
}

@Composable
fun RankedListItem(
    player: PlayerEntity,
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
                        text = player.score.toShortScoreFormat(),
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
            match = MatchObject(
                entity = MatchEntitiesDefaultPreview[0],
                players = PlayerEntitiesDefaultPreview.map { PlayerObject(entity = it) }
            ),
            onAddPlayerTap = {},
            onDeleteMatchTap = {},
            onPlayerTap = {},
            onViewDetailedScoresTap = {},
            saveMatch = {}
        )
    }
}
