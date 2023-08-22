package com.waynebloom.scorekeeper.ui.components

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.ext.onFocusSelectAll

@Composable
@SuppressLint("ModifierParameter")
fun OutlinedTextFieldWithErrorDescription(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    groupModifier: Modifier = Modifier,
    label: @Composable (() -> Unit)?,
    isError: Boolean,
    colors: TextFieldColors,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    @StringRes errorDescription: Int,
    selectAllOnFocus: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = groupModifier.fillMaxWidth()
    ) {
        val textFieldModifier = if (selectAllOnFocus) {
            Modifier
                .onFocusSelectAll(
                    textFieldValueState = textFieldValue,
                    onTextFieldValueChanged = { onValueChange(it) }
                )
                .fillMaxWidth()
        } else Modifier

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            colors = colors,
            label = label,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            modifier = textFieldModifier.fillMaxWidth()
        )

        if (isError) {
            Text(
                text = stringResource(id = errorDescription),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
