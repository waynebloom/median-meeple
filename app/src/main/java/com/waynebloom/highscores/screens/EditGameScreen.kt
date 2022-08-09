package com.waynebloom.highscores.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.R
import com.waynebloom.highscores.components.ScreenHeader
import com.waynebloom.highscores.data.Game

@Composable
fun EditGameScreen(
    game: Game,
    onSaveTap: (Game) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteTap: (String) -> Unit = {},
    isNewGame: Boolean = false
) {
    var newName by rememberSaveable(game.name) { mutableStateOf(game.name) }
    val buttonOnClick = {
        onSaveTap(
            Game(
                id = game.id,
                name = newName,
                imageId = game.imageId
            )
        )
    }

    Column(modifier = modifier) {
        ScreenHeader(
            title = if (!isNewGame) game.name else stringResource(id = R.string.header_new_game),
            image = game.imageId,
            titleBarButton = {
                Button(
                    onClick = { buttonOnClick() },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(end = 16.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Done, contentDescription = null)
                }
            }
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = newName,
                label = { Text(text = stringResource(id = R.string.field_name)) },
                onValueChange = { newName = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { buttonOnClick() }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isNewGame) {
                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                    onClick = { onDeleteTap(game.id) },
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