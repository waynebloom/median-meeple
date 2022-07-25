package com.waynebloom.highscores.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ScreenHeader(
    title: String,
    @DrawableRes image: Int,
    topRightButton: @Composable () -> Unit = {},
    titleBarButton: @Composable () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        topRightButton()
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.background.copy(alpha = 1f),
                    shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0))
                )
                .fillMaxWidth()
        ) {
            Text(
                style = MaterialTheme.typography.h4,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                text = title,
                modifier = Modifier
                    .padding(all = 16.dp)
            )
            titleBarButton()
        }
    }
}