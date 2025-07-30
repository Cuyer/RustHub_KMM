package pl.cuyer.rusthub.presentation.features.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase
import pl.cuyer.rusthub.domain.usecase.ads.PreloadNativeAdUseCase

class NativeAdViewModel(
    private val preloadNativeAd: PreloadNativeAdUseCase,
    private val getNativeAd: GetNativeAdUseCase
) : BaseViewModel() {

    private val adMap = mutableMapOf<Int, MutableStateFlow<NativeAdWrapper?>>()

    fun observeAd(slot: Int, adId: String): StateFlow<NativeAdWrapper?> {
        val flow = adMap.getOrPut(slot) { MutableStateFlow(null) }
        if (flow.value == null) {
            coroutineScope.launch {
                val ad = getNativeAd(adId)
                if (ad == null) {
                    preloadNativeAd(adId)
                }
                flow.value = ad
            }
        }
        return flow.asStateFlow()
    }

    fun releaseAd(slot: Int) {
        adMap.remove(slot)
    }

    fun preloadBatch(adId: String, count: Int = 3) {
        coroutineScope.launch {
            repeat(count) { preloadNativeAd(adId) }
        }
    }
}
