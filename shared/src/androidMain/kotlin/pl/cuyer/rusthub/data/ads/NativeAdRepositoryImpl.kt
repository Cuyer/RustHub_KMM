package pl.cuyer.rusthub.data.ads
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.RequiresPermission
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository
import pl.cuyer.rusthub.util.ActivityProvider
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

@SuppressLint("MissingPermission")
class NativeAdRepositoryImpl(
    private val activityProvider: ActivityProvider
) : NativeAdRepository {

    private data class CachedAd(val ad: NativeAd, val loadedAt: Long) {
        fun isExpired() = System.currentTimeMillis() - loadedAt >= EXPIRATION_TIME_MS
    }

    private val cache = ConcurrentHashMap<String, ConcurrentLinkedDeque<CachedAd>>()
    private val mutexes = ConcurrentHashMap<String, Mutex>()
    private val preloadedIds = ConcurrentHashMap.newKeySet<String>()
    private val flows = ConcurrentHashMap<String, MutableStateFlow<NativeAdWrapper?>>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        scope.launch {
            while (isActive) {
                delay(EXPIRATION_TIME_MS)
                clearExpiredAds()
                preloadedIds.forEach { preload(it) }
            }
        }
    }

    private suspend fun <T> withMutex(adId: String, block: suspend (ConcurrentLinkedDeque<CachedAd>) -> T): T {
        val mutex = mutexes.getOrPut(adId) { Mutex() }
        val queue = cache.getOrPut(adId) { ConcurrentLinkedDeque() }
        return mutex.withLock {
            block(queue)
        }
    }

    override fun preload(adId: String) {
        preloadedIds.add(adId)
        scope.launch {
            val toLoad = withMutex(adId) { queue ->
                removeExpiredAds(queue)
                MAX_CACHE_SIZE - queue.size
            }
            repeat(toLoad.coerceAtLeast(0)) { loadAd(adId) }
        }
    }

    override fun get(adId: String): Flow<NativeAdWrapper?> {
        val flow = flows.getOrPut(adId) { MutableStateFlow(null) }
        scope.launch {
            var ad: NativeAd? = null
            withMutex(adId) { queue ->
                while (queue.isNotEmpty() && ad == null) {
                    val cached = queue.pollFirst() ?: break
                    if (cached.isExpired()) {
                        cached.ad.destroy()
                    } else {
                        ad = cached.ad
                    }
                }
            }
            if (ad != null) {
                flow.value = ad
            } else {
                preload(adId)
            }
        }
        return flow.asStateFlow()
    }

    override suspend fun clear() {
        scope.cancel()
        cache.forEach { (id, queue) ->
            withMutex(id) {
                while (queue.isNotEmpty()) {
                    queue.pollFirst()?.ad?.destroy()
                }
            }
        }
        cache.clear()
        mutexes.clear()
        preloadedIds.clear()
        flows.clear()
    }

    private fun loadAd(adId: String) {
        val activity = activityProvider.currentActivity()
        Napier.d(tag = "ads_state", message = "Current activity: $activity, adId: $adId")
        if (activity == null || activity.isFinishing || activity.isDestroyed) return
        Napier.d(tag = "ads_state", message = "Activity is valid, loading ad: $adId")
        val queue = cache.getOrPut(adId) { ConcurrentLinkedDeque() }
        val mutex = mutexes.getOrPut(adId) { Mutex() }
        val flow = flows.getOrPut(adId) { MutableStateFlow(null) }
        val loader = AdLoader.Builder(activity, adId)
            .forNativeAd { ad ->
                scope.launch {
                    mutex.withLock {
                        queue.addLast(CachedAd(ad, System.currentTimeMillis()))
                    }
                    flow.value = ad
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Napier.d(tag = "ads_state", message = "Ad loaded: $adId")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Napier.e(tag = "ads_state", message = "Ad failed to load: $adId, error: ${error.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
    }

    private fun clearExpiredAds() {
        cache.forEach { (id, queue) ->
            scope.launch {
                withMutex(id) {
                    removeExpiredAds(queue)
                }
            }
        }
    }

    private fun removeExpiredAds(queue: ConcurrentLinkedDeque<CachedAd>) {
        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            val cached = iterator.next()
            if (cached.isExpired()) {
                iterator.remove()
                cached.ad.destroy()
            }
        }
    }

    companion object {
        private const val MAX_CACHE_SIZE = 3
        private const val EXPIRATION_TIME_MS = 60 * 60 * 1000L
    }
}