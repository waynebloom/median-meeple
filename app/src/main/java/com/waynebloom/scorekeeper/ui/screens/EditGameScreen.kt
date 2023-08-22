package com.waynebloom.scorekeeper.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.CustomIconButton
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.data.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ui.LocalGameColors
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.viewmodel.EditGameViewModel
import com.waynebloom.scorekeeper.viewmodel.EditGameViewModelFactory
import com.waynebloom.scorekeeper.viewmodel.ScoringCategorySectionState
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditGameScreen(
    game: GameObject,
    saveGame: (EntityStateBundle<GameEntity>,
        List<EntityStateBundle<CategoryTitleEntity>>) -> Unit,
    onDeleteTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<EditGameViewModel>(
        key = TopLevelScreen.EditGame.name,
        factory = EditGameViewModelFactory(
            initialGame = game,
            saveCallback = saveGame
        ))
    .onRecompose(
        gameId = game.entity.id,
        rowHeightInPx = LocalDensity.current
            .run { Size.minTappableSize.toPx() })

    val keyboardController = LocalSoftwareKeyboardController.current
    val themeColor = LocalGameColors.current.getColorByKey(viewModel.themeColorString)
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = themeColor,
        focusedLabelColor = themeColor,
        cursorColor = themeColor,
    )
    val textSelectionColors = TextSelectionColors(
        handleColor = themeColor,
        backgroundColor = themeColor.copy(Alpha.textSelectionBackground),
    )

    Scaffold(
        topBar = {
            EditGameScreenTopBar(
                title = game.entity.name,
                themeColor = themeColor,
                submitButtonEnabled = viewModel.isNameValid,
                onDoneTap = { viewModel.onSaveTap(keyboardController) },
                onDeleteTap = { onDeleteTap(viewModel.initialGameEntity.id) }
            )
        }
    ) {

        CompositionLocalProvider(LocalTextSelectionColors.provides(textSelectionColors)) {
            val coroutineScope = rememberCoroutineScope()

            LazyColumn(
                state = viewModel.lazyListState,
                verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
                contentPadding = PaddingValues(vertical = Spacing.betweenSections),
                modifier = modifier.padding(it)
            ) {

                item {

                    GameDetailsSection(
                        selectedMode = viewModel.scoringMode,
                        nameTextFieldValue = viewModel.name,
                        textFieldColors = textFieldColors,
                        isNameValid = viewModel.isNameValid,
                        themeColor = themeColor,
                        onNameChanged = { viewModel.onNameChanged(it) },
                        onScoringModeTap = { viewModel.onScoringModeChanged(it) },
                        modifier = Modifier.padding(horizontal = Spacing.screenEdge),
                    )
                }

                item {

                    ScoringCategorySection(
                        categories = viewModel.getCategoriesToDisplay(),
                        state = viewModel.getCategoryListState(),
                        showInput = viewModel.showCategoryInput,
                        inputFieldTitleResource = viewModel.getInputFieldTitle(),
                        currentInput = viewModel.categoryInput,
                        showInputError = !viewModel.isCategoryInputValid && !viewModel.isFreshInput,
                        textFieldColors = textFieldColors,
                        themeColor = themeColor,
                        toggleEditMode = { viewModel.toggleEditMode(it, coroutineScope) },
                        onNewTitleTap = { viewModel.showInputForNewCategory(coroutineScope) },
                        onTitleTap = { viewModel.showInputForExistingCategory(
                            index = it,
                            isTransitioningToEditMode = false,
                            coroutineScope = coroutineScope,
                        ) },
                        onDeleteTap = { viewModel.deleteCategory(it) },
                        onDrag = { viewModel.onDrag(it) },
                        onDragEnd = { viewModel.onDragEnd() },
                        onDragStart = { viewModel.onDragStart(it) },
                        onInputCancel = { viewModel.clearAndHideCategoryInput() },
                        onInputChange = { viewModel.onCategoryInputChanged(it) },
                        onInputSubmit = { viewModel.submitCategoryInput() },
                        modifier = Modifier.padding(horizontal = Spacing.screenEdge),
                    )
                }

                item {

                    CustomThemeSection(
                        selectedColor = viewModel.themeColorString,
                        colorOptions = LocalGameColors.current.getColorsAsKeyList(),
                        onColorTap = { colorString -> viewModel.selectColor(colorString) }
                    )

                    Spacer(modifier = Modifier.height(Spacing.screenEdge))
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

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = Spacing.screenEdge, end = 8.dp)
                .defaultMinSize(minHeight = Size.topBarHeight)
                .fillMaxWidth()
        ) {

            Text(
                text = title,
                color = themeColor,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                CustomIconButton(
                    onTap = onDoneTap,
                    backgroundColor = Color.Transparent,
                    foregroundColor = themeColor,
                    enabled = submitButtonEnabled,
                    imageVector = Icons.Rounded.Done
                )

                CustomIconButton(
                    onTap = onDeleteTap,
                    backgroundColor = Color.Transparent,
                    foregroundColor = MaterialTheme.colors.error,
                    imageVector = Icons.Rounded.Delete
                )
            }
        }

        Divider()
    }
}

