package pl.cuyer.rusthub.domain.repository.ads

import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper

import kotlinx.coroutines.flow.Flow

interface NativeAdRepository {
    fun preload(adId: String)
    fun get(adId: String): Flow<NativeAdWrapper?>
    suspend fun clear()
}

