package com.waynebloom.scorekeeper.components

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.databinding.LargeImageAdBinding
import com.waynebloom.scorekeeper.databinding.SmallImageAdBinding
import com.waynebloom.scorekeeper.ext.sentenceCase

@Composable
fun LargeImageAdCard(
    modifier: Modifier = Modifier,
    ad: NativeAd? = null,
    primaryColor: Int = MaterialTheme.colorScheme.primary.toArgb(),
    onPrimaryColor: Int = MaterialTheme.colorScheme.onPrimary.toArgb(),
    onSurface: Int = MaterialTheme.colorScheme.onSurface.toArgb(),
) {

    Surface(
        shape = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(32.dp)),
        tonalElevation = 2.dp,
        modifier = modifier
            .heightIn(min = 300.dp)
            .fillMaxWidth()
    ) {
        if (ad != null) {
            AndroidViewBinding(LargeImageAdBinding::inflate) {
                adView.apply {
                    adTag.apply {
                        background.setTint(primaryColor)
                        setTextColor(onPrimaryColor)
                    }
                    callToActionView = adCtaButton.apply {
                        text = ad.callToAction?.sentenceCase() ?: "Learn More"
                        backgroundTintList = ColorStateList.valueOf(primaryColor)
                        setTextColor(onPrimaryColor)
                    }
                    headlineView = adHeadline.apply {
                        text = ad.headline
                        setTextColor(onSurface)
                    }
                    mediaView = adImage.apply {
                        mediaContent = ad.mediaContent
                        setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
                            override fun onChildViewAdded(parent: View?, child: View?) {
                                if (child is ImageView) {
                                    child.adjustViewBounds = true
                                    child.scaleType = ImageView.ScaleType.FIT_CENTER
                                }
                            }
                            override fun onChildViewRemoved(parent: View?, child: View?) {}
                        })
                    }
                    iconView = adAppIcon.apply {
                        setImageDrawable(ad.icon?.drawable)
                    }
                    setNativeAd(ad)
                }
            }
        } else {
            AdLoadingIndicator(96.dp)
        }
    }
}

@Composable
fun SmallImageAdCard(
    modifier: Modifier = Modifier,
    ad: NativeAd? = null,
    primaryColor: Int = MaterialTheme.colorScheme.primary.toArgb(),
    onPrimaryColor: Int = MaterialTheme.colorScheme.onPrimary.toArgb(),
    onSurface: Int = MaterialTheme.colorScheme.onSurface.toArgb(),
) {

    Surface(
        shape = MaterialTheme.shapes.medium.copy(
            bottomEnd = CornerSize(32.dp),
            bottomStart = CornerSize(32.dp),
        ),
        tonalElevation = 2.dp,
        modifier = modifier.heightIn(min = 230.dp)
    ) {
        if (ad != null)  {
            AndroidViewBinding(SmallImageAdBinding::inflate) {
                adView.apply {
                    adTag.apply {
                        background.setTint(primaryColor)
                        setTextColor(onPrimaryColor)
                    }
                    callToActionView = adCtaButton.apply {
                        text = ad.callToAction?.sentenceCase() ?: "Learn More"
                        backgroundTintList = ColorStateList.valueOf(primaryColor)
                        setTextColor(onPrimaryColor)
                    }
                    headlineView = adHeadline.apply {
                        text = ad.headline
                        setTextColor(onSurface)
                    }
                    mediaView = adImage.apply {
                        mediaContent = ad.mediaContent
                        setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
                            override fun onChildViewAdded(parent: View?, child: View?) {
                                if (child is ImageView) {
                                    child.adjustViewBounds = true
                                    child.scaleType = ImageView.ScaleType.CENTER_CROP
                                }
                            }
                            override fun onChildViewRemoved(parent: View?, child: View?) {}
                        })
                    }
                    iconView = adAppIcon.apply {
                        setImageDrawable(ad.icon?.drawable)
                    }
                    setNativeAd(ad)
                }
            }
        } else {
            AdLoadingIndicator(48.dp)
        }
    }
}

@Composable
fun AdLoadingIndicator(
    size: Dp,
    modifier: Modifier = Modifier,
    transition: InfiniteTransition = rememberInfiniteTransition(label = ""),
) {
    val alpha by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        label = "",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                    CircleShape
                )
        ) {
            Text(
                text = "Ad",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.surface
                    .copy(alpha = alpha)
                    .compositeOver(MaterialTheme.colorScheme.primary),
                textAlign = TextAlign.Center,
            )
        }
    }
}
