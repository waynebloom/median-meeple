package com.waynebloom.scorekeeper.components

import android.content.res.ColorStateList
import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.databinding.AdCardBinding
import com.waynebloom.scorekeeper.ext.sentenceCase

@Composable
fun AdCard(
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
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        AndroidViewBinding(
            factory = AdCardBinding::inflate,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 12.dp)
        ) {
            if (ad != null) {
                adLoadingTag.visibility = View.INVISIBLE
                adProgressBar.visibility = View.INVISIBLE
                adTag.background.setTint(primaryColor)
                adTag.setTextColor(onPrimaryColor)

                adView.apply {
                    setNativeAd(ad)
                    visibility = View.VISIBLE

                    bodyView = adBody.apply {
                        text = ad.body
                        setTextColor(onSurface)
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
                    iconView = adAppIcon.apply {
                        setImageDrawable(ad.icon?.drawable)
                    }
                    priceView = adPrice.apply {
                        text = ad.price
                        setTextColor(onSurface)
                    }
                    starRatingView = adStars.apply {
                        rating = ad.starRating?.toFloat() ?: 0f
                        progressDrawable.setTint(primaryColor)
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
