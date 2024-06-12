package com.waynebloom.scorekeeper.components

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.valentinilk.shimmer.shimmer
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
    ) {
        if (ad == null) {
            // TODO: Loading state
        } else {
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
                    starRatingView = adStars.apply {
                        rating = ad.starRating?.toFloat() ?: 0f
                        progressDrawable.setTint(primaryColor)
                    }
                    setNativeAd(ad)
                }
            }
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
    val surfaceModifier = if (ad == null) {
        modifier.shimmer().height(200.dp)
    } else {
        modifier
    }
    Surface(
        shape = MaterialTheme.shapes.medium.copy(
            bottomEnd = CornerSize(32.dp),
            bottomStart = CornerSize(32.dp),
        ),
        tonalElevation = 2.dp,
        modifier = surfaceModifier
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
        }
    }
}
