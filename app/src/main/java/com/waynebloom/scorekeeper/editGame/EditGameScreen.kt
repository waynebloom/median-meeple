package com.waynebloom.scorekeeper.editGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.IconButton
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.components.RadioButtonOption
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme

@Composable
fun EditGameScreen(
    uiState: EditGameUiState,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    onCategoryDialogDismiss: () -> Unit,
    onCategoryInputChanged: (TextFieldValue, Int) -> Unit,
    onColorClick: (String) -> Unit,
    onDeleteCategoryClick: () -> Unit,
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
                    categories = uiState.categories,
                    indexOfCategoryReceivingInput = uiState.indexOfCategoryReceivingInput,
                    isCategoryDialogOpen = uiState.isCategoryDialogOpen,
                    colorOptions = LocalCustomThemeColors.current.getColorsAsKeyList(),
                    selectedColorId = uiState.color,
                    modifier = modifier,
                    onConfirmClick = onConfirmClick,
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun EditGameScreen(
    nameInput: TextFieldInput,
    scoringMode: ScoringMode,
    categories: List<CategoryDomainModel>,
    indexOfCategoryReceivingInput: Int?,
    isCategoryDialogOpen: Boolean,
    colorOptions: List<String>,
    selectedColorId: String,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    onCategoryDialogDismiss: () -> Unit,
    onCategoryInputChanged: (TextFieldValue, Int) -> Unit,
    onColorClick: (String) -> Unit,
    onDeleteCategoryClick: () -> Unit,
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

            EditCategoriesBottomSheet(
                categories = categories,
                indexOfCategoryReceivingInput = indexOfCategoryReceivingInput,
                onCategoryClick = onCategoryClick,
                onDismiss = onCategoryDialogDismiss,
                onDeleteCategoryClick = onDeleteCategoryClick,
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDragStart = onDragStart,
                onHideInputField = onHideCategoryInputField,
                onInputChanged = onCategoryInputChanged,
                onNewClick = onNewCategoryClick
            )
        }

        Scaffold(
            topBar = {
                EditGameScreenTopBar(
                    title = nameInput.value.text,
                    onConfirmClick = onConfirmClick,
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
                        onScoringModeClick = onScoringModeChanged,
                        modifier = Modifier.padding(horizontal = Spacing.screenEdge)
                    )
                }

                item {

                    Column(modifier = modifier) {

                        Text(
                            text = stringResource(id = R.string.header_scoring_categories),
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = Spacing.screenEdge),
                        )
                        Spacer(modifier = Modifier.height(Spacing.sectionContent))
                        if (categories.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.screenEdge)
                            ) {

                                categories.forEachIndexed { index, category ->

                                    Chip(
                                        onClick = { onCategoryClick(index) },
                                        shape = MaterialTheme.shapes.small,
                                        content = { Text(text = category.name.text) },
                                        border = BorderStroke(1.dp, MaterialTheme.colors.onBackground.copy(alpha = Alpha.disabled)),
                                        colors = ChipDefaults.chipColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = MaterialTheme.colors.onBackground,
                                        ),
                                    )
                                }
                            }
                            Button(
                                onClick = onEditButtonClick,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .padding(top = Spacing.sectionContent)
                                    .height(40.dp)
                                    .padding(horizontal = Spacing.screenEdge)
                                    .fillMaxWidth(),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colors.primary,
                                    backgroundColor = MaterialTheme.colors.background,
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                            ) {
                                Text(text = stringResource(id = R.string.button_edit_categories))
                            }
                        } else {
                            HelperBox(
                                message = stringResource(id = R.string.info_categories_section_helper),
                                type = HelperBoxType.Info,
                                modifier = Modifier.padding(horizontal = Spacing.screenEdge)
                            )
                            Button(
                                onClick = onNewCategoryClick,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .padding(top = Spacing.sectionContent)
                                    .height(40.dp)
                                    .padding(horizontal = Spacing.screenEdge)
                                    .fillMaxWidth(),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colors.primary,
                                    backgroundColor = MaterialTheme.colors.background,
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                            ) {
                                Text(text = stringResource(id = R.string.button_add_a_category))
                            }
                        }
                    }
                }

                item {

                    CustomThemeSection(
                        selectedColor = selectedColorId,
                        colorOptions = colorOptions,
                        onColorClick = onColorClick
                    )
                }

                item {
                    Divider()
                    Spacer(Modifier.height(Spacing.sectionContent))

                    // TODO: this needs a confirmation dialog before release
                    Button(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .padding(top = Spacing.subSectionContent, bottom = Spacing.screenEdge)
                            .height(40.dp)
                            .padding(horizontal = Spacing.screenEdge)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colors.error,
                            backgroundColor = MaterialTheme.colors.background,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colors.error),
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditGameScreenTopBar(
    title: String,
    onConfirmClick: () -> Unit,
) {

    val dataWasSavedToast = Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

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
                onClick = {
                    onConfirmClick()
                    dataWasSavedToast.show()
                },
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                imageVector = Icons.Rounded.Check
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
    onScoringModeClick: (ScoringMode) -> Unit,
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

        OutlinedTextFieldWithErrorDescription(
            value = nameTextFieldValue,
            onValueChange = onNameChanged,
            label = { Text(text = stringResource(id = R.string.field_name)) },
            isError = !isNameValid,
            errorDescriptionResource = R.string.error_empty_name,
            selectAllOnFocus = true,
            shape = MaterialTheme.shapes.medium,
        )

        Column(modifier = Modifier.fillMaxWidth()) {

            ScoringMode.entries.forEach { option ->

                RadioButtonOption(
                    menuOption = option,
                    isSelected = selectedMode == option,
                    onSelected = { onScoringModeClick(it as ScoringMode) }
                )
            }
        }
    }
}

