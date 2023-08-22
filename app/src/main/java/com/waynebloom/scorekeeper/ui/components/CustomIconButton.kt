package com.waynebloom.scorekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions

@Composable
fun CustomIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color,
    enabled: Boolean = true,
    onTap: () -> Unit,
) {
    var boxModifier = modifier
        .clip(MaterialTheme.shapes.medium)
        .background(
            color = backgroundColor,
            shape = MaterialTheme.shapes.medium
        )
    var iconModifier = Modifier
        .size(Dimensions.Size.minTappableSize)
        .padding(12.dp)

    boxModifier = if (enabled) {
        boxModifier.clickable { onTap() }
    } else boxModifier
    iconModifier = if (!enabled) {
        iconModifier.alpha(Alpha.disabled)
    } else iconModifier

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = imageVector,
            tint = foregroundColor,
            contentDescription = null,
            modifier = iconModifier
        )
    }
}

@Composable
fun CustomIconButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color,
    enabled: Boolean = true,
    endText: String? = null,
    onTap: () -> Unit,
) {
    var boxModifier = modifier
        .clip(MaterialTheme.shapes.medium)
        .background(
            color = backgroundColor,
            shape = MaterialTheme.shapes.medium
        )
    var iconModifier = Modifier
        .size(Dimensions.Size.minTappableSize)
        .padding(12.dp)

    boxModifier = if (enabled) {
        boxModifier.clickable { onTap() }
    } else boxModifier
    iconModifier = if (!enabled) {
        iconModifier.alpha(Alpha.disabled)
    } else iconModifier

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {

        if (endText == null) {

            Icon(
                painter = painter,
                tint = foregroundColor,
                contentDescription = null,
                modifier = iconModifier
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colors.surface, MaterialTheme.shapes.medium)
                    .padding(end = 12.dp)
            ) {

                Icon(
                    painter = painter,
                    tint = foregroundColor,
                    contentDescription = null,
                    modifier = iconModifier
                )

                Text(text = endText)
            }
        }
    }
}
