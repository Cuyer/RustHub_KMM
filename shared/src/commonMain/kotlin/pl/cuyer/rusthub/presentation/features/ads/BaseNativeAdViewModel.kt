package pl.cuyer.rusthub.presentation.features.ads

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase

open class BaseNativeAdViewModel(
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
            getNativeAdUseCase(adId)
                .collectLatest { ad ->
                    _state.update { current -> current.copy(ads = current.ads + (adId to ad)) }
                }
        }
    }

    open fun clear() {
        coroutineScope.launch {
            clearNativeAdsUseCase()
        }
    }
}
