package com.waynebloom.scorekeeper.components

import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.*
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.databinding.AdCardBinding
import com.waynebloom.scorekeeper.ext.sentenceCase

@Composable
fun AdCard(
    currentAd: NativeAd? = null,
    themeColor: Int = MaterialTheme.colors.primary.toArgb(),
    onThemeColor: Int = MaterialTheme.colors.onPrimary.toArgb(),
    onSurface: Int = MaterialTheme.colors.onSurface.toArgb()
) {
    Surface(shape = MaterialTheme.shapes.small) {
        AndroidViewBinding(
            factory = AdCardBinding::inflate,
            modifier = Modifier.padding(16.dp)
        ) {
            if (currentAd != null) {
                adLoadingTag.visibility = View.INVISIBLE
                adProgressBar.visibility = View.INVISIBLE
                adTag.background.setTint(themeColor)
                adTag.setTextColor(onThemeColor)

                adView.apply {
                    setNativeAd(currentAd)
                    visibility = View.VISIBLE

                    advertiserView = adAdvertiserName.apply {
                        text = if (currentAd.advertiser.isNullOrBlank()) {
                            context.getString(R.string.ad_no_advertiser)
                        } else currentAd.advertiser
                        setTextColor(onSurface)
                    }
                    bodyView = adBody.apply {
                        text = currentAd.body
                        setTextColor(onSurface)
                    }
                    callToActionView = adCtaButton.apply {
                        text = currentAd.callToAction?.sentenceCase() ?: "Learn More"
                        setBackgroundColor(themeColor)
                        setTextColor(onThemeColor)
                    }
                    headlineView = adHeadline.apply {
                        text = currentAd.headline
                        setTextColor(onSurface)
                    }
                    iconView = adAppIcon.apply { setImageDrawable(currentAd.icon?.drawable) }
                    priceView = adPrice.apply {
                        text = currentAd.price
                        setTextColor(onSurface)
                    }
                    starRatingView = adStars.apply {
                        rating = currentAd.starRating?.toFloat() ?: 0f
                        progressDrawable.setTint(themeColor)
                    }
                    storeView = adSourceStore.apply {
                        text = currentAd.store
                        setTextColor(onSurface)
                    }
                }
            } else {
                adLoadingTag.setTextColor(themeColor)
                adLoadingTag.visibility = View.VISIBLE
                adProgressBar.indeterminateDrawable.setTint(themeColor)
                adProgressBar.visibility = View.VISIBLE
                adView.visibility = View.INVISIBLE
            }
        }
    }
}
