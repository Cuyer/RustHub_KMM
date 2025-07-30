package pl.cuyer.rusthub.presentation.features.ads

import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase

class NativeAdViewModel(
    getNativeAdUseCase: GetNativeAdUseCase,
    clearNativeAdsUseCase: ClearNativeAdsUseCase
) : BaseNativeAdViewModel(
    getNativeAdUseCase,
    clearNativeAdsUseCase
) {
    override fun onCleared() {
        clear()
        super.onCleared()
    }
}
