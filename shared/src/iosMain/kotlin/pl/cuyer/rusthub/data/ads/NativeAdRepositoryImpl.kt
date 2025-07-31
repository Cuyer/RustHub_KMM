package pl.cuyer.rusthub.data.ads

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository

class NativeAdRepositoryImpl : NativeAdRepository {
    override fun get(adId: String): Flow<NativeAdWrapper?> = flowOf(null)
    override suspend fun clear() {}
}

