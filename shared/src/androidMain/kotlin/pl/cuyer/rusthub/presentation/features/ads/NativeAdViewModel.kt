package pl.cuyer.rusthub.presentation.features.ads

import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase
import pl.cuyer.rusthub.domain.usecase.ads.PreloadNativeAdUseCase

class NativeAdViewModel(
    preloadNativeAdUseCase: PreloadNativeAdUseCase,
    getNativeAdUseCase: GetNativeAdUseCase,
    clearNativeAdsUseCase: ClearNativeAdsUseCase
) : BaseNativeAdViewModel(
    preloadNativeAdUseCase,
    getNativeAdUseCase,
    clearNativeAdsUseCase
) {
    override fun onCleared() {
        clear()
        super.onCleared()
    }
}
