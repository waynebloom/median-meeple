package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.PreviewGameObjects
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.DullColoredTextCard
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.waynebloom.scorekeeper.viewmodel.EditGameViewModel
import com.waynebloom.scorekeeper.viewmodel.EditGameViewModelViewModelFactory
import com.waynebloom.scorekeeper.viewmodel.SubscoreTitleSectionHeaderState
import com.waynebloom.scorekeeper.viewmodel.SubscoreTitleSectionListState
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditGameScreen(
    game: GameObject,
    saveGame: (EntityStateBundle<GameEntity>,
        List<EntityStateBundle<SubscoreTitleEntity>>) -> Unit,
    onDeleteTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<EditGameViewModel>(
        key = ScorekeeperScreen.EditGame.name,
        factory = EditGameViewModelViewModelFactory(
            initialGame = game,
            saveCallback = saveGame
        )
    ).also { it.initialGameEntity.id = game.entity.id }
    val keyboardController = LocalSoftwareKeyboardController.current
    val themeColor = LocalGameColors.current.getColorByKey(viewModel.themeColorString)
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

    CompositionLocalProvider(LocalTextSelectionColors.provides(textSelectionColors)) {

        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {

            EditGameScreenTopBar(
                title = game.entity.name,
                themeColor = themeColor,
                submitButtonEnabled = viewModel.nameIsValid,
                onDoneTap = { viewModel.onSaveTap(keyboardController) },
                onDeleteTap = { onDeleteTap(viewModel.initialGameEntity.id) }
            )

            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                EditGameScreenInfoBox()

                HeadedSection(
                    title = R.string.header_details,
                    topPadding = 32
                ) {

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        NameField(
                            nameTextFieldValue = viewModel.nameTextFieldValue,
                            textFieldColors = textFieldColors,
                            isError = !viewModel.nameIsValid,
                            onNameChanged = { viewModel.setName(it) }
                        )

                        ScoringModeSelector(
                            initialMode = ScoringMode.getModeByOrdinal(viewModel.scoringMode),
                            onItemTap = { viewModel.selectMode(it.ordinal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                SubscoreTitlesSection(
                    viewModel = viewModel,
                    themeColor = themeColor,
                    textFieldColors = textFieldColors
                )

                Spacer(modifier = Modifier.height(40.dp))

                HeadedSection(
                    title = R.string.header_theme,
                    topPadding = 0
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        if (viewModel.colorMenuVisible) {
                            ColorSelectorOpen(
                                currentColorKey = viewModel.themeColorString,
                                colorOptions = LocalGameColors.current.getColorsAsKeyList(),
                                onColorTap = { colorString -> viewModel.selectColor(colorString) }
                            )
                        } else {
                            ColorSelectorClosed(
                                currentColorKey = viewModel.themeColorString,
                                onColorSelectorTap = { viewModel.colorMenuVisible = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditGameScreenTopBar(
    title: String,
    themeColor: Color,
    submitButtonEnabled: Boolean,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
        ) {

            CustomIconButton(
                onTap = onDoneTap,
                enabled = submitButtonEnabled,
                foregroundColor = themeColor,
                imageVector = Icons.Rounded.Done
            )

            CustomIconButton(
                onTap = onDeleteTap,
                foregroundColor = MaterialTheme.colors.error,
                imageVector = Icons.Rounded.Delete
            )
        }
    }
}

@Composable
private fun EditGameScreenInfoBox(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(vertical = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
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
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                modifier = Modifier.padding(top = 2.dp),
            )

            Text(text = stringResource(id = R.string.info_edit_game_screen_helper))
        }
    }
}

@Composable
private fun NameField(
    nameTextFieldValue: TextFieldValue,
    textFieldColors: TextFieldColors,
    isError: Boolean,
    onNameChanged: (TextFieldValue) -> Unit
) {
    OutlinedTextFieldWithErrorDescription(
        textFieldValue = nameTextFieldValue,
        onValueChange = onNameChanged,
        label = { Text(text = stringResource(id = R.string.field_name)) },
        isError = isError,
        colors = textFieldColors,
        errorDescription = R.string.error_empty_name,
        selectAllOnFocus = true
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
                .clip(MaterialTheme.shapes.small)
                .clickable { selectorExpanded = true }
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 16.dp, end = 8.dp)
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
                ScoringMode.values().forEach { scoringMode ->
                    DropdownMenuItem(
                        onClick = {
                            onItemTap(scoringMode)
                            selectorExpanded = false
                        }
                    ) {
                        Text(text = stringResource(id = scoringMode.label))
                    }
                }
            }
        }
    }
}

// region Subscore Titles

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SubscoreTitlesSection(
    viewModel: EditGameViewModel,
    themeColor: Color,
    textFieldColors: TextFieldColors
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AnimatedContent(targetState = viewModel.subscoreTitleSectionHeaderState) { state ->
            when(state) {
                SubscoreTitleSectionHeaderState.TitleAndActionBar -> {
                    SubscoreTitlesDefaultHeader(
                        listState = viewModel.subscoreTitleSectionListState,
                        showListStateButton = viewModel.getSubscoreTitlesToDisplay().isNotEmpty(),
                        themeColor = themeColor,
                        onNewTitleTap = { viewModel.showEditorForNewSubscoreTitle() },
                        onHorizontalListTap = { viewModel.showHorizontalList() },
                        onVerticalListTap = { viewModel.showVerticalList() }
                    )
                }
                SubscoreTitleSectionHeaderState.NewItem -> {
                    SubscoreTitlesEditorHeader(
                        editorFieldTitle = viewModel.getEditorFieldTitle(),
                        titleInput = viewModel.subscoreTitleInput,
                        themeColor = themeColor,
                        textFieldColors = textFieldColors,
                        onTitleChanged = { value -> viewModel.subscoreTitleInput = value},
                        cancelAction = { viewModel.clearEditor() },
                        submitAction = { viewModel.addSubscoreTitle() }
                    )
                }
                SubscoreTitleSectionHeaderState.EditItem -> {
                    SubscoreTitlesEditorHeader(
                        editorFieldTitle = viewModel.getEditorFieldTitle(),
                        titleInput = viewModel.subscoreTitleInput,
                        themeColor = themeColor,
                        textFieldColors = textFieldColors,
                        onTitleChanged = { value -> viewModel.subscoreTitleInput = value},
                        cancelAction = { viewModel.clearEditor() },
                        submitAction = { viewModel.updateCurrentSubscoreTitle() }
                    )
                }
            }
        }

        AnimatedContent(targetState = viewModel.subscoreTitleSectionListState) { state ->
            when(state) {
                SubscoreTitleSectionListState.Horizontal -> {
                    SubscoreTitlesHorizontalList(
                        titles = viewModel.getSubscoreTitlesToDisplay(),
                        onDeleteTap = { index -> viewModel.deleteSubscoreTitle(index) },
                        onTitleTap = { index -> viewModel.showEditorForEditSubscoreTitle(index) }
                    )
                }
                SubscoreTitleSectionListState.Vertical -> {
                    SubscoreTitlesVerticalList(
                        titles = viewModel.getSubscoreTitlesToDisplay(),
                        themeColor = themeColor,
                        onDeleteTap = { index -> viewModel.deleteSubscoreTitle(index) },
                        onTitleTap = { index -> viewModel.showEditorForEditSubscoreTitle(index) },
                        onUpTap = { index -> viewModel.changePosition(index, index - 1) },
                        onDownTap = { index -> viewModel.changePosition(index, index + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscoreTitlesEditorHeader(
    editorFieldTitle: Int,
    titleInput: String,
    themeColor: Color,
    textFieldColors: TextFieldColors,
    onTitleChanged: (String) -> Unit,
    submitAction: () -> Unit,
    cancelAction: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = titleInput,
            onValueChange = { onTitleChanged(it) },
            label = { Text(stringResource(editorFieldTitle)) },
            maxLines = 3,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { submitAction() }
            ),
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 12.dp)
                .background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
        ) {

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { submitAction() }
            ) {

                Icon(
                    imageVector = Icons.Rounded.Done,
                    tint = themeColor,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { cancelAction() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    tint = MaterialTheme.colors.error,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SubscoreTitlesDefaultHeader(
    listState: SubscoreTitleSectionListState,
    showListStateButton: Boolean,
    themeColor: Color,
    onNewTitleTap: () -> Unit,
    onHorizontalListTap: () -> Unit,
    onVerticalListTap: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.field_categories)
                .uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
        ) {

            if (showListStateButton) {
                AnimatedContent(
                    targetState = listState,
                    transitionSpec = {
                        fadeIn() with fadeOut()
                    }
                ) { state ->
                    when(state) {
                        SubscoreTitleSectionListState.Horizontal -> {
                            CustomIconButton(
                                onTap = onVerticalListTap,
                                foregroundColor = themeColor,
                                painter = painterResource(id = R.drawable.vertical_list)
                            )
                        }
                        SubscoreTitleSectionListState.Vertical -> {
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { onHorizontalListTap() }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.horizontal_list),
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
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onNewTitleTap() }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SubscoreTitlesHorizontalList(
    titles: List<SubscoreTitleEntity>,
    onDeleteTap: (Int) -> Unit,
    onTitleTap: (Int) -> Unit
) {
    if (titles.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            itemsIndexed(titles) { index, subscoreTitle ->

                Chip(
                    onClick = { onTitleTap(index) },
                    shape = MaterialTheme.shapes.small,
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.surface
                    ),
                    modifier = Modifier.height(48.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.widthIn(max = 256.dp)
                    ) {

                        Text(
                            text = subscoreTitle.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.body1
                        )

                        Icon(
                            imageVector = Icons.Rounded.Close,
                            tint = MaterialTheme.colors.error,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onDeleteTap(index) }
                        )
                    }
                }
            }
        }
    } else ScoringCategoriesSectionEmptyInfoBox()
}

@Composable
private fun ScoringCategoriesSectionEmptyInfoBox(modifier: Modifier = Modifier) {
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

            Text(text = stringResource(id = R.string.info_categories_section_helper))
        }
    }
}

@Composable
private fun SubscoreTitlesVerticalList(
    titles: List<SubscoreTitleEntity>,
    themeColor: Color,
    onDeleteTap: (Int) -> Unit,
    onTitleTap: (Int) -> Unit,
    onUpTap: (Int) -> Unit,
    onDownTap: (Int) -> Unit
) {
    if (titles.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            titles.forEachIndexed { index, subscoreTitle ->

                Surface(
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onTitleTap(index) }
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {

                        Text(
                            text = subscoreTitle.title,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )

                        if (index > 0) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowUp,
                                tint = themeColor,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onUpTap(index) }
                            )
                        } else Box(modifier = Modifier.size(24.dp))

                        if (index < titles.size - 1) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                tint = themeColor,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onDownTap(index) }
                            )
                        } else Box(modifier = Modifier.size(24.dp))

                        Icon(
                            imageVector = Icons.Rounded.Close,
                            tint = MaterialTheme.colors.error,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onDeleteTap(index) }
                        )
                    }
                }
            }
        }
    } else ScoringCategoriesSectionEmptyInfoBox()
}

// endregion

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
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
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
            game = PreviewGameObjects[0],
            saveGame = { _, _ -> },
            onDeleteTap = {}
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

@Preview
@Composable
fun SubscoreTitleSectionPreview() {
    ScoreKeeperTheme {
        /*SubscoreTitlesSection(
            titles = PreviewSubscoreTitleEntities.map { it.title },
            themeColor = deepOrange100
        )*/
    }
}