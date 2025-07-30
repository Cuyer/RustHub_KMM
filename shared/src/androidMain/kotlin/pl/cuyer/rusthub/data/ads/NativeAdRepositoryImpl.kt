package pl.cuyer.rusthub.data.ads

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository
import java.util.concurrent.ConcurrentHashMap

class NativeAdRepositoryImpl(
    private val context: Context
) : NativeAdRepository {

    private val adMap = ConcurrentHashMap<String, NativeAd>()

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun preload(adId: String) {
        if (adMap[adId] != null) return
        val loader = AdLoader.Builder(context, adId)
            .forNativeAd { ad ->
                adMap[adId] = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Ignore errors and try again later
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun get(adId: String): NativeAdWrapper? {
        val ad = adMap.remove(adId)
        if (ad == null) {
            preload(adId)
        }
        return ad
    }
}

