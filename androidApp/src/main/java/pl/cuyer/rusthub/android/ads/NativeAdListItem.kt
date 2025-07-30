package pl.cuyer.rusthub.android.ads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.compose_util.LocalNativeAdView
import com.google.android.gms.compose_util.NativeAdAttribution
import com.google.android.gms.compose_util.NativeAdBodyView
import com.google.android.gms.compose_util.NativeAdButton
import com.google.android.gms.compose_util.NativeAdCallToActionView
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdPriceView
import com.google.android.gms.compose_util.NativeAdStarRatingView
import com.google.android.gms.compose_util.NativeAdView
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
private fun ApplyNativeListAd(ad: NativeAdWrapper) {
    val nativeAdView = LocalNativeAdView.current
    LaunchedEffect(nativeAdView, ad) { nativeAdView?.setNativeAd(ad) }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NativeAdListLayout(
    modifier: Modifier = Modifier,
    ad: NativeAdWrapper
) {
    ElevatedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraSmall) {
        NativeAdView(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    NativeAdAttribution(text = stringResource(SharedRes.strings.ad_label))
                    ad.headline?.let {
                        NativeAdHeadlineView {
                            Text(it, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    ad.body?.let {
                        NativeAdBodyView {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        ad.starRating?.let { rating ->
                            NativeAdStarRatingView {
                                Text(
                                    text = stringResource(SharedRes.strings.rated, rating),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        ad.price?.let { price ->
                            NativeAdPriceView(modifier = Modifier.padding(start = 8.dp)) {
                                Text(text = price, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                ad.callToAction?.let { cta ->
                    NativeAdCallToActionView(modifier = Modifier.padding(start = 8.dp)) {
                        NativeAdButton(text = cta)
                    }
                }
                ApplyNativeListAd(ad)
            }
        }
    }
}

@Composable
fun NativeAdListItem(
    modifier: Modifier = Modifier,
    ad: NativeAdWrapper?
) {
    ad?.let { NativeAdListLayout(modifier, it) }
}

