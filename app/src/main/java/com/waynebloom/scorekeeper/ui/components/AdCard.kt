package com.waynebloom.scorekeeper.ui.components

import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.databinding.AdCardBinding
import com.waynebloom.scorekeeper.ext.sentenceCase

@Composable
fun AdCard(
    ad: NativeAd? = null,
    primaryColor: Int = MaterialTheme.colors.primary.toArgb(),
    onPrimaryColor: Int = MaterialTheme.colors.onPrimary.toArgb(),
    onSurface: Int = MaterialTheme.colors.onSurface.toArgb()
) {
    Surface(shape = MaterialTheme.shapes.large) {
        AndroidViewBinding(
            factory = AdCardBinding::inflate,
            modifier = Modifier.padding(16.dp)
        ) {
            if (ad != null) {
                adLoadingTag.visibility = View.INVISIBLE
                adProgressBar.visibility = View.INVISIBLE
                adTag.background.setTint(primaryColor)
                adTag.setTextColor(onPrimaryColor)

                adView.apply {
                    setNativeAd(ad)
                    visibility = View.VISIBLE

                    advertiserView = adAdvertiserName.apply {
                        text = if (ad.advertiser.isNullOrBlank()) {
                            context.getString(R.string.ad_no_advertiser)
                        } else ad.advertiser
                        setTextColor(onSurface)
                    }
                    bodyView = adBody.apply {
                        text = ad.body
                        setTextColor(onSurface)
                    }
                    callToActionView = adCtaButton.apply {
                        text = ad.callToAction?.sentenceCase() ?: "Learn More"
                        setBackgroundColor(primaryColor)
                        setTextColor(onPrimaryColor)
                    }
                    headlineView = adHeadline.apply {
                        text = ad.headline
                        setTextColor(onSurface)
                    }
                    iconView = adAppIcon.apply { setImageDrawable(ad.icon?.drawable) }
                    priceView = adPrice.apply {
                        text = ad.price
                        setTextColor(onSurface)
                    }
                    starRatingView = adStars.apply {
                        rating = ad.starRating?.toFloat() ?: 0f
                        progressDrawable.setTint(primaryColor)
                    }
                    storeView = adSourceStore.apply {
                        text = ad.store
                        setTextColor(onSurface)
                    }
                }
            } else {
                adLoadingTag.setTextColor(primaryColor)
                adLoadingTag.visibility = View.VISIBLE
                adProgressBar.indeterminateDrawable.setTint(primaryColor)
                adProgressBar.visibility = View.VISIBLE
                adView.visibility = View.INVISIBLE
            }
        }
    }
}
