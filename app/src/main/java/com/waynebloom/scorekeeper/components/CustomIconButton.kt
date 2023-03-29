package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    foregroundColor: Color,
    enabled: Boolean = true,
    onTap: () -> Unit,
) {
    var boxModifier = modifier
        .clip(MaterialTheme.shapes.small)
        .background(
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.small
        )
    boxModifier = if (enabled) {
        boxModifier.clickable { onTap() }
    } else boxModifier.alpha(0.5f)

    Box(modifier = boxModifier) {
        Icon(
            imageVector = imageVector,
            tint = foregroundColor,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp)
        )
    }
}

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    foregroundColor: Color,
    enabled: Boolean = true,
    onTap: () -> Unit,
) {
    var boxModifier = modifier
        .clip(MaterialTheme.shapes.small)
        .background(
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.small
        )
    boxModifier = if (enabled) {
        boxModifier.clickable { onTap() }
    } else boxModifier.alpha(0.5f)

    Box(modifier = boxModifier) {
        Icon(
            painter = painter,
            tint = foregroundColor,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp)
        )
    }
}