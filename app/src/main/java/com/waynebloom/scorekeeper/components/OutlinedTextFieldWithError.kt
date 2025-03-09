package com.waynebloom.scorekeeper.components

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.ext.onFocusSelectAll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("ModifierParameter")
fun OutlinedTextFieldWithErrorDescription(
	value: TextFieldValue,
	onValueChange: (TextFieldValue) -> Unit,
	modifier: Modifier = Modifier,
	selectAllOnFocus: Boolean = true,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	textStyle: TextStyle = LocalTextStyle.current,
	label: @Composable (() -> Unit)? = null,
	placeholder: @Composable (() -> Unit)? = null,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	isError: Boolean = false,
	@StringRes errorDescriptionResource: Int? = null,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	singleLine: Boolean = true,
	maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
	minLines: Int = 1,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	shape: Shape = OutlinedTextFieldDefaults.shape,
	contentPadding: PaddingValues = PaddingValues(Dimensions.Spacing.screenEdge),
	colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {

	val textFieldModifier = if (selectAllOnFocus) {
		Modifier
			.onFocusSelectAll(
				textFieldValueState = value,
				onTextFieldValueChanged = { onValueChange(it) }
			)
			.fillMaxWidth()
	} else Modifier

	val textColor = textStyle.color.takeOrElse {
		colors.focusedTextColor
	}
	val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

	Column(
		verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.subSectionContent),
		modifier = modifier.fillMaxWidth()
	) {

		// Decoration box padding isn't exposed in OutlinedTextField, so I copied this
		// over from OutlinedTextField and added my own local bits.
		BasicTextField(
			value = value,
			modifier = if (label != null) {
				textFieldModifier
					.semantics(mergeDescendants = true) {}
					.padding(top = 8.dp)
			} else {
				textFieldModifier
			}
				.background(colors.focusedContainerColor, shape)
				.defaultMinSize(
					minWidth = TextFieldDefaults.MinWidth,
					minHeight = TextFieldDefaults.MinHeight
				),
			onValueChange = onValueChange,
			enabled = enabled,
			readOnly = readOnly,
			textStyle = mergedTextStyle,
			cursorBrush = if (isError) {
				SolidColor(colors.cursorColor)
			} else {
				SolidColor(colors.errorCursorColor)
			},
			visualTransformation = visualTransformation,
			keyboardOptions = keyboardOptions,
			keyboardActions = keyboardActions,
			interactionSource = interactionSource,
			singleLine = singleLine,
			maxLines = maxLines,
			minLines = minLines,
			decorationBox = @Composable { innerTextField ->
				OutlinedTextFieldDefaults.DecorationBox(
					value = value.text,
					visualTransformation = visualTransformation,
					innerTextField = innerTextField,
					placeholder = placeholder,
					label = label,
					leadingIcon = leadingIcon,
					trailingIcon = trailingIcon,
					singleLine = singleLine,
					enabled = enabled,
					isError = isError,
					interactionSource = interactionSource,
					colors = colors,
					contentPadding = contentPadding,
					container = {
						OutlinedTextFieldDefaults.ContainerBox(
							enabled,
							isError,
							interactionSource,
							colors,
							shape
						)
					}
				)
			}
		)

		if (isError && errorDescriptionResource != null) {
			Text(
				text = stringResource(id = errorDescriptionResource),
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(start = Dimensions.Spacing.betweenSections)
			)
		}
	}
}
