package pl.cuyer.rusthub.presentation.features.ads

import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase

expect class NativeAdViewModel(
    getNativeAdUseCase: GetNativeAdUseCase,
    clearNativeAdsUseCase: ClearNativeAdsUseCase
) : BaseNativeAdViewModel
