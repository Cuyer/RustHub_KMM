package pl.cuyer.rusthub.data.ads

import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository

class NativeAdRepositoryImpl : NativeAdRepository {
    override fun preload(adId: String) {}
    override fun get(adId: String): NativeAdWrapper? = null
    override fun clear() {}
}

