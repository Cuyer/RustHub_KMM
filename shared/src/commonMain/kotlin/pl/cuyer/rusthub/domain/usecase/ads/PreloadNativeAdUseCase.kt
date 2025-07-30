package pl.cuyer.rusthub.domain.usecase.ads

import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository

class PreloadNativeAdUseCase(private val repository: NativeAdRepository) {
    operator fun invoke(adId: String) {
        repository.preload(adId)
    }
}