// endregion

// region Categories

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
private fun EditCategoriesBottomSheetContent(
    categories: List<CategoryDomainModel>,
    indexOfCategoryReceivingInput: Int?,
    onCategoryClick: (Int) -> Unit,
    onInputChanged: (TextFieldValue, Int) -> Unit,
    onDeleteCategoryClick: () -> Unit,
    onNewClick: () -> Unit,
    onDoneClick: () -> Unit,
    onHideInputField: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragStart: (Int) -> Unit,
    onDragEnd: () -> Unit,
) {

    Column(
        Modifier
            .padding(top = 64.dp)
            .imePadding()
            .clip(
                MaterialTheme.shapes.medium.copy(
                    bottomEnd = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp),
                )
            )
            .background(MaterialTheme.colors.background)
    ) {

        Text(
            text = stringResource(R.string.button_edit_categories),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(Spacing.screenEdge)
        )
        Divider()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
            contentPadding = PaddingValues(
                end = Spacing.screenEdge,
                top = Spacing.sectionContent,
            ),
            modifier = Modifier.weight(1f, fill = false)
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
                    ) {

                        when (it) {

                            index -> {  // confirm button
                                IconButton(
                                    painter = painterResource(id = R.drawable.ic_checkmark),
                                    backgroundColor = Color.Transparent,
                                    foregroundColor = MaterialTheme.colors.onBackground,
                                    onClick = onHideInputField,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            null -> {   // drag handle
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_drag_handle),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .size(48.dp)
                                        .padding(12.dp)
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragStart = { onDragStart(index) },
                                                onDragEnd = onDragEnd,
                                                onDrag = { _, dragAmount ->
                                                    onDrag(
                                                        dragAmount
                                                    )
                                                }
                                            )
                                        },
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            }
                            else -> {   // visual placeholder dot
                                Box(
                                    Modifier
                                        .padding(start = 4.dp)
                                        .size(48.dp)
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
                        val bringIntoViewRequester = remember { BringIntoViewRequester() }

                        LaunchedEffect(index) {
                            bringIntoViewRequester.bringIntoView()
                            focusRequester.requestFocus()
                        }

                        OutlinedTextFieldWithErrorDescription(
                            value = category.name,
                            onValueChange = { onInputChanged(it, index) },
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(start = 4.dp, end = Spacing.sectionContent)
                                .focusRequester(focusRequester)
                                .bringIntoViewRequester(bringIntoViewRequester),
                            selectAllOnFocus = true,
                            isError = category.name.text.isBlank(),
                            errorDescriptionResource = R.string.field_error_empty,
                            keyboardActions = KeyboardActions { onHideInputField() },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(Spacing.sectionContent),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colors.onBackground,
                                focusedBorderColor = MaterialTheme.colors.onBackground.copy(
                                    alpha = Alpha.disabled
                                ),
                            ),
                        )

                        IconButton(
                            imageVector = Icons.Rounded.Delete,
                            backgroundColor = Color.Transparent,
                            foregroundColor = MaterialTheme.colors.error,
                            onClick = onDeleteCategoryClick,
                        )
                    } else {

                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(shape = MaterialTheme.shapes.medium)
                                .clickable {
                                    onCategoryClick(index)
                                }
                                .padding(start = 4.dp, end = Spacing.sectionContent)
                        ) {

                            Text(
                                text = category.name.text,
                                color = MaterialTheme.colors.onBackground,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenEdge)
        ) {
            Button(
                onClick = onNewClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(top = Spacing.subSectionContent, bottom = Spacing.screenEdge)
                    .height(40.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colors.onPrimary,
                    backgroundColor = MaterialTheme.colors.primary,
                ),
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                    )
                    Text(text = "New")
                }
            }
            Spacer(modifier = Modifier.width(Spacing.sectionContent))
            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(top = Spacing.subSectionContent, bottom = Spacing.screenEdge)
                    .height(40.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colors.onPrimary,
                    backgroundColor = MaterialTheme.colors.primary,
                ),
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                    )
                    Text(text = "Done")
                }
            }
        }
    }
}

