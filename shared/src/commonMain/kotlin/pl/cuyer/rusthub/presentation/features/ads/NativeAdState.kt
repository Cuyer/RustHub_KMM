package pl.cuyer.rusthub.presentation.features.ads

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper

@Immutable
data class NativeAdState(
    val ads: Map<String, NativeAdWrapper?> = emptyMap()
)
