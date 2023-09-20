package com.waynebloom.scorekeeper.ui.editGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.components.CustomIconButton
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.ui.components.RadioButtonOption
import com.waynebloom.scorekeeper.ui.editGame.EditGameViewModel.EditGameUiState
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.CustomGameTheme
import com.waynebloom.scorekeeper.viewmodel.CategorySectionState
import java.util.*

@Composable
fun EditGameScreen(
    uiState: EditGameUiState,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onCategoryInputChanged: (TextFieldValue) -> Unit,
    onCategoryInputFocusChanged: (FocusState) -> Unit,
    onColorClick: (String) -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
    onDeleteClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onEditModeClick: () -> Unit,
    onNameChanged: (TextFieldValue) -> Unit,
    onNewCategoryClick: () -> Unit,
    onScoringModeChanged: (ScoringMode) -> Unit,
) {

    when(uiState) {
        is EditGameUiState.Loading -> Loading()
        is EditGameUiState.Content -> {

            CustomGameTheme(gameColor = uiState.getResolvedColor()) {

                EditGameScreen(
                    categories = uiState.displayedCategories,
                    categoryTextFieldInput = uiState.categoryInput,
                    categoryInputTitle = uiState.categoryInputTitle,
                    categorySectionState = uiState.categorySectionDisplayState,
                    colorOptions = LocalCustomThemeColors.current.getColorsAsKeyList(),
                    lazyListState = uiState.lazyListState,
                    modifier = modifier,
                    nameInput = uiState.nameInput,
                    scoringMode = uiState.scoringMode,
                    selectedColorId = uiState.color,
                    showCategoryInput = uiState.showCategoryInput,
                    onCategoryClick = onCategoryClick,
                    onCategoryInputChanged = onCategoryInputChanged,
                    onCategoryInputFocusChanged = onCategoryInputFocusChanged,
                    onColorClick = onColorClick,
                    onDeleteCategoryClick = onDeleteCategoryClick,
                    onDeleteClick = onDeleteClick,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragStart = onDragStart,
                    onEditModeClick = onEditModeClick,
                    onNameChanged = onNameChanged,
                    onNewCategoryClick = onNewCategoryClick,
                    onScoringModeChanged = onScoringModeChanged
                )
            }
        }
    }
}

@Composable
fun EditGameScreen(
    categories: List<CategoryUiModel>,
    categoryTextFieldInput: TextFieldInput,
    categoryInputTitle: String,
    categorySectionState: CategorySectionState,
    colorOptions: List<String>,
    lazyListState: LazyListState,
    nameInput: TextFieldInput,
    scoringMode: ScoringMode,
    selectedColorId: String,
    showCategoryInput: Boolean,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onCategoryInputChanged: (TextFieldValue) -> Unit,
    onCategoryInputFocusChanged: (FocusState) -> Unit,
    onColorClick: (String) -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
    onDeleteClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onEditModeClick: () -> Unit,
    onNameChanged: (TextFieldValue) -> Unit,
    onNewCategoryClick: () -> Unit,
    onScoringModeChanged: (ScoringMode) -> Unit,
) {

    Scaffold(
        topBar = {
            EditGameScreenTopBar(
                title = nameInput.value.text,
                onDeleteTap = { onDeleteClick() }
            )
        }
    ) {

        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
            contentPadding = PaddingValues(vertical = Spacing.betweenSections),
            modifier = modifier.padding(it)
        ) {

            item {

                GameDetailsSection(
                    selectedMode = scoringMode,
                    nameTextFieldValue = nameInput.value,
                    isNameValid = nameInput.isValid,
                    onNameChanged = onNameChanged,
                    onScoringModeTap = onScoringModeChanged,
                    modifier = Modifier.padding(horizontal = Spacing.screenEdge),
                )
            }

            item {

                ScoringCategorySection(
                    categories = categories,
                    inputTitle = categoryInputTitle,
                    categoryTextFieldInput = categoryTextFieldInput,
                    showInput = showCategoryInput,
                    state = categorySectionState,
                    onCategoryClick = onCategoryClick,
                    onCategoryInputFocusChanged = onCategoryInputFocusChanged,
                    onDeleteCategoryClick = onDeleteCategoryClick,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragStart = onDragStart,
                    onEditModeClick = onEditModeClick,
                    onInputChanged = onCategoryInputChanged,
                    onNewClick = onNewCategoryClick,
                    modifier = Modifier.padding(horizontal = Spacing.screenEdge),
                )
            }

            item {

                CustomThemeSection(
                    selectedColor = selectedColorId,
                    colorOptions = colorOptions,
                    onColorClick = onColorClick
                )

                Spacer(modifier = Modifier.height(Spacing.screenEdge))
            }
        }
    }
}

@Composable
private fun EditGameScreenTopBar(
    title: String,
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
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            CustomIconButton(
                onTap = onDeleteTap,
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.error,
                imageVector = Icons.Rounded.Delete
            )
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
            isError = !isNameValid,
            onNameChanged = onNameChanged
        )

        ScoringModeSelector(
            selectedMode = selectedMode,
            onItemTap = { onScoringModeTap(it) }
        )
    }
}

