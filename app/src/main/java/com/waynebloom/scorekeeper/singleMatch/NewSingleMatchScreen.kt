package com.waynebloom.scorekeeper.singleMatch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.IconButton
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.onFocusSelectAll
import com.waynebloom.scorekeeper.ext.toShortFormatString
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.ui.PreviewData

@Composable
fun SingleMatchScreen(
    uiState: SingleMatchUiState,
    onAddPlayerClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPlayerClick: (Long) -> Unit,
    onViewDetailedScoresClick: () -> Unit,
    onNotesChanged: (TextFieldValue) -> Unit,
    onSaveClick: () -> Unit
) {

    SingleMatchScreen(
        game = uiState.game,
        match = uiState.match,
        notes = uiState.notes,
        onAddPlayerClick,
        onDeleteClick,
        onPlayerClick,
        onViewDetailedScoresClick,
        onNotesChanged,
        onSaveClick
    )
}

@Composable
fun SingleMatchScreen(
    game: GameDomainModel,
    match: MatchDomainModel,
    notes: TextFieldValue,
    onAddPlayerClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPlayerClick: (Long) -> Unit,
    onViewDetailedScoresClick: () -> Unit,
    onNotesChanged: (TextFieldValue) -> Unit,
    onSaveClick: () -> Unit,
) {

    Scaffold(
        topBar = {
            SingleMatchScreenTopBar(
                title = game.name.value.text,
                onDoneClick = onSaveClick,
            )
        },
    ) { innerPadding ->

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = Dimensions.Spacing.screenEdge,
                vertical = Dimensions.Spacing.sectionContent),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.betweenSections),
            modifier = Modifier.padding(innerPadding),
        ) {

            item {

                PlayersSection(
                    players = match.players,
                    scoringMode = game.scoringMode,
                    isScorecardButtonEnabled = match.players.any { it.useCategorizedScore },
                    showMaximumPlayersErrorState = match.players.size >= NewSingleMatchViewModel.MAXIMUM_PLAYERS,
                    onAddPlayerClick = onAddPlayerClick,
                    onPlayerClick = onPlayerClick,
                    onViewScorecardClick = onViewDetailedScoresClick
                )
            }

            item {

                OtherSection(
                    notes = notes,
                    onNotesChanged = onNotesChanged,
                    onSaveClick = onSaveClick
                )
            }

            item {
                Divider()
                Spacer(Modifier.height(Dimensions.Spacing.sectionContent))

                // TODO: this needs a confirmation dialog before release
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .padding(
                            top = Dimensions.Spacing.subSectionContent,
                            bottom = Dimensions.Spacing.screenEdge
                        )
                        .height(40.dp)
                        .fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.error,
                        backgroundColor = MaterialTheme.colors.background,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colors.error),
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
private fun SingleMatchScreenTopBar(
    title: String,
    onDoneClick: () -> Unit,
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
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Row {

                IconButton(
                    imageVector = Icons.Rounded.Done,
                    backgroundColor = Color.Transparent,
                    onClick = onDoneClick,
                )
            }
        }

        Divider()
    }
}

@Composable
private fun PlayersSection(
    players: List<PlayerDomainModel>,
    scoringMode: ScoringMode,
    isScorecardButtonEnabled: Boolean,
    showMaximumPlayersErrorState: Boolean,
    onAddPlayerClick: () -> Unit,
    onPlayerClick: (Long) -> Unit,
    onViewScorecardClick: () -> Unit
) {
    Column {

        Text(
            text = stringResource(id = R.string.header_players),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(Dimensions.Spacing.sectionContent))

        if (showMaximumPlayersErrorState) {
            HelperBox(
                message = stringResource(id = R.string.error_maximum_players_reached),
                type = HelperBoxType.Info,
            )

            Spacer(Modifier.height(Dimensions.Spacing.sectionContent))
        }

        if (players.isNotEmpty()) {
            val playersInRankOrder = when(scoringMode) {
                ScoringMode.Ascending -> players.sortedBy(PlayerDomainModel::totalScore)
                ScoringMode.Descending -> players.sortedBy(PlayerDomainModel::totalScore).reversed()
                ScoringMode.Manual -> players.sortedBy(PlayerDomainModel::position)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)) {

                playersInRankOrder.forEachIndexed { index, player ->
                    val displayedRank = if (scoringMode == ScoringMode.Manual) {
                        player.position
                    } else index + 1

                    RankedListItem(
                        player = player,
                        rank = displayedRank,
                        onPlayerClick = onPlayerClick
                    )
                }
            }
        } else {

            HelperBox(
                message = stringResource(id = R.string.info_empty_players),
                type = HelperBoxType.Missing
            )
        }

        Row {

            val border = if (isScorecardButtonEnabled) {
                BorderStroke(1.dp, MaterialTheme.colors.primary)
            } else {
                null
            }

            Button(
                onClick = onViewScorecardClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(top = Dimensions.Spacing.sectionContent)
                    .weight(1f)
                    .height(40.dp)
                    .fillMaxWidth(),
                enabled = isScorecardButtonEnabled,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.background,
                ),
                border = border,
            ) {
                Text(text = "View Scorecard")
            }

            Button(
                onClick = onAddPlayerClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(
                        top = Dimensions.Spacing.sectionContent,
                        start = Dimensions.Spacing.sectionContent
                    )
                    .weight(1f)
                    .height(40.dp)
                    .fillMaxWidth(),
                enabled = !showMaximumPlayersErrorState,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colors.onPrimary,
                    backgroundColor = MaterialTheme.colors.primary,
                ),
            ) {
                Text(text = "Add Player")
            }
        }
    }
}

@Composable
private fun OtherSection(
    notes: TextFieldValue,
    onNotesChanged: (TextFieldValue) -> Unit,
    onSaveClick: () -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_other),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        OutlinedTextField(
            value = notes,
            label = { Text(text = stringResource(id = R.string.field_notes)) },
            onValueChange = onNotesChanged,
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSaveClick() }
            ),
            maxLines = 8,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusSelectAll(notes, onNotesChanged)
        )
    }
}

@Composable
private fun RankedListItem(
    player: PlayerDomainModel,
    rank: Int,
    onPlayerClick: (Long) -> Unit,
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
                .clickable { onPlayerClick(player.id) },
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
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp)
                    )
                    Text(
                        text = player.name.text,
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
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = player.totalScore.toShortFormatString(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlayerSectionBelowMaxPlayersPreview() {
    MedianMeepleTheme {
        Surface(color = MaterialTheme.colors.background) {
            PlayersSection(
                players = PreviewData.Players,
                scoringMode = ScoringMode.Ascending,
                isScorecardButtonEnabled = true,
                showMaximumPlayersErrorState = false,
                onAddPlayerClick = {},
                onPlayerClick = {},
                onViewScorecardClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun PlayerSectionAboveMaxPlayersPreview() {
    MedianMeepleTheme {
        Surface(color = MaterialTheme.colors.background) {
            PlayersSection(
                players = PreviewData.Players,
                scoringMode = ScoringMode.Ascending,
                isScorecardButtonEnabled = true,
                showMaximumPlayersErrorState = true,
                onAddPlayerClick = {},
                onPlayerClick = {},
                onViewScorecardClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun OtherSectionPreview() {
    MedianMeepleTheme {
        Surface(color = MaterialTheme.colors.background) {
            OtherSection(
                notes = TextFieldValue("This match has some notes."),
                onNotesChanged = {},
                onSaveClick = {},
            )
        }
    }
}
