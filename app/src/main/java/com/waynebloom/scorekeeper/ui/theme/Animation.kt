package com.waynebloom.scorekeeper.ui.theme

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with

object DurationMillis {
    const val short = 150
    const val medium = 300
    const val long = 500
}

val enterTransitionWithDelay = fadeIn(
    animationSpec = tween(
        durationMillis = DurationMillis.short,
        delayMillis = DurationMillis.short))
val exitTransition = fadeOut(animationSpec = tween(durationMillis = DurationMillis.short))

val fadeInWithFadeOut = fadeIn() with fadeOut()
val delayedFadeInWithFadeOut = enterTransitionWithDelay with exitTransition
val sizeTransformWithDelay = SizeTransform { initialSize, targetSize ->
    val delay = if (targetSize.height > initialSize.height) 0 else DurationMillis.short
    tween(DurationMillis.short, delay, Ease)
}