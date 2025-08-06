package pl.cuyer.rusthub.presentation.features.monument

import pl.cuyer.rusthub.domain.model.MonumentType

sealed interface MonumentAction {
    data class OnMonumentClick(val slug: String) : MonumentAction
    data class OnSearch(val query: String) : MonumentAction
    data class OnTypeChange(val type: MonumentType?) : MonumentAction
    data class OnError(val exception: Throwable) : MonumentAction
    data object OnRefresh : MonumentAction
}
