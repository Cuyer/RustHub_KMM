package pl.cuyer.rusthub.presentation.features.ads

import androidx.compose.runtime.Immutable

@Immutable
sealed interface AdAction {
    @Immutable
    data class LoadAd(val adId: String) : AdAction
}
