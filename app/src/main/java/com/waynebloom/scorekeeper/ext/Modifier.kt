package com.waynebloom.scorekeeper.ext

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun Modifier.onFocusSelectAll(
    textFieldValueState: TextFieldValue,
    onTextFieldValueChanged: (TextFieldValue) -> Unit
): Modifier =
    composed(
        inspectorInfo = debugInspectorInfo {
            name = "textFieldValueState"
            properties["textFieldValueState"] = textFieldValueState
        }
    ) {
        var triggerEffect: Boolean? by remember { mutableStateOf(null) }
        if (triggerEffect != null) {
            LaunchedEffect(triggerEffect) {
                textFieldValueState.let {
                    val updatedTextFieldValue = it.copy(selection = TextRange(0, it.text.length))
                    onTextFieldValueChanged(updatedTextFieldValue)
                }
            }
        }
        Modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                triggerEffect = triggerEffect?.let { bool ->
                    !bool
                } ?: true
            }
        }
    }
