package pl.cuyer.rusthub.domain.usecase.ads

import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository

class ClearNativeAdsUseCase(private val repository: NativeAdRepository) {
    operator fun invoke() {
        repository.clear()
    }
}
