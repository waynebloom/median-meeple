package com.waynebloom.scorekeeper.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun getActivityWindow(): Window? = LocalView.current.context.getActivityWindow()

private tailrec fun Context.getActivityWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.getActivityWindow()
        else -> null
    }

/**
 * A different [Window] is used to display dialogs, so enabling edge-to-edge in MedianMeepleActivity
 * does not affect dialogs. This function copies the attributes of the activity's window to the
 * dialog's window so that the dialog is also edge-to-edge.
 *
 * This solution was sourced from
 * [Camouflage the Status Bar With Edge-to-Edge Jetpack Compose Screens and Dialogs](https://www.droidcon.com/2024/01/15/camouflage-the-status-bar-with-edge-to-edge-jetpack-compose-screens-and-dialogs/)
 * by Katie Barnett.
 */
@Composable
fun SetDialogDestinationToEdgeToEdge() {
    val activityWindow = getActivityWindow()
    val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
    val parentView = LocalView.current.parent as View
    SideEffect {
        if (activityWindow != null && dialogWindow != null) {
            val attributes = WindowManager.LayoutParams().apply {
                copyFrom(activityWindow.attributes)
                type = dialogWindow.attributes.type
                alpha = dialogWindow.attributes.alpha
                dimAmount = dialogWindow.attributes.dimAmount
                flags = dialogWindow.attributes.flags
                windowAnimations = dialogWindow.attributes.windowAnimations
            }
            dialogWindow.attributes = attributes
            parentView.layoutParams = FrameLayout.LayoutParams(
                activityWindow.decorView.width,
                activityWindow.decorView.height
            )
        }
    }
}