// region Game Details

@Composable
private fun GameDetailsSection(
    selectedMode: ScoringMode,
    nameTextFieldValue: TextFieldValue,
    isNameValid: Boolean,
    textFieldColors: TextFieldColors,
    themeColor: Color,
    onNameChanged: (TextFieldValue) -> Unit,
    onScoringModeTap: (ScoringMode) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = modifier,
    ) {

        Text(
            text = stringResource(id = R.string.header_game_details),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        NameField(
            nameTextFieldValue = nameTextFieldValue,
            textFieldColors = textFieldColors,
            isError = !isNameValid,
            onNameChanged = onNameChanged
        )

        ScoringModeSelector(
            selectedMode = selectedMode,
            themeColor = themeColor,
            onItemTap = { onScoringModeTap(it) }
        )
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
fun ScoringModeSelector(
    selectedMode: ScoringMode,
    themeColor: Color,
    onItemTap: (ScoringMode) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier.fillMaxWidth()) {

        ScoringMode.values().forEach { option ->

            RadioButtonOption(
                menuOption = option,
                themeColor = themeColor,
                isSelected = selectedMode == option,
                onSelected = { onItemTap(it as ScoringMode) }
            )
        }
    }
}

// endregion

// region Categories

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoringCategorySection(
    categories: List<String>,
    state: ScoringCategorySectionState,
    showInput: Boolean,
    @StringRes inputFieldTitleResource: Int,
    currentInput: TextFieldValue,
    showInputError: Boolean,
    textFieldColors: TextFieldColors,
    themeColor: Color,
    toggleEditMode: (Int?) -> Unit,
    onNewTitleTap: () -> Unit,
    onTitleTap: (Int) -> Unit,
    onDeleteTap: (Int) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onInputCancel: () -> Unit,
    onInputChange: (TextFieldValue) -> Unit,
    onInputSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier) {

        ScoringCategoryHeader(
            isInEditMode = state == ScoringCategorySectionState.EditMode,
            showEditModeButton = categories.isNotEmpty(),
            themeColor = themeColor,
            onNewTitleTap = onNewTitleTap,
            toggleEditMode = { toggleEditMode(null) },
        )

        Spacer(modifier = Modifier.height(Spacing.sectionContent))

        AnimatedContent(
            targetState = state,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
        ) {

            when(it) {
                ScoringCategorySectionState.Default -> {
                    ScoringCategoryList(
                        categories = categories,
                        onCategoryTap = toggleEditMode,
                    )
                }
                ScoringCategorySectionState.EditMode -> {
                    ScoringCategoryListEditMode(
                        categories = categories,
                        onDeleteTap = onDeleteTap,
                        onTitleTap = onTitleTap,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        onDragStart = onDragStart,
                    )
                }
                ScoringCategorySectionState.Empty -> {
                    HelperBox(
                        message = stringResource(id = R.string.info_categories_section_helper),
                        type = HelperBoxType.Info
                    )
                }
            }
        }

        AnimatedContent(
            targetState = showInput,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
        ) {
            if (it) {

                ScoringCategoryInput(
                    fieldTitleResource = inputFieldTitleResource,
                    currentInput = currentInput,
                    showInputError = showInputError,
                    textFieldColors = textFieldColors,
                    themeColor = themeColor,
                    onCancel = onInputCancel,
                    onInputChange = onInputChange,
                    onSubmit = onInputSubmit
                )
            }
        }
    }
}

