package com.waynebloom.scorekeeper.theme

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import com.waynebloom.scorekeeper.constants.DurationMs


@OptIn(ExperimentalAnimationApi::class)
object Animation {
    private val enterTransitionWithDelay = fadeIn(
        animationSpec = tween(
            durationMillis = DurationMs.SHORT,
            delayMillis = DurationMs.SHORT))
    private val exitTransition = fadeOut(animationSpec = tween(durationMillis = DurationMs.SHORT))

    val fadeInWithFadeOut = fadeIn() with fadeOut()
    val delayedFadeInWithFadeOut = enterTransitionWithDelay with exitTransition
    val sizeTransformWithDelay = SizeTransform { initialSize, targetSize ->
        val delay = if (targetSize.height > initialSize.height) 0 else DurationMs.SHORT
        tween(DurationMs.SHORT, delay, Ease)
    }
}
