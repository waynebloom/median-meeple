package com.waynebloom.highscores.screens

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.PreviewGameData
import com.waynebloom.highscores.PreviewMatchData
import com.waynebloom.highscores.PreviewScoreData
import com.waynebloom.highscores.R
import com.waynebloom.highscores.components.HeadedSection
import com.waynebloom.highscores.components.ScreenHeader
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Match
import com.waynebloom.highscores.data.Score
import com.waynebloom.highscores.ui.theme.HighScoresTheme
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
var keyboardController: SoftwareKeyboardController? = null
lateinit var focusManager: FocusManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleMatchScreen(
    game: Game,
    match: Match,
    onSaveTap: (Match, List<Score>) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteTap: (String, String) -> Unit = {_,_->},
    openInEditMode: Boolean = false,
    isNewMatch: Boolean = false
) {
    var editMode: Boolean by rememberSaveable(openInEditMode) { mutableStateOf(openInEditMode) }
    var newScores: List<Score> by rememberSaveable(match.scores) { mutableStateOf(match.scores) }
    var newNotes: String by rememberSaveable(match.matchNotes) { mutableStateOf(match.matchNotes) }
    keyboardController = LocalSoftwareKeyboardController.current
    focusManager = LocalFocusManager.current

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
                    Match(
                        id = match.id,
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
            title = stringResource(id = R.string.text_edit_match),
            image = game.imageId,
            titleBarButton = {
                Button(
                    onClick = { buttonOnClick() },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(end = 16.dp)
                ) {
                    Icon(imageVector = buttonIcon, contentDescription = null)
                }
            }
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeadedSection(title = R.string.header_scores) {
                ScoresSection(
                    scores = newScores,
                    editMode = editMode,
                    onScoreChange = { newIndex, newScore ->
                        newScores = newScores.mapIndexed { existingIndex, existingScore ->
                            if (existingIndex == newIndex) newScore else existingScore
                        }
                    },
                    onNewScoreTap = { newScores = newScores.plus(Score(matchId = match.id)) }
                )
            }
            HeadedSection(title = R.string.header_other) {
                OutlinedTextField(
                    value = newNotes,
                    label = { Text(text = stringResource(id = R.string.field_notes)) },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
            if (!isNewMatch && editMode) {
                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                    onClick = { onDeleteTap(game.id, match.id) },
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                        Text(
                            text = stringResource(id = R.string.button_delete),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ScoresSection(
    scores: List<Score>,
    editMode: Boolean,
    onScoreChange: (Int, Score) -> Unit,
    onNewScoreTap: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        scores.forEachIndexed { index, score  ->
            ScoreCard(
                score = score,
                editMode = editMode,
                onNameChange = { name ->
                    onScoreChange(index, score.copy(name = name))
                },
                onScoreChange = { scoreValue ->
                    onScoreChange(index, score.copy(scoreValue = scoreValue.toIntOrNull()))
                }
            )
        }
        if (scores.isEmpty()) {
            val emptyContentColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
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
    score: Score,
    editMode: Boolean,
    onNameChange: (String) -> Unit,
    onScoreChange: (String) -> Unit
) {
    val textFieldHeight = TextFieldDefaults.MinHeight

    Row {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp, top = 20.dp)
                .weight(0.15f)
                .size(32.dp)
        )
        Column(modifier = Modifier.weight(0.9f)) {
            OutlinedTextField(
                value = score.name,
                label = { Text(text = stringResource(id = R.string.field_name)) },
                onValueChange = { onNameChange(it) },
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
                    value = score.scoreValue?.toString() ?: "",
                    label = { Text(text = stringResource(id = R.string.field_score)) },
                    onValueChange = { onScoreChange(it) },
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
                        onClick = { /*TODO*/ },
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
    HighScoresTheme {
        ScoresSection(
            scores = PreviewScoreData,
            editMode = false,
            onScoreChange = {_,_->},
            onNewScoreTap = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SingleMatchScreenPreview() {
    HighScoresTheme {
        SingleMatchScreen(
            game = PreviewGameData[0],
            match = PreviewMatchData[0],
            onSaveTap = {_,_->}
        )
    }
}