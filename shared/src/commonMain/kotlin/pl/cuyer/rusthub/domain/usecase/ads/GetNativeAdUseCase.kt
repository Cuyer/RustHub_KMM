package pl.cuyer.rusthub.domain.usecase.ads

import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository

class GetNativeAdUseCase(private val repository: NativeAdRepository) {
    operator fun invoke(adId: String): NativeAdWrapper? {
        return repository.get(adId)
    }
}

