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
    private fun disposeAds() {
        state.value.ads.values.forEach { ad ->
            ad?.destroy()
        }
    }

    override fun onCleared() {
        disposeAds()
        clear()
        super.onCleared()
    }
}