@Composable
private fun EditCategoriesBottomSheet(
    categories: List<CategoryDomainModel>,
    indexOfCategoryReceivingInput: Int?,
    onCategoryClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    onDeleteCategoryClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragStart: (Int) -> Unit,
    onHideInputField: () -> Unit,
    onInputChanged: (TextFieldValue, Int) -> Unit,
    onNewClick: () -> Unit,
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false,
        ),
    ) {

        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.fillMaxHeight()
        ) {

            // showing this as a "bottom sheet" breaks the baseline "tap background to dismiss"
            // behavior. This code mimics it.
            Box(
                Modifier
                    .clickable(onClick = onDismiss)
                    .fillMaxSize())

            EditCategoriesBottomSheetContent(
                categories,
                indexOfCategoryReceivingInput,
                onCategoryClick,
                onInputChanged,
                onDeleteCategoryClick,
                onNewClick,
                onDismiss,
                onHideInputField,
                onDrag,
                onDragStart,
                onDragEnd,
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
            onColorClick = onColorClick
        )
    }
}

@Composable
fun ColorSelector(
    colorOptions: List<String>,
    selectedColor: String,
    onColorClick: (String) -> Unit,
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
                    .clickable { onColorClick(key) }
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

    UserSelectedPrimaryColorTheme(primaryColor = EditGameSampleData.Default.getResolvedColor()) {
        EditGameScreen(
            uiState = EditGameSampleData.Default,
            onConfirmClick = {},
            onCategoryClick = {},
            onCategoryDialogDismiss = {},
            onCategoryInputChanged = {_,_->},
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
            onScoringModeChanged = {},
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun EditGameScreenNoCategoriesPreview() {

    UserSelectedPrimaryColorTheme(primaryColor = EditGameSampleData.Default.getResolvedColor()) {
        EditGameScreen(
            uiState = EditGameSampleData.NoCategories,
            onConfirmClick = {},
            onCategoryClick = {},
            onCategoryDialogDismiss = {},
            onCategoryInputChanged = {_,_->},
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
            onScoringModeChanged = {},
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun EditGameScreenEditCategoriesPreview() {

    val uiState = EditGameSampleData.CategoryDialog

    UserSelectedPrimaryColorTheme(primaryColor = uiState.getResolvedColor()) {

        EditCategoriesBottomSheet(
            categories = uiState.categories,
            indexOfCategoryReceivingInput = uiState.indexOfCategoryReceivingInput,
            onCategoryClick = {},
            onDismiss = {},
            onDeleteCategoryClick = {},
            onDrag = {},
            onDragEnd = {},
            onDragStart = {},
            onHideInputField = {},
            onInputChanged = {_,_->},
            onNewClick = {}
        )
    }
}
