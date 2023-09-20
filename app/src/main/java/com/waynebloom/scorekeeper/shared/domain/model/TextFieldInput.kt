package com.waynebloom.scorekeeper.shared.domain.model

import androidx.compose.ui.text.input.TextFieldValue

data class TextFieldInput(
    var hasReceivedInput: Boolean = false,
    var isValid: Boolean = true,
    var value: TextFieldValue = TextFieldValue(),
)
