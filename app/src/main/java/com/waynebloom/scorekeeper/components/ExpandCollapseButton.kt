package com.waynebloom.scorekeeper.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.ui.theme.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.sizeTransformWithDelay

@Composable
fun ExpandCollapseButton(
    text: String?,
    expanded: Boolean,
    themeColor: Color,
    onTap: () -> Unit,
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .defaultMinSize(
                minWidth = Dimensions.Size.minTappableSize,
                minHeight = Dimensions.Size.minTappableSize)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.surface)
            .clickable { onTap() }
    ) {

        AnimatedContent(
            targetState = expanded,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
        ) {

            if (it) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_up),
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp))
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (text != null) {

                        Text(
                            text = text,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpandCollapseButtonPreview() {
    MedianMeepleTheme {
        ExpandCollapseButtonPreview()
    }
}