package com.waynebloom.scorekeeper.ui.editGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.components.IconButton
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.components.MedianMeepleFab
import com.waynebloom.scorekeeper.ui.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.ui.components.RadioButtonOption
import com.waynebloom.scorekeeper.ui.editGame.EditGameViewModel.EditGameUiState
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.theme.UserSelectedPrimaryColorTheme
import java.util.*

@Composable
fun EditGameScreen(
    uiState: EditGameUiState,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onCategoryDialogDismiss: () -> Unit,
    onCategoryInputChanged: (CategoryUiModel, TextFieldValue) -> Unit,
    onColorClick: (String) -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
    onDeleteClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onEditButtonClick: () -> Unit,
    onHideCategoryInputField: () -> Unit,
    onNameChanged: (TextFieldValue) -> Unit,
    onNewCategoryClick: () -> Unit,
    onScoringModeChanged: (ScoringMode) -> Unit,
) {

    when(uiState) {
        is EditGameUiState.Loading -> Loading()
        is EditGameUiState.Content -> {

            UserSelectedPrimaryColorTheme(primaryColor = uiState.getResolvedColor()) {

                EditGameScreen(
                    nameInput = uiState.nameInput,
                    scoringMode = uiState.scoringMode,
                    categories = uiState.displayedCategories,
                    indexOfCategoryReceivingInput = uiState.indexOfCategoryReceivingInput,
                    indexOfSelectedCategory = uiState.indexOfSelectedCategory,
                    isCategoryDialogOpen = uiState.isCategoryDialogOpen,
                    colorOptions = LocalCustomThemeColors.current.getColorsAsKeyList(),
                    selectedColorId = uiState.color,
                    modifier = modifier,
                    onCategoryClick = onCategoryClick,
                    onCategoryDialogDismiss = onCategoryDialogDismiss,
                    onCategoryInputChanged = onCategoryInputChanged,
                    onColorClick = onColorClick,
                    onDeleteCategoryClick = onDeleteCategoryClick,
                    onDeleteClick = onDeleteClick,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragStart = onDragStart,
                    onEditButtonClick = onEditButtonClick,
                    onHideCategoryInputField = onHideCategoryInputField,
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
    nameInput: TextFieldInput,
    scoringMode: ScoringMode,
    categories: List<CategoryUiModel>,
    indexOfCategoryReceivingInput: Int?,
    indexOfSelectedCategory: Int?,
    isCategoryDialogOpen: Boolean,
    colorOptions: List<String>,
    selectedColorId: String,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onCategoryDialogDismiss: () -> Unit,
    onCategoryInputChanged: (CategoryUiModel, TextFieldValue) -> Unit,
    onColorClick: (String) -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
    onDeleteClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onEditButtonClick: () -> Unit,
    onHideCategoryInputField: () -> Unit,
    onNameChanged: (TextFieldValue) -> Unit,
    onNewCategoryClick: () -> Unit,
    onScoringModeChanged: (ScoringMode) -> Unit,
) {

    Box {

        if (isCategoryDialogOpen) {
            BackHandler {
                onCategoryDialogDismiss()
            }
        }

        Scaffold(
            topBar = {
                EditGameScreenTopBar(
                    title = nameInput.value.text,
                    onDeleteTap = { onDeleteClick() }
                )
            }
        ) {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
                contentPadding = PaddingValues(
                    bottom = Spacing.screenEdge,
                    top = Spacing.betweenSections
                ),
                modifier = modifier.padding(it)
            ) {

                item {

                    GameDetailsSection(
                        selectedMode = scoringMode,
                        nameTextFieldValue = nameInput.value,
                        isNameValid = nameInput.isValid,
                        onNameChanged = onNameChanged,
                        onScoringModeTap = onScoringModeChanged,
                        modifier = Modifier.padding(horizontal = Spacing.screenEdge)
                    )
                }

                item {

                    Column(modifier = modifier) {

                        ScoringCategoryHeader(
                            onEditButtonClick = onEditButtonClick,
                            modifier = Modifier.padding(horizontal = Spacing.screenEdge)
                        )

                        Spacer(modifier = Modifier.height(Spacing.sectionContent))

                        ScoringCategoryList(
                            categories = categories,
                            onCategoryClick = onCategoryClick,
                            modifier = Modifier.padding(horizontal = Spacing.screenEdge)
                        )
                    }
                }

                item {

                    CustomThemeSection(
                        selectedColor = selectedColorId,
                        colorOptions = colorOptions,
                        onColorClick = onColorClick
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isCategoryDialogOpen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOutCubic
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOutCubic
                )
            ) + fadeOut(),
        ) {

            EditCategoriesFullScreenDialog(
                categories = categories,
                indexOfCategoryReceivingInput = indexOfCategoryReceivingInput,
                indexOfSelectedCategory = indexOfSelectedCategory,
                onCategoryClick = onCategoryClick,
                onCloseClick = onCategoryDialogDismiss,
                onDeleteCategoryClick = onDeleteCategoryClick,
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDragStart = onDragStart,
                onHideInputField = onHideCategoryInputField,
                onInputChanged = onCategoryInputChanged,
                onNewClick = onNewCategoryClick
            )
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

            IconButton(
                onClick = onDeleteTap,
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
        value = nameTextFieldValue,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EditCategoriesFullScreenDialog(
    categories: List<CategoryUiModel>,
    indexOfCategoryReceivingInput: Int?,
    indexOfSelectedCategory: Int?,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onCloseClick: () -> Unit,
    onDeleteCategoryClick: (CategoryUiModel) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onHideInputField: () -> Unit,
    onInputChanged: (CategoryUiModel, TextFieldValue) -> Unit,
    onNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(Size.topBarHeight)
                ) {

                    IconButton(
                        onClick = onCloseClick,
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.a11y_categories_close_button),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.header_edit_categories),
                        style = MaterialTheme.typography.h5.copy(
                            fontSize = 22.sp
                        )
                    )
                }

                Divider()
            }
        },
        floatingActionButton = { MedianMeepleFab(onClick = onNewClick) }
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
            contentPadding = PaddingValues(
                bottom = Spacing.paddingForFab,
                end = Spacing.screenEdge,
                start = Spacing.screenEdge,
                top = Spacing.sectionContent),
            modifier = modifier.padding(it)
        ) {

            itemsIndexed(
                key = { index, _ -> index },
                items = categories,
            ) { index, category ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(Size.minTappableSize)
                ) {

                    AnimatedContent(
                        targetState = indexOfCategoryReceivingInput,
                        transitionSpec = { scaleIn() togetherWith scaleOut() },
                        label = EditGameConstants.AnimationLabel.CategoryIcon,
                    ) { animationState ->

                        when (animationState) {
                            index -> {
                                IconButton(
                                    painter = painterResource(id = R.drawable.ic_checkmark),
                                    backgroundColor = Color.Transparent,
                                    foregroundColor = MaterialTheme.colors.onBackground,
                                    onClick = onHideInputField
                                )
                            }
                            null -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_drag_handle),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .size(24.dp)
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragStart = { onDragStart(index) },
                                                onDragEnd = onDragEnd,
                                                onDrag = { _, dragAmount -> onDrag(dragAmount) }
                                            )
                                        },
                                )
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(Size.minTappableSize)
                                        .padding(20.dp)
                                        .background(
                                            color = MaterialTheme.colors.onBackground,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }

                    if (index == indexOfCategoryReceivingInput) {

                        val focusRequester = remember { FocusRequester() }

                        LaunchedEffect(index) {
                            focusRequester.requestFocus()
                        }

                        OutlinedTextFieldWithErrorDescription(
                            value = category.name.value,
                            onValueChange = { value -> onInputChanged(category, value) },
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(horizontal = Spacing.sectionContent)
                                .focusRequester(focusRequester),
                            isError = !category.name.isValid,
                            errorDescription = R.string.field_error_empty,
                            keyboardActions = KeyboardActions { onHideInputField() },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            selectAllOnFocus = true,
                            contentPadding = PaddingValues(Spacing.sectionContent)
                        )
                    } else {

                        val bringIntoViewRequester = remember { BringIntoViewRequester() }

                        LaunchedEffect(index) {
                            if (index == indexOfSelectedCategory) {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }

                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(shape = MaterialTheme.shapes.medium)
                                .clickable { onCategoryClick(category) }
                                .padding(horizontal = Spacing.sectionContent)
                                .bringIntoViewRequester(bringIntoViewRequester)
                        ) {

                            Text(
                                text = category.name.value.text,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }

                    IconButton(
                        imageVector = Icons.Rounded.Delete,
                        backgroundColor = Color.Transparent,
                        foregroundColor = MaterialTheme.colors.error,
                        onClick = { onDeleteCategoryClick(category) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoringCategoryHeader(
    onEditButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.header_scoring_categories),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        IconButton(
            painter = painterResource(id = R.drawable.ic_edit),
            foregroundColor = MaterialTheme.colors.primary,
            onClick = onEditButtonClick
        )
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
fun EditGameScreenDefaultPreview() {

    EditGameScreen(
        uiState = EditGameSampleData.Default,
        onCategoryClick = {},
        onCategoryDialogDismiss = {},
        onCategoryInputChanged = {_,_ ->},
        onColorClick = {},
        onDeleteCategoryClick = {},
        onDeleteClick = {},
        onDrag = {},
        onDragEnd = {},
        onDragStart = {},
        onEditButtonClick = {},
        onHideCategoryInputField = {},
        onNameChanged = {},
        onNewCategoryClick = {},
        onScoringModeChanged = {}
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun EditGameScreenEditCategoriesPreview() {

    var uiState = EditGameSampleData.CategoryDialog

    UserSelectedPrimaryColorTheme(primaryColor = uiState.getResolvedColor()) {

        EditGameScreen(
            nameInput = uiState.nameInput,
            scoringMode = uiState.scoringMode,
            categories = uiState.displayedCategories,
            indexOfCategoryReceivingInput = uiState.indexOfCategoryReceivingInput,
            indexOfSelectedCategory = uiState.indexOfSelectedCategory,
            isCategoryDialogOpen = uiState.isCategoryDialogOpen,
            colorOptions = listOf(),
            selectedColorId = uiState.color,
            onCategoryClick = { uiState = uiState.copy(indexOfCategoryReceivingInput = it.position) },
            onCategoryDialogDismiss = {},
            onCategoryInputChanged = {_,_->},
            onColorClick = {},
            onDeleteCategoryClick = {},
            onDeleteClick = {},
            onDrag = {},
            onDragEnd = {},
            onDragStart = {},
            onEditButtonClick = {},
            onHideCategoryInputField = { uiState = uiState.copy(indexOfCategoryReceivingInput = null) },
            onNameChanged = {},
            onNewCategoryClick = {},
            onScoringModeChanged = {}
        )
    }
}