@Composable
private fun ScoringCategoryInput(
    @StringRes fieldTitleResource: Int,
    currentInput: TextFieldValue,
    showInputError: Boolean,
    textFieldColors: TextFieldColors,
    themeColor: Color,
    onCancel: () -> Unit,
    onInputChange: (TextFieldValue) -> Unit,
    onSubmit: () -> Unit,
) {

    Column {

        Spacer(modifier = Modifier.height(Spacing.sectionContent))

        OutlinedTextFieldWithErrorDescription(
            textFieldValue = currentInput,
            onValueChange = { onInputChange(it) },
            label = { Text(stringResource(fieldTitleResource)) },
            isError = showInputError,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit() }
            ),
            errorDescription = R.string.field_error_empty,
            selectAllOnFocus = true
        )

        Spacer(modifier = Modifier.height(Spacing.sectionContent))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.surface)
        ) {

            CustomIconButton(
                imageVector = Icons.Rounded.Check,
                foregroundColor = themeColor,
                onTap = onSubmit
            )

            CustomIconButton(
                imageVector = Icons.Rounded.Close,
                foregroundColor = themeColor,
                onTap = onCancel
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoringCategoryHeader(
    isInEditMode: Boolean,
    showEditModeButton: Boolean,
    themeColor: Color,
    onNewTitleTap: () -> Unit,
    toggleEditMode: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.field_categories),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.medium)
        ) {

            AnimatedContent(
                targetState = showEditModeButton,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay }
            ) { showEditModeButton ->

                if (showEditModeButton) {

                    AnimatedContent(
                        targetState = isInEditMode,
                        transitionSpec = { fadeIn() with fadeOut() }
                    ) { isInEditMode ->

                        if (isInEditMode) {
                            CustomIconButton(
                                painter = painterResource(id = R.drawable.ic_edit_off),
                                foregroundColor = themeColor,
                                onTap = toggleEditMode
                            )
                        } else {
                            CustomIconButton(
                                painter = painterResource(id = R.drawable.ic_edit),
                                foregroundColor = themeColor,
                                onTap = toggleEditMode
                            )
                        }
                    }
                }
            }

            CustomIconButton(
                imageVector = Icons.Rounded.Add,
                foregroundColor = themeColor,
                onTap = onNewTitleTap
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun ScoringCategoryList(
    categories: List<String>,
    modifier: Modifier = Modifier,
    onCategoryTap: (Int) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
        modifier = modifier.fillMaxWidth()
    ) {

        categories.forEachIndexed { index, category ->

            Chip(
                onClick = { onCategoryTap(index) },
                shape = MaterialTheme.shapes.small,
                content = { Text(text = category) },
                border = BorderStroke(1.dp, MaterialTheme.colors.onBackground.copy(alpha = Alpha.disabled)),
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.onBackground,
                ),
            )
        }
    }
}

@Composable
private fun ScoringCategoryListEditMode(
    categories: List<String>,
    onDeleteTap: (Int) -> Unit,
    onTitleTap: (Int) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = Modifier.defaultMinSize(minHeight = Size.minTappableSize)
    ) {

        categories.forEachIndexed { index, category ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(Size.minTappableSize)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_drag_handle),
                    contentDescription = null,
                    modifier = Modifier
                        .size(Size.minTappableSize)
                        .padding(12.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { onDragStart(index) },
                                onDragEnd = onDragEnd,
                                onDrag = { _, dragAmount -> onDrag(dragAmount) }
                            )
                        },
                )

                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onTitleTap(index) }
                        .padding(horizontal = Spacing.sectionContent),
                    content = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.body1,
                        )
                    }
                )

                CustomIconButton(
                    imageVector = Icons.Rounded.Delete,
                    backgroundColor = Color.Transparent,
                    foregroundColor = MaterialTheme.colors.error,
                    onTap = { onDeleteTap(index) },
                )
            }
        }
    }
}

// endregion

@Composable
fun CustomThemeSection(
    colorOptions: List<String>,
    selectedColor: String,
    onColorTap: (String) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_custom_theme),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = Spacing.screenEdge)
        )

        ColorSelector(
            colorOptions = colorOptions,
            selectedColor = selectedColor,
            onColorTap = onColorTap
        )
    }
}

@Composable
fun ColorSelector(
    colorOptions: List<String>,
    selectedColor: String,
    onColorTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(colorOptions.indexOf(selectedColor))

    LazyRow(
        state = lazyListState,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        contentPadding = PaddingValues(horizontal = Spacing.screenEdge),
        modifier = modifier,
    ) {

        items(items = colorOptions) { key ->

            val color = LocalGameColors.current.getColorByKey(key)
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color)
                    .clickable { onColorTap(key) }
            ) {

                AnimatedVisibility(
                    visible = key == selectedColor,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colors.background,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun EditGameScreenPreview() {
    MedianMeepleTheme {
        Scaffold {
            it
            EditGameScreen(
                game = GameObjectsDefaultPreview[0],
                saveGame = { _, _ -> },
                onDeleteTap = {}
            )
        }
    }
}
