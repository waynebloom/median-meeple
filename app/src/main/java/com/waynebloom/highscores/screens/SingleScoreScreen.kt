package com.waynebloom.highscores.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.R
import com.waynebloom.highscores.components.ScreenHeader
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Score
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleScoreScreen(
    game: Game,
    score: Score,
    onSaveTap: (Score) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteTap: (String, String) -> Unit = {_,_->},
    openInEditMode: Boolean = false,
    isNewScore: Boolean = false
) {
    var editMode: Boolean by rememberSaveable(openInEditMode) { mutableStateOf(openInEditMode) }
    var newName: String by rememberSaveable(score.name) { mutableStateOf(score.name) }
    var newScore: String by rememberSaveable(score.score) { mutableStateOf(score.score.toString()) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
                    Score(
                        id = score.id,
                        gameOwnerId = game.id,
                        name = newName,
                        score = newScore.toInt(),
                        timeModified = Date().time
                    )
                )
            }
        } else {
            buttonIcon = Icons.Rounded.Edit
            buttonOnClick = { editMode = true }
        }
        ScreenHeader(
            title = game.name,
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
            val textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = MaterialTheme.colors.primary,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.background,
                cursorColor = MaterialTheme.colors.primary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth())
            {
                OutlinedTextField(
                    value = newName,
                    label = { Text(text = stringResource(id = R.string.field_name)) },
                    colors = textFieldColors,
                    onValueChange = { newName = it },
                    enabled = editMode,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Right) }
                    ),
                    modifier = Modifier.weight(0.75f)
                )
                OutlinedTextField(
                    value = newScore,
                    label = { Text(text = stringResource(id = R.string.field_score)) },
                    colors = textFieldColors,
                    onValueChange = { newScore = it },
                    enabled = editMode,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { buttonOnClick() }
                    ),
                    modifier = Modifier.weight(0.25f)
                )
            }
            if (!isNewScore) {
                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                    onClick = { onDeleteTap(game.id, score.id) },
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