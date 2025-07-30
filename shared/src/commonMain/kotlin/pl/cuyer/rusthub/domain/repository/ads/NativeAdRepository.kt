package pl.cuyer.rusthub.domain.repository.ads

import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper

interface NativeAdRepository {
    fun preload(adId: String)
    suspend fun get(adId: String): NativeAdWrapper?
    suspend fun clear()
}