@Composable
private fun NameField(
    nameTextFieldValue: TextFieldValue,
    isError: Boolean,
    onNameChanged: (TextFieldValue) -> Unit
) {

    OutlinedTextFieldWithErrorDescription(
        textFieldValue = nameTextFieldValue,
        onValueChange = onNameChanged,
        label = { Text(text = stringResource(id = R.string.field_name)) },
        isError = isError,
        errorDescription = R.string.error_empty_name,
        selectAllOnFocus = true
    )
}

@Composable
fun ScoringModeSelector(
    selectedMode: ScoringMode,
    onItemTap: (ScoringMode) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier.fillMaxWidth()) {

        ScoringMode.values().forEach { option ->

            RadioButtonOption(
                menuOption = option,
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
    categories: List<CategoryUiModel>,
    categoryTextFieldInput: TextFieldInput,
    inputTitle: String,
    showInput: Boolean,
    state: CategorySectionState,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onCategoryInputFocusChanged: (FocusState) -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onEditModeClick: () -> Unit,
    onInputChanged: (TextFieldValue) -> Unit,
    onNewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier) {

        ScoringCategoryHeader(
            isInEditMode = state == CategorySectionState.EditMode,
            showEditModeButton = categories.isNotEmpty(),
            onEditModeClick = onEditModeClick,
            onNewCategoryClick = onNewClick
        )

        Spacer(modifier = Modifier.height(Spacing.sectionContent))

        AnimatedContent(
            targetState = state,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
            label = EditGameConstants.AnimationLabel.CategorySection
        ) {

            when(it) {
                CategorySectionState.Default -> {
                    ScoringCategoryList(
                        categories = categories,
                        onCategoryClick = onCategoryClick,
                    )
                }
                CategorySectionState.EditMode -> {
                    ScoringCategoryListEditMode(
                        categories = categories,
                        onDeleteCategoryClick = onDeleteCategoryClick,
                        onCategoryClick = onCategoryClick,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        onDragStart = onDragStart,
                    )
                }
                CategorySectionState.Empty -> {
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
            label = EditGameConstants.AnimationLabel.CategorySectionInput
        ) { visible ->

            if (visible) {
                
                Spacer(Modifier.height(Spacing.sectionContent))

                OutlinedTextFieldWithErrorDescription(
                    textFieldValue = categoryTextFieldInput.value,
                    onValueChange = { onInputChanged(it) },
                    label = { Text(text = inputTitle) },
                    isError = !categoryTextFieldInput.isValid && !categoryTextFieldInput.hasReceivedInput,
                    errorDescription = R.string.field_error_empty,
                    selectAllOnFocus = true,
                    modifier = Modifier.onFocusChanged(onCategoryInputFocusChanged)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoringCategoryHeader(
    isInEditMode: Boolean,
    showEditModeButton: Boolean,
    onEditModeClick: () -> Unit,
    onNewCategoryClick: () -> Unit,
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
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
                label = EditGameConstants.AnimationLabel.EditModeButton
            ) { showEditModeButton ->

                if (showEditModeButton) {

                    AnimatedContent(
                        targetState = isInEditMode,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = EditGameConstants.AnimationLabel.CategorySectionHeaderState
                    ) { isInEditMode ->

                        if (isInEditMode) {
                            CustomIconButton(
                                painter = painterResource(id = R.drawable.ic_edit_off),
                                foregroundColor = MaterialTheme.colors.primary,
                                onTap = onEditModeClick
                            )
                        } else {
                            CustomIconButton(
                                painter = painterResource(id = R.drawable.ic_edit),
                                foregroundColor = MaterialTheme.colors.primary,
                                onTap = onEditModeClick
                            )
                        }
                    }
                }
            }

            CustomIconButton(
                imageVector = Icons.Rounded.Add,
                foregroundColor = MaterialTheme.colors.primary,
                onTap = onNewCategoryClick
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
private fun ScoringCategoryList(
    categories: List<CategoryUiModel>,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryUiModel) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
        modifier = modifier.fillMaxWidth()
    ) {

        categories.forEach {

            Chip(
                onClick = { onCategoryClick(it) },
                shape = MaterialTheme.shapes.small,
                content = { Text(text = it.name.value.text) },
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
    categories: List<CategoryUiModel>,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
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
                        .clickable { onCategoryClick(category) }
                        .padding(horizontal = Spacing.sectionContent),
                    content = {
                        Text(
                            text = category.name.value.text,
                            style = MaterialTheme.typography.body1,
                        )
                    }
                )

                CustomIconButton(
                    imageVector = Icons.Rounded.Delete,
                    backgroundColor = Color.Transparent,
                    foregroundColor = MaterialTheme.colors.error,
                    onTap = { onDeleteCategoryClick(category) },
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
    onColorClick: (String) -> Unit,
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
            onColorTap = onColorClick
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

            val color = LocalCustomThemeColors.current.getColorByKey(key)
            
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

    EditGameScreen(
        uiState = EditGameSampleData.UiState,
        onCategoryClick = {},
        onCategoryInputChanged = {},
        onCategoryInputFocusChanged = {},
        onColorClick = {},
        onDeleteCategoryClick = {},
        onDeleteClick = {},
        onDrag = {},
        onDragEnd = {},
        onDragStart = {},
        onEditModeClick = {},
        onNameChanged = {},
        onNewCategoryClick = {},
        onScoringModeChanged = {}
    )
}
