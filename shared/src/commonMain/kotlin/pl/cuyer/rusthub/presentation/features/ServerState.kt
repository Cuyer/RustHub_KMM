package pl.cuyer.rusthub.presentation.features

import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.SearchQueryUi

data class ServerState(
    val isLoading: Boolean = false,
    val filters: FilterUi? = null,
    val searchQuery: List<SearchQueryUi> = emptyList()
)