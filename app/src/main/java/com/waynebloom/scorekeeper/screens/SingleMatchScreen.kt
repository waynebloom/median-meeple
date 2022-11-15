package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.PreviewGameData
import com.waynebloom.scorekeeper.PreviewMatchData
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.components.ScreenHeader
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.ext.updateElement
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.waynebloom.scorekeeper.ui.theme.orange100
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleMatchScreen(
    game: GameEntity,
    match: MatchObject,
    onSaveTap: (MatchEntity, List<ScoreObject>) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteMatchTap: (Long) -> Unit = {},
    openInEditMode: Boolean = false,
    isNewMatch: Boolean = false
) {
    var editMode: Boolean by rememberSaveable(openInEditMode) { mutableStateOf(openInEditMode) }
    var newScores: List<ScoreObject> by rememberSaveable(match.scores) {
        mutableStateOf(match.scores.map { ScoreObject(it) })
    }
    var newNotes: String by rememberSaveable(match.entity.matchNotes) { mutableStateOf(match.entity.matchNotes) }

    val gameColor = LocalGameColors.current.getColorByKey(game.color)
    val textSelectionColors = TextSelectionColors(
        handleColor = gameColor,
        backgroundColor = gameColor.copy(0.3f)
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(
        LocalTextSelectionColors.provides(textSelectionColors)
    ) {
        Column(modifier = modifier) {
            val buttonOnClick: () -> Unit
            val buttonIcon: ImageVector
            if (editMode) {
                buttonIcon = Icons.Rounded.Done
                buttonOnClick = {
                    editMode = false
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                    onSaveTap(
                        MatchEntity(
                            id = match.entity.id,
                            gameOwnerId = game.id,
                            timeModified = Date().time,
                            matchNotes = newNotes
                        ),
                        newScores
                    )
                }
            } else {
                buttonIcon = Icons.Rounded.Edit
                buttonOnClick = { editMode = true }
            }
            ScreenHeader(
                title = game.name,
                color = gameColor
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                HeadedSection(
                    title = R.string.header_scores,
                    topPadding = 40
                ) {
                    ScoresSection(
                        scores = newScores,
                        gameColor = gameColor,
                        editMode = editMode,
                        focusManager = focusManager,
                        onScoreChange = { scoreToChange, updatedEntity ->
                            newScores = newScores.updateElement({it == scoreToChange}) {
                                scoreToChange.copy(
                                    entity = updatedEntity,
                                    action = if (scoreToChange.action != DatabaseAction.INSERT) {
                                        DatabaseAction.UPDATE
                                    } else scoreToChange.action
                                )
                            }
                        },
                        onNewScoreTap = {
                            val newScore = ScoreObject(
                                entity = ScoreEntity(matchId = match.entity.id),
                                action = DatabaseAction.INSERT
                            )
                            newScores = newScores.plus(newScore)
                        },
                        onDeleteScoreTap = { score ->
                            newScores = if (score.action != DatabaseAction.INSERT) {
                                newScores.updateElement({it == score}) {
                                    score.copy(action = DatabaseAction.DELETE)
                                }
                            } else {
                                newScores.minus(score)
                            }
                        }
                    )
                }
                HeadedSection(title = R.string.header_other) {
                    OutlinedTextField(
                        value = newNotes,
                        label = { Text(text = stringResource(id = R.string.field_notes)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = gameColor,
                            focusedLabelColor = gameColor,
                            cursorColor = gameColor,
                            disabledBorderColor = gameColor.copy(0.75f)
                        ),
                        onValueChange = { newNotes = it },
                        enabled = editMode,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = true,
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { buttonOnClick() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = gameColor,
                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        onClick = { buttonOnClick() },
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    ) {
                        Icon(imageVector = buttonIcon, contentDescription = null)
                    }
                    if (!isNewMatch) {
                        Button(
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                            onClick = { onDeleteMatchTap(match.entity.id) },
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .height(48.dp)
                                .weight(1f)
                        ) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ScoresSection(
    scores: List<ScoreObject>,
    gameColor: Color,
    editMode: Boolean,
    focusManager: FocusManager,
    onScoreChange: (ScoreObject, ScoreEntity) -> Unit,
    onNewScoreTap: () -> Unit,
    onDeleteScoreTap: (ScoreObject) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        scores.forEach { score ->
            if (score.action != DatabaseAction.DELETE)
                ScoreCard(
                    score = score,
                    gameColor = gameColor,
                    editMode = editMode,
                    focusManager = focusManager,
                    onScoreChange = { scoreToChange, updatedScore -> onScoreChange(scoreToChange, updatedScore) },
                    onDeleteTap = onDeleteScoreTap
                )
        }
        if (scores.isEmpty() || scores.find { it.action != DatabaseAction.DELETE } == null) {
            val emptyContentColor = gameColor.copy(alpha = 0.5f)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = emptyContentColor,
                        shape = MaterialTheme.shapes.small
                    )
                    .height(64.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    color = emptyContentColor,
                    text = stringResource(id = R.string.text_empty_scores)
                )
            }
        }
        if (editMode) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = gameColor,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                onClick = { onNewScoreTap() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ScoreCard(
    score: ScoreObject,
    gameColor: Color,
    editMode: Boolean,
    focusManager: FocusManager,
    onScoreChange: (ScoreObject, ScoreEntity) -> Unit,
    onDeleteTap: (ScoreObject) -> Unit
) {
    val textFieldHeight = TextFieldDefaults.MinHeight
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = gameColor,
        focusedLabelColor = gameColor,
        cursorColor = gameColor,
        disabledBorderColor = gameColor.copy(0.75f)
    )

    Row {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            tint = gameColor,
            modifier = Modifier
                .padding(end = 16.dp, top = 20.dp)
                .weight(0.15f)
                .size(32.dp)
        )
        Column(modifier = Modifier.weight(0.9f)) {
            OutlinedTextField(
                value = score.entity.name,
                label = { Text(text = stringResource(id = R.string.field_name)) },
                colors = textFieldColors,
                onValueChange = {
                    onScoreChange(score, score.entity.copy(name = it))
                },
                enabled = editMode,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(
                    value = score.entity.scoreValue?.toString() ?: "",
                    label = { Text(text = stringResource(id = R.string.field_score)) },
                    colors = textFieldColors,
                    onValueChange = {
                        onScoreChange(score, score.entity.copy(scoreValue = it.toLongOrNull()))
                    },
                    enabled = editMode,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    modifier = Modifier
                        .weight(0.85f)
                )
                if (editMode) {
                    Button(
                        onClick = { onDeleteTap(score) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                        modifier = Modifier
                            .weight(0.25f)
                            .height(textFieldHeight)
                            .padding(start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScoresSectionPreview() {
    ScoreKeeperTheme {
        ScoresSection(
            scores = listOf(),
            gameColor = orange100,
            editMode = false,
            focusManager = LocalFocusManager.current,
            onScoreChange = {_,_->},
            onNewScoreTap = {},
            onDeleteScoreTap = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SingleMatchScreenPreview() {
    ScoreKeeperTheme {
        SingleMatchScreen(
            game = PreviewGameData[0],
            match = EMPTY_MATCH_OBJECT.apply {
                 entity = PreviewMatchData[0]
            },
            onSaveTap = {_,_->}
        )
    }
}