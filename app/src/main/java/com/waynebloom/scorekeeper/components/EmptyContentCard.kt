package com.waynebloom.scorekeeper.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyContentCard(
    text: String,
    color: Color = MaterialTheme.colors.primary
) {
    val emptyContentColor = color.copy(alpha = 0.75f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = emptyContentColor,
                shape = MaterialTheme.shapes.small
            )
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = emptyContentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}