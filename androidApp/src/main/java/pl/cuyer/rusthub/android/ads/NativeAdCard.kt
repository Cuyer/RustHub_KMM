package pl.cuyer.rusthub.android.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import androidx.core.graphics.drawable.toBitmap
import coil3.request.crossfade
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.compose_util.NativeAdAdvertiserView
import com.google.android.gms.compose_util.NativeAdAttribution
import com.google.android.gms.compose_util.NativeAdBodyView
import com.google.android.gms.compose_util.NativeAdCallToActionView
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdIconView
import com.google.android.gms.compose_util.NativeAdMediaView
import com.google.android.gms.compose_util.NativeAdPriceView
import com.google.android.gms.compose_util.NativeAdStarRatingView
import com.google.android.gms.compose_util.NativeAdStoreView
import com.google.android.gms.compose_util.NativeAdView
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.BuildConfig

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NativeAdCard(modifier: Modifier = Modifier, adId: String) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val context = LocalContext.current
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val loader = AdLoader.Builder(context, adId)
            .forNativeAd { ad ->
                if (!isDisposed) {
                    nativeAd = ad
                } else {
                    ad.destroy()
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    nativeAd = null
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
        onDispose {
            isDisposed = true
            nativeAd?.destroy()
            nativeAd = null
        }
    }

    nativeAd?.let { ad ->
        ElevatedCard(modifier = modifier.fillMaxWidth()) {
            NativeAdView(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    NativeAdAttribution(text = stringResource(SharedRes.strings.ad_label))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ad.icon?.let {
                            NativeAdIconView(modifier = Modifier.padding(end = 8.dp)) {
                                it.drawable?.toBitmap()?.let { bmp ->
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(context).data(bmp).crossfade(true)
                                            .build(),
                                        contentDescription = ad.headline,
                                        modifier = Modifier.height(40.dp)
                                    )
                                }
                            }
                        }
                        Column {
                            ad.headline?.let {
                                NativeAdHeadlineView {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleLargeEmphasized
                                    )
                                }
                            }

                            ad.starRating?.let { rating ->
                                NativeAdStarRatingView {
                                    Text(
                                        text = stringResource(SharedRes.strings.rated, rating),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                            ad.advertiser?.let { advertiser ->
                                NativeAdAdvertiserView {
                                    Text(
                                        text = advertiser,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                    ad.body?.let {
                        NativeAdBodyView(modifier = Modifier.padding(top = 4.dp)) {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    ad.mediaContent?.let {
                        NativeAdMediaView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ad.price?.let { price ->
                            NativeAdPriceView(modifier = Modifier.padding(end = 8.dp)) {
                                Text(text = price, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        ad.store?.let { store ->
                            NativeAdStoreView(modifier = Modifier.padding(end = 8.dp)) {
                                Text(text = store, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        ad.callToAction?.let { cta ->
                            NativeAdCallToActionView {
                                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                                    Text(text = cta, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

