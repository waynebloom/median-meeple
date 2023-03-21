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
import com.waynebloom.scorekeeper.components.DullColoredTextCard
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.components.ScreenHeader
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.ext.toShortScoreFormat
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
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
        key = ScorekeeperScreen.SingleMatch.name,
        factory = SingleMatchViewModelFactory(
            matchEntity = match.entity,
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

    Column(modifier = modifier) {

        ScreenHeader(
            title = game.entity.name,
            color = themeColor
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            
            Spacer(modifier = Modifier.padding(top = 40.dp))

            PlayersSection(
                players = match.players,
                showDetailedScoresButton = viewModel.shouldShowDetailedScoresButton(
                    players = match.players.map { it.entity },
                    subscoreTitles = game.subscoreTitles
                ),
                themeColor = themeColor,
                onAddPlayerTap = onAddPlayerTap,
                onPlayerTap = onPlayerTap,
                onViewDetailedScoresTap = onViewDetailedScoresTap
            )

            HeadedSection(title = R.string.header_other) {
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = themeColor,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    onClick = { viewModel.onSaveTap(keyboardController, focusManager) },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f),
                    content = {
                        Icon(imageVector = Icons.Rounded.Done, contentDescription = null)
                    }
                )

                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                    onClick = { onDeleteMatchTap(match.entity.id) },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f),
                    content = {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlayersSectionHeader(
    showDetailedScoresButton: Boolean,
    themeColor: Color,
    onAddPlayerTap: () -> Unit,
    onViewDetailedScoresTap: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
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
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onViewDetailedScoresTap() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.List,
                        tint = themeColor,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onAddPlayerTap() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    tint = themeColor,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun PlayersSection(
    players: List<PlayerObject>,
    showDetailedScoresButton: Boolean,
    themeColor: Color,
    onAddPlayerTap: () -> Unit,
    onPlayerTap: (Long) -> Unit,
    onViewDetailedScoresTap: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        PlayersSectionHeader(
            showDetailedScoresButton = showDetailedScoresButton,
            themeColor = themeColor,
            onAddPlayerTap = onAddPlayerTap,
            onViewDetailedScoresTap = onViewDetailedScoresTap,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (players.isNotEmpty()) {
            val playersInRankOrder = players.sortedBy { it.entity.score.toBigDecimal() }.reversed()
            playersInRankOrder.forEachIndexed { index, score ->
                PlayerCard(
                    player = score.entity,
                    rank = index + 1,
                    themeColor = themeColor,
                    onPlayerTap = onPlayerTap
                )
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
fun PlayerCard(
    player: PlayerEntity,
    rank: Int,
    themeColor: Color,
    onPlayerTap: (Long) -> Unit,
    modifier: Modifier = Modifier
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
    ScoreKeeperTheme {
        PlayersSection(
            players = PreviewPlayerEntities.map { PlayerObject(entity = it) },
            showDetailedScoresButton = true,
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
    ScoreKeeperTheme {
        SingleMatchScreen(
            game = PreviewGameObjects[0],
            match = MatchObject(
                entity = PreviewMatchEntities[0],
                players = PreviewPlayerEntities.map { PlayerObject(entity = it) }
            ),
            onAddPlayerTap = {},
            onDeleteMatchTap = {},
            onPlayerTap = {},
            onViewDetailedScoresTap = {},
            saveMatch = {}
        )
    }
}
