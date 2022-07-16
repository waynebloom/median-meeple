package com.waynebloom.highscores.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.R
import com.waynebloom.highscores.model.Game
import com.waynebloom.highscores.model.Score

@Composable
fun SingleScoreScreen(
    game: Game,
    score: Score,
    onSaveTap: (Score) -> Unit,
    modifier: Modifier = Modifier,
    openInEditMode: Boolean = false
) {
    var editMode: Boolean by rememberSaveable { mutableStateOf(openInEditMode) }
    var newName: String by rememberSaveable { mutableStateOf(score.name) }
    var newScore: String by rememberSaveable { mutableStateOf(score.score.toString()) }

    Column(modifier = modifier) {
        ScoreDetailHeader(
            gameName = game.name,
            gameImage = game.image,
            editMode = editMode,
            onEditTap = { editMode = true },
            onSaveTap = {
                editMode = false
                onSaveTap(
                    Score(
                        id = score.id,
                        forGame = game.name,
                        name = newName,
                        score = newScore.toInt()
                    )
                )
            }
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = MaterialTheme.colors.secondary,
                backgroundColor = MaterialTheme.colors.background,
                cursorColor = MaterialTheme.colors.secondary
            )
            OutlinedTextField(
                colors = textFieldColors,
                label = { Text(text = stringResource(id = R.string.field_name)) },
                onValueChange = { newName = it },
                readOnly = !editMode,
                value = newName,
            )
            OutlinedTextField(
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = stringResource(id = R.string.field_score)) },
                onValueChange = { newScore = it },
                readOnly = !editMode,
                value = newScore,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ScoreDetailHeader(
    gameName: String,
    @DrawableRes gameImage: Int,
    editMode: Boolean,
    onEditTap: () -> Unit,
    onSaveTap: () -> Unit
) {
    val buttonOnClick: () -> Unit
    val buttonText: String
    if (editMode) {
        buttonOnClick = onSaveTap
        buttonText = "Save"
    } else {
        buttonOnClick = onEditTap
        buttonText = "Edit"
    }
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = gameImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.background.copy(alpha = 1f),
                    shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0))
                )
        ) {
            Text(
                style = MaterialTheme.typography.h4,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                text = gameName,
                modifier = Modifier
                    .weight(0.75f)
                    .padding(all = 16.dp)
            )
            Button(
                onClick = { buttonOnClick() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                modifier = Modifier
                    .weight(0.25f)
                    .padding(vertical = 16.dp)
                    .padding(end = 16.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}