package pl.cuyer.rusthub.presentation.features.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase
import pl.cuyer.rusthub.domain.usecase.ads.PreloadNativeAdUseCase

open class BaseNativeAdViewModel(
    private val preloadNativeAdUseCase: PreloadNativeAdUseCase,
    private val getNativeAdUseCase: GetNativeAdUseCase,
    private val clearNativeAdsUseCase: ClearNativeAdsUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(NativeAdState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = NativeAdState()
    )

    fun onAction(action: AdAction) {
        when (action) {
            is AdAction.LoadAd -> loadAd(action.adId)
        }
    }

    private fun loadAd(adId: String) {
        coroutineScope.launch {
            val ad = getNativeAdUseCase(adId)
            if (ad != null) {
                _state.update { current -> current.copy(ads = current.ads + (adId to ad)) }
            } else {
                preloadNativeAdUseCase(adId)
            }
        }
    }

    open fun clear() {
        coroutineScope.launch {
            clearNativeAdsUseCase()
        }
    }
}
