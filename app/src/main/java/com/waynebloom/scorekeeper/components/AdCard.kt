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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.*
import com.waynebloom.scorekeeper.databinding.AdCardBinding

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
                adBody.setTextColor(onSurface)
                adCtaButton.setBackgroundColor(themeColor)
                adCtaButton.setTextColor(onThemeColor)
                adHeadline.setTextColor(onSurface)
                adLoadingTag.visibility = View.INVISIBLE
                adPrice.setTextColor(onSurface)
                adProgressBar.visibility = View.INVISIBLE
                adSourceStore.setTextColor(onSurface)
                adStars.progressDrawable.setTint(themeColor)
                adTag.background.setTint(themeColor)
                adTag.setTextColor(onThemeColor)
                adView.visibility = View.VISIBLE

                adView.apply {
                    bodyView = adBody.apply { text = currentAd.body }
                    callToActionView = adCtaButton.apply {
                        text = currentAd.callToAction?.sentenceCase() ?: "Learn More"
                    }
                    headlineView = adHeadline.apply { text = currentAd.headline }
                    iconView = adAppIcon.apply { setImageDrawable(currentAd.icon?.drawable) }
                    priceView = adPrice.apply { text = currentAd.price }
                    starRatingView = adStars.apply { rating = currentAd.starRating?.toFloat() ?: 0f }
                    storeView = adSourceStore.apply { text = currentAd.store }
                    setNativeAd(currentAd)
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

fun showAdAtIndex(index: Int, listSize: Int): Boolean {
    val offset = if (listSize <= 5) listSize - 1 else 4
    return (index - offset) % 10 == 0
}
