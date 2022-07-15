package com.waynebloom.highscores.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.painter.Painter
import com.waynebloom.highscores.R

data class Game(
    val name: String,
    @DrawableRes val image: Int = R.drawable.default_img,
    val scores: MutableList<Score> = MutableList(15) { i ->
        Score(
            name = "Score #$i",
            score = i * 2
        )
    }
)
