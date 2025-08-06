package pl.cuyer.rusthub.presentation.features.monument

import pl.cuyer.rusthub.domain.model.MonumentType

sealed interface MonumentAction {
    data class OnMonumentClick(val slug: String) : MonumentAction
    data class OnSearch(val query: String) : MonumentAction
    data class OnTypeChange(val type: MonumentType?) : MonumentAction
    data object OnRefresh : MonumentAction
    data class OnError(val exception: Throwable) : MonumentAction
    data object OnClearSearchQuery : MonumentAction
    data object DeleteSearchQueries : MonumentAction
    data class DeleteSearchQueryByQuery(val query: String) : MonumentAction
}
