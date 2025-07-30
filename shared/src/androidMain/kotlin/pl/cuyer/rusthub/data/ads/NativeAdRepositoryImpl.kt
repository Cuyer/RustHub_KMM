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

    private val cache = ConcurrentHashMap<String, ArrayDeque<NativeAd>>()
    private val loading = ConcurrentHashMap.newKeySet<String>()
    private val maxCacheSize = 3

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun preload(adId: String) {
        if ((cache[adId]?.size ?: 0) >= maxCacheSize) return
        if (!loading.add(adId)) return
        val loader = AdLoader.Builder(context, adId)
            .forNativeAd { ad ->
                cache.getOrPut(adId) { ArrayDeque() }.addLast(ad)
                loading.remove(adId)
                if (cache[adId]!!.size < maxCacheSize) {
                    preload(adId)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading.remove(adId)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun get(adId: String): NativeAdWrapper? {
        val ad = cache[adId]?.removeFirstOrNull()
        if (cache[adId].isNullOrEmpty()) {
            preload(adId)
        }
        return ad
    }
}

