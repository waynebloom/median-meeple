package com.waynebloom.scorekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun IconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color = MaterialTheme.colors.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    visibleSize: Dp = 48.dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    var boxModifier = modifier.clip(shape).background(backgroundColor, shape).size(visibleSize)
    var iconModifier = Modifier.fillMaxSize().padding(visibleSize / 4)
    val clickableModifier = Modifier.minimumInteractiveComponentSize().clickable(onClick = onClick)

    boxModifier = if (enabled) {
        boxModifier.then(clickableModifier)
    } else {
        boxModifier
    }
    iconModifier = if (!enabled) {
        iconModifier.alpha(Alpha.disabled)
    } else {
        iconModifier
    }

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
fun IconButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color = MaterialTheme.colors.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    visibleSize: Dp = 48.dp,
    enabled: Boolean = true,
    endText: String? = null,
    onClick: () -> Unit,
) {
    var boxModifier = modifier.clip(shape).background(backgroundColor, shape).size(visibleSize)
    var iconModifier = Modifier.fillMaxSize().padding(visibleSize / 4)
    val clickableModifier = Modifier.minimumInteractiveComponentSize().clickable { onClick() }

    boxModifier = if (enabled) {
        boxModifier.then(clickableModifier)
    } else {
        boxModifier
    }
    iconModifier = if (!enabled) {
        iconModifier.alpha(Alpha.disabled)
    } else {
        iconModifier
    }

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
                    .background(backgroundColor, shape)
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

@Composable
fun SmallIconButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    foregroundColor: Color = MaterialTheme.colors.primary,
    enabled: Boolean = true,
    endText: String? = null,
    onClick: () -> Unit,
) {
    val boxModifier = modifier
        .minimumInteractiveComponentSize()
        .clip(MaterialTheme.shapes.small)
        .background(backgroundColor, CircleShape)
        .size(32.dp)
    val boxClickableModifier = if (enabled) Modifier.clickable { onClick() } else Modifier
    val iconModifier = Modifier
        .padding(8.dp)
        .alpha(alpha = if (enabled) 1f else Alpha.disabled)

    Box(
        modifier = boxModifier.then(boxClickableModifier),
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

@Preview
@Composable
fun IconButtonPreview() {
    MedianMeepleTheme {

        IconButton(
            painter = painterResource(id = R.drawable.ic_bar_chart),
            onClick = {}
        )
    }
}

@Preview
@Composable
fun SmallIconButtonPreview() {
    MedianMeepleTheme {

        SmallIconButton(
            painter = painterResource(id = R.drawable.ic_bar_chart),
            onClick = {}
        )
    }
}
