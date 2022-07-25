package com.waynebloom.highscores.data

import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.waynebloom.highscores.R
import java.util.*

val EMPTY_GAME = Game()

@Entity
data class Game(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @NonNull
    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "image")
    @DrawableRes val imageId: Int = R.drawable.default_img,
)
