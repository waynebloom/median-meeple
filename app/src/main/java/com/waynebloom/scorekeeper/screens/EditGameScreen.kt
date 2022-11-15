package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.components.ScreenHeader
import com.waynebloom.scorekeeper.data.EMPTY_GAME_ENTITY
import com.waynebloom.scorekeeper.data.GameEntity
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.waynebloom.scorekeeper.ui.theme.ScorekeeperShapes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditGameScreen(
    game: GameEntity,
    onSaveTap: (GameEntity) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteTap: (Long) -> Unit = {},
    isNewGame: Boolean = false
) {
    var newName by rememberSaveable(game.name) { mutableStateOf(game.name) }
    var newMode by rememberSaveable(game.scoringMode) { mutableStateOf(game.scoringMode) }
    var newColor by rememberSaveable(game.color) { mutableStateOf(game.color) }
    var colorMenuVisible by rememberSaveable { mutableStateOf(false) }
    var saveTapped by rememberSaveable { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val buttonOnClick = {
        if (!saveTapped) {
            keyboardController?.hide()
            onSaveTap(
                game.copy(
                    name = newName,
                    color = newColor,
                    scoringMode = newMode
                )
            )
            saveTapped = true
        }
    }

    Column(modifier = modifier) {
        val gameColor = LocalGameColors.current.getColorByKey(newColor)
        val textSelectionColors = TextSelectionColors(
            handleColor = gameColor,
            backgroundColor = gameColor.copy(0.3f)
        )

        ScreenHeader(
            title = if (!isNewGame) game.name else stringResource(id = R.string.header_new_game),
            color = gameColor
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeadedSection(
                title = R.string.header_details,
                topPadding = 40
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CompositionLocalProvider(
                        LocalTextSelectionColors.provides(textSelectionColors)
                    ) {
                        NameField(
                            initialName = newName,
                            color = gameColor,
                            imeSubmitTapped = buttonOnClick,
                            onNameChanged = { newName = it }
                        )
                    }
                    ScoringModeSelector(
                        initialMode = ScoringMode.getModeByOrdinal(newMode),
                        onItemTap = { newMode = it.ordinal }
                    )
                }
            }
            HeadedSection(title = R.string.header_theme) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (colorMenuVisible) {
                        ColorSelectorOpen(
                            currentColorKey = newColor,
                            colorOptions = LocalGameColors.current.getColorsAsKeyList(),
                            onColorTap = { colorName ->
                                newColor = colorName
                                colorMenuVisible = false
                            }
                        )
                    } else {
                        ColorSelectorClosed(
                            currentColorKey = newColor,
                            onColorSelectorTap = { colorMenuVisible = true }
                        )
                    }
                }
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
                    Icon(imageVector = Icons.Rounded.Done, contentDescription = null)
                }
                if (!isNewGame) {
                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                        onClick = { onDeleteTap(game.id) },
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

@Composable
private fun NameField(
    initialName: String,
    color: Color,
    imeSubmitTapped: () -> Unit,
    onNameChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = initialName,
        label = { Text(text = stringResource(id = R.string.field_name)) },
        onValueChange = { onNameChanged(it) },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = color,
            focusedLabelColor = color,
            cursorColor = color,
            backgroundColor = MaterialTheme.colors.background
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { imeSubmitTapped() }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ScoringModeSelector(
    initialMode: ScoringMode,
    onItemTap: (ScoringMode) -> Unit
) {
    var selectorExpanded by rememberSaveable { mutableStateOf(false) }
    var boxSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                boxSize = layoutCoordinates.size.toSize()
            }
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .clickable { selectorExpanded = true }
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = stringResource(id = initialMode.label),
                    style = MaterialTheme.typography.body1
                )
                Icon(
                    imageVector = if (selectorExpanded) {
                        ImageVector.vectorResource(id = R.drawable.ic_arrow_left)
                    } else Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        ScoreKeeperTheme(shapes = MaterialTheme.shapes.copy(medium = MaterialTheme.shapes.small)) {
            DropdownMenu(
                expanded = selectorExpanded,
                onDismissRequest = { selectorExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { boxSize.width.toDp() })
            ) {
                DropdownMenuItem(
                    onClick = {
                        onItemTap(ScoringMode.Ascending)
                        selectorExpanded = false
                    }
                ) {
                    Text(text = stringResource(id = ScoringMode.Ascending.label))
                }
                DropdownMenuItem(
                    onClick = {
                        onItemTap(ScoringMode.Descending)
                        selectorExpanded = false
                    }
                ) {
                    Text(text = stringResource(id = ScoringMode.Descending.label))
                }
            }
        }
    }
}

@Composable
fun ColorSelectorOpen(
    currentColorKey: String,
    colorOptions: List<String>,
    onColorTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                reverseLayout = true,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .height(64.dp)
                    .weight(1f, fill = false)
            ) {
                items(colorOptions) { colorKey ->
                    val color = LocalGameColors.current.getColorByKey(colorKey)
                    if (colorKey == currentColorKey) {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = color),
                            shape = MaterialTheme.shapes.small,
                            onClick = { onColorTap(colorKey) },
                            modifier = Modifier
                                .padding(12.dp)
                                .size(40.dp)
                        ) {}
                    } else {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = color),
                            shape = MaterialTheme.shapes.small,
                            onClick = { onColorTap(colorKey) },
                            modifier = Modifier.size(64.dp)
                        ) {}
                    }
                }
            }
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_left),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorSelectorClosed(
    currentColorKey: String,
    onColorSelectorTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = { onColorSelectorTap() },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .height(40.dp)
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .clip(MaterialTheme.shapes.small)
                    .background(LocalGameColors.current.getColorByKey(currentColorKey))
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    backgroundColor = 0xFF333333,
    showBackground = true
)
@Composable
fun EditGameScreenPreview() {
    ScoreKeeperTheme {
        EditGameScreen(
            game = EMPTY_GAME_ENTITY,
            onSaveTap = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ColorSelectorPreview() {
    ScoreKeeperTheme {
        ColorSelectorClosed(
            currentColorKey = "DEEP_ORANGE",
            onColorSelectorTap = {}
        )
    }
}