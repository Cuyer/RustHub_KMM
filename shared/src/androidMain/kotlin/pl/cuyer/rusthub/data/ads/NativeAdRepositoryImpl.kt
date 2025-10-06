package pl.cuyer.rusthub.data.ads

import android.annotation.SuppressLint
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository
import pl.cuyer.rusthub.util.ActivityProvider
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class NativeAdRepositoryImpl(
    private val activityProvider: ActivityProvider
) : NativeAdRepository {

    override fun get(adId: String): Flow<NativeAdWrapper?> = flow {
        emit(loadAd(adId))
    }

    override suspend fun clear() {
        // no-op
    }

    private suspend fun loadAd(adId: String): NativeAd? = suspendCancellableCoroutine { cont ->
        val activity = activityProvider.currentActivity()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val loader = AdLoader.Builder(activity, adId)
            .forNativeAd { ad ->
                if (!cont.isCompleted) cont.resume(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Napier.w(tag = "ads_state", message = "Failed to load ad: ${error.message}")
                    if (!cont.isCompleted) cont.resume(null)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder()
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                .build())
            .build()

        loader.loadAd(AdRequest.Builder().build())
        cont.invokeOnCancellation {
            // no-op
        }
    }
}
