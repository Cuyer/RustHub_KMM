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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class NativeAdRepositoryImpl(
    private val context: Context
) : NativeAdRepository {

    private data class CachedAd(val ad: NativeAd, val loadedAt: Long)

    private val cache: ConcurrentMap<String, ArrayDeque<CachedAd>> = ConcurrentHashMap()
    private val preloadedIds = ConcurrentHashMap.newKeySet<String>()
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    init {
        scope.launch {
            while (isActive) {
                delay(EXPIRATION_TIME_MS)
                clearExpiredAds()
                preloadedIds.forEach { preload(it) }
            }
        }
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun preload(adId: String) {
        preloadedIds.add(adId)
        val queue = cache.getOrPut(adId) { ArrayDeque() }
        removeExpiredAds(queue)
        val toLoad = MAX_CACHE_SIZE - queue.size
        repeat(toLoad) { loadAd(adId) }
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun get(adId: String): NativeAdWrapper? {
        val queue = cache[adId]
        var ad: NativeAd? = null
        queue?.let {
            while (it.isNotEmpty() && ad == null) {
                val cached = it.removeFirst()
                if (cached.isExpired()) {
                    cached.ad.destroy()
                } else {
                    ad = cached.ad
                }
            }
        }
        preload(adId)
        return ad
    }

    override fun clear() {
        scope.cancel()
        cache.forEach { (_, queue) ->
            while (queue.isNotEmpty()) {
                queue.removeFirst().ad.destroy()
            }
        }
        cache.clear()
        preloadedIds.clear()
    }

    private fun loadAd(adId: String) {
        val loader = AdLoader.Builder(context, adId)
            .forNativeAd { ad ->
                val queue = cache.getOrPut(adId) { ArrayDeque() }
                queue.addLast(CachedAd(ad, System.currentTimeMillis()))
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

    private fun clearExpiredAds() {
        cache.forEach { (_, queue) ->
            removeExpiredAds(queue)
        }
    }

    private fun removeExpiredAds(queue: ArrayDeque<CachedAd>) {
        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            val cached = iterator.next()
            if (cached.isExpired()) {
                iterator.remove()
                cached.ad.destroy()
            }
        }
    }

    private fun CachedAd.isExpired(): Boolean =
        System.currentTimeMillis() - loadedAt >= EXPIRATION_TIME_MS

    companion object {
        private const val MAX_CACHE_SIZE = 3
        private const val EXPIRATION_TIME_MS = 60 * 60 * 1000L
    }
}
