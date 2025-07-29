package pl.cuyer.rusthub.android.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
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
import com.google.android.gms.compose_util.NativeAdBodyView
import com.google.android.gms.compose_util.NativeAdCallToActionView
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdIconView
import com.google.android.gms.compose_util.NativeAdMediaView
import com.google.android.gms.compose_util.NativeAdView
import pl.cuyer.rusthub.android.BuildConfig

@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val loader = AdLoader.Builder(context, BuildConfig.ADMOB_NATIVE_AD_ID)
            .forNativeAd { ad -> nativeAd = ad }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    nativeAd = null
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
        onDispose {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NativeAdIconView(modifier = Modifier.padding(end = 8.dp)) {
                            ad.icon?.drawable?.toBitmap()?.let { bmp ->
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(context).data(bmp).crossfade(true).build(),
                                contentDescription = ad.headline,
                                modifier = Modifier.height(40.dp)
                            )
                        }
                    }
                    NativeAdHeadlineView {
                        Text(text = ad.headline ?: "", style = MaterialTheme.typography.titleLargeEmphasized)
                    }
                }
                NativeAdBodyView(modifier = Modifier.padding(top = 4.dp)) {
                    ad.body?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
                NativeAdMediaView(modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp))
                ad.callToAction?.let { cta ->
                    NativeAdCallToActionView(modifier = Modifier.padding(top = 4.dp)) {
                        Box(modifier = Modifier.align(Alignment.End)) {
                            Text(text = cta, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

